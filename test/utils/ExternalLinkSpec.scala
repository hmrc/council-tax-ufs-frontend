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

package utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ExternalLinkSpec extends AnyWordSpec with Matchers {

  "ExternalLink.absolute" should {
    "add https to a pointer without a scheme" in {
      ExternalLink.absolute("www.manchester.gov.uk") shouldBe Some("https://www.manchester.gov.uk")
    }

    "keep http and https links as they are" in {
      ExternalLink.absolute("https://www.cardiff.gov.uk/a?b=1") shouldBe Some("https://www.cardiff.gov.uk/a?b=1")
      ExternalLink.absolute("http://example.gov.uk") shouldBe Some("http://example.gov.uk")
    }

    "trim whitespace and handle a scheme-relative pointer" in {
      ExternalLink.absolute("  www.leeds.gov.uk ") shouldBe Some("https://www.leeds.gov.uk")
      ExternalLink.absolute("//www.leeds.gov.uk") shouldBe Some("https://www.leeds.gov.uk")
    }

    "reject blank pointers and any scheme other than http or https" in {
      ExternalLink.absolute("   ") shouldBe None
      ExternalLink.absolute("javascript:alert(1)") shouldBe None
      ExternalLink.absolute("mailto:a@b.gov.uk") shouldBe None
    }
  }
}
