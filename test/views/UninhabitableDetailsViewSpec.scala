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
import forms.UninhabitableDetailsFormProvider
import models.{NormalMode, UninhabitableDetails}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.UninhabitableDetailsView

import scala.jdk.CollectionConverters._

class UninhabitableDetailsViewSpec extends SpecBase {

  private val application = applicationBuilder(userAnswers = None).build()
  private val view        = application.injector.instanceOf[UninhabitableDetailsView]
  private implicit val msgs: play.api.i18n.Messages = messages(application)

  private implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  private def form: Form[UninhabitableDetails] = new UninhabitableDetailsFormProvider()()

  private def applyView(form: Form[UninhabitableDetails]): HtmlFormat.Appendable =
    view(form, NormalMode, Some("/back"))(fakeRequest, msgs)

  private def asDocument(html: HtmlFormat.Appendable): Document = Jsoup.parse(html.toString)

  "UninhabitableDetailsView" - {

    "must render the correct page title" in {
      asDocument(applyView(form)).title() must include(msgs("uninhabitableDetails.title"))
    }

    "must render the heading as the fieldset legend" in {
      asDocument(applyView(form)).select("legend h1").text() mustBe msgs("uninhabitableDetails.heading")
    }

    "must render a radio for each reason" in {
      val doc    = asDocument(applyView(form))
      val labels = doc.select(".govuk-radios__item label").asScala.map(_.text()).toSeq

      labels mustBe Seq(
        msgs("uninhabitableDetails.fireDamage"),
        msgs("uninhabitableDetails.floodDamage"),
        msgs("uninhabitableDetails.other")
      )
    }

    "must render the back link" in {
      asDocument(applyView(form)).select(".govuk-back-link").attr("href") mustBe "/back"
    }

    "must render a submit button" in {
      asDocument(applyView(form)).select("button.govuk-button").text() mustBe msgs("site.continue")
    }

    "must not show an error summary when there are no errors" in {
      asDocument(applyView(form)).select(".govuk-error-summary").isEmpty mustBe true
    }

    "must show the required error and link it to the first radio when nothing is selected" in {
      val doc = asDocument(applyView(form.bind(Map.empty[String, String])))

      doc.select(".govuk-error-summary").text() must include(msgs("uninhabitableDetails.error.required"))
      doc.select(".govuk-error-summary a").attr("href") mustBe "#value_0"
    }

    "must render a form with the correct action" in {
      asDocument(applyView(form)).select("form").attr("action") mustBe
        routes.UninhabitableDetailsController.onSubmit(NormalMode).url
    }
  }
}
