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
import play.api.data.Form

import javax.inject.Inject

object TextAreaFormProvider {
  val maxCharacters = 5000
}

/** Base for free text question pages. Message keys follow the convention
  * `<prefix>.error.required` (mandatory pages only) and `<prefix>.error.maxLength`.
  * An optional page accepts a blank answer, which is stored as "".
  */
class TextAreaFormProvider @Inject() extends Mappings {
  import TextAreaFormProvider.maxCharacters

  protected def textAreaForm(prefix: String, optional: Boolean = false): Form[String] =
    Form(
      "value" -> textArea(
        requiredKey  = Option.unless(optional)(s"$prefix.error.required"),
        maxLengthKey = s"$prefix.error.maxLength",
        max          = Some(maxCharacters)
      )
    )
}
