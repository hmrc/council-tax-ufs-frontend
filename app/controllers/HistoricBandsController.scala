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

import play.api.Logging
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc.*
import services.PropertyDetailService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.HistoricBandFullView
import views.html.HistoricBandsView
import views.html.errors.ApiErrorView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class HistoricBandsController @Inject()(
  propertyDetailService: PropertyDetailService,
  historicBandsView: HistoricBandsView,
  historicBandFullView: HistoricBandFullView,
  apiErrorView: ApiErrorView,
  val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(propertyId: String): Action[AnyContent] =
    Action.async { implicit request =>
      implicit val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
      implicit val lang: Lang = request.lang

      propertyDetailService.getHistoricBands(propertyId).map {
        case Left(error) if error.statusCode == NOT_FOUND =>
          Redirect(routes.JourneyRecoveryController.onPageLoad())

        case Left(error) =>
          logger.error(s"[HistoricBandsController] API error status=${error.statusCode}")
          InternalServerError(apiErrorView(models.ApiError(error.statusCode, error.message)))

        case Right(None) =>
          Redirect(routes.JourneyRecoveryController.onPageLoad())

        case Right(Some(viewModel)) =>
          Ok(historicBandsView(viewModel))
      }
    }

  def onBandPageLoad(propertyId: String, bandIndex: Int): Action[AnyContent] =
    Action.async { implicit request =>
      implicit val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
      implicit val lang: Lang = request.lang

      propertyDetailService.getHistoricBand(propertyId, bandIndex).map {
        case Left(error) if error.statusCode == NOT_FOUND =>
          Redirect(routes.JourneyRecoveryController.onPageLoad())

        case Left(error) =>
          logger.error(s"[HistoricBandsController] API error status=${error.statusCode}")
          InternalServerError(apiErrorView(models.ApiError(error.statusCode, error.message)))

        case Right(None) =>
          Redirect(routes.JourneyRecoveryController.onPageLoad())

        case Right(Some(viewModel)) =>
          Ok(historicBandFullView(viewModel))
      }
    }
}
