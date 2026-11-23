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

  "ContactDetailsSummary address rows" - {
    "must show each correspondence address field separately with its own accessible Change link" in withMessages { implicit msgs =>
      val contactDetails = ContactDetails(
        "Jo",
        "Bloggs",
        None,
        None,
        Some("19 Somerby Court"),
        None,
        Some("Nottingham"),
        None,
        Some("NG9 3NB"),
        Set("post")
      )
      val answers = emptyUserAnswers.set(ContactDetailsPage, contactDetails).success.value
      val rows    = ContactDetailsSummary.addressRows(answers)

      rows.map(_.key.content.asHtml.toString) mustBe Seq(
        msgs("contactDetails.checkYourAnswers.addressLine1"),
        msgs("contactDetails.checkYourAnswers.addressLine2"),
        msgs("contactDetails.checkYourAnswers.townOrCity"),
        msgs("contactDetails.checkYourAnswers.county"),
        msgs("contactDetails.checkYourAnswers.postcode")
      )
      rows.map(valueHtml) mustBe Seq("19 Somerby Court", "", "Nottingham", "", "NG9 3NB")
      rows.foreach(_.actions.value.items.head.href mustBe controllers.routes.ContactDetailsController.onPageLoad(CheckMode).url)
    }

    "must return no address rows when the address is empty" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers
        .set(ContactDetailsPage, ContactDetails("Jo", "Bloggs", None, None, None, None, None, None, None, Set("post")))
        .success.value

      ContactDetailsSummary.addressRows(answers) mustBe empty
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

  "UninhabitableDetailsSummary" - {
    "must show the selected answer with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(UninhabitableDetailsPage, UninhabitableDetails.FireDamage).success.value
      val row     = UninhabitableDetailsSummary.row(answers).value
      valueHtml(row) must include(msgs("uninhabitableDetails.fireDamage"))
      row.actions.value.items.head.href mustBe controllers.routes.UninhabitableDetailsController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      UninhabitableDetailsSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "UninhabitableDateSummary" - {
    "must show the formatted date with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(UninhabitableDatePage, LocalDate.of(2025, 4, 27)).success.value
      val row     = UninhabitableDateSummary.row(answers).value
      valueHtml(row) must include("27 April 2025")
      row.actions.value.items.head.href mustBe controllers.routes.UninhabitableDateController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      UninhabitableDateSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "UninhabitableDateLastOccupiedSummary" - {
    "must show the formatted date with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(UninhabitableDateLastOccupiedPage, LocalDate.of(2024, 1, 5)).success.value
      val row     = UninhabitableDateLastOccupiedSummary.row(answers).value
      valueHtml(row) must include("5 January 2024")
      row.actions.value.items.head.href mustBe
        controllers.routes.UninhabitableDateLastOccupiedController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      UninhabitableDateLastOccupiedSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "UninhabitableDetailsPlanSummary" - {
    "must show the answer with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(UninhabitableDetailsPlanPage, "Rebuild & renovate").success.value
      val row     = UninhabitableDetailsPlanSummary.row(answers).value
      valueHtml(row) mustBe "Rebuild &amp; renovate"
      row.actions.value.items.head.href mustBe
        controllers.routes.UninhabitableDetailsPlanController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      UninhabitableDetailsPlanSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "UninhabitableDetailsPlanHabitableSummary" - {
    "must show the answer with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(UninhabitableDetailsPlanHabitablePage, "New roof").success.value
      val row     = UninhabitableDetailsPlanHabitableSummary.row(answers).value
      valueHtml(row) mustBe "New roof"
      row.actions.value.items.head.href mustBe
        controllers.routes.UninhabitableDetailsPlanHabitableController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      UninhabitableDetailsPlanHabitableSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "UninhabitableDetailsPlanPlanningApplicationSummary" - {
    "must show the answer with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(UninhabitableDetailsPlanPlanningApplicationPage, "Ref 12345").success.value
      val row     = UninhabitableDetailsPlanPlanningApplicationSummary.row(answers).value
      valueHtml(row) mustBe "Ref 12345"
      row.actions.value.items.head.href mustBe
        controllers.routes.UninhabitableDetailsPlanPlanningApplicationController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      UninhabitableDetailsPlanPlanningApplicationSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "UninhabitableDetailsOtherSummary" - {
    "must show the answer with a change link" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers.set(UninhabitableDetailsOtherPage, "Storm damage & subsidence").success.value
      val row     = UninhabitableDetailsOtherSummary.row(answers).value
      valueHtml(row) mustBe "Storm damage &amp; subsidence"
      row.actions.value.items.head.href mustBe
        controllers.routes.UninhabitableDetailsOtherController.onPageLoad(CheckMode).url
    }
    "must return None when unanswered" in withMessages { implicit msgs =>
      UninhabitableDetailsOtherSummary.row(emptyUserAnswers) mustBe None
    }
  }

  "ChangesSummaries" - {
    "must list only the answered questions, in the order they are asked" in withMessages { implicit msgs =>
      val answers = emptyUserAnswers
        .set(UninhabitableDetailsPlanPage, "plan").success.value
        .set(UninhabitableDetailsPage, UninhabitableDetails.FireDamage).success.value
        .set(UninhabitableDatePage, LocalDate.of(2025, 4, 27)).success.value
        .set(HasEvidencePage, false).success.value

      ChangesSummaries.rows(answers).map(row => valueHtml(row)) must have size 4
      ChangesSummaries.rows(answers).map(_.key.content.asHtml.toString) mustBe Seq(
        msgs("uninhabitableDetails.checkYourAnswersLabel"),
        msgs("uninhabitableDate.checkYourAnswersLabel"),
        msgs("uninhabitableDetailsPlan.checkYourAnswersLabel"),
        msgs("evidence.checkYourAnswers.label")
      )
    }

    "must have no rows when nothing has been answered" in withMessages { implicit msgs =>
      ChangesSummaries.rows(emptyUserAnswers) mustBe empty
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
