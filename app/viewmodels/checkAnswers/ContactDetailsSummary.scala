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
import pages.ContactDetailsPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ContactDetailsSummary {

  private val contactMethodOrder = Seq("email", "phone", "post")

  /** Name, email, phone and correspondence address. */
  def rows(answers: UserAnswers)(implicit messages: Messages): Seq[SummaryListRow] =
    answers.get(ContactDetailsPage).toSeq.flatMap { answer =>
      val address = Seq(answer.addressLine1, answer.addressLine2, answer.townOrCity, answer.county, answer.postcode).flatten

      Seq(
        Some("contactDetails.checkYourAnswers.name" -> Seq(s"${answer.firstName} ${answer.lastName}")),
        answer.email.map("contactDetails.checkYourAnswers.email" -> Seq(_)),
        answer.phoneNumber.map("contactDetails.checkYourAnswers.phoneNumber" -> Seq(_)),
        Option.when(address.nonEmpty)("contactDetails.checkYourAnswers.address" -> address)
      ).flatten.map { case (key, lines) => row(key, lines) }
    }

  /** How the user asked to be contacted, on one line in a fixed order (Email,Phone,Post). */
  def preferredContactRows(answers: UserAnswers)(implicit messages: Messages): Seq[SummaryListRow] =
    answers.get(ContactDetailsPage).toSeq.map { answer =>
      row("contactDetails.checkYourAnswers.contactMethods",
        Seq(contactMethodOrder.filter(answer.value.contains).map(m => messages(s"contactMethod.$m")).mkString(",")))
    }

  private def row(key: String, lines: Seq[String])(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key     = key,
      value   = ValueViewModel(HtmlContent(lines.map(HtmlFormat.escape(_).toString).mkString("<br/>"))),
      actions = Seq(
        ActionItemViewModel("site.change", routes.ContactDetailsController.onPageLoad(CheckMode).url)
          .withVisuallyHiddenText(messages(s"$key.hidden"))
      )
    )
}
