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
import pages.SubmissionReferencePage
import play.api.test.FakeRequest
import play.api.test.Helpers._

class RequestSentControllerSpec extends SpecBase {

  "RequestSent Controller" - {

    "must show the reference number and what happens next" in {
      val answers = emptyUserAnswers.set(SubmissionReferencePage, "HDJ2123FK7BN").success.value
      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.RequestSentController.onPageLoad().url)).value
        val html = contentAsString(result)

        status(result) mustEqual OK
        html must include("Your request has been sent")
        html must include("HDJ2-123F-K7BN")
        html must include("What happens next")
        html must include("govuk-panel--confirmation")
      }
    }

    "must send the user back to Check Your Answers if nothing has been submitted" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.RequestSentController.onPageLoad().url)).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad(models.Journey.Challenge).url
      }
    }

    "must redirect to Journey Recovery if there is no session data" in {
      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val result = route(application, FakeRequest(GET, routes.RequestSentController.onPageLoad().url)).value
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
