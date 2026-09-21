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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class PostcodeFormatterSpec extends AnyFreeSpec with Matchers {

  "PostcodeFormatter.format" - {

    "must add a space before the last 3 characters when given an unspaced full postcode" in {
      PostcodeFormatter.format("CF141AA") mustBe "CF14 1AA"
    }

    "must leave an already-spaced postcode correctly formatted" in {
      PostcodeFormatter.format("CF14 1AA") mustBe "CF14 1AA"
    }

    "must uppercase a lowercase postcode" in {
      PostcodeFormatter.format("cf141aa") mustBe "CF14 1AA"
    }

    "must trim leading and trailing whitespace" in {
      PostcodeFormatter.format("  CF141AA  ") mustBe "CF14 1AA"
    }

    "must collapse multiple internal spaces before reformatting" in {
      PostcodeFormatter.format("CF14   1AA") mustBe "CF14 1AA"
    }

    "must return a short partial postcode unchanged (no space inserted)" in {
      PostcodeFormatter.format("BR3") mustBe "BR3"
    }

    "must return a postcode of exactly 3 characters unchanged" in {
      PostcodeFormatter.format("CF1") mustBe "CF1"
    }

    "must insert a space for a postcode of exactly 4 characters" in {
      PostcodeFormatter.format("BR34") mustBe "B R34"
    }

    "must handle an empty string without error" in {
      PostcodeFormatter.format("") mustBe ""
    }

    "must handle a string of only whitespace" in {
      PostcodeFormatter.format("   ") mustBe ""
    }

    "must handle the shortest full postcode format correctly" in {
      PostcodeFormatter.format("B11AA") mustBe "B1 1AA"
    }
  }
}