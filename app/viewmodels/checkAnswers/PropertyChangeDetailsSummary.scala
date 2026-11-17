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
import pages.{PropertyMergedDetailsPage, PropertyNewAddressPage, PropertySplitDetailsPage, PropertySplitOrMergePage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object PropertyChangeDetailsSummary {

  // only the details for the option currently chosen, so a stale answer from the other option is never shown
  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(PropertySplitOrMergePage).flatMap {
      case PropertySplitOrMerge.Merged =>
        answers.get(PropertyMergedDetailsPage).map(build("propertyMergedDetails", _, routes.PropertyChangeDetailsController.onPageLoadMerged(CheckMode).url))
      case PropertySplitOrMerge.Split =>
        answers.get(PropertySplitDetailsPage).map(build("propertySplitDetails", _, routes.PropertyChangeDetailsController.onPageLoadSplit(CheckMode).url))
    }

  def newAddressRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(PropertyNewAddressPage).map(build("propertyNewAddress", _, routes.PropertyChangeDetailsController.onPageLoadNewAddress(CheckMode).url))

  private def build(prefix: String, answer: String, changeUrl: String)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key     = s"$prefix.checkYourAnswersLabel",
      value   = ValueViewModel(HtmlFormat.escape(answer).toString),
      actions = Seq(
        ActionItemViewModel("site.change", changeUrl)
          .withVisuallyHiddenText(messages(s"$prefix.change.hidden"))
      )
    )
}
