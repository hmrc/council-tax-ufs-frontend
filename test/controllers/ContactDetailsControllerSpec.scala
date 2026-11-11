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

package controllers

import base.SpecBase
import forms.ContactDetailsFormProvider
import models.{NormalMode, ContactDetails, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.ContactDetailsPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ContactDetailsView

import scala.concurrent.Future

class ContactDetailsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ContactDetailsFormProvider()
  val form = formProvider()

  lazy val contactDetailsRoute = routes.ContactDetailsController.onPageLoad(NormalMode).url

  val backLinkUrl = Some(routes.WhichBandController.onPageLoad(NormalMode).url)

  val validAnswer = ContactDetails("John", "Smith", Some("john@example.com"), None, None, None, None, None, None, Set("email"))

  val userAnswers = UserAnswers(userAnswersId).set(ContactDetailsPage, validAnswer).success.value

  "ContactDetails Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, contactDetailsRoute)

        val view = application.injector.instanceOf[ContactDetailsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, backLinkUrl)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, contactDetailsRoute)

        val view = application.injector.instanceOf[ContactDetailsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode, backLinkUrl)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, contactDetailsRoute)
            .withFormUrlEncodedBody(("firstName", "John"), ("lastName", "Smith"), ("email", "john@example.com"), ("value[0]", "email"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, contactDetailsRoute)
            .withFormUrlEncodedBody(("value[0]", "invalid value"))

        val boundForm = form.bind(Map("value[0]" -> "invalid value"))

        val view = application.injector.instanceOf[ContactDetailsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, backLinkUrl)(request, messages(application)).toString
      }
    }

    "must link the contact method error summary to the first checkbox when no contact method is selected" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(POST, contactDetailsRoute).withFormUrlEncodedBody()
        val result  = route(application, request).value
        val html    = contentAsString(result)

        status(result) mustEqual BAD_REQUEST
        html must include("href=\"#value_0\"")
        html must include("id=\"value_0\"")
      }
    }

    "must return OK and an empty form for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, contactDetailsRoute)

        val view = application.injector.instanceOf[ContactDetailsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, backLinkUrl)(request, messages(application)).toString
      }
    }

    "must redirect to the next page for a valid POST if no existing data is found" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, contactDetailsRoute)
            .withFormUrlEncodedBody(("firstName", "John"), ("lastName", "Smith"), ("email", "john@example.com"), ("value[0]", "email"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }
  }
}
