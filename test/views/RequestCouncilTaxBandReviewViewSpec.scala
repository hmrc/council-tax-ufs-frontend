package views

import base.SpecBase
import controllers.routes
import forms.RequestCouncilTaxBandReviewFormProvider
import models.NormalMode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.RequestCouncilTaxBandReviewView

class RequestCouncilTaxBandReviewViewSpec extends SpecBase {

  val form: Form[Set[String]] = new RequestCouncilTaxBandReviewFormProvider()()

  val application = applicationBuilder(userAnswers = None).build()
  val view        = application.injector.instanceOf[RequestCouncilTaxBandReviewView]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val testBackLink = "/back-link"

  def applyView(form: Form[Set[String]]): HtmlFormat.Appendable =
    view(form, NormalMode, testBackLink)(fakeRequest, messages(application))

  def asDocument(html: HtmlFormat.Appendable): Document =
    Jsoup.parse(html.toString)

  "RequestCouncilTaxBandReviewView" - {

    "must render the correct page title" in {
      val doc = asDocument(applyView(form))
      doc.title() must include(messages(application)("requestCouncilTaxBandReview.title"))
    }

    "must render the heading" in {
      val doc = asDocument(applyView(form))
      doc.select("h1.govuk-heading-l").text() must include(
        messages(application)("requestCouncilTaxBandReview.heading")
      )
    }

    "must render the first paragraph" in {
      val doc = asDocument(applyView(form))
      doc.select("p.govuk-body").first().text() mustBe
        messages(application)("requestCouncilTaxBandReview.p1.1")
    }

    "must render the inset text" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-inset-text").text() mustBe
        messages(application)("requestCouncilTaxBandReview.p1.2")
    }

    "must render subheading1" in {
      val doc = asDocument(applyView(form))
      doc.select("h2.govuk-heading-m").first().text() mustBe
        messages(application)("requestCouncilTaxBandReview.subheading1")
    }

    "must render the first bullet list" in {
      val doc     = asDocument(applyView(form))
      val bullets = doc.select("ul.govuk-list--bullet").first().select("li")
      bullets.get(0).text() mustBe messages(application)("requestCouncilTaxBandReview.subheading2.bullet1")
      bullets.get(1).text() mustBe messages(application)("requestCouncilTaxBandReview.subheading2.bullet2")
      bullets.get(2).text() mustBe messages(application)("requestCouncilTaxBandReview.subheading2.bullet3")
    }

    "must render the checkbox" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-checkboxes__item").size() mustBe 1
    }

    "must render a submit button" in {
      val doc = asDocument(applyView(form))
      doc.select("button.govuk-button").text() mustBe
        messages(application)("site.continue")
    }

    "must not show an error summary when there are no errors" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-error-summary").isEmpty mustBe true
    }

    "must show an error summary when the form has errors" in {
      val boundForm = form.bind(Map("value" -> ""))
      val doc       = asDocument(applyView(boundForm))
      doc.select(".govuk-error-summary").isEmpty mustBe false
    }

    "must render a form with the correct action" in {
      val doc = asDocument(applyView(form))
      doc.select("form").attr("action") mustBe
        routes.RequestCouncilTaxBandReviewController.onSubmit(NormalMode).url
    }
  }
}