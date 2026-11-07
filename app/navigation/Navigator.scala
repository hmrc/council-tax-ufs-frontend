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
    case CouncilTaxResponsiblePage        => councilTaxResponsibleRoutes
    case RequestCouncilTaxBandReviewPage  => _ => routes.ReasonForReviewController.onPageLoad(NormalMode)
    case ReasonForReviewPage              => reasonForReviewRoutes
    case `scenarioResponsiblePage`        => scenarioResponsibleRoutes
    case `noticeAlterationPage`           => noticeAlterationRoutes
    case CouncilTaxLiabilityStartDatePage => _ => routes.WhyBandIsWrongController.onPageLoad(NormalMode)
    case DateNoticePage                   => _ => routes.WhyBandIsWrongController.onPageLoad(NormalMode)
    case WhyBandIsWrongPage               => _ => routes.HasEvidenceController.onPageLoad(NormalMode)
    case HasEvidencePage                  => hasEvidenceRoutes
    case UploadEvidencePage               => _ => whichBand
    case ContinueWithoutEvidencePage      => _ => whichBand
    case WhichBandPage                    => whichBandRoutes
    case CouncilTaxPayerDetailsPage       => _ => routes.ContactDetailsController.onPageLoad(NormalMode)
    case ContactDetailsPage               => checkYourAnswers
    case _                                => _ => journeyRecovery
  }

  private def journeyRecovery: Call = routes.JourneyRecoveryController.onPageLoad()

  private def checkYourAnswers(answers: UserAnswers): Call =
    routes.CheckYourAnswersController.onPageLoad(Journey.from(answers))

  private def whichBand: Call = routes.WhichBandController.onPageLoad(NormalMode)

  // The payer's details are only collected when the user is acting for the person responsible.
  private def whichBandRoutes(answers: UserAnswers): Call =
    if (isActingFor(answers)) routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
    else routes.ContactDetailsController.onPageLoad(NormalMode)

  private def isActingFor(answers: UserAnswers): Boolean =
    answers.get(CouncilTaxResponsiblePage).contains(CouncilTaxResponsible.ActingFor)

  private def hasEvidenceRoutes(answers: UserAnswers): Call =
    answers.get(HasEvidencePage) match {
      case Some(true)  => routes.UploadEvidenceController.onPageLoad(NormalMode)
      case Some(false) => routes.ContinueWithoutEvidenceController.onPageLoad()
      case None        => routes.HasEvidenceController.onPageLoad(NormalMode)
    }

  private def reasonForReviewRoutes(answers: UserAnswers): Call =
    answers.get(ReasonForReviewPage) match {
      case Some(ReasonForReview.Bandiswrong) => routes.scenarioResponsibleController.onPageLoad(NormalMode)
      case Some(ReasonForReview.Changesaffectedproperty) => routes.deletionOptionsController.onPageLoad(NormalMode)
      case _                             => journeyRecovery
    }

  private def scenarioResponsibleRoutes(answers: UserAnswers): Call =
    answers.get(scenarioResponsiblePage) match {
      case Some(scenarioResponsible.Lessthan) => routes.ReviewOverviewController.formalReview()
      case Some(scenarioResponsible.Morethan) => routes.noticeAlterationController.onPageLoad(NormalMode)
      case _                                 => journeyRecovery
    }

  private def noticeAlterationRoutes(answers: UserAnswers): Call =
    answers.get(noticeAlterationPage) match {
      case Some(noticeAlteration.Yes) => routes.ReviewOverviewController.formalReview()
      case Some(noticeAlteration.No)  => routes.ReviewOverviewController.informalReview()
      case _                          => journeyRecovery
    }

  /**
   * In Check mode an answer can change which questions apply (acting for someone, a different
   * review reason, more or less than six months...). Rather than returning straight to Check Your
   * Answers, send the user to the first question their current path still needs, then back.
   * Nothing is required until a reason for the review has been chosen.
   */
  private def missingPage(answers: UserAnswers): Option[Call] =
    answers.get(ReasonForReviewPage).flatMap {
      case ReasonForReview.Bandiswrong =>
        val scenario      = answers.get(scenarioResponsiblePage)
        val moreThan      = scenario.contains(scenarioResponsible.Morethan)
        val noticeAltered = answers.get(noticeAlterationPage).contains(noticeAlteration.Yes)

        Seq(
          scenario.isEmpty                                                              -> routes.scenarioResponsibleController.onPageLoad(CheckMode),
          (scenario.contains(scenarioResponsible.Lessthan)
            && answers.get(CouncilTaxLiabilityStartDatePage).isEmpty)                   -> routes.CouncilTaxLiabilityStartDateController.onPageLoad(CheckMode),
          (moreThan && answers.get(noticeAlterationPage).isEmpty)                       -> routes.noticeAlterationController.onPageLoad(CheckMode),
          (moreThan && noticeAltered && answers.get(DateNoticePage).isEmpty)            -> routes.DateNoticeController.onPageLoad(CheckMode),
          answers.get(WhyBandIsWrongPage).isEmpty                                       -> routes.WhyBandIsWrongController.onPageLoad(CheckMode),
          answers.get(HasEvidencePage).isEmpty                                          -> routes.HasEvidenceController.onPageLoad(CheckMode),
          needsFile(answers)                                                            -> routes.UploadEvidenceController.onPageLoad(CheckMode),
          answers.get(WhichBandPage).isEmpty                                            -> routes.WhichBandController.onPageLoad(CheckMode),
          (isActingFor(answers) && answers.get(CouncilTaxPayerDetailsPage).isEmpty)     -> routes.CouncilTaxPayerDetailsController.onPageLoad(CheckMode),
          answers.get(ContactDetailsPage).isEmpty                                       -> routes.ContactDetailsController.onPageLoad(CheckMode)
        ).collectFirst { case (true, call) => call }

      case ReasonForReview.Changesaffectedproperty =>
        Option.when(answers.get(deletionOptionsPage).isEmpty)(routes.deletionOptionsController.onPageLoad(CheckMode))

      case _ => None
    }

  // Switching to "Yes" still needs a file, whatever else has been answered.
  private def needsFile(answers: UserAnswers): Boolean =
    answers.get(HasEvidencePage).contains(true) && answers.get(UploadEvidencePage).forall(_.isEmpty)

  private def checkRoutes(answers: UserAnswers): Call =
    missingPage(answers)
      .orElse(Option.when(needsFile(answers))(routes.UploadEvidenceController.onPageLoad(CheckMode)))
      .getOrElse(checkYourAnswers(answers))

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRoutes(userAnswers)
  }

  /**
   * Explicit back links for journey pages, so Back never relies on browser history. History would
   * return users to the sign-in page (auth stub locally, Government Gateway / One Login in QA and
   * production) after login, wherever the sign-in point sits in the journey.
   */
  def backLink(page: Page, mode: Mode, userAnswers: Option[UserAnswers]): Call = mode match {
    case CheckMode =>
      checkYourAnswers(userAnswers.getOrElse(UserAnswers("")))
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
          } else if (answers.get(noticeAlterationPage).contains(noticeAlteration.Yes)) {
            routes.DateNoticeController.onPageLoad(NormalMode)
          } else {
            reviewOverviewFor(answers)
          }
        case DateNoticePage                  => reviewOverviewFor(answers)
        case HasEvidencePage                 => routes.WhyBandIsWrongController.onPageLoad(NormalMode)
        case UploadEvidencePage              => routes.HasEvidenceController.onPageLoad(NormalMode)
        case ContinueWithoutEvidencePage     => routes.HasEvidenceController.onPageLoad(NormalMode)
        case WhichBandPage =>
          if (answers.get(HasEvidencePage).contains(true)) routes.UploadEvidenceController.onPageLoad(NormalMode)
          else routes.ContinueWithoutEvidenceController.onPageLoad()
        case CouncilTaxPayerDetailsPage => routes.WhichBandController.onPageLoad(NormalMode)
        case ContactDetailsPage =>
          if (isActingFor(answers)) routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
          else routes.WhichBandController.onPageLoad(NormalMode)
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
    routes.ContactDetailsController.onPageLoad(NormalMode)

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
      case _                                     => journeyRecovery
    }
}