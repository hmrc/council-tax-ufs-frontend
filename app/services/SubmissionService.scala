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
import pages.SubmissionReferencePage
import play.api.Logging
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.ReferenceGenerator

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionService @Inject()(
  connector: SubmissionConnector,
  referenceGenerator: ReferenceGenerator,
  sessionRepository: SessionRepository
)(implicit ec: ExecutionContext) extends Logging {

  // submitting twice returns the first reference and doesn't send again
  def submit(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[Either[String, String]] =
    answers.get(SubmissionReferencePage) match {
      case Some(existing) => Future.successful(Right(existing))
      case None =>
        val reference = referenceGenerator.generate()
        connector.submit(reference, answers).flatMap {
          case Left(error) =>
            logger.error(s"[SubmissionService][submit] Submission failed for reference $reference: $error")
            Future.successful(Left(error))
          case Right(_) =>
            for {
              updated <- Future.fromTry(answers.set(SubmissionReferencePage, reference))
              _       <- sessionRepository.set(updated)
            } yield Right(reference)
        }
    }
}
