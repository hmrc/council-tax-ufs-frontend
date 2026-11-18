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

import models._
import pages._

import java.time.LocalDate

/** The pages whose answers can be sent, by the name they are stored under, with the type of answer each holds and,
  * for pages with fixed options, the values those options can have.
  *
  * conf/journeys.conf refers to pages by name, so it is checked against this at startup: a mistyped page, the
  * wrong answer type or an option that does not exist stops the application starting. Because these entries use
  * the page objects and option lists themselves, renaming a page or option in the code breaks the build here.
  */
object PageCatalogue {

  final case class Entry(name: String, answerType: AnswerType, options: Option[Set[String]])

  private def text(page: QuestionPage[String]) = Entry(page.toString, AnswerType.Text, None)

  private def date(page: QuestionPage[LocalDate]) = Entry(page.toString, AnswerType.Date, None)

  private def choice[A](page: QuestionPage[A], values: Seq[A]) =
    Entry(page.toString, AnswerType.Choice, Some(values.map(_.toString).toSet))

  val entries: Map[String, Entry] = Seq(
    choice(ReasonForReviewPage, ReasonForReview.values),
    choice(scenarioResponsiblePage, scenarioResponsible.values),
    choice(noticeAlterationPage, noticeAlteration.values),
    choice(deletionOptionsPage, deletionOptions.values),
    choice(PropertyUninhabitablePage, PropertyUninhabitable.values),
    choice(UninhabitableDetailsPage, UninhabitableDetails.values),
    choice(PropertySplitOrMergePage, PropertySplitOrMerge.values),
    choice(ChangeOfUsePage, ChangeOfUse.values),
    choice(WhichBandPage, WhichBand.values),
    text(WhyBandIsWrongPage),
    text(AddressChangedPage),
    text(DemolishedDetailsPage),
    text(UninhabitableDetailsPlanPage),
    text(UninhabitableDetailsOtherPage),
    text(UninhabitableDetailsPlanHabitablePage),
    text(UninhabitableDetailsPlanPlanningApplicationPage),
    text(RenoDetailsPage),
    text(PartDemoDetailsPage),
    text(PropertyChangeDetailsPage),
    text(DomesticPartsPage),
    text(BusinessPartsPage),
    text(WhatHasChangedPage),
    date(CouncilTaxLiabilityStartDatePage),
    date(DateNoticePage),
    date(DemolishedDatePage),
    date(UninhabitableDatePage),
    date(UninhabitableDateLastOccupiedPage),
    date(RenoStartDatePage),
    date(RenoCompletionDatePage),
    date(PartDemoDatePage),
    date(PropertyChangeDatePage),
    date(DomesticDatePage),
    date(BusinessDatePage),
    date(WhenDidChangeHappenPage)
  ).map(e => e.name -> e).toMap
}
