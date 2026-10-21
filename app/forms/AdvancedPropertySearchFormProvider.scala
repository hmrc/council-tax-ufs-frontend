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

class AdvancedPropertySearchFormProvider @Inject() extends Mappings {

  def apply(): Form[AdvancedPropertySearch] = Form(
    mapping(
      "propertyNameOrNumber" -> optional(text())
        .verifying("advancedPropertySearch.error.propertyNameOrNumber.length", _.forall(_.length <= 100))
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),

      "street" -> optional(text())
        .verifying("advancedPropertySearch.error.street.length", _.forall(_.length <= 100))
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),

      "townOrCity" -> optional(text())
        .verifying("advancedPropertySearch.error.townOrCity.length", _.forall(_.length <= 100))
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),

      "postcode" -> text(),

      "councilTaxBand" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),

      "bandStatus" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),

      "localAuthority" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),

      "localAuthorityReferenceNumber" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),

      "courtCode" -> optional(text())
        .transform[String](_.getOrElse(""), s => if (s.trim.isEmpty) None else Some(s)),

      "propertyUse" -> text()
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

