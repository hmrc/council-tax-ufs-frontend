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
import forms.HasEvidenceFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{HasEvidencePage, UploadEvidencePage}
import models.UploadedEvidence
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.HasEvidenceView

import scala.concurrent.Future

class HasEvidenceControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val hasEvidenceRoute = routes.HasEvidenceController.onPageLoad(NormalMode).url
  // reverse routes only know the service prefix once the app is running
  def backLink = routes.WhyBandIsWrongController.onPageLoad(NormalMode).url
  val form = new HasEvidenceFormProvider()()

  "HasEvidence Controller" - {

    "must return OK and the correct view for a GET" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, hasEvidenceRoute)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HasEvidenceView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, backLink)(request, messages(application)).toString
      }
    }

    "must populate the view on a GET when the question has previously been answered" in {
      val userAnswers = UserAnswers(userAnswersId).set(HasEvidencePage, true).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, hasEvidenceRoute)
        val view = application.injector.instanceOf[HasEvidenceView]
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode, backLink)(request, messages(application)).toString
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
        val request = FakeRequest(POST, hasEvidenceRoute).withFormUrlEncodedBody(("value", "true"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must discard a previously chosen file when the answer changes to no" in {
      val answers = UserAnswers(userAnswersId)
        .set(HasEvidencePage, true).success.value
        .set(UploadEvidencePage, Seq(UploadedEvidence("a.pdf", "application/pdf", 10))).success.value

      answers.set(HasEvidencePage, false).success.value.get(UploadEvidencePage) mustBe None
      answers.set(HasEvidencePage, true).success.value.get(UploadEvidencePage) must not be None
    }

    "must return a Bad Request and errors when no answer is submitted" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(POST, hasEvidenceRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val view = application.injector.instanceOf[HasEvidenceView]
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, backLink)(request, messages(application)).toString
      }
    }
  }
}
