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

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.data.validation.{Constraint => PlayConstraint, Invalid => PlayInvalid, Valid => PlayValid}

class PostcodeSearchFormProvider @Inject() extends Mappings {

  private val postcodePattern = "^[A-Z]{1,2}[0-9][A-Z0-9]?( ?[0-9][A-Z]{2})?$".r
  private val maxLength = 10

  private val validPostcode: PlayConstraint[String] =
    PlayConstraint("constraints.postcode") { value =>
      if (value.length > maxLength)
        PlayInvalid("postcodeSearch.error.length", maxLength)
      else if (!postcodePattern.matches(value))
        PlayInvalid("postcodeSearch.error.invalid")
      else
        PlayValid
    }

  def apply(): Form[String] =
    Form(
      "value" -> text("postcodeSearch.error.required")
        .transform[String](_.trim.toUpperCase, identity)
        .verifying(validPostcode)
    )
}
