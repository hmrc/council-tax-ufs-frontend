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

package viewmodels

import base.SpecBase
import models.{CheckMode, UploadedEvidence, UserAnswers}
import pages.{HasEvidencePage, UploadEvidencePage}
import play.api.test.Helpers._
import viewmodels.checkAnswers.EvidenceSummary

class EvidenceSummarySpec extends SpecBase {

  "EvidenceSummary" - {
    "must show the file name with a change link to the upload page" in {
      val application = applicationBuilder().build()
      running(application) {
        implicit val msgs: play.api.i18n.Messages = messages(application)
        val answers = UserAnswers("id").set(UploadEvidencePage, Seq(UploadedEvidence("bill.pdf", "application/pdf", 1), UploadedEvidence("shot.jpg", "image/jpeg", 1))).success.value
        val row = EvidenceSummary.row(answers).value
        row.value.content.asHtml.toString must include("bill.pdf")
        row.value.content.asHtml.toString must include("shot.jpg")
        row.actions.value.items.head.href mustBe controllers.routes.UploadEvidenceController.onPageLoad(CheckMode).url
      }
    }

    "must show none with a change link to the question when there is no evidence" in {
      val application = applicationBuilder().build()
      running(application) {
        implicit val msgs: play.api.i18n.Messages = messages(application)
        val row = EvidenceSummary.row(UserAnswers("id").set(HasEvidencePage, false).success.value).value
        row.value.content.asHtml.toString must include("No documents uploaded")
        row.actions.value.items.head.href mustBe controllers.routes.HasEvidenceController.onPageLoad(CheckMode).url
      }
    }

    "must show nothing before the question is answered" in {
      val application = applicationBuilder().build()
      running(application) {
        implicit val msgs: play.api.i18n.Messages = messages(application)
        EvidenceSummary.row(UserAnswers("id")) mustBe None
      }
    }
  }
}
