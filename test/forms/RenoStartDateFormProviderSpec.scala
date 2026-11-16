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

import forms.behaviours.DateBehaviours
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages

import java.time.{LocalDate, ZoneOffset}

class RenoStartDateFormProviderSpec extends DateBehaviours {

  private implicit val messages: Messages = stubMessages()
  private val form = new RenoStartDateFormProvider()()

  private val earliest = LocalDate.of(1900, 1, 1)
  private val today    = LocalDate.now(ZoneOffset.UTC)

  ".value" - {

    behave like dateField(form, "value", datesBetween(min = earliest, max = today))

    behave like mandatoryDateField(form, "value", "renoStartDate.error.required.all")

    behave like dateFieldWithMax(form, "value", today, FormError("value", "renoStartDate.error.future"))

    "must reject a date that is not real" in {

      val result = form.bind(Map("value.day" -> "31", "value.month" -> "2", "value.year" -> "2024"))

      result.errors must contain only FormError("value", "renoStartDate.error.invalid")
    }

    "must ask for the missing parts when only some of the date is entered" in {

      val oneMissing = form.bind(Map("value.day" -> "1", "value.month" -> "4", "value.year" -> ""))
      oneMissing.errors.map(_.message) must contain("renoStartDate.error.required")

      val twoMissing = form.bind(Map("value.day" -> "1", "value.month" -> "", "value.year" -> ""))
      twoMissing.errors.map(_.message) must contain("renoStartDate.error.required.two")
    }
  }
}
