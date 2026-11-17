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
import play.api.libs.json.Reads
import play.api.mvc.Call

/** The pages of the "property has changed" journeys, in order, for the answers given so far.
  *
  * Next page, back link and "what is still missing" in check mode are all read from this one list, so a
  * journey is described once and its three behaviours cannot drift apart.
  */
private[navigation] object ChangesJourney {

  final case class Step(page: Page, call: Mode => Call, answered: UserAnswers => Boolean)

  private def question[A: Reads](page: QuestionPage[A], call: Mode => Call): Step =
    Step(page, call, _.get(page).isDefined)

  // information pages and the end of the journey have nothing to answer
  private def info(page: Page, call: Call): Step = Step(page, _ => call, _ => true)

  private val chooseOption       = question(deletionOptionsPage, routes.deletionOptionsController.onPageLoad)
  private val scenarioOther      = info(scenarioOtherPage, routes.ScenarioOtherController.onPageLoad())
  private val formalReview       = info(FormalReviewPage, routes.ReviewOverviewController.formalReview())
  private val whatHasChanged     = question(WhatHasChangedPage, routes.WhatHasChangedController.onPageLoad)
  private val whenDidChange      = question(WhenDidChangeHappenPage, routes.WhenDidChangeHappenController.onPageLoad)
  private val propertyUninhabit  = question(PropertyUninhabitablePage, routes.PropertyUninhabitableController.onPageLoad)
  private val uninhabitableWhy   = question(UninhabitableDetailsPage, routes.UninhabitableDetailsController.onPageLoad)
  private val uninhabitableOther = question(UninhabitableDetailsOtherPage, routes.UninhabitableDetailsOtherController.onPageLoad)
  private val uninhabitableDate  = question(UninhabitableDatePage, routes.UninhabitableDateController.onPageLoad)
  private val lastOccupied       = question(UninhabitableDateLastOccupiedPage, routes.UninhabitableDateLastOccupiedController.onPageLoad)
  private val plan               = question(UninhabitableDetailsPlanPage, routes.UninhabitableDetailsPlanController.onPageLoad)
  private val planHabitable      = question(UninhabitableDetailsPlanHabitablePage, routes.UninhabitableDetailsPlanHabitableController.onPageLoad)
  private val planningApp        = question(UninhabitableDetailsPlanPlanningApplicationPage, routes.UninhabitableDetailsPlanPlanningApplicationController.onPageLoad)
  private val demolishedDate     = question(DemolishedDatePage, routes.DemolishedDateController.onPageLoad)
  private val demolishedDetails  = question(DemolishedDetailsPage, routes.DemolishedDetailsController.onPageLoad)
  private val renoStart            = question(RenoStartDatePage, routes.RenoStartDateController.onPageLoad)
  private val renoCompletion       = question(RenoCompletionDatePage, routes.RenoCompletionDateController.onPageLoad)
  private val renoDetails          = question(RenoDetailsPage, routes.RenoDetailsController.onPageLoad)
  private val partDemoDate        = question(PartDemoDatePage, routes.PartDemoDateController.onPageLoad)
  private val partDemoDetails     = question(PartDemoDetailsPage, routes.PartDemoDetailsController.onPageLoad)
  private val changeOfUse         = question(ChangeOfUsePage, routes.ChangeOfUseController.onPageLoad)
  private val domesticParts       = question(DomesticPartsPage, routes.DomesticPartsController.onPageLoad)
  private val domesticDate        = question(DomesticDatePage, routes.DomesticDateController.onPageLoad)
  private val businessParts        = question(BusinessPartsPage, routes.BusinessPartsController.onPageLoad)
  private val businessDate         = question(BusinessDatePage, routes.BusinessDateController.onPageLoad)
  private val addressChanged      = question(AddressChangedPage, routes.AddressChangedController.onPageLoad)
  private val splitOrMerge        = question(PropertySplitOrMergePage, routes.PropertySplitOrMergeController.onPageLoad)
  private val changeDetails       = question(PropertyChangeDetailsPage, routes.PropertyChangeDetailsController.onPageLoad)
  private val changeDate          = question(PropertyChangeDatePage, routes.PropertyChangeDateController.onPageLoad)
  private val hasEvidence        = question(HasEvidencePage, routes.HasEvidenceController.onPageLoad)
  private val uploadEvidence     = Step(UploadEvidencePage, routes.UploadEvidenceController.onPageLoad, _.get(UploadEvidencePage).exists(_.nonEmpty))
  private val noEvidence         = info(ContinueWithoutEvidencePage, routes.ContinueWithoutEvidenceController.onPageLoad())
  private val payerDetails       = question(CouncilTaxPayerDetailsPage, routes.CouncilTaxPayerDetailsController.onPageLoad)
  private val contactDetails     = question(ContactDetailsPage, routes.ContactDetailsController.onPageLoad)

  // the deletion options are only asked after that reason is chosen, and are dropped if the reason changes
  private def isChanges(answers: UserAnswers): Boolean =
    answers.get(ReasonForReviewPage).contains(ReasonForReview.Changesaffectedproperty) ||
      answers.get(deletionOptionsPage).isDefined

  private def isActingFor(answers: UserAnswers): Boolean =
    answers.get(CouncilTaxResponsiblePage).contains(CouncilTaxResponsible.ActingFor)

  // evidence is asked for the derelict journey, and the next question depends on the answer
  private def evidence(answers: UserAnswers): Seq[Step] =
    Seq(hasEvidence) ++ (answers.get(HasEvidencePage) match {
      case Some(true)  => Seq(uploadEvidence)
      case Some(false) => Seq(noEvidence)
      case None        => Nil
    })

  private def derelict(answers: UserAnswers): Seq[Step] = {
    val reasons =
      if (answers.get(UninhabitableDetailsPage).contains(UninhabitableDetails.Other)) Seq(uninhabitableOther, uninhabitableDate, lastOccupied)
      else Seq(uninhabitableDate, lastOccupied, plan, planHabitable, planningApp)

    Seq(formalReview, uninhabitableWhy) ++ reasons ++ evidence(answers)
  }

  // fire, flood and other all follow the same questions
  private val demolished: Seq[Step] = Seq(formalReview, uninhabitableWhy, demolishedDate, demolishedDetails)

  private val partDemo: Seq[Step] = Seq(formalReview, partDemoDate, partDemoDetails)

  // a property now used as a home or a business: no evidence is asked
  private val domestic: Seq[Step] = Seq(formalReview, domesticParts, domesticDate, addressChanged)

  private val business: Seq[Step] = Seq(formalReview, businessParts, businessDate, addressChanged)

  // a merged or split property: the questions are worded by the answer, and no evidence is asked
  private val mergedOrSplit: Seq[Step] = Seq(formalReview, changeDetails, changeDate, addressChanged)

  private val reno: Seq[Step] = Seq(formalReview, renoStart, renoCompletion, renoDetails)

  /** The pages for the chosen option, and whether that option's journey is built through to the end. */
  private def optionSteps(answers: UserAnswers): (Seq[Step], Boolean) =
    answers.get(deletionOptionsPage) match {
      case Some(models.deletionOptions.AnotherReason) => (Seq(scenarioOther, whatHasChanged, whenDidChange), true)
      case Some(models.deletionOptions.Affected)      => (Seq(formalReview, whatHasChanged, whenDidChange), true)
      case Some(models.deletionOptions.Demolished) =>
        answers.get(PropertyUninhabitablePage) match {
          case Some(PropertyUninhabitable.Derelict)   => (propertyUninhabit +: derelict(answers), true)
          case Some(PropertyUninhabitable.Demolished) => (propertyUninhabit +: (demolished ++ evidence(answers)), true)
          case Some(PropertyUninhabitable.Reno)       => (propertyUninhabit +: (reno ++ evidence(answers)), true)
          case Some(PropertyUninhabitable.PartDemo)   => (propertyUninhabit +: (partDemo ++ evidence(answers)), true)
          case None                                   => (Seq(propertyUninhabit), false)
        }
      case Some(models.deletionOptions.Changed) =>
        answers.get(ChangeOfUsePage) match {
          case Some(ChangeOfUse.Domestic) => (changeOfUse +: domestic, true)
          case Some(ChangeOfUse.Business) => (changeOfUse +: business, true)
          case None                       => (Seq(changeOfUse), false)
        }
      case Some(models.deletionOptions.Split) =>
        answers.get(PropertySplitOrMergePage) match {
          case Some(_) => (splitOrMerge +: mergedOrSplit, true)
          case None    => (Seq(splitOrMerge), false)
        }
      case _ => (Nil, false)
    }

  /** Empty unless the user is on the property changes journey, so other journeys keep their own routes.
    * An option whose pages are not built yet ends the list early, so it never reaches the contact details.
    */
  def steps(answers: UserAnswers): Seq[Step] =
    if (!isChanges(answers)) Nil
    else {
      val (options, built) = optionSteps(answers)
      val start            = chooseOption +: options
      if (!built) start
      else
        start ++ Option.when(isActingFor(answers))(payerDetails) ++ Seq(contactDetails) :+
          info(CheckYourAnswersStep, routes.CheckYourAnswersController.onPageLoad(Journey.Deletion))
    }

  def next(page: Page, answers: UserAnswers): Option[Call] =
    neighbour(page, answers, offset = 1)

  def previous(page: Page, answers: UserAnswers): Option[Call] =
    neighbour(page, answers, offset = -1)

  private def neighbour(page: Page, answers: UserAnswers, offset: Int): Option[Call] = {
    val path  = steps(answers)
    val index = path.indexWhere(_.page == page)
    Option.when(index >= 0)(index + offset).flatMap(path.lift).map(_.call(NormalMode))
  }

  /** The first question the current path still needs, if any. */
  def firstMissing(answers: UserAnswers): Option[Call] =
    steps(answers).find(!_.answered(answers)).map(_.call(CheckMode))

  private case object CheckYourAnswersStep extends Page
}
