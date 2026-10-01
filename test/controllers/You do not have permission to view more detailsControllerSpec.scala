package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.You do not have permission to view more detailsView

class You do not have permission to view more detailsControllerSpec extends SpecBase {

  "You do not have permission to view more details Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.You do not have permission to view more detailsController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[You do not have permission to view more detailsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }
  }
}
