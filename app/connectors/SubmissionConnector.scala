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

import models.UserAnswers
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

/**
 * Sends the completed review request to bridge-integration.
 *
 * TODO: STUB. bridge-integration hasn't given us the submit endpoint yet. Until it does this logs and reports
 * success without sending anything, so it must not reach a real environment. Replace it with an HttpClientV2
 * POST of the reference and answers, returning Left for any non-success response, including a duplicate reference.
 */
@Singleton
class SubmissionConnector @Inject()() extends Logging {

  def submit(reference: String, answers: UserAnswers)(implicit hc: HeaderCarrier): Future[Either[String, Unit]] = {
    logger.warn(s"[SubmissionConnector][submit] STUB - nothing sent for reference $reference")
    Future.successful(Right(()))
  }
}
