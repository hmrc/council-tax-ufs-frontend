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

class CouncilTaxPayerDetailsFormProviderSpec extends StringFieldBehaviours {

  val form = new CouncilTaxPayerDetailsFormProvider()()

  // names only allow letters, spaces, hyphens and apostrophes
  private val validNames: Gen[String] =
    Gen.choose(1, 100).flatMap(n => Gen.listOfN(n, Gen.alphaChar).map(_.mkString))

  private val tooLongNames: Gen[String] =
    Gen.choose(101, 200).flatMap(n => Gen.listOfN(n, Gen.alphaChar).map(_.mkString))

  ".firstName" - {

    val fieldName = "firstName"
    val requiredKey = "councilTaxPayerDetails.error.firstName.required"
    val lengthKey = "councilTaxPayerDetails.error.firstName.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validNames
    )

    s"not bind strings longer than $maxLength characters" in {
      forAll(tooLongNames -> "longString") { (string: String) =>
        form.bind(Map(fieldName -> string)).apply(fieldName).errors must contain only FormError(fieldName, lengthKey)
      }
    }

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".lastName" - {

    val fieldName = "lastName"
    val requiredKey = "councilTaxPayerDetails.error.lastName.required"
    val lengthKey = "councilTaxPayerDetails.error.lastName.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validNames
    )

    s"not bind strings longer than $maxLength characters" in {
      forAll(tooLongNames -> "longString") { (string: String) =>
        form.bind(Map(fieldName -> string)).apply(fieldName).errors must contain only FormError(fieldName, lengthKey)
      }
    }

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
