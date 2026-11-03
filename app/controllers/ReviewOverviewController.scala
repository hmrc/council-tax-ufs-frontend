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

import config.FrontendAppConfig
import controllers.actions.{DataRetrievalAction, SessionIdentifierAction}
import models.{NormalMode, ReviewType, noticeAlteration, scenarioResponsible}
import pages.{noticeAlterationPage, scenarioResponsiblePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import navigation.Navigator
import views.html.ReviewOverviewView

import javax.inject.{Inject, Singleton}

@Singleton
class ReviewOverviewController @Inject()(
  override val messagesApi: MessagesApi,
  appConfig: FrontendAppConfig,
  reviewOverviewView: ReviewOverviewView,
  val controllerComponents: MessagesControllerComponents,
  sessionIdentify: SessionIdentifierAction,
  getData: DataRetrievalAction,
  navigator: Navigator
) extends FrontendBaseController with I18nSupport {

  def formalReview(): Action[AnyContent] = show(ReviewType.Formal)

  def informalReview(): Action[AnyContent] = show(ReviewType.Informal)

  def continueToLogin(reviewType: String): Action[AnyContent] = (sessionIdentify andThen getData) { implicit request =>
    val continueCall = reviewType match {
      case "formal" =>
        val noticeBasedFormalReview = request.userAnswers.exists { answers =>
          answers.get(scenarioResponsiblePage).contains(scenarioResponsible.Morethan) &&
          answers.get(noticeAlterationPage).contains(noticeAlteration.Yes)
        }

        if (noticeBasedFormalReview) {
          Some(routes.WhyBandIsWrongController.onPageLoad(NormalMode))
        } else {
          Some(routes.CouncilTaxLiabilityStartDateController.onPageLoad(NormalMode))
        }
      case "informal" => Some(routes.WhyBandIsWrongController.onPageLoad(NormalMode))
      case _          => None
    }

    continueCall match {
      // The target page uses AuthenticatedIdentifierAction, which sends users who aren't signed in to login
      // (carrying ?k=<sessionId>) and lets signed-in users straight through.
      case Some(call) => Redirect(call)
      case None       => BadRequest
    }
  }

  private def show(reviewType: ReviewType): Action[AnyContent] = (sessionIdentify andThen getData) { implicit request =>
    Ok(reviewOverviewView(reviewType, navigator.reviewOverviewBackLink(reviewType, request.userAnswers).url))
  }

}