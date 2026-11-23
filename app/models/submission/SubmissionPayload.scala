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

package models.submission

import play.api.libs.json._

/** One answered question, identified by its JSIP code (J01, S01, P02, I04 ...). */
final case class QuestionAnswer(code: String, question: String, answer: String) {

  // the receiving side expects each question and answer as a JSON document held in a string
  def text: String = Json.stringify(Json.obj("Question" -> question, "Answer" -> answer))
}

final case class SubmissionFile(mimeType: String, label: String, pointer: Option[String])

final case class Communications(
  forenames: String,
  surname: String,
  email: Option[String],
  telephoneNumber: Option[String],
  councilTaxPayerFirstName: Option[String],
  councilTaxPayerLastName: Option[String],
  councilTaxPayerEmail: Option[String],
  councilTaxPayerContactPreference: Option[String],
  councilTaxPayerContactAddress: Option[String]
)

final case class Premise(premise: QuestionAnswer, statements: Seq[QuestionAnswer], information: Seq[QuestionAnswer])

final case class SubmissionPayload(
  submissionId: String,
  submissionDate: String,
  files: Seq[SubmissionFile],
  communications: Communications,
  premises: Seq[Premise],
  propositions: Seq[QuestionAnswer]
) {
  def codes: Seq[String] =
    premises.flatMap(p => p.premise.code +: (p.statements ++ p.information).map(_.code)) ++ propositions.map(_.code)
}

object SubmissionPayload {

  // capacity and relationship are fixed for now; they may become dynamic or be dropped
  private val customerCapacity: JsObject = Json.obj("code" -> "TXP", "meaning" -> "Council Tax Payer")

  private val relationship: JsObject = Json.obj(
    "manifestation" -> Json.obj(
      "artifact_code" -> "CTB",
      "protodata_ptr" -> "https://www.sandwell.gov.uk/council-tax/council-tax-bill-explained"
    ),
    "type"  -> Json.obj("code" -> "REPRESENTATIVE", "meaning" -> "Acts on behalf of taxpayer"),
    "class" -> Json.obj("code" -> "COUNCIL_TAX", "meaning" -> "Council Tax Challenge")
  )

  private def ref(qa: QuestionAnswer): JsObject =
    Json.obj("unique_statutory_ref" -> qa.code, "text" -> qa.text)

  private val fileWrites: Writes[SubmissionFile] = Writes { f =>
    Json.obj(
      "node"       -> JsNull,
      "mime_type"  -> f.mimeType,
      "label"      -> f.label,
      "is_pointer" -> true,
      "pointer"    -> f.pointer.fold[JsValue](JsNull)(JsString(_)),
      "data"       -> JsNull
    )
  }

  private def communicationsJson(c: Communications): JsObject =
    Json.obj("forenames" -> c.forenames, "surname" -> c.surname) ++
      JsObject(Seq(
        c.email.map("email" -> JsString(_)),
        c.telephoneNumber.map("telephone_number" -> JsString(_)),
        c.councilTaxPayerFirstName.map("CouncilTaxPayerFirstName" -> JsString(_)),
        c.councilTaxPayerLastName.map("CouncilTaxPayerLastName" -> JsString(_)),
        c.councilTaxPayerEmail.map("CouncilTaxPayerEmail" -> JsString(_)),
        c.councilTaxPayerContactPreference.map("CouncilTaxPayerContactPreference" -> JsString(_)),
        c.councilTaxPayerContactAddress.map("CouncilTaxPayerContactAddress" -> JsString(_))
      ).flatten)

  private def personJson(c: Communications): JsObject = {
    val person = Json.obj(
      "communications"   -> communicationsJson(c),
      "customerCapacity" -> customerCapacity
    )

    if (c.councilTaxPayerFirstName.isDefined) person ++ Json.obj("taxPayerCapacity" -> customerCapacity)
    else person
  }

  implicit val writes: OWrites[SubmissionPayload] = OWrites { p =>
    Json.obj(
      "submissionId"    -> p.submissionId,
      "submission_date" -> p.submissionDate,
      "files"           -> Json.toJson(p.files)(Writes.seq(fileWrites)),
      "product" -> Json.obj(
        "person"       -> personJson(p.communications),
        "relationship" -> relationship
      ),
      "premises" -> p.premises.map { pr =>
        ref(pr.premise) ++ Json.obj(
          "statements"  -> pr.statements.map(ref),
          "information" -> pr.information.map(ref)
        )
      },
      "proposition" -> p.propositions.map(qa => Json.obj("unique_statutory_ref" -> qa.code, "alterations" -> Json.arr(qa.text)))
    )
  }
}
