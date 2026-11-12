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
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages

import java.time.{LocalDate, ZoneOffset}
import javax.inject.Inject

/** Base for single-date question pages. Message keys follow the convention
  * `<prefix>.error.required.all`, `.required.two`, `.required`, `.invalid` and `.range`;
  * the minimum and maximum errors can be overridden when a page words them differently.
  */
class DateFormProvider @Inject() extends Mappings {

  protected def dateForm(
    prefix:      String,
    monthsBack:  Int,
    minErrorKey: Option[String] = None,
    maxErrorKey: Option[String] = None
  )(implicit messages: Messages): Form[LocalDate] =
    boundedDateForm(
      prefix   = prefix,
      earliest = Some(LocalDate.now(ZoneOffset.UTC).minusMonths(monthsBack)),
      minErrorKey = minErrorKey,
      maxErrorKey = maxErrorKey
    )

  /** Dates up to and including today. `earliest = None` leaves the date open ended at the
    * start, for pages that only ask the answer not to be in the future.
    */
  protected def boundedDateForm(
    prefix:      String,
    earliest:    Option[LocalDate],
    minErrorKey: Option[String] = None,
    maxErrorKey: Option[String] = None
  )(implicit messages: Messages): Form[LocalDate] = {
    val today = LocalDate.now(ZoneOffset.UTC)

    val date: Mapping[LocalDate] = localDate(
      invalidKey     = s"$prefix.error.invalid",
      allRequiredKey = s"$prefix.error.required.all",
      twoRequiredKey = s"$prefix.error.required.two",
      requiredKey    = s"$prefix.error.required"
    ).verifying(maxDate(today, maxErrorKey.getOrElse(s"$prefix.error.range")))

    Form(
      "value" -> earliest.fold(date) { min =>
        date.verifying(minDate(min, minErrorKey.getOrElse(s"$prefix.error.range")))
      }
    )
  }
}
