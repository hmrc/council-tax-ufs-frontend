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

package navigation

import base.SpecBase
import controllers.routes
import pages._
import models._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "backLink" - {

      "must send the liability start date back to the formal review overview" in {
        navigator.backLink(CouncilTaxLiabilityStartDatePage, NormalMode, None) mustBe
          routes.ReviewOverviewController.formalReview()
      }

      "must send why the band is wrong back to the liability start date when responsible for less than six months" in {
        val answers = UserAnswers("id").set(scenarioResponsiblePage, scenarioResponsible.Lessthan).success.value
        navigator.backLink(WhyBandIsWrongPage, NormalMode, Some(answers)) mustBe
          routes.CouncilTaxLiabilityStartDateController.onPageLoad(NormalMode)
      }

      "must send why the band is wrong back to the notice date for a Notice of Alteration" in {
        val answers = UserAnswers("id")
          .set(scenarioResponsiblePage, scenarioResponsible.Morethan).success.value
          .set(noticeAlterationPage, noticeAlteration.Yes).success.value
        navigator.backLink(WhyBandIsWrongPage, NormalMode, Some(answers)) mustBe
          routes.DateNoticeController.onPageLoad(NormalMode)
      }

      "must send the notice date back to the formal review overview" in {
        val answers = UserAnswers("id").set(noticeAlterationPage, noticeAlteration.Yes).success.value
        navigator.backLink(DateNoticePage, NormalMode, Some(answers)) mustBe routes.ReviewOverviewController.formalReview()
        navigator.backLink(DateNoticePage, CheckMode, Some(answers)) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
      }

      "must route the notice date to why the band is wrong, or Check Your Answers in check mode" in {
        navigator.nextPage(DateNoticePage, NormalMode, UserAnswers("id")) mustBe routes.WhyBandIsWrongController.onPageLoad(NormalMode)
        navigator.nextPage(DateNoticePage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
      }

      "must send why the band is wrong back to the informal review overview otherwise" in {
        val answers = UserAnswers("id")
          .set(scenarioResponsiblePage, scenarioResponsible.Morethan).success.value
          .set(noticeAlterationPage, noticeAlteration.No).success.value
        navigator.backLink(WhyBandIsWrongPage, NormalMode, Some(answers)) mustBe
          routes.ReviewOverviewController.informalReview()
      }

      "must send responsible back to the selected property, or postcode search without one" in {
        val answers = UserAnswers("id").set(PropertyIdPage, "abc-123").success.value
        navigator.backLink(CouncilTaxResponsiblePage, NormalMode, Some(answers)) mustBe
          routes.PropertyDetailController.onPageLoad("abc-123")
        navigator.backLink(CouncilTaxResponsiblePage, NormalMode, None) mustBe
          routes.PostcodeSearchController.onPageLoad(NormalMode)
      }

      "must send each question back to the question before it" in {
        navigator.backLink(RequestCouncilTaxBandReviewPage, NormalMode, None) mustBe routes.CouncilTaxResponsibleController.onPageLoad(NormalMode)
        navigator.backLink(ReasonForReviewPage, NormalMode, None) mustBe routes.RequestCouncilTaxBandReviewController.onPageLoad(NormalMode)
        navigator.backLink(scenarioResponsiblePage, NormalMode, None) mustBe routes.ReasonForReviewController.onPageLoad(NormalMode)
        navigator.backLink(deletionOptionsPage, NormalMode, None) mustBe routes.ReasonForReviewController.onPageLoad(NormalMode)
        navigator.backLink(noticeAlterationPage, NormalMode, None) mustBe routes.scenarioResponsibleController.onPageLoad(NormalMode)
      }

      "must send the review overview back to the question that led to it" in {
        val lessThan = UserAnswers("id").set(scenarioResponsiblePage, scenarioResponsible.Lessthan).success.value
        val moreThan = UserAnswers("id").set(scenarioResponsiblePage, scenarioResponsible.Morethan).success.value
        navigator.reviewOverviewBackLink(ReviewType.Formal, Some(lessThan)) mustBe routes.scenarioResponsibleController.onPageLoad(NormalMode)
        navigator.reviewOverviewBackLink(ReviewType.Formal, Some(moreThan)) mustBe routes.noticeAlterationController.onPageLoad(NormalMode)
        navigator.reviewOverviewBackLink(ReviewType.Informal, Some(moreThan)) mustBe routes.noticeAlterationController.onPageLoad(NormalMode)
      }

      "must send Check Your Answers back to why the band is wrong" in {
        navigator.checkYourAnswersBackLink mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
      }

      "must send each evidence page back to the page before it" in {
        navigator.backLink(HasEvidencePage, NormalMode, None) mustBe routes.WhyBandIsWrongController.onPageLoad(NormalMode)
        navigator.backLink(UploadEvidencePage, NormalMode, None) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
        navigator.backLink(ContinueWithoutEvidencePage, NormalMode, None) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
      }

      "must send both pages back to Check Your Answers in Check mode" in {
        navigator.backLink(CouncilTaxLiabilityStartDatePage, CheckMode, None) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
        navigator.backLink(WhyBandIsWrongPage, CheckMode, None) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
      }
    }

    "in Normal mode" - {

      "must route the liability start date to the reason textarea" in {
        navigator.nextPage(CouncilTaxLiabilityStartDatePage, NormalMode, UserAnswers("id")) mustBe
          routes.WhyBandIsWrongController.onPageLoad(NormalMode)
      }

      "must route the reason textarea to the evidence question" in {
        navigator.nextPage(WhyBandIsWrongPage, NormalMode, UserAnswers("id")) mustBe
          routes.HasEvidenceController.onPageLoad(NormalMode)
      }

      "must route a yes to having evidence to the upload page" in {
        val answers = UserAnswers("id").set(HasEvidencePage, true).success.value
        navigator.nextPage(HasEvidencePage, NormalMode, answers) mustBe routes.UploadEvidenceController.onPageLoad(NormalMode)
      }

      "must route a no to having evidence to the continue without evidence page" in {
        val answers = UserAnswers("id").set(HasEvidencePage, false).success.value
        navigator.nextPage(HasEvidencePage, NormalMode, answers) mustBe routes.ContinueWithoutEvidenceController.onPageLoad()
      }

      "must route a changed evidence answer back to Check Your Answers, collecting a file first when needed" in {
        val yesWithoutFile = UserAnswers("id").set(HasEvidencePage, true).success.value
        val yesWithFile = yesWithoutFile.set(UploadEvidencePage, Seq(UploadedEvidence("a.pdf", "application/pdf", 1))).success.value
        val no = UserAnswers("id").set(HasEvidencePage, false).success.value

        navigator.nextPage(HasEvidencePage, CheckMode, yesWithoutFile) mustBe routes.UploadEvidenceController.onPageLoad(CheckMode)
        navigator.nextPage(HasEvidencePage, CheckMode, yesWithFile) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
        navigator.nextPage(HasEvidencePage, CheckMode, no) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
        navigator.nextPage(UploadEvidencePage, CheckMode, yesWithFile) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
      }

      "must collect any question the current path still needs before returning to Check Your Answers" in {
        val base = UserAnswers("id").set(ReasonForReviewPage, ReasonForReview.Bandiswrong).success.value

        navigator.nextPage(ReasonForReviewPage, CheckMode, base) mustBe routes.scenarioResponsibleController.onPageLoad(CheckMode)

        val moreThan = base.set(scenarioResponsiblePage, scenarioResponsible.Morethan).success.value
        navigator.nextPage(scenarioResponsiblePage, CheckMode, moreThan) mustBe routes.noticeAlterationController.onPageLoad(CheckMode)

        val noticeYes = moreThan.set(noticeAlterationPage, noticeAlteration.Yes).success.value
        navigator.nextPage(noticeAlterationPage, CheckMode, noticeYes) mustBe routes.DateNoticeController.onPageLoad(CheckMode)

        val acting = noticeYes.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value
          .set(DateNoticePage, java.time.LocalDate.of(2026, 1, 1)).success.value
          .set(WhyBandIsWrongPage, "reason").success.value
          .set(HasEvidencePage, false).success.value
          .set(WhichBandPage, WhichBand.Bandc).success.value
          .set(ContactDetailsPage, ContactDetails("a", "b", None, None, None, None, None, None, None, Set("email"))).success.value
        navigator.nextPage(CouncilTaxResponsiblePage, CheckMode, acting) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(CheckMode)

        val complete = acting.set(CouncilTaxPayerDetailsPage, CouncilTaxPayerDetails("p", "q", "p@example.com")).success.value
        navigator.nextPage(CouncilTaxResponsiblePage, CheckMode, complete) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
      }

      "must drop answers that no longer apply when an earlier answer changes" in {
        val all = UserAnswers("id")
          .set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value
          .set(CouncilTaxPayerDetailsPage, CouncilTaxPayerDetails("p", "q", "p@example.com")).success.value
          .set(scenarioResponsiblePage, scenarioResponsible.Morethan).success.value
          .set(noticeAlterationPage, noticeAlteration.Yes).success.value
          .set(DateNoticePage, java.time.LocalDate.of(2026, 1, 1)).success.value

        all.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.Yes).success.value.get(CouncilTaxPayerDetailsPage) mustBe None
        all.set(scenarioResponsiblePage, scenarioResponsible.Lessthan).success.value.get(DateNoticePage) mustBe None
        all.set(noticeAlterationPage, noticeAlteration.No).success.value.get(DateNoticePage) mustBe None
        all.set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value.get(scenarioResponsiblePage) mustBe None
      }

      "must route an unanswered evidence question back to itself" in {
        navigator.nextPage(HasEvidencePage, NormalMode, UserAnswers("id")) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
      }

      "must route both evidence outcomes to the which band page" in {
        val expected = routes.WhichBandController.onPageLoad(NormalMode)
        navigator.nextPage(UploadEvidencePage, NormalMode, UserAnswers("id")) mustBe expected
        navigator.nextPage(ContinueWithoutEvidencePage, NormalMode, UserAnswers("id")) mustBe expected
      }

      "must route which band to contact details, and Check Your Answers in check mode" in {
        navigator.nextPage(WhichBandPage, NormalMode, UserAnswers("id")) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        navigator.nextPage(WhichBandPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
      }

        "must navigate the contact pages" in {
        val acting = UserAnswers("id").set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value
        navigator.nextPage(WhichBandPage, NormalMode, acting) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
        navigator.nextPage(CouncilTaxPayerDetailsPage, NormalMode, acting) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        navigator.nextPage(ContactDetailsPage, NormalMode, acting) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
        navigator.nextPage(ContactDetailsPage, CheckMode, acting) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
        navigator.backLink(CouncilTaxPayerDetailsPage, NormalMode, Some(acting)) mustBe routes.WhichBandController.onPageLoad(NormalMode)
        navigator.backLink(ContactDetailsPage, NormalMode, Some(acting)) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
        navigator.backLink(ContactDetailsPage, NormalMode, Some(UserAnswers("id"))) mustBe routes.WhichBandController.onPageLoad(NormalMode)
      }

      "must link back from which band to the evidence page the user came from" in {
        val yes = UserAnswers("id").set(HasEvidencePage, true).success.value
        val no  = UserAnswers("id").set(HasEvidencePage, false).success.value
        navigator.backLink(WhichBandPage, NormalMode, Some(yes)) mustBe routes.UploadEvidenceController.onPageLoad(NormalMode)
        navigator.backLink(WhichBandPage, NormalMode, Some(no)) mustBe routes.ContinueWithoutEvidenceController.onPageLoad()
        navigator.backLink(WhichBandPage, CheckMode, Some(yes)) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
      }

      "must route less than six months to the formal review overview" in {
        val answers = UserAnswers("id")
          .set(scenarioResponsiblePage, scenarioResponsible.Lessthan)
          .success
          .value

        navigator.nextPage(scenarioResponsiblePage, NormalMode, answers) mustBe
          routes.ReviewOverviewController.formalReview()
      }

      "must route more than six months to the Notice of Alteration question" in {
        val answers = UserAnswers("id")
          .set(scenarioResponsiblePage, scenarioResponsible.Morethan)
          .success
          .value

        navigator.nextPage(scenarioResponsiblePage, NormalMode, answers) mustBe
          routes.noticeAlterationController.onPageLoad(NormalMode)
      }

      "must route a Notice of Alteration yes answer to the formal review overview" in {
        val answers = UserAnswers("id")
          .set(noticeAlterationPage, noticeAlteration.Yes)
          .success
          .value

        navigator.nextPage(noticeAlterationPage, NormalMode, answers) mustBe
          routes.ReviewOverviewController.formalReview()
      }

      "must route a Notice of Alteration no answer to the informal review overview" in {
        val answers = UserAnswers("id")
          .set(noticeAlterationPage, noticeAlteration.No)
          .success
          .value

        navigator.nextPage(noticeAlterationPage, NormalMode, answers) mustBe
          routes.ReviewOverviewController.informalReview()
      }

      "must go from a page that doesn't exist in the route map to journey recovery" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.JourneyRecoveryController.onPageLoad()
      }
    }

    "in Check mode" - {

      "must return the reason textarea to Check Your Answers after editing" in {
        navigator.nextPage(WhyBandIsWrongPage, CheckMode, UserAnswers("id")) mustBe
          routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Challenge)
      }
    }
  }
}