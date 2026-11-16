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

class WhatHasChangedFormProviderSpec extends SpecBase {

  private val form = new WhatHasChangedFormProvider()()

  ".value" - {

    "must bind valid text" in {
      form.bind(Map("value" -> "The property was split in two")).value.value mustBe "The property was split in two"
    }

    "must bind text of exactly 5000 characters" in {
      form.bind(Map("value" -> "a" * 5000)).errors mustBe empty
    }

    "must not bind blank or missing text" in {
      form.bind(Map("value" -> "")).errors mustBe Seq(FormError("value", "whatHasChanged.error.required"))
      form.bind(Map("value" -> "   ")).errors mustBe Seq(FormError("value", "whatHasChanged.error.required"))
      form.bind(Map.empty[String, String]).errors mustBe Seq(FormError("value", "whatHasChanged.error.required"))
    }

    "must not bind text longer than 5000 characters" in {
      form.bind(Map("value" -> "a" * 5001)).errors.map(_.message) mustBe Seq("whatHasChanged.error.maxLength")
    }
  }
}
