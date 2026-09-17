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

package controllers

import controllers.actions.SessionIdentifierAction
import models.NormalMode
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.api.Logging
import services.PropertyDetailService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.PropertyDetailView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class PropertyDetailController @Inject()(
  sessionIdentify: SessionIdentifierAction,
  propertyDetailService: PropertyDetailService,
  propertyDetailView: PropertyDetailView,
  val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(propertyId: String): Action[AnyContent] =
      Action.async { implicit request =>
     logger.info(s"PropertyDetailController invoked: $propertyId")
      implicit val hc =
        HeaderCarrierConverter.fromRequestAndSession(
          request,
          request.session
        )

      propertyDetailService.getPropertyDetail(propertyId).map {
        case Left(_) =>
          Redirect(routes.JourneyRecoveryController.onPageLoad())

        case Right(None) =>
          Redirect(routes.JourneyRecoveryController.onPageLoad())

        case Right(Some(viewModel)) =>
          Ok(propertyDetailView(viewModel))
      }
    }
}