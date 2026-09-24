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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination._

class PaginationHelperSpec extends AnyWordSpec with Matchers {

  "PaginationHelper.visiblePageNumbers" should {
    "show all pages when totalPages is 7 or less" in {
      PaginationHelper.visiblePageNumbers(currentPage = 3, totalPages = 5) shouldBe
        Seq(Some(1), Some(2), Some(3), Some(4), Some(5))
    }

    "show a compact range with ellipses for larger page counts" in {
      PaginationHelper.visiblePageNumbers(currentPage = 5, totalPages = 12) shouldBe
        Seq(Some(1), None, Some(4), Some(5), Some(6), None, Some(12))
    }

    "handle the first page correctly" in {
      PaginationHelper.visiblePageNumbers(currentPage = 1, totalPages = 12) shouldBe
        Seq(Some(1), Some(2), None, Some(12))
    }

    "handle the last page correctly" in {
      PaginationHelper.visiblePageNumbers(currentPage = 12, totalPages = 12) shouldBe
        Seq(Some(1), None, Some(11), Some(12))
    }
  }

  "PaginationHelper.buildPagination" should {
    "return an empty pagination when there is only one page" in {
      val pagination = PaginationHelper.buildPagination(
        currentPage = 1,
        totalPages = 1,
        buildPageUrl = page => s"/page/$page"
      )

      pagination shouldBe Pagination()
    }

    "build page links and previous/next links for the middle of a paginated list" in {
      val pagination = PaginationHelper.buildPagination(
        currentPage = 5,
        totalPages = 12,
        buildPageUrl = page => s"/page/$page"
      )

      pagination.items should not be None
      pagination.items.get.map(_.number) shouldBe
        Seq(Some("1"), None, Some("4"), Some("5"), Some("6"), None, Some("12"))

      pagination.previous shouldBe Some(PaginationLink(href = "/page/4"))
      pagination.next shouldBe Some(PaginationLink(href = "/page/6"))

      pagination.items.get.find(_.number.contains("5")).flatMap(_.current) shouldBe Some(true)
    }

    "build previous and next links correctly on the first page" in {
      val pagination = PaginationHelper.buildPagination(
        currentPage = 1,
        totalPages = 4,
        buildPageUrl = page => s"/page/$page"
      )

      pagination.previous shouldBe None
      pagination.next shouldBe Some(PaginationLink(href = "/page/2"))
    }

    "build previous and next links correctly on the last page" in {
      val pagination = PaginationHelper.buildPagination(
        currentPage = 4,
        totalPages = 4,
        buildPageUrl = page => s"/page/$page"
      )

      pagination.previous shouldBe Some(PaginationLink(href = "/page/3"))
      pagination.next shouldBe None
    }
  }
}