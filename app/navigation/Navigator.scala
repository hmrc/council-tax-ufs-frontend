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

import controllers.routes
import models._
import pages._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

/** Decides the next page, the back link and the Check Your Answers behaviour for every journey.
  *
  * Two journeys share the start (find the property, who is responsible, reason for review):
  *  - "my band is wrong" is routed here, page by page;
  *  - "the property has changed" is described once in [[ChangesJourney]], which also gives its back links
  *    and check mode behaviour, so this class only asks it first.
  */
@Singleton
class Navigator @Inject()() {

  // ---------------------------------------------------------------------------------------------
  // Public API
  // ---------------------------------------------------------------------------------------------

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode => ChangesJourney.next(page, userAnswers).getOrElse(normalRoutes(page)(userAnswers))
    case CheckMode  => checkRoutes(userAnswers)
  }

  /**
   * Explicit back links for journey pages, so Back never relies on browser history. History would
   * return users to the sign-in page (auth stub locally, Government Gateway / One Login in QA and
   * production) after login, wherever the sign-in point sits in the journey.
   */
  def backLink(page: Page, mode: Mode, userAnswers: Option[UserAnswers]): Call = mode match {
    case CheckMode if page == deletionOptionsPage => routes.ReasonForReviewController.onPageLoad(CheckMode)
    case CheckMode  => checkYourAnswers(userAnswers.getOrElse(UserAnswers("")))
    case NormalMode =>
      val answers = userAnswers.getOrElse(UserAnswers(""))
      ChangesJourney.previous(page, answers).getOrElse(normalBackLink(page, answers))
  }

  def reviewOverviewBackLink(reviewType: ReviewType, userAnswers: Option[UserAnswers]): Call = reviewType match {
    case ReviewType.Deletion | ReviewType.Derelict | ReviewType.Demolished | ReviewType.Reno | ReviewType.PartDemo | ReviewType.SplitOrMerge =>
      userAnswers
        .flatMap(ChangesJourney.previous(FormalReviewPage, _))
        .getOrElse(routes.deletionOptionsController.onPageLoad(NormalMode))
    case ReviewType.Formal
      if userAnswers.exists(_.get(scenarioResponsiblePage).contains(scenarioResponsible.Lessthan)) =>
      routes.scenarioResponsibleController.onPageLoad(NormalMode)
    case _ =>
      routes.noticeAlterationController.onPageLoad(NormalMode)
  }

  /** The formal review wording for a property changes answer, if its path includes the formal review page. */
  def changesReviewType(answers: UserAnswers): Option[ReviewType] =
    Option.when(ChangesJourney.steps(answers).exists(_.page == FormalReviewPage)) {
      answers.get(deletionOptionsPage) match {
        // local area changes and a change of use share the general "changes" wording
        case Some(deletionOptions.Affected | deletionOptions.Changed) => ReviewType.Deletion
        case Some(deletionOptions.Split)                               => ReviewType.SplitOrMerge
        case _ =>
          answers.get(PropertyUninhabitablePage) match {
            case Some(PropertyUninhabitable.Demolished) => ReviewType.Demolished
            case Some(PropertyUninhabitable.Reno)       => ReviewType.Reno
            case Some(PropertyUninhabitable.PartDemo)   => ReviewType.PartDemo
            case _                                      => ReviewType.Derelict
          }
      }
    }

  def checkYourAnswersBackLink: Call =
    routes.ContactDetailsController.onPageLoad(NormalMode)

  // ---------------------------------------------------------------------------------------------
  // Normal mode: the next page. The property changes journey is answered by ChangesJourney before
  // this table is reached, so the pages below are the shared start and the band challenge.
  // ---------------------------------------------------------------------------------------------

  private val normalRoutes: Page => UserAnswers => Call = {
    // find the property, then who is responsible and why a review is wanted
    case CouncilTaxResponsiblePage        => councilTaxResponsibleRoutes
    case RequestCouncilTaxBandReviewPage  => _ => routes.ReasonForReviewController.onPageLoad(NormalMode)
    case ReasonForReviewPage              => reasonForReviewRoutes

    // "my band is wrong": how long responsible, notice of alteration, then the details
    case `scenarioResponsiblePage`        => scenarioResponsibleRoutes
    case `noticeAlterationPage`           => noticeAlterationRoutes
    case CouncilTaxLiabilityStartDatePage => _ => routes.WhyBandIsWrongController.onPageLoad(NormalMode)
    case DateNoticePage                   => _ => routes.WhyBandIsWrongController.onPageLoad(NormalMode)
    case WhyBandIsWrongPage               => _ => routes.HasEvidenceController.onPageLoad(NormalMode)
    case HasEvidencePage                  => hasEvidenceRoutes
    case UploadEvidencePage               => _ => whichBand
    case ContinueWithoutEvidencePage      => _ => whichBand
    case WhichBandPage                    => payerOrContactDetails

    // contact details end every journey
    case CouncilTaxPayerDetailsPage       => _ => routes.ContactDetailsController.onPageLoad(NormalMode)
    case ContactDetailsPage               => checkYourAnswers

    case _                                => _ => journeyRecovery
  }

  private def councilTaxResponsibleRoutes(answers: UserAnswers): Call =
    answers.get(CouncilTaxResponsiblePage) match {
      case Some(CouncilTaxResponsible.Yes)       => routes.RequestCouncilTaxBandReviewController.onPageLoad(NormalMode)
      case Some(CouncilTaxResponsible.ActingFor) => routes.RequestCouncilTaxBandReviewController.onPageLoad(NormalMode)
      case Some(CouncilTaxResponsible.No)        =>
        answers.get(PropertyIdPage) match {
          case Some(id) => routes.PermissionController.onPageLoad(id)
          case None     => routes.PostcodeSearchController.onPageLoad(NormalMode)
        }
      case _                                     => journeyRecovery
    }

  private def reasonForReviewRoutes(answers: UserAnswers): Call =
    answers.get(ReasonForReviewPage) match {
      case Some(ReasonForReview.Bandiswrong)             => routes.scenarioResponsibleController.onPageLoad(NormalMode)
      case Some(ReasonForReview.Changesaffectedproperty) => routes.deletionOptionsController.onPageLoad(NormalMode)
      case _                                             => journeyRecovery
    }

  private def scenarioResponsibleRoutes(answers: UserAnswers): Call =
    answers.get(scenarioResponsiblePage) match {
      case Some(scenarioResponsible.Lessthan) => routes.ReviewOverviewController.formalReview()
      case Some(scenarioResponsible.Morethan) => routes.noticeAlterationController.onPageLoad(NormalMode)
      case _                                  => journeyRecovery
    }

  private def noticeAlterationRoutes(answers: UserAnswers): Call =
    answers.get(noticeAlterationPage) match {
      case Some(noticeAlteration.Yes) => routes.ReviewOverviewController.formalReview()
      case Some(noticeAlteration.No)  => routes.ReviewOverviewController.informalReview()
      case _                          => journeyRecovery
    }

  private def hasEvidenceRoutes(answers: UserAnswers): Call =
    answers.get(HasEvidencePage) match {
      case Some(true)  => routes.UploadEvidenceController.onPageLoad(NormalMode)
      case Some(false) => routes.ContinueWithoutEvidenceController.onPageLoad()
      case None        => routes.HasEvidenceController.onPageLoad(NormalMode)
    }

  // payer details are only asked when acting for someone
  private def payerOrContactDetails(answers: UserAnswers): Call =
    if (isActingFor(answers)) routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
    else routes.ContactDetailsController.onPageLoad(NormalMode)

  // ---------------------------------------------------------------------------------------------
  // Normal mode: the back link, for the pages ChangesJourney does not know about
  // ---------------------------------------------------------------------------------------------

  private def normalBackLink(page: Page, answers: UserAnswers): Call = page match {
    // find the property, then who is responsible and why a review is wanted
    case CouncilTaxResponsiblePage =>
      answers.get(PropertyIdPage)
        .map(id => routes.PropertyDetailController.onPageLoad(id))
        .getOrElse(routes.PostcodeSearchController.onPageLoad(NormalMode))
    case RequestCouncilTaxBandReviewPage  => routes.CouncilTaxResponsibleController.onPageLoad(NormalMode)
    case ReasonForReviewPage              => routes.RequestCouncilTaxBandReviewController.onPageLoad(NormalMode)

    // "my band is wrong"
    case `scenarioResponsiblePage`        => routes.ReasonForReviewController.onPageLoad(NormalMode)
    case `noticeAlterationPage`           => routes.scenarioResponsibleController.onPageLoad(NormalMode)
    case CouncilTaxLiabilityStartDatePage => routes.ReviewOverviewController.formalReview()
    case DateNoticePage                   => reviewOverviewFor(answers)
    case WhyBandIsWrongPage =>
      if (answers.get(scenarioResponsiblePage).contains(scenarioResponsible.Lessthan)) {
        routes.CouncilTaxLiabilityStartDateController.onPageLoad(NormalMode)
      } else if (answers.get(noticeAlterationPage).contains(noticeAlteration.Yes)) {
        routes.DateNoticeController.onPageLoad(NormalMode)
      } else {
        reviewOverviewFor(answers)
      }
    case HasEvidencePage                  => routes.WhyBandIsWrongController.onPageLoad(NormalMode)
    case UploadEvidencePage               => routes.HasEvidenceController.onPageLoad(NormalMode)
    case ContinueWithoutEvidencePage      => routes.HasEvidenceController.onPageLoad(NormalMode)
    case WhichBandPage =>
      if (answers.get(HasEvidencePage).contains(true)) routes.UploadEvidenceController.onPageLoad(NormalMode)
      else routes.ContinueWithoutEvidenceController.onPageLoad()

    // contact details end every journey
    case CouncilTaxPayerDetailsPage       => routes.WhichBandController.onPageLoad(NormalMode)
    case ContactDetailsPage =>
      if (isActingFor(answers)) routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
      else routes.WhichBandController.onPageLoad(NormalMode)

    // the deletion options follow the reason for review
    case `deletionOptionsPage`            => routes.ReasonForReviewController.onPageLoad(NormalMode)

    case _                                => routes.PostcodeSearchController.onPageLoad(NormalMode)
  }

  private def reviewOverviewFor(answers: UserAnswers): Call =
    if (answers.get(noticeAlterationPage).contains(noticeAlteration.Yes)) routes.ReviewOverviewController.formalReview()
    else routes.ReviewOverviewController.informalReview()

  // ---------------------------------------------------------------------------------------------
  // Check mode. A changed answer can change which questions apply, so instead of going straight back
  // to Check Your Answers, go to the first question the current path still needs.
  // ---------------------------------------------------------------------------------------------

  private def checkRoutes(answers: UserAnswers): Call =
    missingPage(answers)
      .orElse(Option.when(needsFile(answers))(routes.UploadEvidenceController.onPageLoad(CheckMode)))
      .getOrElse(checkYourAnswers(answers))

  // Nothing is needed until a reason is chosen.
  private def missingPage(answers: UserAnswers): Option[Call] =
    answers.get(ReasonForReviewPage).flatMap {
      case ReasonForReview.Bandiswrong             => bandChallengeMissingPage(answers)
      case ReasonForReview.Changesaffectedproperty => ChangesJourney.firstMissing(answers)
    }

  private def bandChallengeMissingPage(answers: UserAnswers): Option[Call] = {
    val scenario      = answers.get(scenarioResponsiblePage)
    val moreThan      = scenario.contains(scenarioResponsible.Morethan)
    val noticeAltered = answers.get(noticeAlterationPage).contains(noticeAlteration.Yes)

    Seq(
      scenario.isEmpty                                                          -> routes.scenarioResponsibleController.onPageLoad(CheckMode),
      (scenario.contains(scenarioResponsible.Lessthan)
        && answers.get(CouncilTaxLiabilityStartDatePage).isEmpty)               -> routes.CouncilTaxLiabilityStartDateController.onPageLoad(CheckMode),
      (moreThan && answers.get(noticeAlterationPage).isEmpty)                   -> routes.noticeAlterationController.onPageLoad(CheckMode),
      (moreThan && noticeAltered && answers.get(DateNoticePage).isEmpty)        -> routes.DateNoticeController.onPageLoad(CheckMode),
      answers.get(WhyBandIsWrongPage).isEmpty                                   -> routes.WhyBandIsWrongController.onPageLoad(CheckMode),
      answers.get(HasEvidencePage).isEmpty                                      -> routes.HasEvidenceController.onPageLoad(CheckMode),
      needsFile(answers)                                                        -> routes.UploadEvidenceController.onPageLoad(CheckMode),
      answers.get(WhichBandPage).isEmpty                                        -> routes.WhichBandController.onPageLoad(CheckMode),
      (isActingFor(answers) && answers.get(CouncilTaxPayerDetailsPage).isEmpty) -> routes.CouncilTaxPayerDetailsController.onPageLoad(CheckMode),
      answers.get(ContactDetailsPage).isEmpty                                   -> routes.ContactDetailsController.onPageLoad(CheckMode)
    ).collectFirst { case (true, call) => call }
  }

  // changing to "Yes" still needs a file
  private def needsFile(answers: UserAnswers): Boolean =
    answers.get(HasEvidencePage).contains(true) && answers.get(UploadEvidencePage).forall(_.isEmpty)

  // ---------------------------------------------------------------------------------------------
  // Shared helpers
  // ---------------------------------------------------------------------------------------------

  private def journeyRecovery: Call = routes.JourneyRecoveryController.onPageLoad()

  private def whichBand: Call = routes.WhichBandController.onPageLoad(NormalMode)

  private def checkYourAnswers(answers: UserAnswers): Call =
    routes.CheckYourAnswersController.onPageLoad(Journey.from(answers))

  private def isActingFor(answers: UserAnswers): Boolean =
    answers.get(CouncilTaxResponsiblePage).contains(CouncilTaxResponsible.ActingFor)
}
