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

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.AdvancedPropertySearch

import play.api.data.Forms.optional

import forms.mappings.Mappings
import models.AdvancedPropertySearch
import play.api.data.Form
import play.api.data.Forms.optional
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class AdvancedPropertySearchFormProvider @Inject() extends Mappings {

  private val postcodePattern = "^[A-Z]{1,2}[0-9][A-Z0-9]?( ?[0-9][A-Z]{2})?$".r
  private val maxLength = 10

  private val validPostcode: Constraint[String] =
    Constraint("constraints.postcode") { value =>
      if (value.length > maxLength)
        Invalid("advancedPropertySearch.error.postcode.length", maxLength)
      else if (!postcodePattern.matches(value))
        Invalid("advancedPropertySearch.error.postcode.invalid")
      else
        Valid
    }

  // '%' is a wildcard in the downstream search and causes it to fail, so it is ignored
  private def stripWildcards(value: String): String = value.replace("%", "").trim

  private def freeText(errorKey: String, max: Int) =
    optional(text())
      .transform[Option[String]](_.map(stripWildcards).filter(_.nonEmpty), identity)
      .verifying(errorKey, _.forall(_.length <= max))
      .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s))

  def apply(): Form[AdvancedPropertySearch] = Form(
    mapping(
      "propertyNameOrNumber" -> freeText("advancedPropertySearch.error.propertyNameOrNumber.length", 80),

      "street" -> freeText("advancedPropertySearch.error.street.length", 80),

      "townOrCity" -> freeText("advancedPropertySearch.error.townOrCity.length", 80),

      "postcode" -> text("advancedPropertySearch.error.postcode.required")
        .transform[String](_.trim.toUpperCase, identity)
        .verifying(validPostcode),

      "councilTaxBand" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),
      "bandStatus" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),
      "localAuthority" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),
      "laReferenceNumber" -> freeText("advancedPropertySearch.error.laReferenceNumber.length", 20),
      "courtCode" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),
      "propertyUse" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s))

    )(AdvancedPropertySearch.apply)(x => Some((
      x.propertyNameOrNumber,
      x.street,
      x.townOrCity,
      x.postcode,
      x.councilTaxBand,
      x.bandStatus,
      x.localAuthority,
      x.localAuthorityReferenceNumber,
      x.courtCode,
      x.propertyUse
    )))
  )
}

