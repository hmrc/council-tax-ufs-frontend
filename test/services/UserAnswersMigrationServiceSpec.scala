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

package services

import base.SpecBase
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import repositories.SessionRepository
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import utils.UrlEncryptor

import scala.concurrent.{ExecutionContext, Future}

class UserAnswersMigrationServiceSpec extends SpecBase with MockitoSugar {

  private given ExecutionContext = ExecutionContext.global

  private def setup() = {
    val repo      = mock[SessionRepository]
    val encryptor = mock[UrlEncryptor]
    when(repo.set(any())).thenReturn(Future.successful(true))
    when(repo.clear(any())).thenReturn(Future.successful(true))
    (repo, encryptor, new UserAnswersMigrationService(repo, encryptor))
  }

  "UserAnswersMigrationService" - {

    "must expose the pre-login session query parameter name" in {
      val (_, _, service) = setup()
      service.preLoginSessionParam mustBe "k"
    }

    "encryptSessionId must delegate to the url encryptor" in {
      val (_, encryptor, service) = setup()
      when(encryptor.encrypt("session-1")).thenReturn("enc")

      service.encryptSessionId("session-1") mustBe "enc"
    }

    "preLoginSessionIds" - {

      "must return the decrypted k param followed by the current session id" in {
        val (_, encryptor, service) = setup()
        when(encryptor.decrypt("enc")).thenReturn("old-session")
        val hc = HeaderCarrier(sessionId = Some(SessionId("current")))

        service.preLoginSessionIds(FakeRequest("GET", "/?k=enc"))(using hc) mustBe Seq("old-session", "current")
      }

      "must ignore a k param that cannot be decrypted" in {
        val (_, encryptor, service) = setup()
        when(encryptor.decrypt("bad")).thenThrow(new RuntimeException("boom"))
        val hc = HeaderCarrier(sessionId = Some(SessionId("current")))

        service.preLoginSessionIds(FakeRequest("GET", "/?k=bad"))(using hc) mustBe Seq("current")
      }

      "must return only the session id when there is no k param" in {
        val (_, _, service) = setup()

        service.preLoginSessionIds(FakeRequest("GET", "/"))(using HeaderCarrier(sessionId = Some(SessionId("current")))) mustBe Seq("current")
      }

      "must return nothing when there is neither a k param nor a session id" in {
        val (_, _, service) = setup()

        service.preLoginSessionIds(FakeRequest("GET", "/"))(using HeaderCarrier()) mustBe empty
      }
    }

    "migrate" - {

      "must do nothing when the source id equals the target id or the list is empty" in {
        val (repo, _, service) = setup()

        service.migrate(Seq("user"), "user").futureValue
        service.migrate(Seq.empty, "user").futureValue

        verifyNoInteractions(repo)
      }

      "must do nothing when the session has no stored answers" in {
        val (repo, _, service) = setup()
        when(repo.get("session")).thenReturn(Future.successful(None))

        service.migrate(Seq("session"), "user").futureValue

        verify(repo, never()).set(any())
        verify(repo, never()).clear(any())
      }

      "must move session answers to the user when the user has none, then clear the session" in {
        val (repo, _, service) = setup()
        val sessionAnswers = UserAnswers("session", Json.obj("a" -> 1))
        when(repo.get("session")).thenReturn(Future.successful(Some(sessionAnswers)))
        when(repo.get("user")).thenReturn(Future.successful(None))

        service.migrate(Seq("session"), "user").futureValue

        val captor = org.mockito.ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(repo).set(captor.capture())
        captor.getValue.id mustBe "user"
        captor.getValue.data mustBe Json.obj("a" -> 1)
        verify(repo).clear("session")
      }

      "must merge into existing user answers with the pre-login answers winning" in {
        val (repo, _, service) = setup()
        when(repo.get("session")).thenReturn(Future.successful(Some(UserAnswers("session", Json.obj("a" -> "new", "c" -> 3)))))
        when(repo.get("user")).thenReturn(Future.successful(Some(UserAnswers("user", Json.obj("a" -> "old", "b" -> 2)))))

        service.migrate(Seq("session"), "user").futureValue

        val captor = org.mockito.ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(repo).set(captor.capture())
        captor.getValue.id mustBe "user"
        captor.getValue.data mustBe Json.obj("a" -> "new", "b" -> 2, "c" -> 3)
        verify(repo).clear("session")
      }

      "must migrate each distinct source id once" in {
        val (repo, _, service) = setup()
        when(repo.get(any())).thenReturn(Future.successful(None))

        service.migrate(Seq("s1", "s1", "s2", "user"), "user").futureValue

        verify(repo, times(1)).get("s1")
        verify(repo, times(1)).get("s2")
        verify(repo, never()).get("user")
      }
    }
  }
}
