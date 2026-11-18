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
import forms.PropertySplitOrMergeFormProvider
import models.{NormalMode, PropertySplitOrMerge, UserAnswers, deletionOptions}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{PropertyChangeDatePage, PropertyChangeDetailsPage, PropertySplitOrMergePage, deletionOptionsPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.PropertySplitOrMergeView

import scala.concurrent.Future

class PropertySplitOrMergeControllerSpec extends SpecBase with MockitoSugar {

  // the question is reached from the deletion options, so its back link follows that choice
  private val optionChosen: UserAnswers = emptyUserAnswers.set(deletionOptionsPage, deletionOptions.Split).success.value

  def onwardRoute = Call("GET", "/foo")

  lazy val propertySplitOrMergeRoute = routes.PropertySplitOrMergeController.onPageLoad(NormalMode).url

  val formProvider = new PropertySplitOrMergeFormProvider()
  val form = formProvider()

  "PropertySplitOrMerge Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(optionChosen)).build()

      running(application) {
        val request = FakeRequest(GET, propertySplitOrMergeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[PropertySplitOrMergeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, routes.deletionOptionsController.onPageLoad(NormalMode).url)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = optionChosen.set(PropertySplitOrMergePage, PropertySplitOrMerge.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, propertySplitOrMergeRoute)

        val view = application.injector.instanceOf[PropertySplitOrMergeView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(PropertySplitOrMerge.values.head), NormalMode, routes.deletionOptionsController.onPageLoad(NormalMode).url)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(optionChosen))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, propertySplitOrMergeRoute)
            .withFormUrlEncodedBody(("value", PropertySplitOrMerge.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(optionChosen)).build()

      running(application) {
        val request =
          FakeRequest(POST, propertySplitOrMergeRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[PropertySplitOrMergeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, routes.deletionOptionsController.onPageLoad(NormalMode).url)(request, messages(application)).toString
      }
    }

    "must drop the details and date when the answer changes, because they are worded for the other choice" in {

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val answered = optionChosen
        .set(PropertySplitOrMergePage, PropertySplitOrMerge.Merged).success.value
        .set(PropertyChangeDetailsPage, "two flats").success.value
        .set(PropertyChangeDatePage, java.time.LocalDate.of(2025, 4, 27)).success.value

      val application = applicationBuilder(userAnswers = Some(answered))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      running(application) {
        val changed = route(application, FakeRequest(POST, propertySplitOrMergeRoute).withFormUrlEncodedBody(("value", PropertySplitOrMerge.Split.toString))).value
        status(changed) mustEqual SEE_OTHER

        val captor = org.mockito.ArgumentCaptor.forClass(classOf[UserAnswers])
        org.mockito.Mockito.verify(mockSessionRepository).set(captor.capture())
        captor.getValue.get(PropertyChangeDetailsPage) mustBe None
        captor.getValue.get(PropertyChangeDatePage) mustBe None
      }
    }

    "must keep the details and date when the same answer is submitted again" in {

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val answered = optionChosen
        .set(PropertySplitOrMergePage, PropertySplitOrMerge.Merged).success.value
        .set(PropertyChangeDetailsPage, "two flats").success.value

      val application = applicationBuilder(userAnswers = Some(answered))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      running(application) {
        val same = route(application, FakeRequest(POST, propertySplitOrMergeRoute).withFormUrlEncodedBody(("value", PropertySplitOrMerge.Merged.toString))).value
        status(same) mustEqual SEE_OTHER

        val captor = org.mockito.ArgumentCaptor.forClass(classOf[UserAnswers])
        org.mockito.Mockito.verify(mockSessionRepository).set(captor.capture())
        captor.getValue.get(PropertyChangeDetailsPage) mustBe Some("two flats")
      }
    }
  }
}
