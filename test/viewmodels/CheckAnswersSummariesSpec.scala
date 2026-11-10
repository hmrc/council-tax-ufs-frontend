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
import models._
import models.requests.RequestSupport
import org.scalatest.OptionValues
import pages._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.SessionKeys
import viewmodels.checkAnswers._

import java.time.LocalDate

class CheckAnswersSummariesSpec extends SpecBase with OptionValues {

  private def withMessages(test: Messages => Unit): Unit = {
    val application = applicationBuilder().build()
    running(application) {
      test(messages(application))
    }
  }

  private def valueHtml(row: uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow): String =
    row.value.content.asHtml.toString

  "scenarioResponsibleSummary" - {
    "must show the selected answer with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(scenarioResponsiblePage, scenarioResponsible.values.head).success.value
      val row     = scenarioResponsibleSummary.row(answers).value
      valueHtml(row) must include(msgs(s"scenarioResponsible.${scenarioResponsible.values.head}"))
      row.actions.value.items.head.href mustBe controllers.routes.scenarioResponsibleController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      scenarioResponsibleSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "noticeAlterationSummary" - {
    "must show the selected answer with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(noticeAlterationPage, noticeAlteration.Yes).success.value
      val row     = noticeAlterationSummary.row(answers).value
      valueHtml(row) must include(msgs("noticeAlteration.yes"))
      row.actions.value.items.head.href mustBe controllers.routes.noticeAlterationController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      noticeAlterationSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "deletionOptionsSummary" - {
    "must show the selected answer with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(deletionOptionsPage, deletionOptions.Demolished).success.value
      val row     = deletionOptionsSummary.row(answers).value
      valueHtml(row) must include(msgs("deletionOptions.demolished"))
      row.actions.value.items.head.href mustBe controllers.routes.deletionOptionsController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      deletionOptionsSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "RequestCouncilTaxBandReviewSummary" - {
    "must show the selected answers with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(RequestCouncilTaxBandReviewPage, Set[RequestCouncilTaxBandReview](RequestCouncilTaxBandReview.Option1)).success.value
      val row     = RequestCouncilTaxBandReviewSummary.row(answers).value
      valueHtml(row) must include(msgs("requestCouncilTaxBandReview.readContent.label"))
      row.actions.value.items.head.href mustBe controllers.routes.RequestCouncilTaxBandReviewController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      RequestCouncilTaxBandReviewSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "WhichBandSummary" - {
    "must show the selected band with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(WhichBandPage, WhichBand.Bandc).success.value
      val row     = WhichBandSummary.row(answers).value
      valueHtml(row) must include(msgs("whichBand.bandC"))
      row.actions.value.items.head.href mustBe controllers.routes.WhichBandController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      WhichBandSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "ReasonForReviewSummary" - {
    "must show the selected reason with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(ReasonForReviewPage, ReasonForReview.Bandiswrong).success.value
      val row     = ReasonForReviewSummary.row(answers).value
      valueHtml(row) must include(msgs("reasonForReview.band-is-wrong"))
      row.actions.value.items.head.href mustBe controllers.routes.ReasonForReviewController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      ReasonForReviewSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "PostcodeSearchSummary" - {
    "must show the escaped postcode with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(PostcodeSearchPage, "CF5 <4GP>").success.value
      val row     = PostcodeSearchSummary.row(answers).value
      valueHtml(row) must include("CF5 &lt;4GP&gt;")
      row.actions.value.items.head.href mustBe controllers.routes.PostcodeSearchController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      PostcodeSearchSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "DateNoticeSummary" - {
    "must show the formatted date with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(DateNoticePage, LocalDate.of(2025, 4, 27)).success.value
      val row     = DateNoticeSummary.row(answers).value
      valueHtml(row) must include("27 April 2025")
      row.actions.value.items.head.href mustBe controllers.routes.DateNoticeController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      DateNoticeSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "AdvancedPropertySearchSummary" - {
    "must show the non-empty address lines separated by line breaks with a change link" in withMessages { implicit msgs =>
      val search  = AdvancedPropertySearch("19", "", "Cardiff", "CF5 4GP", "", "", "", "", "", "")
      val answers = emptyUserAnswers.set(AdvancedPropertySearchPage, search).success.value
      val row     = AdvancedPropertySearchSummary.row(answers).value
      valueHtml(row) mustBe "19<br/>Cardiff<br/>CF5 4GP"
      row.actions.value.items.head.href mustBe controllers.routes.AdvancedPropertySearchController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      AdvancedPropertySearchSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "Page" - {
    "must implicitly convert to its string name" in {
      val name: String = PostcodeSearchPage
      name mustBe "postcodeSearch"
    }
  }

  "RequestSupport" - {
    "isLoggedIn must be true only when an auth token is in the session" in {
      RequestSupport.isLoggedIn(FakeRequest()) mustBe false
      RequestSupport.isLoggedIn(FakeRequest().withSession(SessionKeys.authToken -> "Bearer token")) mustBe true
    }

    "sessionId must return the session id or a default" in {
      RequestSupport.sessionId(FakeRequest()) mustBe "Unknown Session Id"
      RequestSupport.sessionId(FakeRequest().withSession(SessionKeys.sessionId -> "abc")) mustBe "abc"
    }

    "hc must carry the session id from the request" in {
      val request = FakeRequest().withSession(SessionKeys.sessionId -> "abc")
      RequestSupport.hc(request).sessionId.map(_.value) mustBe Some("abc")
    }
  }
}
