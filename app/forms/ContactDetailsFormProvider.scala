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

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}
import models.ContactDetails
import play.api.data.Forms.{optional, set}


class ContactDetailsFormProvider @Inject() extends Mappings {

  private val emailRegex = """^[^@\s]+@[^@\s]+\.[^@\s]+$""".r
  private val nameRegex = """^[A-Za-z\s\-']+$""".r
  private val phoneAllowedCharsRegex = """^[0-9\s\-\+\(\)]+$""".r
  private val postcodePattern = "^[A-Z]{1,2}[0-9][A-Z0-9]?( ?[0-9][A-Z]{2})?$".r

  private def selectedMethods(data: Map[String, String]): Set[String] =
    data.collect { case (k, v) if k.matches("""value\[\d+\]""") => v }.toSet

  // --- Name formatter: required, letters only, max 100, trimmed ---
  private def nameFormatter(requiredErrorKey: String, invalidErrorKey: String, lengthErrorKey: String): Formatter[String] =
    new Formatter[String] {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
        val raw = data.getOrElse(key, "")
        val trimmed = raw.trim
        if (trimmed.isEmpty) Left(Seq(FormError(key, requiredErrorKey)))
        else if (trimmed.length > 100) Left(Seq(FormError(key, lengthErrorKey)))
        else if (!nameRegex.matches(trimmed)) Left(Seq(FormError(key, invalidErrorKey)))
        else Right(trimmed)
      }
      override def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
    }

  // --- Email: conditional, format-checked, trimmed ---
  private val conditionalEmailFormatter: Formatter[Option[String]] =
    new Formatter[Option[String]] {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] = {
        val methods = selectedMethods(data)
        val value   = data.get(key).map(_.trim).filter(_.nonEmpty)
        if (methods.contains("email") && value.isEmpty)
          Left(Seq(FormError(key, "contactDetails.error.email.required")))
        else if (value.exists(v => !emailRegex.matches(v)))
          Left(Seq(FormError(key, "contactDetails.error.email.invalid")))
        else Right(value)
      }
      override def unbind(key: String, value: Option[String]): Map[String, String] =
        Map(key -> value.getOrElse(""))
    }

  // --- Phone: conditional, digits + common formatting chars, 10-15 digits ---
  private val conditionalPhoneFormatter: Formatter[Option[String]] =
    new Formatter[Option[String]] {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] = {
        val methods = selectedMethods(data)
        val value   = data.get(key).map(_.trim).filter(_.nonEmpty)

        if (methods.contains("phone") && value.isEmpty)
          Left(Seq(FormError(key, "contactDetails.error.phoneNumber.required")))
        else value match {
          case Some(v) if !phoneAllowedCharsRegex.matches(v) =>
            Left(Seq(FormError(key, "contactDetails.error.phoneNumber.invalid")))
          case Some(v) =>
            val digitsOnly = v.replaceAll("""[^0-9]""", "")
            if (digitsOnly.length < 10) Left(Seq(FormError(key, "contactDetails.error.phoneNumber.tooShort")))
            else if (digitsOnly.length > 15) Left(Seq(FormError(key, "contactDetails.error.phoneNumber.tooLong")))
            else Right(Some(v))
          case None => Right(None)
        }
      }
      override def unbind(key: String, value: Option[String]): Map[String, String] =
        Map(key -> value.getOrElse(""))
    }

  // --- Conditional text with max length (address line 1, town, postcode) ---
  private def conditionalText(method: String, requiredErrorKey: String, lengthErrorKey: String, maxLength: Int = 80): Formatter[Option[String]] =
    new Formatter[Option[String]] {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] = {
        val methods = selectedMethods(data)
        val value   = data.get(key).map(_.trim).filter(_.nonEmpty)

        if (methods.contains(method) && value.isEmpty) Left(Seq(FormError(key, requiredErrorKey)))
        else if (value.exists(_.length > maxLength)) Left(Seq(FormError(key, lengthErrorKey)))
        else Right(value)
      }
      override def unbind(key: String, value: Option[String]): Map[String, String] =
        Map(key -> value.getOrElse(""))
    }

  // --- Always-optional text with just a max length (address line 2, county) ---
  private def optionalTextWithLength(lengthErrorKey: String, maxLength: Int = 80): Formatter[Option[String]] =
    new Formatter[Option[String]] {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] = {
        val value = data.get(key).map(_.trim).filter(_.nonEmpty)
        if (value.exists(_.length > maxLength)) Left(Seq(FormError(key, lengthErrorKey)))
        else Right(value)
      }
      override def unbind(key: String, value: Option[String]): Map[String, String] =
        Map(key -> value.getOrElse(""))
    }

  // --- Postcode: conditional, min length, format-checked ---
  private val conditionalPostcodeFormatter: Formatter[Option[String]] =
    new Formatter[Option[String]] {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] = {
        val methods = selectedMethods(data)
        val raw     = data.get(key).map(_.trim).filter(_.nonEmpty)
        val normalised = raw.map(_.toUpperCase.replaceAll("""\s+""", " "))

        if (methods.contains("post") && raw.isEmpty)
          Left(Seq(FormError(key, "contactDetails.error.postcode.required")))
        else normalised match {
          case Some(v) if v.replaceAll("""\s""", "").length < 5 =>
            Left(Seq(FormError(key, "contactDetails.error.postcode.tooShort")))
          case Some(v) if !postcodePattern.matches(v.replaceAll("""\s""", "")) =>
            Left(Seq(FormError(key, "contactDetails.error.postcode.invalid")))
          case other => Right(other)
        }
      }
      override def unbind(key: String, value: Option[String]): Map[String, String] =
        Map(key -> value.getOrElse(""))
    }

  def apply(): Form[ContactDetails] = Form(
    mapping(
      "firstName" -> of(nameFormatter(
        "contactDetails.error.firstName.required",
        "contactDetails.error.firstName.invalid",
        "contactDetails.error.firstName.length"
      )),
      "lastName" -> of(nameFormatter(
        "contactDetails.error.lastName.required",
        "contactDetails.error.lastName.invalid",
        "contactDetails.error.lastName.length"
      )),

      "email"       -> of(conditionalEmailFormatter),
      "phoneNumber" -> of(conditionalPhoneFormatter),

      "addressLine1" -> of(conditionalText("post", "contactDetails.error.addressLine1.required", "contactDetails.error.addressLine1.length")),
      "addressLine2" -> of(optionalTextWithLength("contactDetails.error.addressLine2.length")),
      "townOrCity"   -> of(conditionalText("post", "contactDetails.error.townOrCity.required", "contactDetails.error.townOrCity.length")),
      "county"       -> of(optionalTextWithLength("contactDetails.error.county.length")),
      "postcode"     -> of(conditionalPostcodeFormatter),

      "value" -> set(text())
        .verifying("contactDetails.error.value.required", _.nonEmpty)
    )(ContactDetails.apply)(x => Some((
      x.firstName, x.lastName, x.email, x.phoneNumber,
      x.addressLine1, x.addressLine2, x.townOrCity, x.county, x.postcode, x.value
    )))
  )
}
