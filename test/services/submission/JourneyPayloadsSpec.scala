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

import models.submission.SubmissionPayload

/** The exact questions and answers each of the ten journeys sends, in the order they are sent. */
class JourneyPayloadsSpec extends JourneyFixtures {

  private val app     = applicationBuilder().build()
  private val builder = app.injector.instanceOf[SubmissionPayloadBuilder]

  private val d = "20260729T000000Z"

  private val bandWhy   = ("S01", "Tell us why you think the Council Tax band is wrong?", "Neighbours are in a lower band")
  private val bandWhich = ("P01", "What do you think the Council Tax band should be?", "Band C")
  private val notice    = "Have you received a Notice of Alteration within the last six months?"
  private val changed   = "What has changed?"
  private val demolishedOption = "My property has been demolished, is derelict or is undergoing significant renovation and repairs and cannot be lived in"
  private val uninhabitableWhich = "Choose which applies to your property"
  private val uninhabitableWhy   = "Why do you think the property cannot be lived in?"
  private val addressChanged     = "Has the address of your home changed?"

  // (code, question, answer): the premise, then statements, information and propositions
  private val expected: Seq[(String, String, Seq[(String, String, String)])] = Seq(
    ("Journey 1: responsible for less than six months", "J01", Seq(
      ("J01", "How long have you been responsible for paying Council Tax on the property?", "Less than 6 months"),
      bandWhy,
      ("P02", "When did you become responsible for paying Council Tax for your home?", d),
      bandWhich)),
    ("Journey 2: more than six months, no Notice of Alteration", "J02", Seq(
      ("J02", notice, "No"), bandWhy, bandWhich)),
    ("Journey 3: more than six months, Notice of Alteration received", "J03", Seq(
      ("J03", notice, "Yes"), bandWhy,
      ("I04", "What date is on the Notice of Alteration?", d),
      bandWhich)),
    ("Journey 4.1: fully demolished", "J04.1", Seq(
      ("J04", changed, demolishedOption),
      ("S02", uninhabitableWhich, "My property has been fully demolished"),
      ("S01", uninhabitableWhy, "Fire damage"),
      ("I10", "Tell us about your fully demolished property", "Burnt down"),
      ("P02", "When was the property demolished?", d))),
    ("Journey 4.2: derelict", "J04.2", Seq(
      ("J04", changed, demolishedOption),
      ("S02", uninhabitableWhich, "My property is derelict or in a state of disrepair and cannot be lived in"),
      ("S01", uninhabitableWhy, "Flood damage"),
      ("I06", "When did the property become derelict or could not be lived in?", d),
      ("I01", "When did someone last live in the property?", d),
      ("I07", "What do you plan to do with the property?", "Rebuild"),
      ("I08", "What do you need to do to make the property habitable?", "New roof"),
      ("I09", "Have you made any planning applications for the property?", "PA/123"))),
    ("Journey 4.3: major works or renovation", "J04.3", Seq(
      ("J04", changed, demolishedOption),
      ("S02", uninhabitableWhich, "My property is having major works or renovations done and cannot be lived in"),
      ("S01", "Tell us about the major works or renovation to your property", "Extension"),
      ("I03", "What is the date that the work completed?", d),
      ("P02", "When did the major works or renovation start?", d))),
    ("Journey 4.4: part of the property demolished", "J04.4", Seq(
      ("J04", changed, demolishedOption),
      ("S02", uninhabitableWhich, "Part of the property has been demolished"),
      ("S01", "Provide details of the works started on the property and what the works consist of", "Garage"),
      ("P02", "When was part of the property demolished?", d))),
    ("Journey 5: merged", "J05", Seq(
      ("J05", "How has the property been split or merged?", "The property has been merged with one or more other properties to make a larger property"),
      ("S01", "How have your properties been merged?", "Knocked through"),
      ("I11", addressChanged, "No"),
      ("P02", "When was the work to merge your properties completed?", d))),
    ("Journey 6: split", "J06", Seq(
      ("J06", "How has the property been split or merged?", "The property has been split into two or more separate properties to make smaller properties"),
      ("S01", "How has the property been split?", "Two flats"),
      ("I11", addressChanged, "Yes, 1A and 1B"),
      ("P02", "When was the work to split your home completed?", d))),
    ("Journey 7: now used as a home", "J07", Seq(
      ("J07", "How has the use of the property changed?", "Part or all of the property is now used as a home"),
      ("S01", "Which parts of the property are now used as a home?", "Shop"),
      ("I11", addressChanged, "No"),
      ("P02", "When did all or part of your property become your home?", d))),
    ("Journey 8: now used as a business", "J08", Seq(
      ("J08", "How has the use of the property changed?", "The property is now used as a business"),
      ("S01", "Which parts of your home are being used for business?", "Garage"),
      ("I11", addressChanged, "No"),
      ("P02", "When did all or part of your home change to business use?", d))),
    ("Journey 9: changes to the local area", "J09", Seq(
      ("J09", changed, "There have been changes to the local area that have affected my property"),
      ("S01", "What permanent changes have taken place in your local area?", "New bypass"),
      ("P02", "When did these permanent changes take place?", d))),
    ("Journey 10: another reason", "J10", Seq(
      ("J10", changed, "Another reason"),
      ("S01", changed, "Something"),
      ("P02", "When did this change happen?", d)))
  )

