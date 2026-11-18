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

import com.typesafe.config.ConfigFactory
import models.submission.{Premise, QuestionAnswer, SubmissionPayload}
import play.api.Configuration

class JourneyRegistrySpec extends JourneyFixtures {

  private val app      = applicationBuilder().build()
  private val registry = app.injector.instanceOf[JourneyRegistry]

  private def registryFrom(hocon: String) = new JourneyRegistry(Configuration(ConfigFactory.parseString(hocon).resolve()))

  private def journey(extra: String = "", premise: String = "J01", key: String = "J01", selectors: String = """reasonForReview = "band-is-wrong"""") =
    s"""{ key = $key, number = 1, name = Test, selectors { $selectors }
       |  premise { code = $premise, question = scenarioResponsible.heading, page = scenarioResponsible, type = choice }
       |  questions = [ { code = S01, question = whyBandIsWrong.heading, page = whyBandIsWrong, type = text $extra } ] }""".stripMargin

  private def config(journeys: String*) = s"journeys.list = [ ${journeys.mkString("\n")} ]"

  "JourneyRegistry configuration" - {

    "must define the ten journeys, with journey 4 in four shapes" in {
      registry.all.map(_.number).distinct.sorted mustBe (1 to 10)
      registry.all.map(_.key) mustBe Seq("J01", "J02", "J03", "J04.1", "J04.2", "J04.3", "J04.4", "J05", "J06", "J07", "J08", "J09", "J10")
      registry.all.filter(_.number == 4).map(_.name).distinct mustBe Seq("Ufs_Deletion")
    }

    "must have no problems" in {
      JourneyRegistry.problems(registry.all) mustBe empty
    }

    "must have every question text in English and Welsh" in {
      val api = app.injector.instanceOf[play.api.i18n.MessagesApi]
      for (lang <- Seq("en", "cy"); d <- registry.all; q <- d.premise +: d.questions)
        withClue(s"${d.key} ${q.code} $lang: ") { api.isDefinedAt(q.questionKey)(play.api.i18n.Lang(lang)) mustBe true }
    }

    "must read a list of values and an optional question" in {
      val i07 = registry.all.find(_.key == "J04.2").value.questions.filter(_.code == "I07")
      i07.map(_.when.requirements("uninhabitableDetails")) mustBe Seq(Set("fireDamage", "floodDamage"), Set("other"))
      registry.all.find(_.key == "J04.2").value.questions.filter(!_.required).map(_.code) mustBe Seq("I08", "I09")
    }

    "must start with a journey added in config alone" in {
      val added = registryFrom(config(journey(), journey(key = "J11", premise = "J11", selectors = """reasonForReview = "changes-affected-property"""")))
      added.all.map(_.key) mustBe Seq("J01", "J11")
    }

    "must refuse to start with an unknown answer type" in {
      an[IllegalArgumentException] must be thrownBy registryFrom(config(journey().replace("type = text", "type = number")))
    }

    "must refuse to start with a missing field" in {
      a[com.typesafe.config.ConfigException] must be thrownBy registryFrom(config(journey().replace("page = whyBandIsWrong, ", "")))
    }

    "must refuse to start with a malformed code" in {
      val ex = the[IllegalStateException] thrownBy registryFrom(config(journey().replace("code = S01", "code = X1")))
      ex.getMessage must include("X1")
    }

    "must refuse to start with a journey with no selectors" in {
      val ex = the[IllegalStateException] thrownBy registryFrom(config(journey(selectors = "")))
      ex.getMessage must include("J01: has no selectors")
    }

    "must refuse to start with a repeated key" in {
      val ex = the[IllegalStateException] thrownBy registryFrom(config(journey(), journey()))
      ex.getMessage must include("journey J01 is defined twice")
    }
  }

  "JourneyRegistry configuration checks against the real pages" - {

    def problemsIn(hocon: String) = the[IllegalStateException] thrownBy registryFrom(config(hocon))

    "must refuse a page that does not exist" in {
      problemsIn(journey().replace("page = whyBandIsWrong", "page = whyBandIsWrongg")).getMessage must include("unknown page 'whyBandIsWrongg'")
    }

    "must refuse a question read as the wrong type" in {
      problemsIn(journey().replace("page = whyBandIsWrong, type = text", "page = whyBandIsWrong, type = date")).getMessage must
        include("reads page 'whyBandIsWrong' as Date but it holds Text")
    }

    "must refuse a selector value that is not an option of the page" in {
      problemsIn(journey(selectors = """reasonForReview = "band-is-wrongg"""")).getMessage must
        include("'band-is-wrongg' is not an option of page 'reasonForReview'")
    }

    "must refuse a selector on a page that has no fixed options" in {
      problemsIn(journey(selectors = """whyBandIsWrong = "x"""")).getMessage must include("has no fixed options")
    }

    "must refuse a condition on a question that names an option that does not exist" in {
      problemsIn(journey(extra = ", when { uninhabitableDetails = [fireDamage, floodDamagee] }")).getMessage must
        include("'floodDamagee' is not an option of page 'uninhabitableDetails'")
    }

    "must refuse two journeys that the same answers could select" in {
      val ex = problemsIn(journey(key = "J01") + "\n" + journey(key = "J02", premise = "J02", selectors = """reasonForReview = "band-is-wrong", scenarioResponsible = "less-than""""))
      ex.getMessage must include("J01 and J02 can both be selected by the same answers")
    }

    "must have a label in English and Welsh for every option of every choice answer that is sent" in {
      val api = app.injector.instanceOf[play.api.i18n.MessagesApi]
      for {
        d <- registry.all; q <- d.premise +: d.questions if q.answerType == AnswerType.Choice
        value <- PageCatalogue.entries(q.page).options.value
        lang  <- Seq("en", "cy")
      } withClue(s"${d.key} ${q.code} ${q.prefix}.$value $lang: ") {
        api.isDefinedAt(s"${q.prefix}.$value")(play.api.i18n.Lang(lang)) mustBe true
      }
    }

    "must be reachable: some answers select each journey" in {
      // each journey's own fixture answers select it and no other
      registry.all.map(_.key).foreach(key => registry.select(complete(key)).value.key mustBe key)
    }
  }

  "JourneyRegistry.select" - {

    "must find no journey for no selections" in {
      registry.select(emptyUserAnswers).left.value mustBe "No journey matches the selections made"
    }
  }

  "JourneyRegistry.identify" - {

    def qa(code: String) = QuestionAnswer(code, "q", "a")
    def payload(premise: String, statements: Seq[String], information: Seq[String], propositions: Seq[String]) =
      SubmissionPayload("ref", "d", Nil, models.submission.Communications("a", "b", None, None, None, None, None, None, None),
        Seq(Premise(qa(premise), statements.map(qa), information.map(qa))), propositions.map(qa))

    "must tell journeys 5 and 6 apart by premise although they carry the same codes" in {
      registry.identify(payload("J05", Seq("S01"), Seq("I11"), Seq("P02"))).value.key mustBe "J05"
      registry.identify(payload("J06", Seq("S01"), Seq("I11"), Seq("P02"))).value.key mustBe "J06"
    }

    "must tell journeys 9 and 10 apart by premise" in {
      registry.identify(payload("J09", Seq("S01"), Nil, Seq("P02"))).value.key mustBe "J09"
      registry.identify(payload("J10", Seq("S01"), Nil, Seq("P02"))).value.key mustBe "J10"
    }

    "must tell the four shapes of journey 4 apart by their codes" in {
      registry.identify(payload("J04", Seq("S02", "S01"), Seq("I10"), Seq("P02"))).value.key mustBe "J04.1"
      registry.identify(payload("J04", Seq("S02", "S01"), Seq("I06", "I01", "I07"), Nil)).value.key mustBe "J04.2"
      registry.identify(payload("J04", Seq("S02", "S01"), Seq("I03"), Seq("P02"))).value.key mustBe "J04.3"
      registry.identify(payload("J04", Seq("S02", "S01"), Nil, Seq("P02"))).value.key mustBe "J04.4"
    }

    "must reject a payload missing a code the journey needs" in {
      registry.identify(payload("J03", Seq("S01"), Nil, Seq("P01"))).isLeft mustBe true
    }

    "must reject a payload with a code the journey does not have" in {
      registry.identify(payload("J02", Seq("S01"), Seq("I04"), Seq("P01"))).isLeft mustBe true
    }

    "must reject a premise that no journey has" in {
      registry.identify(payload("J99", Seq("S01"), Nil, Seq("P01"))).isLeft mustBe true
    }
  }
}
