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

package viewmodels.checkAnswers

import models.UserAnswers
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

/** The Check Your Answers rows for the property changes journeys, in the order the questions are asked.
  * A question that was not asked on the path taken has no answer, so it has no row.
  */
object ChangesSummaries {

  private val rows: Seq[(UserAnswers, Messages) => Option[SummaryListRow]] = Seq(
    deletionOptionsSummary.row(_)(using _),
    WhatHasChangedSummary.row(_)(using _),
    WhenDidChangeHappenSummary.row(_)(using _),
    PropertyUninhabitableSummary.row(_)(using _),
    UninhabitableDetailsSummary.row(_)(using _),
    UninhabitableDetailsOtherSummary.row(_)(using _),
    UninhabitableDateSummary.row(_)(using _),
    UninhabitableDateLastOccupiedSummary.row(_)(using _),
    UninhabitableDetailsPlanSummary.row(_)(using _),
    UninhabitableDetailsPlanHabitableSummary.row(_)(using _),
    UninhabitableDetailsPlanPlanningApplicationSummary.row(_)(using _),
    DemolishedDateSummary.row(_)(using _),
    DemolishedDetailsSummary.row(_)(using _),
    RenoStartDateSummary.row(_)(using _),
    RenoCompletionDateSummary.row(_)(using _),
    RenoDetailsSummary.row(_)(using _),
    PartDemoDateSummary.row(_)(using _),
    PartDemoDetailsSummary.row(_)(using _),
    ChangeOfUseSummary.row(_)(using _),
    DomesticPartsSummary.row(_)(using _),
    DomesticDateSummary.row(_)(using _),
    BusinessPartsSummary.row(_)(using _),
    BusinessDateSummary.row(_)(using _),
    AddressChangedSummary.row(_)(using _),
    EvidenceSummary.row(_)(using _)
  )

  def rows(answers: UserAnswers)(implicit messages: Messages): Seq[SummaryListRow] =
    rows.flatMap(row => row(answers, messages))
}
