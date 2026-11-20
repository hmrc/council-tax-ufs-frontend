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
import connectors.BridgeIntegrationConnector
import models.UserAnswers
import pages.{PropertyIdPage, SubmissionReferencePage}
import play.api.Logging
import play.api.libs.json.Json
import repositories.SessionRepository
import services.submission.SubmissionPayloadBuilder
import uk.gov.hmrc.http.HeaderCarrier
import utils.ReferenceGenerator

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class SubmissionService @Inject()(
  connector: BridgeIntegrationConnector,
  propertyDetailService: PropertyDetailService,
  payloadBuilder: SubmissionPayloadBuilder,
  referenceGenerator: ReferenceGenerator,
  sessionRepository: SessionRepository,
  appConfig: FrontendAppConfig,
  enquiryEmailService: EnquiryEmailService
)(implicit ec: ExecutionContext) extends Logging {

  // submitting twice returns the first reference and doesn't send again (so no second email either)
  def submit(answers: UserAnswers, welsh: Boolean = false)(implicit hc: HeaderCarrier): Future[Either[String, String]] =
    answers.get(SubmissionReferencePage) match {
      case Some(existing) => Future.successful(Right(existing))
      case None =>
        val reference = referenceGenerator.generate()
        payloadBuilder.build(reference, answers) match {
          case Left(errors) =>
            logger.error(s"[SubmissionService][submit] Could not build the submission for reference $reference: ${errors.mkString("; ")}")
            Future.successful(Left(errors.mkString("; ")))
          case Right(payload) =>
            // logged before anything else can fail, so the JSON is visible even if the lookup or the post goes wrong
            if (appConfig.logSubmissionPayload)
              logger.warn(s"[SubmissionService][submit] Payload for $reference: ${Json.prettyPrint(Json.toJson(payload))}")
            send(answers, reference, payload).flatMap {
              case Left(error) =>
                logger.error(s"[SubmissionService][submit] Submission failed for reference $reference: $error")
                Future.successful(Left(error))
              case Right(_) =>
                for {
                  updated <- Future.fromTry(answers.set(SubmissionReferencePage, reference))
                  _       <- sessionRepository.set(updated)
                  _       <- enquiryEmailService.sendConfirmation(answers, reference, welsh)
                } yield Right(reference)
            }.recover {
              case NonFatal(ex) =>
                logger.error(s"[SubmissionService][submit] Unexpected failure for reference $reference: ${ex.getClass.getSimpleName}")
                Left("Submission failed unexpectedly")
            }
        }
    }

  // the enquiry goes against the latest list entry of the property, looked up fresh so a historic band is never used
  private def send(answers: UserAnswers, reference: String, payload: models.submission.SubmissionPayload)
                  (implicit hc: HeaderCarrier): Future[Either[String, Unit]] =
    answers.get(PropertyIdPage) match {
      case None => Future.successful(Left("No property selected for the submission"))
      case Some(propertyId) =>
        propertyDetailService.getLatestListEntry(propertyId).flatMap {
          case Left(error)        => Future.successful(Left(error))
          case Right(entry)       => connector.submitEnquiry(entry.listEntryId, entry.listType, payload)
        }
    }
}