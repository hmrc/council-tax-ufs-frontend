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
    case CouncilTaxLiabilityStartDatePage => _ => routes.WhyBandIsWrongController.onPageLoad(NormalMode)
    case WhyBandIsWrongPage              => _ => routes.CheckYourAnswersController.onPageLoad()
    case CouncilTaxResponsiblePage       => councilTaxResponsibleRoutes
    case RequestCouncilTaxBandReviewPage => _ => routes.ReasonForReviewController.onPageLoad(NormalMode)
    case ReasonForReviewPage             => reasonForReviewRoutes
    case `scenarioResponsiblePage`      => scenarioResponsibleRoutes
    case `noticeAlterationPage`          => noticeAlterationRoutes
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
      case Some(scenarioResponsible.Lessthan) => routes.ReviewOverviewController.formalReview()
      case Some(scenarioResponsible.Morethan) => routes.noticeAlterationController.onPageLoad(NormalMode)
      case _                                 => routes.PostcodeSearchController.onPageLoad(NormalMode)
    }

  private def noticeAlterationRoutes(answers: UserAnswers): Call =
    answers.get(noticeAlterationPage) match {
      case Some(noticeAlteration.Yes) => routes.ReviewOverviewController.formalReview()
      case Some(noticeAlteration.No)  => routes.ReviewOverviewController.informalReview()
      case _                          => routes.PostcodeSearchController.onPageLoad(NormalMode)
    }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case CouncilTaxLiabilityStartDatePage => _ => routes.CheckYourAnswersController.onPageLoad()
    case WhyBandIsWrongPage              => _ => routes.CheckYourAnswersController.onPageLoad()
    case _ => _ => routes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

  /**
   * Explicit back links for journey pages, so Back never relies on browser history. History would
   * return users to the sign-in page (auth stub locally, Government Gateway / One Login in QA and
   * production) after login, wherever the sign-in point sits in the journey.
   */
  def backLink(page: Page, mode: Mode, userAnswers: Option[UserAnswers]): Call = mode match {
    case CheckMode =>
      routes.CheckYourAnswersController.onPageLoad()
    case NormalMode =>
      val answers = userAnswers.getOrElse(UserAnswers(""))
      page match {
        case CouncilTaxResponsiblePage =>
          answers.get(PropertyIdPage)
            .map(id => routes.PropertyDetailController.onPageLoad(id))
            .getOrElse(routes.PostcodeSearchController.onPageLoad(NormalMode))
        case RequestCouncilTaxBandReviewPage => routes.CouncilTaxResponsibleController.onPageLoad(NormalMode)
        case ReasonForReviewPage             => routes.RequestCouncilTaxBandReviewController.onPageLoad(NormalMode)
        case `scenarioResponsiblePage`       => routes.ReasonForReviewController.onPageLoad(NormalMode)
        case `deletionOptionsPage`           => routes.ReasonForReviewController.onPageLoad(NormalMode)
        case `noticeAlterationPage`          => routes.scenarioResponsibleController.onPageLoad(NormalMode)
        case CouncilTaxLiabilityStartDatePage => routes.ReviewOverviewController.formalReview()
        case WhyBandIsWrongPage =>
          if (answers.get(scenarioResponsiblePage).contains(scenarioResponsible.Lessthan)) {
            routes.CouncilTaxLiabilityStartDateController.onPageLoad(NormalMode)
          } else {
            reviewOverviewFor(answers)
          }
        case _ =>
          routes.PostcodeSearchController.onPageLoad(NormalMode)
      }
  }

  def reviewOverviewBackLink(reviewType: ReviewType, userAnswers: Option[UserAnswers]): Call = reviewType match {
    case ReviewType.Formal
      if userAnswers.exists(_.get(scenarioResponsiblePage).contains(scenarioResponsible.Lessthan)) =>
      routes.scenarioResponsibleController.onPageLoad(NormalMode)
    case _ =>
      routes.noticeAlterationController.onPageLoad(NormalMode)
  }

  def checkYourAnswersBackLink: Call =
    routes.WhyBandIsWrongController.onPageLoad(NormalMode)

  private def reviewOverviewFor(answers: UserAnswers): Call =
    if (answers.get(noticeAlterationPage).contains(noticeAlteration.Yes)) {
      routes.ReviewOverviewController.formalReview()
    } else {
      routes.ReviewOverviewController.informalReview()
    }

  private def councilTaxResponsibleRoutes(answers: UserAnswers): Call =
    answers.get(CouncilTaxResponsiblePage) match {
      case Some(CouncilTaxResponsible.Yes)       => routes.RequestCouncilTaxBandReviewController.onPageLoad(NormalMode)
      case Some(CouncilTaxResponsible.No)        =>
        answers.get(PropertyIdPage) match {
          case Some(id) => routes.PermissionController.onPageLoad(id)
          case None => routes.PostcodeSearchController.onPageLoad(NormalMode)
        }
      case Some(CouncilTaxResponsible.ActingFor) => routes.RequestCouncilTaxBandReviewController.onPageLoad(NormalMode)
      case _                                     => routes.IndexController.onPageLoad()
    }
}