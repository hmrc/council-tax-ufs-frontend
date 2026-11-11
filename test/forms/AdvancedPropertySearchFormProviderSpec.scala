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

package forms

import models.AdvancedPropertySearch
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.FormError

class AdvancedPropertySearchFormProviderSpec extends AnyFreeSpec with Matchers with OptionValues {

  private val form = new AdvancedPropertySearchFormProvider()()

  private val validData = Map("postcode" -> "SW1A 1AA")

  "AdvancedPropertySearchFormProvider" - {

    "must bind when only a postcode is given, defaulting optional fields to empty" in {
      form.bind(validData).value mustBe Some(
        AdvancedPropertySearch("", "", "", "SW1A 1AA", "", "", "", "", "", "")
      )
    }

    "must trim and upper-case the postcode" in {
      form.bind(Map("postcode" -> " sw1a 1aa ")).value.map(_.postcode) mustBe Some("SW1A 1AA")
    }

    "must bind all optional fields when provided" in {
      val data = validData ++ Map(
        "propertyNameOrNumber" -> "Elm House",
        "street"               -> "Victoria Road",
        "townOrCity"           -> "London",
        "councilTaxBand"       -> "D",
        "laReferenceNumber"    -> "12345"
      )
      val result = form.bind(data).value.value
      result.propertyNameOrNumber mustBe "Elm House"
      result.street mustBe "Victoria Road"
      result.townOrCity mustBe "London"
      result.councilTaxBand mustBe "D"
      result.localAuthorityReferenceNumber mustBe "12345"
    }

    "must ignore % wildcard characters in free text fields" in {
      val data = validData ++ Map("propertyNameOrNumber" -> "%", "street" -> "Vic%toria", "townOrCity" -> " % ", "laReferenceNumber" -> "%")
      val result = form.bind(data).value.value
      result.propertyNameOrNumber mustBe ""
      result.street mustBe "Victoria"
      result.townOrCity mustBe ""
      result.localAuthorityReferenceNumber mustBe ""
    }

    "must require a postcode" in {
      form.bind(Map.empty[String, String]).errors must contain(
        FormError("postcode", "advancedPropertySearch.error.postcode.required")
      )
      form.bind(Map("postcode" -> "")).errors.map(_.message) must contain(
        "advancedPropertySearch.error.postcode.required"
      )
    }

    "must reject an invalid postcode" in {
      form.bind(Map("postcode" -> "NOTAPOSTCODE")).errors.map(_.message) must contain atLeastOneOf (
        "advancedPropertySearch.error.postcode.invalid",
        "advancedPropertySearch.error.postcode.length"
      )
      form.bind(Map("postcode" -> "12345")).errors.map(_.message) must contain(
        "advancedPropertySearch.error.postcode.invalid"
      )
    }

    "must reject a postcode longer than 10 characters" in {
      form.bind(Map("postcode" -> "SW1A 1AA 123")).errors.map(_.message) must contain(
        "advancedPropertySearch.error.postcode.length"
      )
    }

    Seq(
      ("propertyNameOrNumber", 80, "advancedPropertySearch.error.propertyNameOrNumber.length"),
      ("street", 80, "advancedPropertySearch.error.street.length"),
      ("townOrCity", 80, "advancedPropertySearch.error.townOrCity.length"),
      ("laReferenceNumber", 20, "advancedPropertySearch.error.laReferenceNumber.length")
    ).foreach { case (field, max, errorKey) =>
      s"must accept $field of $max characters" in {
        form.bind(validData + (field -> ("a" * max))).errors mustBe empty
      }

      s"must reject $field longer than $max characters" in {
        form.bind(validData + (field -> ("a" * (max + 1)))).errors.map(_.message) must contain(errorKey)
      }
    }
  }
}
