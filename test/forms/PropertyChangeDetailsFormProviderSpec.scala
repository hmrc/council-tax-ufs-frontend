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

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class PropertyChangeDetailsFormProviderSpec extends StringFieldBehaviours {

  Seq("propertyMergedDetails", "propertySplitDetails").foreach { prefix =>
    s"$prefix .value" - {
      val form = new PropertyChangeDetailsFormProvider()(prefix)

      behave like mandatoryField(form, "value", FormError("value", s"$prefix.error.required"))

      "must reject text over the maximum length" in {
        val result = form.bind(Map("value" -> "a" * (PropertyChangeDetailsFormProvider.maxCharacters + 1)))
        result.errors.map(_.message) must contain(s"$prefix.error.maxLength")
      }

      "must accept text at the maximum length" in {
        val result = form.bind(Map("value" -> "a" * PropertyChangeDetailsFormProvider.maxCharacters))
        result.errors mustBe empty
      }
    }
  }
}
