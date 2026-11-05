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
import models.{NormalMode, UploadedEvidence, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.UploadEvidencePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class RemoveEvidenceControllerSpec extends SpecBase with MockitoSugar {

  private val fileA = UploadedEvidence("id-a", "a.pdf", "application/pdf", 1)
  private val fileB = UploadedEvidence("id-b", "b.png", "image/png", 1)
  private val answers: UserAnswers = emptyUserAnswers.set(UploadEvidencePage, Seq(fileA, fileB)).success.value

  private def url(id: String) = routes.RemoveEvidenceController.onPageLoad(id, NormalMode).url
  private def uploadUrl = routes.UploadEvidenceController.onPageLoad(NormalMode).url

  "RemoveEvidence Controller" - {

    "must ask for confirmation naming the file" in {
      val application = applicationBuilder(userAnswers = Some(answers)).build()
      running(application) {
        val result = route(application, FakeRequest(GET, url("id-a"))).value
        status(result) mustEqual OK
        contentAsString(result) must include("Are you sure you want to remove a.pdf?")
      }
    }

    "must return to the upload page for an unknown file" in {
      val application = applicationBuilder(userAnswers = Some(answers)).build()
      running(application) {
        val get = route(application, FakeRequest(GET, url("nope"))).value
        redirectLocation(get).value mustEqual uploadUrl
        val post = route(application, FakeRequest(POST, url("nope")).withFormUrlEncodedBody("value" -> "true")).value
        redirectLocation(post).value mustEqual uploadUrl
      }
    }

    "must remove only the chosen file on yes" in {
      val repo = mock[SessionRepository]
      when(repo.set(any())) thenReturn Future.successful(true)
      val application = applicationBuilder(userAnswers = Some(answers)).overrides(bind[SessionRepository].toInstance(repo)).build()

      running(application) {
        val result = route(application, FakeRequest(POST, url("id-a")).withFormUrlEncodedBody("value" -> "true")).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual uploadUrl

        val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(repo).set(captor.capture())
        captor.getValue.get(UploadEvidencePage).value mustBe Seq(fileB)
      }
    }

    "must keep the file on no" in {
      val repo = mock[SessionRepository]
      val application = applicationBuilder(userAnswers = Some(answers)).overrides(bind[SessionRepository].toInstance(repo)).build()

      running(application) {
        val result = route(application, FakeRequest(POST, url("id-a")).withFormUrlEncodedBody("value" -> "false")).value
        redirectLocation(result).value mustEqual uploadUrl
        verify(repo, never()).set(any())
      }
    }

    "must return a Bad Request when no answer is chosen" in {
      val application = applicationBuilder(userAnswers = Some(answers)).build()
      running(application) {
        val result = route(application, FakeRequest(POST, url("id-a")).withFormUrlEncodedBody("value" -> "")).value
        status(result) mustEqual BAD_REQUEST
        contentAsString(result) must include("Select yes if you want to remove this file")
      }
    }
  }
}
