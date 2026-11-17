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

import play.api.data.Form
import play.api.i18n.Messages

import java.time.LocalDate
import javax.inject.Inject

object UninhabitableDateFormProvider {
  // Council Tax banding started on 1 April 1993, so nothing earlier can be reviewed
  val earliestDate: LocalDate = LocalDate.of(1993, 4, 1)
}

class UninhabitableDateFormProvider @Inject() extends DateFormProvider {

  def apply()(implicit messages: Messages): Form[LocalDate] =
    boundedDateForm(
      prefix      = "uninhabitableDate",
      earliest    = Some(UninhabitableDateFormProvider.earliestDate),
      minErrorKey = Some("uninhabitableDate.error.minimum"),
      maxErrorKey = Some("uninhabitableDate.error.future")
    )
}
