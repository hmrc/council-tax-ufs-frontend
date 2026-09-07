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

package utils

import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination._

object PaginationHelper {

  /**
   * Builds a GOV.UK Pagination viewmodel.
   *
   * The API handles pagination server-side — currentPage and totalPages
   * come directly from the API response. We do NOT slice records locally.
   *
   * @param currentPage  1-based current page number (from API response)
   * @param totalPages   total pages (from API response)
   * @param buildPageUrl function from page number to URL string
   */
  def buildPagination(
    currentPage:  Int,
    totalPages:   Int,
    buildPageUrl: Int => String
  ): Pagination = {

    if (totalPages <= 1) return Pagination()

    val items = visiblePageNumbers(currentPage, totalPages).flatMap {
      case Some(n) =>
        Seq(PaginationItem(
          href    = buildPageUrl(n),
          number  = Some(n.toString),
          current = Some(n == currentPage)
        ))
      case None =>
        Seq(PaginationItem(
          href     = "",
          ellipsis = Some(true)
        ))
    }

    Pagination(
      items    = Some(items),
      previous = Option.when(currentPage > 1)(
        PaginationLink(href = buildPageUrl(currentPage - 1))
      ),
      next     = Option.when(currentPage < totalPages)(
        PaginationLink(href = buildPageUrl(currentPage + 1))
      )
    )
  }

  /**
   * Calculates which page numbers to show, with None = ellipsis.
   *
   * GOV.UK pattern:
   *   Always show: first, last, current, one either side of current
   *   Use ellipsis for gaps > 1
   */
  private[utils] def visiblePageNumbers(
    currentPage: Int,
    totalPages:  Int
  ): Seq[Option[Int]] = {
    if (totalPages <= 7) {
      // Show all pages — no ellipsis needed
      (1 to totalPages).map(Some(_))
    } else {
      val buf = scala.collection.mutable.ArrayBuffer.empty[Option[Int]]

      val windowStart = Math.max(2, currentPage - 1)
      val windowEnd   = Math.min(totalPages - 1, currentPage + 1)

      // Always first page
      buf += Some(1)

      // Gap or page 2
      if (windowStart > 2) buf += None
      else if (windowStart == 2) buf += Some(2)

      // Window around current page
      for (p <- windowStart to windowEnd) {
        if (!buf.contains(Some(p))) buf += Some(p)
      }

      // Gap or second-to-last
      if (windowEnd < totalPages - 1) buf += None
      else if (windowEnd == totalPages - 1) {
        if (!buf.contains(Some(totalPages - 1))) buf += Some(totalPages - 1)
      }

      // Always last page
      if (!buf.contains(Some(totalPages))) buf += Some(totalPages)

      buf.toSeq
    }
  }
}