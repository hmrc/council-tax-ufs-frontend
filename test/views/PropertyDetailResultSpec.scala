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

class PropertyDetailResultSpec extends AnyWordSpec with Matchers {

  "PropertyDetailResponse" should {
    "deserialize the API payload into the model" in {
      val json = Json.parse(
        """
          |{
          |  "results": {
          |    "records": [
          |      {
          |        "data": {
          |          "list": {
          |            "id": { "value": "LIST-123" },
          |            "classification": { "code": "A", "meaning": "Band A" },
          |            "collection_authority": { "code": "ABC" },
          |            "country": { "code": "E92000001" }
          |          },
          |          "list_entry": {
          |            "valuation": { "value": "C" },
          |            "period": {
          |              "effective_from_date": "2024-01-01",
          |              "effective_to_date": "2025-01-01"
          |            },
          |            "administration": {
          |              "alteration_date": "2024-02-01",
          |              "alteration_seq_no": "2",
          |              "entry_seq_no": "7",
          |              "collection_authority_ref": "REF-456"
          |            },
          |            "addresses": { "full": "1 Test Street" },
          |            "use": {
          |              "description": "Residential",
          |              "composite_ind": "N",
          |              "part_exempt_ind": "N"
          |            },
          |            "property": {
          |              "id": { "value": "PROP-123" },
          |              "address": { "full": "1 Test Street" },
          |              "workflow": {
          |                "improvement_ind": "No"
          |              }
          |            }
          |          }
          |        }
          |      }
          |    ]
          |  }
          |}
          |""".stripMargin
      )

      val response = json.as[PropertyDetailResponse]

      response.results.records.size shouldBe 1

      val record = response.results.records.head.data
      record.list.id.flatMap(_.value) shouldBe Some("LIST-123")
      record.list.classification.flatMap(_.code) shouldBe Some("A")
      record.list.collection_authority.flatMap(_.code) shouldBe Some("ABC")
      record.list.country.flatMap(_.code) shouldBe Some("E92000001")

      record.list_entry.valuation.flatMap(_.value) shouldBe Some("C")
      record.list_entry.period.flatMap(_.effective_from_date) shouldBe Some("2024-01-01")
      record.list_entry.period.flatMap(_.effective_to_date) shouldBe Some("2025-01-01")
      record.list_entry.administration.flatMap(_.collection_authority_ref) shouldBe Some("REF-456")
      record.list_entry.addresses.flatMap(_.full) shouldBe Some("1 Test Street")
      record.list_entry.use.flatMap(_.description) shouldBe Some("Residential")
      record.list_entry.property.flatMap(_.id).flatMap(_.value) shouldBe Some("PROP-123")
      record.list_entry.property.flatMap(_.address).flatMap(_.full) shouldBe Some("1 Test Street")
      record.list_entry.property.flatMap(_.workflow).flatMap(_.improvement_ind) shouldBe Some("No")
    }
  }

  "PropertyDetailViewModel" should {
    "hold the display values for the property detail page" in {
      val model = PropertyDetailViewModel(
        propertyId = "PROP-123",
        address = "1 Test Street",
        band = "C",
        effectiveFromDate = "1 January 2024",
        localCouncil = "Test Council",
        localCouncilCode = "E07000001",
        councilRefNumber = "ABC123",
        improvementInd = "No",
        mixedUse = "No",
        courtCode = "V",
        country = "E92000001"
      )

      model.propertyId shouldBe "PROP-123"
      model.address shouldBe "1 Test Street"
      model.band shouldBe "C"
      model.effectiveFromDate shouldBe "1 January 2024"
      model.localCouncil shouldBe "Test Council"
      model.localCouncilCode shouldBe "E07000001"
      model.councilRefNumber shouldBe "ABC123"
      model.improvementInd shouldBe "No"
      model.mixedUse shouldBe "No"
      model.courtCode shouldBe "V"
      model.country shouldBe "E92000001"
    }
  }
}