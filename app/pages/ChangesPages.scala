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

package pages

import queries.Settable

/** The answers each part of the property changes journeys owns, so changing an answer can drop the
  * ones that no longer apply.
  */
object ChangesPages {

  // "another reason" and "local area changes" share these two questions
  val changeDetails: Seq[Settable[?]] = Seq(WhatHasChangedPage, WhenDidChangeHappenPage)

  // answered after the user has chosen that their property is derelict
  val derelictDetails: Seq[Settable[?]] = Seq(
    UninhabitableDetailsPage,
    UninhabitableDetailsOtherPage,
    UninhabitableDatePage,
    UninhabitableDateLastOccupiedPage,
    UninhabitableDetailsPlanPage,
    UninhabitableDetailsPlanHabitablePage,
    UninhabitableDetailsPlanPlanningApplicationPage
  )

  // asked only when "Fire damage" or "Flood damage" is chosen
  val derelictPlans: Seq[Settable[?]] = Seq(
    UninhabitableDetailsPlanPage,
    UninhabitableDetailsPlanHabitablePage,
    UninhabitableDetailsPlanPlanningApplicationPage
  )

  val derelict: Seq[Settable[?]] = PropertyUninhabitablePage +: derelictDetails

  // the derelict questions that the demolished journey does not ask; the reason it shares is kept
  val derelictOnly: Seq[Settable[?]] = derelictDetails.filterNot(_ == UninhabitableDetailsPage)

  // answered only after the user has chosen that their property has been fully demolished
  val demolishedOnly: Seq[Settable[?]] = Seq(DemolishedDatePage, DemolishedDetailsPage)

  // the band challenge journey asks for evidence too, so it is only dropped when moving within these journeys
  val evidence: Seq[Settable[?]] = Seq(HasEvidencePage, UploadEvidencePage)

  // answered only after the user has chosen that their property is having major works or renovations done
  val renoOnly: Seq[Settable[?]] = Seq(RenoStartDatePage, RenoCompletionDatePage, RenoDetailsPage)

  // answered only after the user has chosen that part of their property has been demolished
  val partDemoOnly: Seq[Settable[?]] = Seq(PartDemoDatePage, PartDemoDetailsPage)

  // everything asked after "My property has been demolished, is derelict or ..." is chosen
  val demolishedJourney: Seq[Settable[?]] = derelict ++ demolishedOnly ++ renoOnly ++ partDemoOnly

  // answered after "There has been a change of use to my property" is chosen
  val domesticOnly: Seq[Settable[?]] = Seq(DomesticPartsPage, DomesticDatePage)
  val businessOnly: Seq[Settable[?]] = Seq(BusinessPartsPage, BusinessDatePage)
  val changeOfUse: Seq[Settable[?]] = ChangeOfUsePage +: (domesticOnly ++ businessOnly)

  // answered after "My property has been split into multiple properties or merged ..." is chosen
  val splitOrMerge: Seq[Settable[?]] = Seq(PropertySplitOrMergePage, PropertyChangeDetailsPage, PropertyChangeDatePage)

  // "Has the address of your home changed?" ends both the change of use and the split or merge journeys
  val addressChanged: Seq[Settable[?]] = Seq(AddressChangedPage)

  // every question that follows the deletion options page
  val followUps: Seq[Settable[?]] = changeDetails ++ demolishedJourney ++ changeOfUse ++ splitOrMerge ++ addressChanged ++ evidence

  // the band challenge journey's own questions
  val bandChallenge: Seq[Settable[?]] = Seq(
    scenarioResponsiblePage,
    noticeAlterationPage,
    CouncilTaxLiabilityStartDatePage,
    DateNoticePage,
    WhyBandIsWrongPage,
    WhichBandPage
  )
}
