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
      PropertySearchAuditEvent.standard("session-1", "/council-tax-ufs/postcode-search") mustBe
        PropertySearchAuditEvent("session-1", "standard", "/council-tax-ufs/postcode-search", Map.empty, Seq("postcode"))
    }
  }

  "advanced" - {

    val event = PropertySearchAuditEvent.advanced("Int-1", "/council-tax-ufs/advanced-property-search", advancedSearch)

    "must record the non-PII criteria the user entered and skip blank ones" in {
      event.searchType mustBe "advanced"
      event.searchCriteria mustBe Map(
        "councilTaxBand" -> "E",
        "localAuthority" -> "W07000064",
        "courtCode"      -> "D",
        "propertyUse"    -> "Domestic"
      )
    }

    "must list the PII fields entered by name only, never their values" in {
      event.redactedFields mustBe Seq("propertyNameOrNumber", "street", "postcode")

      val json = Json.stringify(Json.toJson(event))
      json must not include "M4 4AY"
      json must not include "Cheetham"
    }
  }

  "writes" - {

    "must produce the agreed detail field names" in {
      Json.toJson(PropertySearchAuditEvent.standard("session-1", "/page")) mustBe Json.obj(
        "userId"         -> "session-1",
        "searchType"     -> "standard",
        "searchPage"     -> "/page",
        "searchCriteria" -> Json.obj(),
        "redactedFields" -> Json.arr("postcode")
      )
    }
  }
}