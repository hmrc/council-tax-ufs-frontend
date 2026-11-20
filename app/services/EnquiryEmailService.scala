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
import models.{CouncilTaxResponsible, UserAnswers}
import pages.{ContactDetailsPage, CouncilTaxPayerDetailsPage, CouncilTaxResponsiblePage}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/**
 * Confirmation email to the Council Tax payer once an enquiry has been submitted.
 * The payer is the person named on the payer details page when the user is acting for someone,
 * otherwise the user themselves (contact details). No email address, no email.
 * Never fails: the enquiry is already submitted, so a problem here is only logged.
 */
@Singleton
class EnquiryEmailService @Inject()(
  emailConnector: EmailConnector,
  appConfig: FrontendAppConfig
)(implicit ec: ExecutionContext) extends Logging {

  def sendConfirmation(answers: UserAnswers, reference: String, welsh: Boolean)(implicit hc: HeaderCarrier): Future[Unit] =
    payer(answers) match {
      case None =>
        logger.info(s"[EnquiryEmailService][sendConfirmation] No payer email address for $reference, email not sent")
        Future.unit
      case Some((email, name)) =>
        val request = SendEmailRequest(
          to         = Seq(email),
          templateId = appConfig.enquirySubmittedEmailTemplate(welsh),
          parameters = Map("name" -> name, "reference" -> reference)
        )
        emailConnector.sendEmail(request).map {
          case Right(_)    => logger.info(s"[EnquiryEmailService][sendConfirmation] Confirmation email requested for $reference")
          case Left(error) => logger.error(s"[EnquiryEmailService][sendConfirmation] Email not sent for $reference: $error")
        }.recover {
          case NonFatal(ex) => logger.error(s"[EnquiryEmailService][sendConfirmation] Email not sent for $reference: ${ex.getClass.getSimpleName}")
        }
    }

  // same rule as the submission payload: payer details only count when acting for someone
  private def payer(answers: UserAnswers): Option[(String, String)] =
    if (answers.get(CouncilTaxResponsiblePage).contains(CouncilTaxResponsible.ActingFor))
      answers.get(CouncilTaxPayerDetailsPage).map(p => (p.email.trim, s"${p.firstName} ${p.lastName}".trim))
        .filter(_._1.nonEmpty)
    else
      answers.get(ContactDetailsPage).flatMap(c =>
        c.email.map(_.trim).filter(_.nonEmpty).map(email => (email, s"${c.firstName} ${c.lastName}".trim)))
}