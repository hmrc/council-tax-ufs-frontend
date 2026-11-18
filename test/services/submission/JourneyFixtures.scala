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

package services.submission

import base.SpecBase
import models._
import pages._

import java.time.LocalDate

/** Answers for every journey as the user would have them at the end of it, and the codes each must send. */
trait JourneyFixtures extends SpecBase with org.scalatest.EitherValues {

  val date = LocalDate.of(2026, 7, 29)

  val contact = ContactDetails("John", "Smith", Some("john@example.com"), None, Some("1 High Street"), None, Some("London"), None, Some("SW1A 1AA"), Set("email"))

  def set[A](answers: UserAnswers, page: pages.QuestionPage[A], value: A)(implicit w: play.api.libs.json.Writes[A]) =
    answers.set(page, value).success.value

  def challenge(scenario: scenarioResponsible) =
    set(set(emptyUserAnswers, ReasonForReviewPage, ReasonForReview.Bandiswrong), scenarioResponsiblePage, scenario)

  def withBand(a: UserAnswers) =
    set(set(a, WhyBandIsWrongPage, "Neighbours are in a lower band"), WhichBandPage, WhichBand.Bandc)

  def changes(option: deletionOptions) =
    set(set(emptyUserAnswers, ReasonForReviewPage, ReasonForReview.Changesaffectedproperty), deletionOptionsPage, option)

  def uninhabitable(p: PropertyUninhabitable) =
    set(changes(deletionOptions.Demolished), PropertyUninhabitablePage, p)

  // every journey, answered in full, as the user would reach the end of it
  val complete: Map[String, UserAnswers] = Map(
    "J01" -> withBand(set(challenge(scenarioResponsible.Lessthan), CouncilTaxLiabilityStartDatePage, date)),
    "J02" -> withBand(set(challenge(scenarioResponsible.Morethan), noticeAlterationPage, noticeAlteration.No)),
    "J03" -> withBand(set(set(challenge(scenarioResponsible.Morethan), noticeAlterationPage, noticeAlteration.Yes), DateNoticePage, date)),
    "J04.1" -> set(set(set(uninhabitable(PropertyUninhabitable.Demolished), UninhabitableDetailsPage, UninhabitableDetails.FireDamage), DemolishedDatePage, date), DemolishedDetailsPage, "Burnt down"),
    "J04.2" -> set(set(set(set(set(set(uninhabitable(PropertyUninhabitable.Derelict), UninhabitableDetailsPage, UninhabitableDetails.FloodDamage),
      UninhabitableDatePage, date), UninhabitableDateLastOccupiedPage, date), UninhabitableDetailsPlanPage, "Rebuild"),
      UninhabitableDetailsPlanHabitablePage, "New roof"), UninhabitableDetailsPlanPlanningApplicationPage, "PA/123"),
    "J04.3" -> set(set(set(uninhabitable(PropertyUninhabitable.Reno), RenoStartDatePage, date), RenoCompletionDatePage, date), RenoDetailsPage, "Extension"),
    "J04.4" -> set(set(uninhabitable(PropertyUninhabitable.PartDemo), PartDemoDatePage, date), PartDemoDetailsPage, "Garage"),
    "J05" -> set(set(set(set(changes(deletionOptions.Split), PropertySplitOrMergePage, PropertySplitOrMerge.Merged), PropertyChangeDetailsPage, "Knocked through"), PropertyChangeDatePage, date), AddressChangedPage, "No"),
    "J06" -> set(set(set(set(changes(deletionOptions.Split), PropertySplitOrMergePage, PropertySplitOrMerge.Split), PropertyChangeDetailsPage, "Two flats"), PropertyChangeDatePage, date), AddressChangedPage, "Yes, 1A and 1B"),
    "J07" -> set(set(set(set(changes(deletionOptions.Changed), ChangeOfUsePage, ChangeOfUse.Domestic), DomesticPartsPage, "Shop"), DomesticDatePage, date), AddressChangedPage, "No"),
    "J08" -> set(set(set(set(changes(deletionOptions.Changed), ChangeOfUsePage, ChangeOfUse.Business), BusinessPartsPage, "Garage"), BusinessDatePage, date), AddressChangedPage, "No"),
    "J09" -> set(set(changes(deletionOptions.Affected), WhatHasChangedPage, "New bypass"), WhenDidChangeHappenPage, date),
    "J10" -> set(set(changes(deletionOptions.AnotherReason), WhatHasChangedPage, "Something"), WhenDidChangeHappenPage, date)
  ).view.mapValues(set(_, ContactDetailsPage, contact)).toMap

  // the codes each journey must send (the premise first), from the question sheets
  val expectedCodes: Map[String, Seq[String]] = Map(
    "J01" -> Seq("J01", "P02", "S01", "P01"),
    "J02" -> Seq("J02", "S01", "P01"),
    "J03" -> Seq("J03", "I04", "S01", "P01"),
    "J04.1" -> Seq("J04", "S02", "S01", "P02", "I10"),
    "J04.2" -> Seq("J04", "S02", "S01", "I06", "I01", "I07", "I08", "I09"),
    "J04.3" -> Seq("J04", "S02", "P02", "I03", "S01"),
    "J04.4" -> Seq("J04", "S02", "S01", "P02"),
    "J05" -> Seq("J05", "S01", "P02", "I11"),
    "J06" -> Seq("J06", "S01", "P02", "I11"),
    "J07" -> Seq("J07", "S01", "P02", "I11"),
    "J08" -> Seq("J08", "S01", "P02", "I11"),
    "J09" -> Seq("J09", "S01", "P02"),
    "J10" -> Seq("J10", "S01", "P02")
  )

}
