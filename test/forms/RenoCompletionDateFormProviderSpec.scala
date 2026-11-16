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

class RenoCompletionDateFormProviderSpec extends DateBehaviours {

  private implicit val messages: Messages = stubMessages()

  private val today = LocalDate.now(ZoneOffset.UTC)
  private val start = LocalDate.of(2025, 4, 27)
  private val form  = new RenoCompletionDateFormProvider()(Some(start))

  private def bind(date: LocalDate) =
    form.bind(Map(
      "value.day"   -> date.getDayOfMonth.toString,
      "value.month" -> date.getMonthValue.toString,
      "value.year"  -> date.getYear.toString
    ))

  ".value" - {

    behave like dateField(form, "value", datesBetween(min = start.plusDays(1), max = today))

    behave like mandatoryDateField(form, "value", "renoCompletionDate.error.required.all")

    "must reject a date on or before the start date" in {
      bind(start).errors.map(_.message) must contain only "renoCompletionDate.error.beforeStart"
      bind(start.minusDays(1)).errors.map(_.message) must contain only "renoCompletionDate.error.beforeStart"
    }

    "must accept the day after the start date" in {
      bind(start.plusDays(1)).errors mustBe empty
    }

    "must reject a date in the future" in {
      bind(today.plusDays(1)).errors.map(_.message) must contain only "renoCompletionDate.error.future"
    }

    "must reject a date that is not real" in {
      form.bind(Map("value.day" -> "31", "value.month" -> "2", "value.year" -> "2024")).errors must contain only
        FormError("value", "renoCompletionDate.error.invalid")
    }

    "must only check the future when the start date is not known" in {
      new RenoCompletionDateFormProvider()(None).bind(Map("value.day" -> "1", "value.month" -> "1", "value.year" -> "1990")).errors mustBe empty
    }
  }
}
