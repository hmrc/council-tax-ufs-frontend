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

  /** Search by postcode for a given page.
    *
    * Delegates to the API via BridgeIntegrationConnector. Pagination is SERVER-SIDE — the API returns only the records for the requested page. We do
    * NOT slice locally.
    *
    * Returns Right(SearchResultsViewModel) on success. Returns Left(errorMessage) on API error.
    */
  def search(
    postcode: String,
    page:     Int
  )(implicit hc: HeaderCarrier): Future[Either[String, Option[SearchResultsViewModel]]] =

    // Always call API without page param — it returns everything

    connector.postcodeSearch(postcode).map {
      case Left(error) =>
        logger.warn(s"[SearchResultsService][search] API error postcode=$postcode: ${error.message}")
        Left(error.message)

      case Right(result) =>
        val r = result.results
        if r.records.isEmpty then {
          Right(None)
        } else {
          // Build all entries from the full response

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

          val pageSize    = r.page_size.getOrElse(20)
          val totalItems  = r.total_results.getOrElse(allEntries.size) // use actual mapped count
          val totalPages  = Math.ceil(totalItems.toDouble / pageSize).toInt.max(1)
          val safePage    = page.max(1).min(totalPages)
          val from        = (safePage - 1) * pageSize
          val pageEntries = allEntries.slice(from, from + pageSize)

          Right(
            Some(
              SearchResultsViewModel(
                postcode = postcode,
                entries = pageEntries,
                currentPage = safePage,
                totalPages = totalPages,
                totalItems = totalItems,
                pageSize = pageSize
              )
            )
          )

        }
    }
}
