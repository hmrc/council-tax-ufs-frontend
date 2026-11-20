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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import config.FrontendAppConfig
import models.email.SendEmailRequest
import org.mockito.Mockito.when
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}

import scala.concurrent.ExecutionContext.Implicits.global

class EmailConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar
    with WireMockSupport
    with HttpClientV2Support {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector = {
    val config = mock[FrontendAppConfig]
    when(config.emailService).thenReturn(wireMockUrl)
    new EmailConnector(httpClientV2, config)
  }

  private val request = SendEmailRequest(Seq("payer@example.com"), "council_tax_ufs_enquiry_submitted", Map("name" -> "Jo Bloggs", "reference" -> "HDJ2123F"))

  "sendEmail" - {

    "must post the email request to /hmrc/email and succeed on 202 Accepted" in {
      wireMockServer.stubFor(post(urlEqualTo("/hmrc/email")).willReturn(aResponse().withStatus(ACCEPTED)))

      connector.sendEmail(request).futureValue mustBe Right(())

      wireMockServer.verify(postRequestedFor(urlEqualTo("/hmrc/email")).withRequestBody(equalToJson(
        """{
          |  "to": ["payer@example.com"],
          |  "templateId": "council_tax_ufs_enquiry_submitted",
          |  "parameters": { "name": "Jo Bloggs", "reference": "HDJ2123F" },
          |  "force": false
          |}""".stripMargin)))
    }

    "must return an error for any other status" in {
      wireMockServer.stubFor(post(urlEqualTo("/hmrc/email")).willReturn(aResponse().withStatus(BAD_REQUEST)))

      connector.sendEmail(request).futureValue mustBe Left("Send email failed with status 400")
    }

    "must return an error when the email service cannot be reached" in {
      wireMockServer.stubFor(post(urlEqualTo("/hmrc/email")).willReturn(aResponse().withFault(
        com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER)))

      connector.sendEmail(request).futureValue mustBe Left("Call to email service failed")
    }
  }
}