  private def sent(payload: SubmissionPayload): Seq[(String, String, String)] = {
    val p = payload.premises.head
    (p.premise +: (p.statements ++ p.information ++ payload.propositions)).map(qa => (qa.code, qa.question, qa.answer))
  }

  "Every journey" - {

    "must have a test here" in {
      expected.map(_._2).toSet mustBe complete.keySet
    }

    expected.foreach { case (title, key, questions) =>
      s"$title must send exactly these questions and answers" in {
        val payload = builder.build("UFSJ30001EQ", complete(key)).value
        sent(payload).sortBy(_._1) mustBe questions.sortBy(_._1)
        payload.submissionId mustBe "UFSJ30001EQ"
        payload.communications.forenames mustBe "John"
      }

      s"$title must not be sent without the contact details" in {
        builder.build("UFSJ30001EQ", complete(key).remove(pages.ContactDetailsPage).success.value).isLeft mustBe true
      }
    }

    "must put every code of the journey in the JSON that is sent, P02 included" in {
      expected.foreach { case (_, key, questions) =>
        val json = play.api.libs.json.Json.toJson(builder.build("UFSJ30001EQ", complete(key)).value)
        val premise = (json \ "premises" \ 0)
        val codes = (premise \ "unique_statutory_ref").as[String] +:
          (Seq("statements", "information").flatMap(k => (premise \ k).as[Seq[play.api.libs.json.JsObject]]) ++
            (json \ "proposition").as[Seq[play.api.libs.json.JsObject]]).map(o => (o \ "unique_statutory_ref").as[String])
        withClue(s"$key: ") {
          codes.sorted mustBe questions.map(_._1).sorted
          if (questions.exists(_._1 == "P02")) (json \ "proposition").as[Seq[play.api.libs.json.JsObject]].map(o => (o \ "unique_statutory_ref").as[String]) must contain("P02")
        }
      }
    }

    "must not send a journey that has a P02 question once that date is missing" in {
      val withDate = Map("J01" -> pages.CouncilTaxLiabilityStartDatePage, "J04.1" -> pages.DemolishedDatePage, "J04.3" -> pages.RenoStartDatePage,
        "J04.4" -> pages.PartDemoDatePage, "J05" -> pages.PropertyChangeDatePage, "J07" -> pages.DomesticDatePage,
        "J08" -> pages.BusinessDatePage, "J09" -> pages.WhenDidChangeHappenPage, "J10" -> pages.WhenDidChangeHappenPage)
      withDate.foreach { case (key, page) =>
        withClue(s"$key: ") { builder.build("UFSJ30001EQ", complete(key).remove(page).success.value).left.value.head must include("P02") }
      }
    }

    "must write dates as yyyyMMddTHHmmssZ, for example 1 April 2005 as 20050401T000000Z" in {
      AnswerFormat.date(java.time.LocalDate.of(2005, 4, 1)) mustBe "20050401T000000Z"
      val answers = set(complete("J01"), pages.CouncilTaxLiabilityStartDatePage, java.time.LocalDate.of(2005, 4, 1))
      builder.build("UFSJ30001EQ", answers).value.propositions.find(_.code == "P02").value.answer mustBe "20050401T000000Z"
    }

    "must not send a journey's answers once the user has switched to another journey" in {
      // a band challenger who earlier chose the property-changes journey keeps old answers in the session
      val stale = set(complete("J02"), pages.deletionOptionsPage, models.deletionOptions.Affected)
      builder.build("UFSJ30001EQ", stale).value.premises.head.premise.code mustBe "J02"
    }
  }
}
