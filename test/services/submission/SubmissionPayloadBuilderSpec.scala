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

import models._
import models.submission.SubmissionPayload
import pages._
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

class SubmissionPayloadBuilderSpec extends JourneyFixtures {

  private val app      = applicationBuilder().build()
  private val builder  = app.injector.instanceOf[SubmissionPayloadBuilder]
  private val registry = app.injector.instanceOf[JourneyRegistry]

  private def build(a: UserAnswers) = builder.build("UFSJ30001EQ", a)

  "SubmissionPayloadBuilder" - {

    "must have a fixture and expected codes for every journey in the registry" in {
      registry.all.map(_.key).toSet mustBe complete.keySet
      registry.all.map(_.key).toSet mustBe expectedCodes.keySet
    }

    registry.all.map(_.key).foreach { key =>
      s"must build journey $key with exactly its codes, and the codes must identify it again" in {
        val payload = build(complete(key)).value
        payload.codes.toSet mustBe expectedCodes(key).toSet
        payload.codes.distinct mustBe payload.codes
        registry.identify(payload).value.key mustBe key
        registry.select(complete(key)).value.key mustBe key
      }
    }

    "must put each question under the category its code names" in {
      val premise = build(complete("J04.2")).value.premises.head
      premise.statements.map(_.code) mustBe Seq("S02", "S01")
      premise.information.map(_.code) mustBe Seq("I06", "I01", "I07", "I08", "I09")
    }

    "must send the plan or the description as I07, depending on the reason, and never both" in {
      val other = set(set(complete("J04.2"), UninhabitableDetailsPage, UninhabitableDetails.Other), UninhabitableDetailsOtherPage, "Condemned")
      val payload = build(other).value
      payload.codes.count(_ == "I07") mustBe 1
      payload.premises.head.information.find(_.code == "I07").value.answer mustBe "Condemned"
      payload.codes.toSet mustBe Set("J04", "S02", "S01", "I06", "I01", "I07", "I08", "I09").--(Set("I08", "I09"))
    }

    "must leave out optional questions that were not answered" in {
      val without = complete("J04.2").remove(UninhabitableDetailsPlanHabitablePage).success.value
        .remove(UninhabitableDetailsPlanPlanningApplicationPage).success.value
      build(without).value.codes.toSet mustBe Set("J04", "S02", "S01", "I06", "I01", "I07")
    }

    "must report a required question that was not answered" in {
      build(complete("J02").remove(WhichBandPage).success.value).left.value mustBe Seq("No answer for P01 (whichBand.heading)")
    }

    "must report when the selections lead to no journey" in {
      build(emptyUserAnswers.set(ContactDetailsPage, contact).success.value).isLeft mustBe true
    }

    "must report missing contact details" in {
      build(complete("J10").remove(ContactDetailsPage).success.value).left.value mustBe Seq("No contact details")
    }

    "must render the question and answer in English, with dates as UTC timestamps and the band as its label" in {
      val payload = build(complete("J03")).value
      val premise = payload.premises.head
      premise.premise.question mustBe "Have you received a Notice of Alteration within the last six months?"
      premise.premise.answer mustBe "Yes"
      premise.information.head.answer mustBe "20260729T000000Z"
      payload.propositions.head.answer mustBe "Band C"
    }

    "must not send (optional) as part of a question" in {
      build(complete("J04.2")).value.premises.head.information.find(_.code == "I08").value.question mustBe
        "What do you need to do to make the property habitable?"
    }

    "must write the agreed JSON shape" in {
      val json = Json.toJson(build(complete("J03")).value).as[JsObject]

      (json \ "submissionId").as[String] mustBe "UFSJ30001EQ"
      (json \ "files").as[Seq[JsObject]] mustBe empty
      (json \ "product" \ "person" \ "communications" \ "forenames").as[String] mustBe "John"
      (json \ "product" \ "person" \ "capacity" \ "code").as[String] mustBe "TXP"
      (json \ "product" \ "relationship" \ "class" \ "code").as[String] mustBe "COUNCIL_TAX"
      (json \ "premises" \ 0 \ "unique_statutory_ref").as[String] mustBe "J03"
      Json.parse((json \ "premises" \ 0 \ "statements" \ 0 \ "text").as[String]) mustBe
        Json.obj("Question" -> "Tell us why you think the Council Tax band is wrong?", "Answer" -> "Neighbours are in a lower band")
      (json \ "premises" \ 0 \ "information" \ 0 \ "unique_statutory_ref").as[String] mustBe "I04"
      (json \ "proposition" \ 0 \ "unique_statutory_ref").as[String] mustBe "P01"
      (json \ "proposition" \ 0 \ "alterations").as[Seq[String]].size mustBe 1
    }

    "must send the council tax payer only when acting for them" in {
      val payer = CouncilTaxPayerDetails("Jane", "Smith", "jane@example.com")
      val self  = build(set(complete("J02"), CouncilTaxPayerDetailsPage, payer)).value.communications
      self.councilTaxPayerFirstName mustBe None

      val acting = build(set(set(complete("J02"), CouncilTaxPayerDetailsPage, payer), CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor)).value.communications
      acting.councilTaxPayerFirstName mustBe Some("Jane")
      acting.councilTaxPayerEmail mustBe Some("jane@example.com")
      acting.councilTaxPayerContactPreference mustBe Some("EMAIL")
      acting.councilTaxPayerContactAddress mustBe Some("1 High Street, London, SW1A 1AA")
    }

    "must list the uploaded files, and none when the user has no evidence" in {
      val upload = UploadedEvidence("plan.pdf", "application/pdf", 10L)
      val files  = build(set(set(complete("J01"), HasEvidencePage, true), UploadEvidencePage, Seq(upload))).value.files
      files.map(_.mimeType) mustBe Seq("application/pdf")

      build(set(set(complete("J01"), HasEvidencePage, false), UploadEvidencePage, Seq(upload))).value.files mustBe empty
    }
  }

