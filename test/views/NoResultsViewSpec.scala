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

import base.SpecBase
import controllers.routes
import models.NormalMode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.NoResultsView

class NoResultsViewSpec extends SpecBase {

  val postcode = "CF14 1AA"

  val application = applicationBuilder(userAnswers = None).build()
  val view         = application.injector.instanceOf[NoResultsView]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  def applyView(postcode: String): HtmlFormat.Appendable =
    view(postcode)(fakeRequest, messages(application))

  def asDocument(html: HtmlFormat.Appendable): Document =
    Jsoup.parse(html.toString)

  "NoResultsView" - {

    "must render the correct page title" in {
      val doc = asDocument(applyView(postcode))
      doc.title() must include(messages(application)("noResults.title", postcode))
    }

    "must render the heading with the postcode" in {
      val doc = asDocument(applyView(postcode))
      doc.select("h1.govuk-heading-xl").text() mustBe
        messages(application)("noResults.heading", postcode)
    }

    "must render the 'cannot find' message and link" in {
      val doc = asDocument(applyView(postcode))
      val paragraph = doc.select("p.govuk-body").first()

      paragraph.text() must include(messages(application)("noResults.cannotFind.check"))

      val link = paragraph.select("a.govuk-link").first()
      link.text() mustBe messages(application)("noResults.cannotFind.otherOptions")
    }

    "must render the contact council message and link" in {
      val doc = asDocument(applyView(postcode))
      val paragraph = doc.select("p.govuk-body").get(1)

      val link = paragraph.select("a.govuk-link").first()
      link.text() mustBe messages(application)("noResults.contactCouncil")

      paragraph.text() must include(messages(application)("noResults.contactCouncilDescription"))
    }

    "must render an input pre-filled with the postcode" in {
      val doc = asDocument(applyView(postcode))
      val input = doc.select("input#value")

      input.attr("name") mustBe "value"
      input.attr("value") mustBe postcode
      input.attr("autocomplete") mustBe "postal-code"
    }

    "must render the input label" in {
      val doc = asDocument(applyView(postcode))
      doc.select("label[for=value]").text() mustBe messages(application)("noResults.label")
    }

    "must render a submit button" in {
      val doc = asDocument(applyView(postcode))
      val button = doc.select("#search-again")

      button.text() mustBe messages(application)("noResults.button")
    }

    "must render a form with the correct action" in {
      val doc = asDocument(applyView(postcode))
      doc.select("form").attr("action") mustBe
        routes.PostcodeSearchController.onSubmit(NormalMode).url
    }
  }
}