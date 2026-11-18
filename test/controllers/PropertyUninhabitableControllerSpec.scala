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
import forms.PropertyUninhabitableFormProvider
import models.{NormalMode, PropertyUninhabitable, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import pages.PropertyUninhabitablePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.PropertyUninhabitableView

import scala.concurrent.Future

class PropertyUninhabitableControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val propertyUninhabitableRoute =
    routes.PropertyUninhabitableController.onPageLoad(NormalMode).url

  private val backLinkUrl =
    "/council-tax-ufs/deletion-options"

  private val journeyAnswers =
    emptyUserAnswers
      .set(deletionOptionsPage, models.deletionOptions.Demolished)
      .success
      .value

  val formProvider = new PropertyUninhabitableFormProvider()
  val form = formProvider()


  "PropertyUninhabitable Controller" - {

    "must return OK and the correct view for a GET" in {

      val application =
        applicationBuilder(userAnswers = Some(journeyAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, propertyUninhabitableRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[PropertyUninhabitableView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(
            form,
            NormalMode,
            backLinkUrl
          )(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId)
          .set(deletionOptionsPage, models.deletionOptions.Demolished).success.value
          .set(PropertyUninhabitablePage, PropertyUninhabitable.values.head).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, propertyUninhabitableRoute)

        val view = application.injector.instanceOf[PropertyUninhabitableView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(
            form.fill(PropertyUninhabitable.values.head),
            NormalMode,
            backLinkUrl
          )(request, messages(application)).toString
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(journeyAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, propertyUninhabitableRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[PropertyUninhabitableView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual
          view(
            boundForm,
            NormalMode,
            backLinkUrl
          )(request, messages(application)).toString
      }
    }
  }
}
