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

import controllers.routes
import models.{CheckMode, PropertySplitOrMerge, UserAnswers}
import pages.{PropertyMergedDatePage, PropertySplitDatePage, PropertySplitOrMergePage}
import play.api.i18n.{Lang, Messages}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.DateTimeFormats.dateTimeFormat
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate

object PropertyChangeDateSummary {

  // only the date for the option currently chosen, so a stale date from the other option is never shown
  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(PropertySplitOrMergePage).flatMap {
      case PropertySplitOrMerge.Merged =>
        answers.get(PropertyMergedDatePage).map(build("propertyMergedDate", _, routes.PropertyChangeDateController.onPageLoadMerged(CheckMode).url))
      case PropertySplitOrMerge.Split =>
        answers.get(PropertySplitDatePage).map(build("propertySplitDate", _, routes.PropertyChangeDateController.onPageLoadSplit(CheckMode).url))
    }

  private def build(prefix: String, answer: LocalDate, changeUrl: String)(implicit messages: Messages): SummaryListRow = {
    implicit val lang: Lang = messages.lang

    SummaryListRowViewModel(
      key     = s"$prefix.checkYourAnswersLabel",
      value   = ValueViewModel(answer.format(dateTimeFormat())),
      actions = Seq(
        ActionItemViewModel("site.change", changeUrl)
          .withVisuallyHiddenText(messages(s"$prefix.change.hidden"))
      )
    )
  }
}
