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

import connectors.BridgeIntegrationConnector
import models.{LatestListEntry, UserAnswers}
import models.submission.{Communications, SubmissionPayload}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.TryValues
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import pages.{PropertyIdPage, SubmissionReferencePage}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import services.submission.SubmissionPayloadBuilder
import org.mockito.ArgumentMatchers.{eq => eqTo}
import utils.ReferenceGenerator

import scala.concurrent.Future

class SubmissionServiceSpec extends AsyncFreeSpec with Matchers with MockitoSugar with TryValues {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private def payload(reference: String) =
    SubmissionPayload(reference, "20261008T000000Z", Nil, Communications("A", "B", None, None, None, None, None, None, None), Nil, Nil)

  private val answers = UserAnswers("id").set(PropertyIdPage, "PROP-1").success.value

  private def emailService(): EnquiryEmailService = {
    val email = mock[EnquiryEmailService]
    when(email.sendConfirmation(any(), any(), any())(any())).thenReturn(Future.unit)
    email
  }

  private def build(
    result: Either[String, Unit],
    listEntry: Either[String, LatestListEntry] = Right(LatestListEntry("LATEST-ENTRY", "CVE")),
    email: EnquiryEmailService = emailService()
  ) = {
    val details   = mock[PropertyDetailService]
    val connector = mock[BridgeIntegrationConnector]
    val builder   = mock[SubmissionPayloadBuilder]
    val generator = mock[ReferenceGenerator]
    val repo      = mock[SessionRepository]
    when(generator.generate()).thenReturn("HDJ2123F")
    when(builder.build(any(), any())).thenAnswer(inv => Right(payload(inv.getArgument[String](0))))
    when(details.getLatestListEntry(eqTo("PROP-1"))(any())).thenReturn(Future.successful(listEntry))
    when(connector.submitEnquiry(any(), any(), any())(any())).thenReturn(Future.successful(result))
    when(repo.set(any())).thenReturn(Future.successful(true))
    (new SubmissionService(connector, details, builder, generator, repo, mock[config.FrontendAppConfig], email), connector, repo)
  }

  "SubmissionService" - {

    "must send the generated reference and keep it in the session when the submission succeeds" in {
      val (service, connector, repo) = build(Right(()))
      service.submit(answers).map { result =>
        result mustBe Right("HDJ2123F")
        verify(connector).submitEnquiry(eqTo("LATEST-ENTRY"), eqTo("CVE"), eqTo(payload("HDJ2123F")))(any())
        verify(repo).set(any())
        succeed
      }
    }

    "must not keep a reference when the submission fails" in {
      val (service, _, repo) = build(Left("boom"))
      service.submit(answers).map { result =>
        result mustBe Left("boom")
        verify(repo, never()).set(any())
        succeed
      }
    }

    "must not submit when the latest list entry cannot be found" in {
      val (service, connector, repo) = build(Right(()), Left("No list entry id"))
      service.submit(answers).map { result =>
        result mustBe Left("No list entry id")
        verify(connector, never()).submitEnquiry(any(), any(), any())(any())
        verify(repo, never()).set(any())
        succeed
      }
    }

    "must fail gracefully when no property is in the session" in {
      val (service, connector, _) = build(Right(()))
      service.submit(UserAnswers("id")).map { result =>
        result.isLeft mustBe true
        verify(connector, never()).submitEnquiry(any(), any(), any())(any())
        succeed
      }
    }

    "must not submit when the payload cannot be built" in {
      val connector = mock[BridgeIntegrationConnector]
      val builder   = mock[SubmissionPayloadBuilder]
      val generator = mock[ReferenceGenerator]
      val repo      = mock[SessionRepository]
      val details   = mock[PropertyDetailService]
      when(generator.generate()).thenReturn("HDJ2123F")
      when(builder.build(any(), any())).thenReturn(Left(Seq("No answer for P01")))
      new SubmissionService(connector, details, builder, generator, repo, mock[config.FrontendAppConfig], emailService()).submit(answers).map { result =>
        result mustBe Left("No answer for P01")
        verify(connector, never()).submitEnquiry(any(), any(), any())(any())
        verify(repo, never()).set(any())
        succeed
      }
    }

    "must send the confirmation email once the submission succeeds, in the user's language" in {
      val email = emailService()
      val (service, _, _) = build(Right(()), email = email)
      service.submit(answers, welsh = true).map { _ =>
        verify(email).sendConfirmation(eqTo(answers), eqTo("HDJ2123F"), eqTo(true))(any())
        succeed
      }
    }

    "must not send the confirmation email when the submission fails" in {
      val email = emailService()
      val (service, _, _) = build(Left("boom"), email = email)
      service.submit(answers).map { _ =>
        verify(email, never()).sendConfirmation(any(), any(), any())(any())
        succeed
      }
    }

    "must not send a second email when the enquiry was already submitted" in {
      val email = emailService()
      val (service, _, _) = build(Right(()), email = email)
      val submitted = UserAnswers("id").set(SubmissionReferencePage, "ABC2345D").success.value
      service.submit(submitted).map { _ =>
        verify(email, never()).sendConfirmation(any(), any(), any())(any())
        succeed
      }
    }

    "must reuse the existing reference and not submit twice" in {
      val (service, connector, _) = build(Right(()))
      val answers = UserAnswers("id").set(SubmissionReferencePage, "ABC2345D").success.value
      service.submit(answers).map { result =>
        result mustBe Right("ABC2345D")
        verify(connector, never()).submitEnquiry(any(), any(), any())(any())
        succeed
      }
    }
  }
}