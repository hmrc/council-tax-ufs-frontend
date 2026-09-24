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

package models


import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class SearchResultsViewModelSpec extends AnyWordSpec with Matchers {

  "PostcodeSearchResult" should {
    "deserialize the API response structure" in {
      val json = Json.parse(
        """
          |{
          |  "results": {
          |    "current_page": 1,
          |    "page_size": 20,
          |    "total_results": 42,
          |    "total_pages": 3,
          |    "has_next": true,
          |    "has_previous": false,
          |    "records": [
          |      {
          |        "list": {
          |          "id": { "value": "LIST-1" },
          |          "class": { "code": "A", "meaning": "Band A" },
          |          "collection_authority": { "code": "ABC" }
          |        },
          |        "list_entry": {
          |          "valuation": { "value": "A" },
          |          "property": {
          |            "id": { "value": "PROPERTY-1" },
          |            "address": { "full": "1 Test Street" }
          |          }
          |        }
          |      }
          |    ]
          |  }
          |}
          |""".stripMargin
      )

      val result = json.as[PostcodeSearchResult]

      result.results.current_page shouldBe Some(1)
      result.results.page_size shouldBe Some(20)
      result.results.total_results shouldBe Some(42)
      result.results.total_pages shouldBe Some(3)
      result.results.has_next shouldBe Some(true)
      result.results.has_previous shouldBe Some(false)

      result.results.records.size shouldBe 1
      result.results.records.head.list.flatMap(_.id).flatMap(_.value) shouldBe Some("LIST-1")
      result.results.records.head.list.flatMap(_.`class`).flatMap(_.meaning) shouldBe Some("Band A")
      result.results.records.head.list_entry.flatMap(_.property).flatMap(_.id).flatMap(_.value) shouldBe Some("PROPERTY-1")
      result.results.records.head.list_entry.flatMap(_.property).flatMap(_.address).flatMap(_.full) shouldBe Some("1 Test Street")
    }
  }

  "SearchResultsViewModel" should {
    "return zero indexes when there are no total results" in {
      val model = SearchResultsViewModel(
        postcode = "SW1A 1AA",
        entries = Seq.empty,
        currentPage = 1,
        totalPages = 0,
        totalItems = 0,
        pageSize = 10
      )

      model.startIndex shouldBe 0
      model.endIndex shouldBe -1
    }

    "calculate indexes for a middle page" in {
      val entries = Seq(
        PropertyEntry("prop-11", "Local Authority 1", "11 Test Street", "A"),
        PropertyEntry("prop-12", "Local Authority 1", "12 Test Street", "B"),
        PropertyEntry("prop-13", "Local Authority 1", "13 Test Street", "C"),
        PropertyEntry("prop-14", "Local Authority 1", "14 Test Street", "D"),
        PropertyEntry("prop-15", "Local Authority 1", "15 Test Street", "E")
      )

      val model = SearchResultsViewModel(
        postcode = "SW1A 1AA",
        entries = entries,
        currentPage = 2,
        totalPages = 3,
        totalItems = 25,
        pageSize = 10
      )

      model.startIndex shouldBe 11
      model.endIndex shouldBe 15
    }

    "cap endIndex at totalItems on the last page" in {
      val entries = Seq(
        PropertyEntry("prop-21", "Local Authority 2", "21 Test Street", "A"),
        PropertyEntry("prop-22", "Local Authority 2", "22 Test Street", "B"),
        PropertyEntry("prop-23", "Local Authority 2", "23 Test Street", "C")
      )

      val model = SearchResultsViewModel(
        postcode = "SW1A 1AA",
        entries = entries,
        currentPage = 3,
        totalPages = 3,
        totalItems = 23,
        pageSize = 10
      )

      model.startIndex shouldBe 21
      model.endIndex shouldBe 23
    }
  }
}