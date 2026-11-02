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

package views

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class BandCountrySpec extends AnyFreeSpec with Matchers {

  "BandCountry" - {

    "must list England and Wales" in {
      BandCountry.all mustBe Seq(BandCountry.England, BandCountry.Wales)
    }

    "England must have bands A to H" in {
      BandCountry.England.key mustBe "england"
      BandCountry.England.bands mustBe ('A' to 'H').map(_.toString)
    }

    "Wales must have bands A to I" in {
      BandCountry.Wales.key mustBe "wales"
      BandCountry.Wales.bands mustBe ('A' to 'I').map(_.toString)
    }

    "fromCode must match names and ONS codes ignoring case and whitespace" in {
      BandCountry.fromCode("Wales") mustBe BandCountry.Wales
      BandCountry.fromCode(" w92000004 ") mustBe BandCountry.Wales
      BandCountry.fromCode("ENGLAND") mustBe BandCountry.England
      BandCountry.fromCode("E92000001") mustBe BandCountry.England
    }

    "fromCode must default to England for unrecognised values" in {
      BandCountry.fromCode("Scotland") mustBe BandCountry.England
      BandCountry.fromCode("") mustBe BandCountry.England
    }
  }
}
