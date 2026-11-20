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

import config.FrontendAppConfig
import connectors.EmailConnector
import models.email.SendEmailRequest
import models.{ContactDetails, CouncilTaxPayerDetails, CouncilTaxResponsible, UserAnswers}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.TryValues
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import pages.{ContactDetailsPage, CouncilTaxPayerDetailsPage, CouncilTaxResponsiblePage}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class EnquiryEmailServiceSpec extends AsyncFreeSpec with Matchers with MockitoSugar with TryValues {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private def contact(email: Option[String]) =
    ContactDetails("Sam", "User", email, None, None, None, None, None, None, Set("email"))

  private def build(result: Future[Either[String, Unit]] = Future.successful(Right(()))) = {
    val connector = mock[EmailConnector]
    val config    = mock[FrontendAppConfig]
    when(config.enquirySubmittedEmailTemplate(false)).thenReturn("template_en")
    when(config.enquirySubmittedEmailTemplate(true)).thenReturn("template_cy")
    when(connector.sendEmail(any())(any())).thenReturn(result)
    (new EnquiryEmailService(connector, config), connector)
  }

  private val payerIsUser = UserAnswers("id")
    .set(CouncilTaxResponsiblePage, CouncilTaxResponsible.Yes).success.value
    .set(ContactDetailsPage, contact(Some("sam@example.com"))).success.value

  private val actingFor = UserAnswers("id")
    .set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value
    .set(ContactDetailsPage, contact(Some("agent@example.com"))).success.value
    .set(CouncilTaxPayerDetailsPage, CouncilTaxPayerDetails("Jo", "Bloggs", "jo@example.com")).success.value

  "sendConfirmation" - {

    "must email the user when they are the Council Tax payer" in {
      val (service, connector) = build()
      service.sendConfirmation(payerIsUser, "HDJ2123F", welsh = false).map { _ =>
        verify(connector).sendEmail(eqTo(SendEmailRequest(
          Seq("sam@example.com"), "template_en", Map("name" -> "Sam User", "reference" -> "HDJ2123F"))))(any())
        succeed
      }
    }

    "must email the Council Tax payer, not the user, when acting for someone" in {
      val (service, connector) = build()
      service.sendConfirmation(actingFor, "HDJ2123F", welsh = false).map { _ =>
        verify(connector).sendEmail(eqTo(SendEmailRequest(
          Seq("jo@example.com"), "template_en", Map("name" -> "Jo Bloggs", "reference" -> "HDJ2123F"))))(any())
        succeed
      }
    }

    "must use the Welsh template when the user is using Welsh" in {
      val (service, connector) = build()
      service.sendConfirmation(payerIsUser, "HDJ2123F", welsh = true).map { _ =>
        verify(connector).sendEmail(eqTo(SendEmailRequest(
          Seq("sam@example.com"), "template_cy", Map("name" -> "Sam User", "reference" -> "HDJ2123F"))))(any())
        succeed
      }
    }

    "must not send anything when the payer gave no email address" in {
      val (service, connector) = build()
      val noEmail = payerIsUser.set(ContactDetailsPage, contact(None)).success.value
      service.sendConfirmation(noEmail, "HDJ2123F", welsh = false).map { _ =>
        verify(connector, never()).sendEmail(any())(any())
        succeed
      }
    }

    "must complete normally when the email service fails" in {
      val (service, _) = build(Future.successful(Left("Send email failed with status 500")))
      service.sendConfirmation(payerIsUser, "HDJ2123F", welsh = false).map(_ mustBe ())
    }

    "must complete normally when the email call throws" in {
      val (service, _) = build(Future.failed(new RuntimeException("boom")))
      service.sendConfirmation(payerIsUser, "HDJ2123F", welsh = false).map(_ mustBe ())
    }
  }
}