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
import models.{NormalMode, UploadedEvidence}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.UploadEvidencePage
import play.api.inject.bind
import play.api.libs.Files.SingletonTemporaryFileCreator
import play.api.mvc.{Call, MultipartFormData}
import play.api.mvc.MultipartFormData.FilePart
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import java.nio.file.Files
import scala.concurrent.Future

class UploadEvidenceControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")
  lazy val uploadRoute = routes.UploadEvidenceController.onPageLoad(NormalMode).url

  private def multipart(fileName: String, contentType: String, bytes: Array[Byte]) = {
    val temp = SingletonTemporaryFileCreator.create("evidence", ".tmp")
    Files.write(temp.path, bytes)
    MultipartFormData(Map.empty[String, Seq[String]], Seq(FilePart("file", fileName, Some(contentType), temp)), Nil)
  }

  private def build(repo: SessionRepository) =
    applicationBuilder(userAnswers = Some(emptyUserAnswers))
      .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)), bind[SessionRepository].toInstance(repo))
      .build()

  private def withAction(action: String, file: MultipartFormData[play.api.libs.Files.TemporaryFile]) =
    file.copy(dataParts = Map("action" -> Seq(action)))

  private def answersWith(files: UploadedEvidence*) =
    emptyUserAnswers.set(UploadEvidencePage, files.toSeq).success.value

  private def buildWith(repo: SessionRepository, answers: models.UserAnswers = emptyUserAnswers) =
    applicationBuilder(userAnswers = Some(answers))
      .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)), bind[SessionRepository].toInstance(repo))
      .build()

  private def saved(repo: SessionRepository): Seq[UploadedEvidence] = {
    val captor = ArgumentCaptor.forClass(classOf[models.UserAnswers])
    verify(repo).set(captor.capture())
    captor.getValue.get(UploadEvidencePage).value
  }

  "UploadEvidence Controller" - {

    "must return OK with the upload form for a GET" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      running(application) {
        val html = contentAsString(route(application, FakeRequest(GET, uploadRoute)).value)
        html must include("Upload evidence")
        html must include("multipart/form-data")
        html must include("""type="file"""")
        html must include("""data-module="govuk-file-upload"""")
        html must include("govuk-file-upload-wrapper")
        html must not include "uploaded-files-heading"
      }
    }

    "must list uploaded files with remove links and a count" in {
      val file = UploadedEvidence("id-1", "bill.pdf", "application/pdf", 2048)
      val application = applicationBuilder(userAnswers = Some(answersWith(file))).build()
      running(application) {
        val html = contentAsString(route(application, FakeRequest(GET, uploadRoute)).value)
        html must include("You have uploaded 1 of 5 files")
        html must include("bill.pdf")
        html must include("2 KB")
        html must include(routes.RemoveEvidenceController.onPageLoad("id-1", NormalMode).url)
        html must include("Upload another file")
      }
    }

    "must show a success banner after an upload" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      running(application) {
        val html = contentAsString(route(application, FakeRequest(GET, uploadRoute).withFlash("uploaded" -> "bill.pdf")).value)
        html must include("govuk-notification-banner--success")
        html must include("bill.pdf has been uploaded")
      }
    }

    "must hide the file input once the maximum number of files is reached" in {
      val files = (1 to 5).map(i => UploadedEvidence(s"id-$i", s"f$i.pdf", "application/pdf", 1))
      val application = applicationBuilder(userAnswers = Some(answersWith(files*))).build()
      running(application) {
        val html = contentAsString(route(application, FakeRequest(GET, uploadRoute)).value)
        html must include("You have uploaded the maximum of 5 files")
        html must not include """type="file""""
      }
    }

    "must add the file and return to the upload page when uploading one at a time" in {
      val repo = mock[SessionRepository]
      when(repo.set(any())) thenReturn Future.successful(true)
      val application = buildWith(repo, answersWith(UploadedEvidence("id-1", "a.pdf", "application/pdf", 1)))

      running(application) {
        val body = withAction("upload", multipart("b.png", "image/png", Array.fill(100)(1.toByte)))
        val result = route(application, FakeRequest(POST, uploadRoute).withMultipartFormDataBody(body)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.UploadEvidenceController.onPageLoad(NormalMode).url
        flash(result).get("uploaded") mustBe Some("b.png")
        saved(repo).map(_.fileName) mustBe Seq("a.pdf", "b.png")
      }
    }

    "must add the chosen file and move on when continuing" in {
      val repo = mock[SessionRepository]
      when(repo.set(any())) thenReturn Future.successful(true)
      val application = buildWith(repo)

      running(application) {
        val body = withAction("continue", multipart("proof.pdf", "application/pdf", Array.fill(100)(1.toByte)))
        val result = route(application, FakeRequest(POST, uploadRoute).withMultipartFormDataBody(body)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
        saved(repo).map(_.fileName) mustBe Seq("proof.pdf")
      }
    }

    "must continue without a new file when files are already uploaded" in {
      val repo = mock[SessionRepository]
      val application = buildWith(repo, answersWith(UploadedEvidence("id-1", "a.pdf", "application/pdf", 1)))

      running(application) {
        val empty = withAction("continue", MultipartFormData[play.api.libs.Files.TemporaryFile](Map.empty, Nil, Nil))
        val result = route(application, FakeRequest(POST, uploadRoute).withMultipartFormDataBody(empty)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
        verify(repo, never()).set(any())
      }
    }

    "must require a file when continuing with none uploaded" in {
      val repo = mock[SessionRepository]
      val application = buildWith(repo)

      running(application) {
        val empty = withAction("continue", MultipartFormData[play.api.libs.Files.TemporaryFile](Map.empty, Nil, Nil))
        val result = route(application, FakeRequest(POST, uploadRoute).withMultipartFormDataBody(empty)).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) must include("Select a file to upload")
        verify(repo, never()).set(any())
      }
    }

    "must show an error and keep the uploaded list when the upload is rejected" in {
      val cases = Seq(
        ("virus.exe", "application/octet-stream", Array[Byte](1), "must be a JPG, PNG or PDF", Seq.empty[UploadedEvidence]),
        ("a.pdf", "application/pdf", Array[Byte](1), "already uploaded a file with this name",
          Seq(UploadedEvidence("id-1", "A.PDF", "application/pdf", 1))),
        ("big.png", "image/png", Array.fill((UploadedEvidence.maxSizeInBytes + 1).toInt)(1.toByte), "must be smaller than 10MB", Seq.empty[UploadedEvidence]),
        ("six.pdf", "application/pdf", Array[Byte](1), "only upload up to 5 files",
          (1 to 5).map(i => UploadedEvidence(s"id-$i", s"f$i.pdf", "application/pdf", 1)))
      )

      cases.foreach { case (name, contentType, bytes, expected, existing) =>
        val repo = mock[SessionRepository]
        val application = buildWith(repo, answersWith(existing*))

        running(application) {
          val body = withAction("upload", multipart(name, contentType, bytes))
          val result = route(application, FakeRequest(POST, uploadRoute).withMultipartFormDataBody(body)).value

          status(result) mustEqual BAD_REQUEST
          val html = contentAsString(result)
          html must include(expected)
          html must include("There is a problem")
          if (existing.size < UploadedEvidence.maxFiles) html must include("govuk-file-upload--error")
          existing.foreach(file => html must include(file.fileName))
          verify(repo, never()).set(any())
        }
      }
    }
  }
}
