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

package navigation

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import controllers.routes
import pages._
import models._

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {
    case CouncilTaxResponsiblePage       => councilTaxResponsibleRoutes
    case RequestCouncilTaxBandReviewPage => _ => routes.ReasonForReviewController.onPageLoad(NormalMode)
    case ReasonForReviewPage             => reasonForReviewRoutes
    case scenarioResponsiblePage         => scenarioResponsibleRoutes
    case _                               => _ => routes.PostcodeSearchController.onPageLoad(NormalMode)
  }

  private def reasonForReviewRoutes(answers: UserAnswers): Call =
    answers.get(ReasonForReviewPage) match {
      case Some(ReasonForReview.Bandiswrong) => routes.scenarioResponsibleController.onPageLoad(NormalMode)
      case Some(ReasonForReview.Changesaffectedproperty) => routes.deletionOptionsController.onPageLoad(NormalMode)
      case _                             => routes.PostcodeSearchController.onPageLoad(NormalMode)
    }

  private def scenarioResponsibleRoutes(answers: UserAnswers): Call =
    answers.get(scenarioResponsiblePage) match {
      case Some(scenarioResponsible.Lessthan) => routes.PostcodeSearchController.onPageLoad(NormalMode)
      case Some(scenarioResponsible.Morethan) => routes.noticeAlterationController.onPageLoad(NormalMode)
      case _                                 => routes.PostcodeSearchController.onPageLoad(NormalMode)
    }


  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ => _ => routes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

  private def councilTaxResponsibleRoutes(answers: UserAnswers): Call =
    answers.get(CouncilTaxResponsiblePage) match {
      case Some(CouncilTaxResponsible.Yes)       => routes.RequestCouncilTaxBandReviewController.onPageLoad(NormalMode)
      case Some(CouncilTaxResponsible.No)        => routes.PermissionController.onPageLoad()
      case Some(CouncilTaxResponsible.ActingFor) => routes.RequestCouncilTaxBandReviewController.onPageLoad(NormalMode)
      case _                                     => routes.IndexController.onPageLoad()
    }
}