  "JourneyRegistry" - {

    "must give each journey a unique key and premise code that starts with J" in {
      registry.all.map(_.key).distinct.size mustBe registry.all.size
      registry.all.foreach(_.premise.code must startWith("J"))
    }

    "must give each question a code whose letter is S, I or P, and an English question" in {
      val messages = app.injector.instanceOf[play.api.i18n.MessagesApi].preferred(Seq(play.api.i18n.Lang("en")))
      for (d <- registry.all; q <- d.premise +: d.questions) {
        q.code must fullyMatch regex "[JSIP][0-9]{2}"
        messages.isDefinedAt(q.questionKey) mustBe true
      }
      registry.all.flatMap(_.questions).foreach(_.code.head must (be('S') or be('I') or be('P')))
    }

    "must not reuse a code within a journey unless the questions are alternatives" in {
      registry.all.foreach { d =>
        val answersFor = complete(d.key)
        d.questions.filter(_.applies(answersFor)).map(_.code).distinct.size mustBe d.questions.count(_.applies(answersFor))
      }
    }

    "must select only one journey for each set of answers" in {
      complete.foreach { case (key, answers) =>
        registry.all.filter(_.selected(answers)).map(_.key) mustBe Seq(key)
      }
    }

    "must never fit one set of codes to two journeys" in {
      for (a <- registry.all; b <- registry.all if a.key < b.key && a.premise.code == b.premise.code) {
        val mustHave = a.requiredCodes ++ b.requiredCodes
        val mayHave  = a.allCodes.intersect(b.allCodes)
        withClue(s"${a.key} and ${b.key}: ") { mustHave.subsetOf(mayHave) mustBe false }
      }
    }

    "must reject a payload whose codes fit no journey" in {
      val payload = build(complete("J02")).value
      val extra   = payload.copy(propositions = payload.propositions :+ models.submission.QuestionAnswer("P09", "q", "a"))
      registry.identify(extra).isLeft mustBe true
    }
  }
}
