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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages

import java.time.{LocalDate, ZoneOffset}

class PropertyChangeDateFormProviderSpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = stubMessages()
  private val today = LocalDate.now(ZoneOffset.UTC)

  Seq("propertyMergedDate", "propertySplitDate").foreach { prefix =>
    val form = new PropertyChangeDateFormProvider()(prefix)

    def bind(date: LocalDate) =
      form.bind(Map("value.day" -> date.getDayOfMonth.toString, "value.month" -> date.getMonthValue.toString, "value.year" -> date.getYear.toString))

    s"$prefix form" - {

      "must accept today" in {
        bind(today).value mustBe Some(today)
      }

      "must accept a date many years ago" in {
        bind(today.minusYears(20)).value mustBe Some(today.minusYears(20))
      }

      "must reject a future date" in {
        bind(today.plusDays(1)).errors.map(_.message) must contain(s"$prefix.error.future")
      }

      "must reject a missing date" in {
        form.bind(Map.empty[String, String]).errors.map(_.message) must contain(s"$prefix.error.required.all")
      }
    }
  }
}
