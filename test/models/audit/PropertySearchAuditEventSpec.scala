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

package models.audit

import models.AdvancedPropertySearch
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

class PropertySearchAuditEventSpec extends AnyFreeSpec with Matchers {

  private val advancedSearch = AdvancedPropertySearch(
    propertyNameOrNumber          = "23",
    street                        = "Cheetham",
    townOrCity                    = "",
    postcode                      = "M4 4AY",
    councilTaxBand                = "E",
    bandStatus                    = " ",
    localAuthority                = "W07000064",
    localAuthorityReferenceNumber = "",
    courtCode                     = "D",
    propertyUse                   = "Domestic"
  )

  "standard" - {

    "must record a standard search with the postcode redacted" in {
      PropertySearchAuditEvent.standard("session-1", "/council-tax-ufs/postcode-search", "NG9 3NB") mustBe
        PropertySearchAuditEvent("session-1", "standard", "/council-tax-ufs/postcode-search", Map("postcodePrefix" -> "NG9"), Seq("postcode"))
    }

    "must never record the full postcode" in {
      Json.stringify(Json.toJson(PropertySearchAuditEvent.standard("s", "/p", "NG9 3NB"))) must not include "3NB"
    }
  }

  "advanced" - {

    val event = PropertySearchAuditEvent.advanced("Int-1", "/council-tax-ufs/advanced-property-search", advancedSearch)

    "must record every filter, using any for the ones left on their default" in {
      event.searchType mustBe "advanced"
      event.searchCriteria mustBe Map(
        "councilTaxBand" -> "E",
        "bandStatus"     -> "any",
        "localAuthority" -> "W07000064",
        "courtCode"      -> "D",
        "propertyUse"    -> "Domestic",
        "postcodePrefix" -> "M44"
      )
    }

    "must record the property use as any when the filter is left on its default" in {
      PropertySearchAuditEvent.advanced("Int-1", "/p", advancedSearch.copy(propertyUse = "")).searchCriteria("propertyUse") mustBe "any"
    }

    "must not record the second half of the postcode" in {
      Json.stringify(Json.toJson(event)) must not include "4AY"
    }

    "must list the PII fields entered by name only, never their values" in {
      event.redactedFields mustBe Seq("propertyNameOrNumber", "street", "postcode")

      val json = Json.stringify(Json.toJson(event))
      json must not include "M4 4AY"
      json must not include "Cheetham"
    }
  }

  "postcodePrefix" - {

    "must take the first three characters of the postcode with spaces removed" in {
      PropertySearchAuditEvent.postcodePrefix("NG9 3NB")   mustBe Some("NG9")
      PropertySearchAuditEvent.postcodePrefix("ng93nb")    mustBe Some("NG9")
      PropertySearchAuditEvent.postcodePrefix(" M4 4AY ")  mustBe Some("M44")
      PropertySearchAuditEvent.postcodePrefix("SW1A 1AA")  mustBe Some("SW1")
      PropertySearchAuditEvent.postcodePrefix("   ")       mustBe None
    }
  }

  "writes" - {

    "must produce the agreed detail field names" in {
      Json.toJson(PropertySearchAuditEvent.standard("session-1", "/page", "NG9 3NB")) mustBe Json.obj(
        "userId"         -> "session-1",
        "searchType"     -> "standard",
        "searchPage"     -> "/page",
        "searchCriteria" -> Json.obj("postcodePrefix" -> "NG9"),
        "redactedFields" -> Json.arr("postcode")
      )
    }
  }
}