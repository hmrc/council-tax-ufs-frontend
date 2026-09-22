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
import org.scalacheck.Gen
import play.api.data.FormError

class PostcodeSearchFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "postcodeSearch.error.required"
  val lengthKey = "postcodeSearch.error.length"
  val maxLength = 10

  val form = new PostcodeSearchFormProvider()()

  // Generates strings that match PostcodeSearchFormProvider's postcodePattern:
  // ^[A-Z]{1,2}[0-9][A-Z0-9]?( ?[0-9][A-Z]{2})?$
  val validPostcodeGen: Gen[String] = {
    val outward: Gen[String] = for {
      letters <- Gen.choose(1, 2).flatMap(n => Gen.listOfN(n, Gen.alphaUpperChar))
      digit   <- Gen.numChar
      suffix  <- Gen.option(Gen.oneOf(Gen.numChar, Gen.alphaUpperChar))
    } yield letters.mkString + digit + suffix.map(_.toString).getOrElse("")

    val inward: Gen[String] = for {
      digit   <- Gen.numChar
      letters <- Gen.listOfN(2, Gen.alphaUpperChar)
    } yield digit.toString + letters.mkString

    for {
      out       <- outward
      withSpace <- Gen.oneOf(true, false)
      in        <- Gen.option(inward)
    } yield in match {
      case Some(i) => out + (if (withSpace) " " else "") + i
      case None    => out
    }
  }

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validPostcodeGen
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}