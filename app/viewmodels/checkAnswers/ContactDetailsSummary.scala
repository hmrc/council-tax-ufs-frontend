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

  // name, email, phone and correspondence address
  def rows(answers: UserAnswers)(implicit messages: Messages): Seq[SummaryListRow] =
    answers.get(ContactDetailsPage).toSeq.flatMap { answer =>
      Seq(
        Some("contactDetails.checkYourAnswers.name" -> Seq(s"${answer.firstName} ${answer.lastName}")),
        answer.email.map("contactDetails.checkYourAnswers.email" -> Seq(_)),
        answer.phoneNumber.map("contactDetails.checkYourAnswers.phoneNumber" -> Seq(_))
      ).flatten.map { case (key, lines) => row(key, lines) }
    }

  def addressRows(answers: UserAnswers)(implicit messages: Messages): Seq[SummaryListRow] =
    answers.get(ContactDetailsPage).toSeq.flatMap { answer =>
      val address = Seq(
        "contactDetails.checkYourAnswers.addressLine1" -> answer.addressLine1,
        "contactDetails.checkYourAnswers.addressLine2" -> answer.addressLine2,
        "contactDetails.checkYourAnswers.townOrCity"    -> answer.townOrCity,
        "contactDetails.checkYourAnswers.county"        -> answer.county,
        "contactDetails.checkYourAnswers.postcode"      -> answer.postcode
      )

      if (address.exists(_._2.nonEmpty)) {
        address.map { case (key, value) =>
          row(key, value.toSeq, Some(s"$key.hidden"))
        }
      } else {
        Seq.empty
      }
    }

  // preferred contact methods on one line, always Email,Phone,Post order
  def preferredContactRows(answers: UserAnswers)(implicit messages: Messages): Seq[SummaryListRow] =
    answers.get(ContactDetailsPage).toSeq.map { answer =>
      row("contactDetails.checkYourAnswers.contactMethods",
        Seq(contactMethodOrder.filter(answer.value.contains).map(m => messages(s"contactMethod.$m")).mkString(",")))
    }

  private def row(key: String, lines: Seq[String], hiddenTextKey: Option[String] = None)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key     = key,
      value   = ValueViewModel(HtmlContent(lines.map(HtmlFormat.escape(_).toString).mkString("<br/>"))),
      actions = Seq(
        ActionItemViewModel("site.change", routes.ContactDetailsController.onPageLoad(CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenTextKey.getOrElse(s"$key.hidden")))
      )
    )
}
