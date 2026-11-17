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
import models.{CheckMode, NormalMode}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{PropertyMergedDetailsPage, PropertySplitDetailsPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository

import scala.concurrent.Future

class PropertyChangeDetailsControllerSpec extends SpecBase with MockitoSugar {

  private val onwardRoute = Call("GET", "/foo")
  private val formalReviewUrl = routes.ReviewOverviewController.splitOrMergeFormalReview().url

  "PropertyChangeDetailsController" - {

    "merged" - {
      lazy val pageUrl = routes.PropertyChangeDetailsController.onPageLoadMerged(NormalMode).url

      "must render the merged question with Back to the formal review page" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val result = route(application, FakeRequest(GET, pageUrl)).value

          status(result) mustBe OK
          contentAsString(result) must include("How have your properties been merged?")
          contentAsString(result) must include("For example, two flats have been merged into a single home.")
          contentAsString(result) must include(s"""$formalReviewUrl" class="govuk-back-link"""")
        }
      }

      "must show the saved answer" in {
        val answers = emptyUserAnswers.set(PropertyMergedDetailsPage, "Two flats knocked through").success.value
        val application = applicationBuilder(userAnswers = Some(answers)).build()

        running(application) {
          val result = route(application, FakeRequest(GET, routes.PropertyChangeDetailsController.onPageLoadMerged(CheckMode).url)).value

          status(result) mustBe OK
          contentAsString(result) must include("Two flats knocked through")
        }
      }

      "must save a valid answer and redirect to the next page" in {
        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.set(any())) thenReturn Future.successful(true)
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(sessionRepository)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.PropertyChangeDetailsController.onSubmitMerged(NormalMode).url)
            .withFormUrlEncodedBody("value" -> "Two flats knocked through")
          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe onwardRoute.url
        }
      }

      "must return Bad Request with the required error when empty" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val request = FakeRequest(POST, routes.PropertyChangeDetailsController.onSubmitMerged(NormalMode).url)
            .withFormUrlEncodedBody("value" -> "")
          val result = route(application, request).value

          status(result) mustBe BAD_REQUEST
          contentAsString(result) must include("Enter how your properties have been merged")
        }
      }
    }

    "split" - {
      lazy val pageUrl = routes.PropertyChangeDetailsController.onPageLoadSplit(NormalMode).url

      "must render the split question with Back to the formal review page" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val result = route(application, FakeRequest(GET, pageUrl)).value

          status(result) mustBe OK
          contentAsString(result) must include("How has the property been split?")
          contentAsString(result) must include("For example, your home has been split into two or more flats.")
          contentAsString(result) must include(s"""$formalReviewUrl" class="govuk-back-link"""")
        }
      }

      "must show the saved answer" in {
        val answers = emptyUserAnswers.set(PropertySplitDetailsPage, "House split into two flats").success.value
        val application = applicationBuilder(userAnswers = Some(answers)).build()

        running(application) {
          val result = route(application, FakeRequest(GET, routes.PropertyChangeDetailsController.onPageLoadSplit(CheckMode).url)).value

          status(result) mustBe OK
          contentAsString(result) must include("House split into two flats")
        }
      }

      "must return Bad Request with the required error when empty" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val request = FakeRequest(POST, routes.PropertyChangeDetailsController.onSubmitSplit(NormalMode).url)
            .withFormUrlEncodedBody("value" -> "")
          val result = route(application, request).value

          status(result) mustBe BAD_REQUEST
          contentAsString(result) must include("Enter how the property has been split")
        }
      }
    }

    "new address" - {

      "must render the question with Back to the completion date for the chosen option" in {
        val answers = emptyUserAnswers.set(pages.PropertySplitOrMergePage, models.PropertySplitOrMerge.Split).success.value
        val application = applicationBuilder(userAnswers = Some(answers)).build()

        running(application) {
          val result = route(application, FakeRequest(GET, routes.PropertyChangeDetailsController.onPageLoadNewAddress(NormalMode).url)).value

          status(result) mustBe OK
          contentAsString(result) must include("Has the address of your home changed?")
          contentAsString(result) must include("Provide details of any new addresses below.")
          contentAsString(result) must include(s"""${routes.PropertyChangeDateController.onPageLoadSplit(NormalMode).url}" class="govuk-back-link""")
        }
      }

      "must return Bad Request with the required error when empty" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val request = FakeRequest(POST, routes.PropertyChangeDetailsController.onSubmitNewAddress(NormalMode).url)
            .withFormUrlEncodedBody("value" -> "")
          val result = route(application, request).value

          status(result) mustBe BAD_REQUEST
          contentAsString(result) must include("Enter details of any new addresses")
        }
      }
    }
  }
}
