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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsValue, Json}

class ApiResponseModelsJsonSpec extends AnyFreeSpec with Matchers {

  private val propertyDetailJson: JsValue = Json.parse(
    """
      |{
      |  "results": { "records": [ { "data": {
      |    "list": {
      |      "id": { "value": "L1" },
      |      "classification": { "code": "C", "meaning": "Council Tax" },
      |      "collection_authority": { "code": "6910", "pointer": "p1" },
      |      "country": { "code": "W", "label": "Wales" }
      |    },
      |    "list_entry": {
      |      "valuation": { "value": "C" },
      |      "period": { "effective_from_date": "1993-04-01", "effective_to_date": "2000-01-01" },
      |      "administration": {
      |        "alteration_date": "2000-01-01", "alteration_seq_no": "1",
      |        "entry_seq_no": "2", "judicially_ordered_by": "VTE"
      |      },
      |      "addresses": { "full": "19 St David Crescent, Cardiff" },
      |      "use": { "description": "Dwelling", "composite_ind": "N", "part_exempt_ind": "N" },
      |      "property": {
      |        "id": { "value": "P1" },
      |        "collection_authority_ref": "REF-1",
      |        "address": { "full": "19 St David Crescent, Cardiff" },
      |        "workflow": { "improvement_ind": "Y" }
      |      }
      |    }
      |  } } ] }
      |}
      |""".stripMargin
  )

  private val postcodeSearchJson: JsValue = Json.parse(
    """
      |{
      |  "results": {
      |    "current_page": 1, "page_size": 20, "total_results": 1, "total_pages": 1,
      |    "has_next": false, "has_previous": false, "last_updated_at": "2025-01-01",
      |    "records": [ {
      |      "list": {
      |        "id": { "value": "L1" },
      |        "class": { "code": "C", "meaning": "Council Tax" },
      |        "collection_authority": { "code": "6910", "label": "Cardiff", "pointer": "p1" },
      |        "country": { "code": "W", "label": "Wales" }
      |      },
      |      "list_entry": {
      |        "valuation": { "value": "C" },
      |        "property": { "id": { "value": "P1" }, "address": { "full": "19 St David Crescent, Cardiff" } }
      |      }
      |    } ]
      |  }
      |}
      |""".stripMargin
  )

  "PropertyDetailResponse" - {

    "must read a fully populated response" in {
      val response = propertyDetailJson.as[PropertyDetailResponse]
      val data     = response.results.records.head.data

      data.list.id.flatMap(_.value) mustBe Some("L1")
      data.list.collection_authority.flatMap(_.pointer) mustBe Some("p1")
      data.list.country.flatMap(_.label) mustBe Some("Wales")
      data.list_entry.valuation.flatMap(_.value) mustBe Some("C")
      data.list_entry.period.flatMap(_.effective_from_date) mustBe Some("1993-04-01")
      data.list_entry.administration.flatMap(_.judicially_ordered_by) mustBe Some("VTE")
      data.list_entry.use.flatMap(_.composite_ind) mustBe Some("N")
      data.list_entry.property.flatMap(_.workflow).flatMap(_.improvement_ind) mustBe Some("Y")
    }

    "must survive a write and read round trip" in {
      val response = propertyDetailJson.as[PropertyDetailResponse]
      Json.toJson(response).as[PropertyDetailResponse] mustBe response
    }

    "must read a response with every optional field missing" in {
      val json = Json.parse("""{ "results": { "records": [ { "data": { "list": {}, "list_entry": {} } } ] } }""")
      val data = json.as[PropertyDetailResponse].results.records.head.data

      data.list.id mustBe None
      data.list_entry.property mustBe None
    }
  }

  "PostcodeSearchResult" - {

    "must read a fully populated response" in {
      val results = postcodeSearchJson.as[PostcodeSearchResult].results
      val record  = results.records.head

      results.total_results mustBe Some(1)
      results.has_next mustBe Some(false)
      results.last_updated_at mustBe Some("2025-01-01")
      record.list.flatMap(_.`class`).flatMap(_.meaning) mustBe Some("Council Tax")
      record.list.flatMap(_.collection_authority).flatMap(_.label) mustBe Some("Cardiff")
      record.list.flatMap(_.country).flatMap(_.label) mustBe Some("Wales")
      record.list_entry.flatMap(_.valuation).flatMap(_.value) mustBe Some("C")
      record.list_entry.flatMap(_.property).flatMap(_.address).flatMap(_.full) mustBe Some("19 St David Crescent, Cardiff")
    }

    "must survive a write and read round trip" in {
      val result = postcodeSearchJson.as[PostcodeSearchResult]
      Json.toJson(result).as[PostcodeSearchResult] mustBe result
    }
  }

