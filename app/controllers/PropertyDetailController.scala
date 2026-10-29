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

import controllers.actions.{DataRetrievalAction, SessionIdentifierAction}
import pages.{PropertyAddressPage, PropertyIdPage}
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc.*
import play.api.Logging
import repositories.SessionRepository
import services.PropertyDetailService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.PropertyDetailView
import views.html.errors.ApiErrorView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import java.util.Locale
import scala.concurrent.Future

@Singleton
class PropertyDetailController @Inject()(
                                          propertyDetailService:    PropertyDetailService,
                                          sessionRepository:        SessionRepository,
                                          sessionIdentify:          SessionIdentifierAction,
                                          getData:                  DataRetrievalAction,
                                          propertyDetailView:       PropertyDetailView,
                                          apiErrorView:             ApiErrorView,
                                          val controllerComponents: MessagesControllerComponents
                                        )(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(propertyId: String): Action[AnyContent] =
    (sessionIdentify andThen getData).async { implicit request =>
      val lowerCasePropertyId = propertyId.toLowerCase(Locale.ROOT)
      if (propertyId != lowerCasePropertyId) {
        Future.successful(Redirect(routes.PropertyDetailController.onPageLoad(lowerCasePropertyId)))
      } else {
        implicit val hc =
          HeaderCarrierConverter.fromRequestAndSession(
            request,
            request.session
          )

        implicit val lang: Lang = request.lang

        propertyDetailService.getPropertyDetail(propertyId).flatMap {
          case Left(error) if error.statusCode == NOT_FOUND =>
            Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))

          case Left(error) =>
            logger.error(s"[PropertyDetailController] API error status=${error.statusCode}")
            Future.successful(InternalServerError(apiErrorView(models.ApiError(error.statusCode, error.message))))

          case Right(None) =>
            Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))

          case Right(Some(viewModel)) =>
            val answers = request.userAnswers.getOrElse(models.UserAnswers(request.userId))
            for {
              withAddress <- Future.fromTry(answers.set(PropertyAddressPage, viewModel.address))
              updated     <- Future.fromTry(withAddress.set(PropertyIdPage, viewModel.propertyId))
              _           <- sessionRepository.set(updated)
            } yield Ok(propertyDetailView(viewModel))
        }
      }
    }
}