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

package services.submission

import models.UserAnswers
import models.submission._
import pages._
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.libs.json.{JsObject, Json}

import java.time.{Clock, LocalDate}
import javax.inject.{Inject, Singleton}

/** Turns the answers in the session into the payload for submission.
  *
  * The journey comes from the user's selections (`JourneyRegistry.select`), the payload is built from that
  * journey's definition, and then checked the other way round: the premise and codes of what was built must
  * identify the same journey. Anything that does not line up is returned as an error and nothing is sent.
  *
  * Questions are always sent in English, whatever language the user was using.
  */
@Singleton
class SubmissionPayloadBuilder @Inject()(registry: JourneyRegistry, messagesApi: MessagesApi, clock: Clock) {

  private val english: Messages = messagesApi.preferred(Seq(Lang("en")))

  private val optionalSuffix = """\s*\(optional\)\s*$""".r

  private def question(key: String): String = optionalSuffix.replaceFirstIn(english(key), "")

  def build(reference: String, answers: UserAnswers): Either[Seq[String], SubmissionPayload] =
    for {
      definition     <- registry.select(answers).left.map(Seq(_))
      premise        <- answered(definition.premise, answers).left.map(Seq(_))
      questions      <- answeredQuestions(definition, answers)
      communications <- communications(answers)
      payload = SubmissionPayload(
        submissionId   = reference,
        submissionDate = AnswerFormat.date(LocalDate.now(clock)),
        files          = files(answers),
        communications = communications,
        premises       = Seq(Premise(premise, questions.filter(_.code.startsWith("S")), questions.filter(_.code.startsWith("I")))),
        propositions   = questions.filter(_.code.startsWith("P"))
      )
      _ <- checked(definition, payload)
    } yield payload

  private def answered(spec: QuestionSpec, answers: UserAnswers): Either[String, QuestionAnswer] =
    spec.answer(answers, english)
      .map(QuestionAnswer(spec.code, question(spec.questionKey), _))
      .toRight(s"No answer for ${spec.code} (${spec.questionKey})")

  private def answeredQuestions(definition: JourneyDefinition, answers: UserAnswers): Either[Seq[String], Seq[QuestionAnswer]] = {
    val results = definition.questions.filter(_.applies(answers)).map(spec => spec -> answered(spec, answers))
    val missing = results.collect { case (spec, Left(error)) if spec.required => error }
    if (missing.nonEmpty) Left(missing) else Right(results.collect { case (_, Right(qa)) => qa })
  }

  // what is sent, read back from its JSON, must carry every question built and identify as the journey chosen
  private def checked(definition: JourneyDefinition, payload: SubmissionPayload): Either[Seq[String], Unit] = {
    val json       = Json.toJson(payload)
    val premise    = (json \ "premises" \ 0 \ "unique_statutory_ref").asOpt[String]
    val sentCodes  = Seq("statements", "information").flatMap(k => (json \ "premises" \ 0 \ k).as[Seq[JsObject]]).flatMap(refOf) ++
      (json \ "proposition").as[Seq[JsObject]].flatMap(refOf)
    val duplicates = sentCodes.diff(sentCodes.distinct).distinct

    premise match {
      case None                                      => Left(Seq("The payload has no premise"))
      case _ if sentCodes.sorted != payload.codes.filterNot(premise.contains).sorted =>
        Left(Seq(s"The JSON sent (${sentCodes.sorted.mkString(", ")}) is not what was built (${payload.codes.sorted.mkString(", ")})"))
      case _ if duplicates.nonEmpty                  => Left(Seq(s"Code used more than once: ${duplicates.mkString(", ")}"))
      case Some(code)                                =>
        registry.identify(code, code +: sentCodes) match {
          case Right(identified) if identified.key == definition.key => Right(())
          case Right(identified) => Left(Seq(s"Built ${definition.key} but the codes identify ${identified.key}"))
          case Left(error)       => Left(Seq(error))
        }
    }
  }

  private def refOf(json: JsObject): Option[String] = (json \ "unique_statutory_ref").asOpt[String]

  private def files(answers: UserAnswers): Seq[SubmissionFile] =
    if (answers.get(HasEvidencePage).contains(true))
      // TODO: send a pointer to the stored file once Upscan and file storage exist; only metadata is kept today
      answers.get(UploadEvidencePage).getOrElse(Nil).map(e => SubmissionFile(e.contentType, "RYCTB Uploaded Document", None))
    else Nil

  private def communications(answers: UserAnswers): Either[Seq[String], Communications] =
    answers.get(ContactDetailsPage).toRight(Seq("No contact details")).map { contact =>
      val payer = answers.get(CouncilTaxPayerDetailsPage).filter(_ =>
        answers.get(CouncilTaxResponsiblePage).contains(models.CouncilTaxResponsible.ActingFor))
      val address = Seq(contact.addressLine1, contact.addressLine2, contact.townOrCity, contact.county, contact.postcode)
        .flatten.filter(_.nonEmpty)

      Communications(
        forenames                        = contact.firstName,
        surname                          = contact.lastName,
        email                            = contact.email,
        telephoneNumber                  = contact.phoneNumber,
        councilTaxPayerFirstName         = payer.map(_.firstName),
        councilTaxPayerLastName          = payer.map(_.lastName),
        councilTaxPayerEmail             = payer.map(_.email),
        councilTaxPayerContactPreference = Option(contact.value.toSeq.sorted.map(_.toUpperCase).mkString(",")).filter(_.nonEmpty),
        councilTaxPayerContactAddress    = Option(address.mkString(", ")).filter(_.nonEmpty)
      )
    }
}
