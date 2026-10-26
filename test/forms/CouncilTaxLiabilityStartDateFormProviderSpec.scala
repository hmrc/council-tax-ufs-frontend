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

import java.time.{LocalDate, ZoneOffset}
import forms.behaviours.DateBehaviours
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages

class CouncilTaxLiabilityStartDateFormProviderSpec extends DateBehaviours {

  private implicit val messages: Messages = stubMessages()
  private val form = new CouncilTaxLiabilityStartDateFormProvider()()
  private val today = LocalDate.now(ZoneOffset.UTC)
  private val earliestAllowedDate = today.minusMonths(6)

  ".value" - {

    val validData = datesBetween(
      min = earliestAllowedDate,
      max = today
    )

    behave like dateField(form, "value", validData)
    behave like dateFieldWithMin(
      form,
      "value",
      earliestAllowedDate,
      play.api.data.FormError("value", "councilTaxLiabilityStartDate.error.range")
    )
    behave like dateFieldWithMax(
      form,
      "value",
      today,
      play.api.data.FormError("value", "councilTaxLiabilityStartDate.error.range")
    )

    behave like mandatoryDateField(form, "value", "councilTaxLiabilityStartDate.error.required.all")
  }
}
