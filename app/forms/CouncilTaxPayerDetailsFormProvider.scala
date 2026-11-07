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
import models.CouncilTaxPayerDetails
import play.api.data.{Form, FormError}
import play.api.data.format.Formatter
import play.api.data.Forms.{mapping, of}

import javax.inject.Inject

class CouncilTaxPayerDetailsFormProvider @Inject() extends Mappings {

  private val emailRegex = """^[^@\s]+@[^@\s]+\.[^@\s]+$""".r
  private val nameRegex = """^[A-Za-z\s\-']+$""".r

  private def nameFormatter(requiredErrorKey: String, invalidErrorKey: String, lengthErrorKey: String): Formatter[String] =
    new Formatter[String] {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
        val trimmed = data.getOrElse(key, "").trim
        if (trimmed.isEmpty) Left(Seq(FormError(key, requiredErrorKey)))
        else if (trimmed.length > 100) Left(Seq(FormError(key, lengthErrorKey)))
        else if (!nameRegex.matches(trimmed)) Left(Seq(FormError(key, invalidErrorKey)))
        else Right(trimmed)
      }
      override def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
    }

  private val emailFormatter: Formatter[String] =
    new Formatter[String] {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
        val trimmed = data.getOrElse(key, "").trim
        if (trimmed.isEmpty) Left(Seq(FormError(key, "councilTaxPayerDetails.error.email.required")))
        else if (!emailRegex.matches(trimmed)) Left(Seq(FormError(key, "councilTaxPayerDetails.error.email.invalid")))
        else Right(trimmed)
      }
      override def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
    }

  def apply(): Form[CouncilTaxPayerDetails] = Form(
    mapping(
      "firstName" -> of(nameFormatter(
        "councilTaxPayerDetails.error.firstName.required",
        "councilTaxPayerDetails.error.firstName.invalid",
        "councilTaxPayerDetails.error.firstName.length"
      )),
      "lastName" -> of(nameFormatter(
        "councilTaxPayerDetails.error.lastName.required",
        "councilTaxPayerDetails.error.lastName.invalid",
        "councilTaxPayerDetails.error.lastName.length"
      )),
      "email" -> of(emailFormatter)
    )(CouncilTaxPayerDetails.apply)(x => Some((x.firstName, x.lastName, x.email)))
  )
}
