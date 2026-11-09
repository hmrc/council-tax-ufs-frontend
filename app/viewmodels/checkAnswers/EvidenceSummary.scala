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
import models.{CheckMode, UserAnswers}
import pages.{HasEvidencePage, UploadEvidencePage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object EvidenceSummary {

  // files one per line with Change -> upload page, or "None" with Change -> the yes/no question
  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    (answers.get(UploadEvidencePage), answers.get(HasEvidencePage)) match {
      case (Some(uploads), _) if uploads.nonEmpty =>
        val names = uploads.map(upload => s"<p class=\"govuk-body\">${HtmlFormat.escape(upload.fileName)}</p>").mkString
        Some(build(names, routes.UploadEvidenceController.onPageLoad(CheckMode).url))
      case (_, Some(false)) =>
        Some(build(messages("evidence.checkYourAnswers.none"), routes.HasEvidenceController.onPageLoad(CheckMode).url))
      case _ => None
    }

  private def build(value: String, changeUrl: String)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "evidence.checkYourAnswers.label",
      value = ValueViewModel(HtmlContent(value)),
      actions = Seq(
        ActionItemViewModel("site.change", changeUrl)
          .withVisuallyHiddenText(messages("evidence.change.hidden"))
      )
    )
}
