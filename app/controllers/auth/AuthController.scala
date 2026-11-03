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

package controllers.auth

import config.FrontendAppConfig
import controllers.actions.SessionIdentifierAction
import models.requests.IdentifierRequest
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthController @Inject()(
                                val controllerComponents: MessagesControllerComponents,
                                config: FrontendAppConfig,
                                sessionRepository: SessionRepository,
                                identify: SessionIdentifierAction
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def signOut(): Action[AnyContent] = identify.async {
    implicit request =>
      clearAnswers.map {
        _ =>
          Redirect(config.signOutUrl, Map("continue" -> Seq(config.exitSurveyUrl)))
      }
  }

  def signOutNoSurvey(): Action[AnyContent] = identify.async {
    implicit request =>
      clearAnswers.map {
        _ =>
          Redirect(config.signOutUrl, Map("continue" -> Seq(routes.SignedOutController.onPageLoad().url)))
      }
  }

  // Clears the signed-in (internalId) record and any leftover sessionId record.
  private def clearAnswers(implicit request: IdentifierRequest[_]): Future[Unit] =
    Future.sequence(
      (request.userId +: request.session.get(SessionKeys.sessionId).toSeq).distinct.map(sessionRepository.clear)
    ).map(_ => ())
}