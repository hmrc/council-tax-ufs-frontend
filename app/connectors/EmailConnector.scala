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

import config.FrontendAppConfig
import models.email.SendEmailRequest
import play.api.Logging
import play.api.http.Status.ACCEPTED
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.net.URI
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class EmailConnector @Inject()(
  http: HttpClientV2,
  appConfig: FrontendAppConfig
)(implicit ec: ExecutionContext) extends Logging {

  // the email service answers 202 Accepted once it has queued the email; any other status means it was not sent
  def sendEmail(request: SendEmailRequest)(implicit hc: HeaderCarrier): Future[Either[String, Unit]] =
    http.post(new URI(s"${appConfig.emailService}/hmrc/email").toURL)
      .withBody(Json.toJson(request))
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case ACCEPTED => Right(())
          case status =>
            logger.error(s"[EmailConnector][sendEmail] Unexpected status=$status for template ${request.templateId}")
            Left(s"Send email failed with status $status")
        }
      }
      .recover {
        case NonFatal(ex) =>
          logger.error(s"[EmailConnector][sendEmail] Request failed: ${ex.getClass.getSimpleName}")
          Left("Call to email service failed")
      }
}