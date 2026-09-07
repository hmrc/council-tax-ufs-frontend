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
import models._
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.ErrorResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SearchResultsService @Inject()(
  connector: BridgeIntegrationConnector
)(implicit ec: ExecutionContext) extends Logging {

  /**
   * Search by postcode for a given page.
   *
   * Delegates to the API via BridgeIntegrationConnector.
   * Pagination is SERVER-SIDE — the API returns only the records for
   * the requested page. We do NOT slice locally.
   *
   * Returns Right(SearchResultsViewModel) on success.
   * Returns Left(errorMessage) on API error.
   */
  def search(
    postcode: String,
    page:     Int
  )(implicit hc: HeaderCarrier): Future[Either[String, Option[SearchResultsViewModel]]] = {

    connector.postcodeSearch(postcode, page).map {

      case Left(error) =>
        logger.warn(s"[SearchResultsService][search] API error for postcode=$postcode page=$page: ${error.message}")
        Left(error.message)

      case Right(result) =>
        val r = result.results

        if (r.records.isEmpty) {
          Right(None)  // No results — show no results page
        } else {
          val entries = r.records.flatMap { record =>
            for {
              listEntry <- record.list_entry
              property  <- listEntry.property
              address   <- property.address
              full      <- address.full
              valuation <- listEntry.valuation
              band      <- valuation.value
              authority <- record.list.flatMap(_.collection_authority).flatMap(_.code)
            } yield PropertyEntry(
              address        = full,
              band           = band,
              localAuthority = authority
            )
          }

          val viewModel = SearchResultsViewModel(
            postcode    = postcode,
            entries     = entries,
            currentPage = r.current_page.getOrElse(page),
            totalPages  = r.total_pages.getOrElse(1),
            totalItems  = r.total_results.getOrElse(entries.size),
            pageSize    = r.page_size.getOrElse(10)
          )

          Right(Some(viewModel))
        }
    }
  }
}