package views

import base.SpecBase
import controllers.routes
import forms.deletionOptionsFormProvider
import models.{deletionOptions, NormalMode}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.html.deletionOptionsView

class DeletionOptionsViewSpec extends SpecBase {

  val form: Form[deletionOptions] = new deletionOptionsFormProvider()()

  val application = applicationBuilder(userAnswers = None).build()
  val view        = application.injector.instanceOf[deletionOptionsView]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val testBackLink = "/back-link"

  def applyView(form: Form[deletionOptions]): HtmlFormat.Appendable =
    view(form, NormalMode, testBackLink)(fakeRequest, messages(application))

  def asDocument(html: HtmlFormat.Appendable): Document =
    Jsoup.parse(html.toString)

  "DeletionOptionsView" - {

    "must render the correct page title" in {
      val doc = asDocument(applyView(form))
      doc.title() must include(messages(application)("deletionOptions.title"))
    }

    "must render the heading" in {
      val doc = asDocument(applyView(form))
      doc.select("h1.govuk-heading-l").text() mustBe
        messages(application)("deletionOptions.heading")
    }

    "must render five radio buttons" in {
      val doc = asDocument(applyView(form))
      doc.select(".govuk-radios__item").size() mustBe 5
    }

    "must render the first radio option" in {
      val doc = asDocument(applyView(form))
      doc.select("label[for=value_0]").text() mustBe
        messages(application)("deletionOptions.demolished")
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
        routes.deletionOptionsController.onSubmit(NormalMode).url
    }
  }
}