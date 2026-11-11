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

import connectors.SubmissionConnector
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.TryValues
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import pages.SubmissionReferencePage
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.ReferenceGenerator

import scala.concurrent.Future

class SubmissionServiceSpec extends AsyncFreeSpec with Matchers with MockitoSugar with TryValues {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private def build(result: Either[String, Unit]) = {
    val connector = mock[SubmissionConnector]
    val generator = mock[ReferenceGenerator]
    val repo      = mock[SessionRepository]
    when(generator.generate()).thenReturn("HDJ2123F")
    when(connector.submit(any(), any())(any())).thenReturn(Future.successful(result))
    when(repo.set(any())).thenReturn(Future.successful(true))
    (new SubmissionService(connector, generator, repo), connector, repo)
  }

  "SubmissionService" - {

    "must send the generated reference and keep it in the session when the submission succeeds" in {
      val (service, connector, repo) = build(Right(()))
      service.submit(UserAnswers("id")).map { result =>
        result mustBe Right("HDJ2123F")
        verify(connector).submit(org.mockito.ArgumentMatchers.eq("HDJ2123F"), any())(any())
        verify(repo).set(any())
        succeed
      }
    }

    "must not keep a reference when the submission fails" in {
      val (service, _, repo) = build(Left("boom"))
      service.submit(UserAnswers("id")).map { result =>
        result mustBe Left("boom")
        verify(repo, never()).set(any())
        succeed
      }
    }

    "must reuse the existing reference and not submit twice" in {
      val (service, connector, _) = build(Right(()))
      val answers = UserAnswers("id").set(SubmissionReferencePage, "ABC2345D").success.value
      service.submit(answers).map { result =>
        result mustBe Right("ABC2345D")
        verify(connector, never()).submit(any(), any())(any())
        succeed
      }
    }
  }
}
