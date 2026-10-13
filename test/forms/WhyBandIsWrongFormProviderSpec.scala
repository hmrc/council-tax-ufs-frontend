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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class WhyBandIsWrongFormProviderSpec extends AnyWordSpec with Matchers {
  private val form = new WhyBandIsWrongFormProvider()()

  "WhyBandIsWrongFormProvider" should {
    "bind an answer up to 1,000 characters" in {
      val answer = "a" * 1000
      form.bind(Map("value" -> answer)).get shouldBe answer
    }

    "reject answers over 1,000 characters" in {
      val bound = form.bind(Map("value" -> ("a" * 1001)))
      bound.errors.map(_.message) should contain("whyBandIsWrong.error.maxLength")
    }

    "require an answer" in {
      form.bind(Map("value" -> " ")).errors.map(_.message) should contain("whyBandIsWrong.error.required")
    }
  }
}