  "Partial API responses" - {

    "must read a postcode search record with every optional field missing" in {
      val json   = Json.parse("""{ "results": { "records": [ {} ] } }""")
      val result = json.as[PostcodeSearchResult].results

      result.current_page mustBe None
      result.last_updated_at mustBe None
      result.records.head.list mustBe None
      result.records.head.list_entry mustBe None
      Json.toJson(json.as[PostcodeSearchResult]).as[PostcodeSearchResult] mustBe json.as[PostcodeSearchResult]
    }

    "must read nested objects whose own fields are all missing" in {
      val json = Json.parse(
        """{ "results": { "records": [ { "list": { "id": {}, "class": {}, "collection_authority": {}, "country": {} },
          |"list_entry": { "valuation": {}, "property": { "id": {}, "address": {} } } } ] } }""".stripMargin
      )
      val record = json.as[PostcodeSearchResult].results.records.head

      record.list.flatMap(_.collection_authority).flatMap(_.label) mustBe None
      record.list.flatMap(_.collection_authority).flatMap(_.pointer) mustBe None
      Json.toJson(json.as[PostcodeSearchResult]).as[PostcodeSearchResult] mustBe json.as[PostcodeSearchResult]
      record.list_entry.flatMap(_.property).flatMap(_.address).flatMap(_.full) mustBe None
    }

    "must read property detail nested objects whose own fields are all missing" in {
      val json = Json.parse(
        """{ "results": { "records": [ { "data": { "list": { "id": {}, "classification": {}, "collection_authority": {}, "country": {} },
          |"list_entry": { "valuation": {}, "period": {}, "administration": {}, "addresses": {}, "use": {},
          |"property": { "id": {}, "address": {}, "workflow": {} } } } } ] } }""".stripMargin
      )
      val data = json.as[PropertyDetailResponse].results.records.head.data

      data.list.collection_authority.flatMap(_.pointer) mustBe None
      Json.toJson(json.as[PropertyDetailResponse]).as[PropertyDetailResponse] mustBe json.as[PropertyDetailResponse]
      data.list_entry.property.flatMap(_.workflow).flatMap(_.improvement_ind) mustBe None
    }

    "must reject a postcode search response with no records" in {
      Json.parse("""{ "results": {} }""").validate[PostcodeSearchResult].isError mustBe true
    }
  }

  "SearchCountry.fromLabel" - {

    "must map england and wales regardless of case and whitespace" in {
      SearchCountry.fromLabel(Some("England")) mustBe Some(SearchCountry.England)
      SearchCountry.fromLabel(Some("  WALES ")) mustBe Some(SearchCountry.Wales)
    }

    "must return None for an unknown or missing label" in {
      SearchCountry.fromLabel(Some("Scotland")) mustBe None
      SearchCountry.fromLabel(None) mustBe None
    }
  }

  "SearchResultsViewModel" - {

    "must calculate 1-based start and end indexes" in {
      val entries = Seq(PropertyEntry("1", "Cardiff", "a", "C"), PropertyEntry("2", "Cardiff", "b", "D"))
      val model   = SearchResultsViewModel("CF5 4GP", entries, currentPage = 2, totalPages = 3, totalItems = 12, pageSize = 5)

      model.startIndex mustBe 6
      model.endIndex mustBe 7
    }

    "must report zero indexes when there are no results" in {
      val model = SearchResultsViewModel("CF5 4GP", Nil, currentPage = 1, totalPages = 0, totalItems = 0, pageSize = 20)

      model.startIndex mustBe 0
      model.endIndex mustBe -1
    }

    "must cap the end index at the total number of items" in {
      val entries = Seq(PropertyEntry("1", "Cardiff", "a", "C"), PropertyEntry("2", "Cardiff", "b", "D"), PropertyEntry("3", "Cardiff", "c", "E"))
      val model   = SearchResultsViewModel("CF5 4GP", entries, currentPage = 1, totalPages = 1, totalItems = 2, pageSize = 5)

      model.endIndex mustBe 2
    }
  }
}
