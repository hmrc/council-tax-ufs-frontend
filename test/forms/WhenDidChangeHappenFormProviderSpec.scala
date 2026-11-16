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

import base.SpecBase
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages

import java.time.{LocalDate, ZoneOffset}

class WhenDidChangeHappenFormProviderSpec extends SpecBase {

  private implicit val messages: Messages = stubMessages()
  private val form  = new WhenDidChangeHappenFormProvider()()
  private val today = LocalDate.now(ZoneOffset.UTC)

  private def bind(date: LocalDate) =
    form.bind(Map(
      "value.day"   -> date.getDayOfMonth.toString,
      "value.month" -> date.getMonthValue.toString,
      "value.year"  -> date.getYear.toString
    ))

  ".value" - {

    "must bind today, a past date and the earliest allowed date" in {
      bind(today).value.value mustBe today
      bind(LocalDate.of(2025, 4, 27)).errors mustBe empty
      bind(LocalDate.of(1993, 4, 1)).errors mustBe empty
    }

    "must not bind a date before 1 April 1993" in {
      bind(LocalDate.of(1993, 3, 31)).errors mustBe Seq(FormError("value", "whenDidChangeHappen.error.tooEarly"))
      bind(LocalDate.of(1990, 1, 1)).errors mustBe Seq(FormError("value", "whenDidChangeHappen.error.tooEarly"))
    }

    "must not bind a future date" in {
      bind(today.plusDays(1)).errors mustBe Seq(FormError("value", "whenDidChangeHappen.error.future"))
    }

    "must not bind blank dates, whether one field or all fields are missing" in {
      form.bind(Map.empty[String, String]).errors.map(_.message) mustBe Seq("whenDidChangeHappen.error.required.all")
      form.bind(Map("value.day" -> "", "value.month" -> "", "value.year" -> "")).errors.map(_.message) mustBe
        Seq("whenDidChangeHappen.error.required.all")
      form.bind(Map("value.day" -> "1", "value.month" -> "4", "value.year" -> "")).errors.map(_.message) must contain
        ("whenDidChangeHappen.error.required")
    }

    "must not bind a date that does not exist" in {
      form.bind(Map("value.day" -> "31", "value.month" -> "2", "value.year" -> "2020")).errors.map(_.message) mustBe
        Seq("whenDidChangeHappen.error.invalid")
    }
  }
}
