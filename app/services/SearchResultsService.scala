/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import connectors.BridgeIntegrationConnector
import models.*
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.ErrorResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SearchResultsService @Inject() (
  connector: BridgeIntegrationConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  /** Search using the Bridge Integration query parameters.
    *
    * The API performs pagination and returns the records and metadata for the requested page.
    *
    * Returns Right(SearchResultsViewModel) on success and Left(ErrorResponse) on API error.
    */
  def search(query: SearchQuery)
    (implicit hc: HeaderCarrier): Future[Either[ErrorResponse, Option[SearchResultsViewModel]]] =
    connector.postcodeSearch(query).map {

      case Left(error) =>
        logger.warn(s"[SearchResultsService][search] API error status=${error.statusCode}")
        Left(error)

      //case Right(_) => Left(ErrorResponse(500, "Test error"))
      // case Right(_) => Left(ErrorResponse(404, "Not found"))

      case Right(result) =>
        val r = result.results
        if r.records.isEmpty && r.total_results.getOrElse(0) == 0 then {
          Right(None)
        } else {
          val allEntries = r.records.flatMap { record =>
            val maybeAddress = for {
              listEntry <- record.list_entry
              property  <- listEntry.property
              address   <- property.address
              full      <- address.full
            } yield full
            val band = (for {
              listEntry <- record.list_entry
              valuation <- listEntry.valuation
              value     <- valuation.value
            } yield value).getOrElse("Unknown")
            val authority = record.list
              .flatMap(_.collection_authority)
              .flatMap(_.code)
              .getOrElse("Unknown")

            val propertyId = record.list_entry
              .flatMap(_.property)
              .flatMap(_.id)
              .flatMap(_.value)
              .getOrElse("")
            maybeAddress.map { full =>
              PropertyEntry(
                propertyId = propertyId,
                address = full,
                band = band,
                localAuthority = authority
              )
            }
          }
          logger.info(s"[SearchResultsService] r.records=${r.records.size} allEntries=${allEntries.size} total_results=${r.total_results}")

          val pageSize    = r.page_size.filter(_ > 0).getOrElse(20)
          val totalItems  = r.total_results.getOrElse(allEntries.size)
          val totalPages  = r.total_pages.filter(_ > 0).getOrElse(Math.ceil(totalItems.toDouble / pageSize).toInt.max(1))
          val currentPage = r.current_page.orElse(query.page.flatMap(_.toIntOption)).getOrElse(1).max(1)
          val countries = r.records
            .flatMap(_.list.flatMap(_.country).flatMap(country => SearchCountry.fromLabel(country.label)))
            .distinct match {
              case Seq() => Seq(SearchCountry.England)
              case values => values
            }

          Right(
            Some(
              SearchResultsViewModel(
                postcode = query.postcode,
                entries = allEntries,
                currentPage = currentPage,
                totalPages = totalPages,
                totalItems = totalItems,
                pageSize = pageSize,
                countries = countries
              )
            )
          )

        }
    }
}
