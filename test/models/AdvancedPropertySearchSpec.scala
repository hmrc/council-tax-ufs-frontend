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
import play.api.libs.json.{JsSuccess, Json}

class AdvancedPropertySearchSpec extends AnyFreeSpec with Matchers {

  private val model = AdvancedPropertySearch("1", "High Street", "Cardiff", "CF14 1AA", "D", "Current", "6815", "REF1", "R", "C")

  "AdvancedPropertySearch" - {

    "must serialise to JSON using its field names" in {
      Json.toJson(model) mustBe Json.obj(
        "propertyNameOrNumber"          -> "1",
        "street"                        -> "High Street",
        "townOrCity"                    -> "Cardiff",
        "postcode"                      -> "CF14 1AA",
        "councilTaxBand"                -> "D",
        "bandStatus"                    -> "Current",
        "localAuthority"                -> "6815",
        "localAuthorityReferenceNumber" -> "REF1",
        "courtCode"                     -> "R",
        "propertyUse"                   -> "C"
      )
    }

    "must round trip through JSON" in {
      Json.toJson(model).validate[AdvancedPropertySearch] mustBe JsSuccess(model)
    }

    "must fail to read JSON with missing fields" in {
      Json.obj("postcode" -> "CF14 1AA").validate[AdvancedPropertySearch].isError mustBe true
    }
  }
}
