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

      "must return from the property changes options to the reason question in Check mode" in {
        navigator.backLink(deletionOptionsPage, CheckMode, None) mustBe
          routes.ReasonForReviewController.onPageLoad(CheckMode)
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

      "must take another reason through the scenario-other page, what has changed and when it happened, to Contact Details" in {
        val answers = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.AnotherReason).success.value

        navigator.nextPage(deletionOptionsPage, NormalMode, answers) mustBe routes.ScenarioOtherController.onPageLoad()
        navigator.nextPage(WhatHasChangedPage, NormalMode, answers) mustBe routes.WhenDidChangeHappenController.onPageLoad(NormalMode)
        navigator.nextPage(WhenDidChangeHappenPage, NormalMode, answers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        navigator.nextPage(ContactDetailsPage, NormalMode, answers) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Deletion)
      }

      "must ask for the Council Tax payer details before contact details when acting for someone on another reason" in {
        val acting = UserAnswers("id")
          .set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.AnotherReason).success.value

        navigator.nextPage(WhenDidChangeHappenPage, NormalMode, acting) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
        navigator.nextPage(CouncilTaxPayerDetailsPage, NormalMode, acting) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        navigator.nextPage(ContactDetailsPage, NormalMode, acting) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Deletion)

        navigator.backLink(CouncilTaxPayerDetailsPage, NormalMode, Some(acting)) mustBe routes.WhenDidChangeHappenController.onPageLoad(NormalMode)
        navigator.backLink(ContactDetailsPage, NormalMode, Some(acting)) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)

        val answered = acting
          .set(WhatHasChangedPage, "Split").success.value
          .set(WhenDidChangeHappenPage, java.time.LocalDate.of(2025, 4, 27)).success.value
        navigator.nextPage(WhenDidChangeHappenPage, CheckMode, answered) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(CheckMode)

        val complete = answered
          .set(CouncilTaxPayerDetailsPage, CouncilTaxPayerDetails("p", "q", "p@example.com")).success.value
          .set(ContactDetailsPage, ContactDetails("a", "b", None, None, None, None, None, None, None, Set("email"))).success.value
        navigator.nextPage(ContactDetailsPage, CheckMode, complete) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Deletion)
      }

      "must take local area changes through the formal review, what changed and when, to Contact Details" in {
        val answers = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.Affected).success.value

        navigator.nextPage(deletionOptionsPage, NormalMode, answers) mustBe routes.ReviewOverviewController.formalReview()
        navigator.nextPage(WhatHasChangedPage, NormalMode, answers) mustBe routes.WhenDidChangeHappenController.onPageLoad(NormalMode)
        navigator.nextPage(WhenDidChangeHappenPage, NormalMode, answers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)

        navigator.reviewOverviewBackLink(ReviewType.Deletion, Some(answers)) mustBe routes.deletionOptionsController.onPageLoad(NormalMode)
        navigator.backLink(WhatHasChangedPage, NormalMode, Some(answers)) mustBe routes.ReviewOverviewController.formalReview()
        navigator.nextPage(deletionOptionsPage, CheckMode, answers) mustBe routes.WhatHasChangedController.onPageLoad(CheckMode)

        val acting = answers.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value
        navigator.nextPage(WhenDidChangeHappenPage, NormalMode, acting) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
      }

      "must give the another reason pages explicit back links" in {
        val answers = Some(
          UserAnswers("id")
            .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
            .set(deletionOptionsPage, deletionOptions.AnotherReason).success.value
        )

        navigator.backLink(scenarioOtherPage, NormalMode, answers) mustBe routes.deletionOptionsController.onPageLoad(NormalMode)
        navigator.backLink(WhatHasChangedPage, NormalMode, answers) mustBe routes.ScenarioOtherController.onPageLoad()
        navigator.backLink(WhenDidChangeHappenPage, NormalMode, answers) mustBe routes.WhatHasChangedController.onPageLoad(NormalMode)
        navigator.backLink(ContactDetailsPage, NormalMode, answers) mustBe routes.WhenDidChangeHappenController.onPageLoad(NormalMode)
        navigator.backLink(WhatHasChangedPage, CheckMode, answers) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Deletion)
      }

      "must ask for whichever another reason answer is missing in check mode" in {
        val base = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.AnotherReason).success.value

        navigator.nextPage(deletionOptionsPage, CheckMode, base) mustBe routes.WhatHasChangedController.onPageLoad(CheckMode)

        val withWhat = base.set(WhatHasChangedPage, "Split").success.value
        navigator.nextPage(WhatHasChangedPage, CheckMode, withWhat) mustBe routes.WhenDidChangeHappenController.onPageLoad(CheckMode)

        val withWhen = withWhat.set(WhenDidChangeHappenPage, java.time.LocalDate.of(2025, 4, 27)).success.value
        navigator.nextPage(WhenDidChangeHappenPage, CheckMode, withWhen) mustBe routes.ContactDetailsController.onPageLoad(CheckMode)

        val complete = withWhen.set(ContactDetailsPage, ContactDetails("a", "b", None, None, None, None, None, None, None, Set("email"))).success.value
        navigator.nextPage(ContactDetailsPage, CheckMode, complete) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Deletion)
      }

      "must drop the another reason answers when they no longer apply" in {
        val all = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.AnotherReason).success.value
          .set(WhatHasChangedPage, "Split").success.value
          .set(WhenDidChangeHappenPage, java.time.LocalDate.of(2025, 4, 27)).success.value

        val otherOption = all.set(deletionOptionsPage, deletionOptions.Split).success.value
        otherOption.get(WhatHasChangedPage) mustBe None
        otherOption.get(WhenDidChangeHappenPage) mustBe None

        all.set(deletionOptionsPage, deletionOptions.AnotherReason).success.value.get(WhatHasChangedPage) mustBe Some("Split")

        val bandChallenge = all.set(ReasonForReviewPage, ReasonForReview.Bandiswrong).success.value
        bandChallenge.get(WhatHasChangedPage) mustBe None
        bandChallenge.get(WhenDidChangeHappenPage) mustBe None
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

    "for a property that is derelict or cannot be lived in" - {

      def derelict(
        details:   Option[UninhabitableDetails] = None,
        evidence:  Option[Boolean]              = None,
        actingFor: Boolean                      = false
      ): UserAnswers = {
        val start = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.Demolished).success.value
          .set(PropertyUninhabitablePage, PropertyUninhabitable.Derelict).success.value
        val withDetails  = details.fold(start)(d => start.set(UninhabitableDetailsPage, d).success.value)
        val withEvidence = evidence.fold(withDetails)(e => withDetails.set(HasEvidencePage, e).success.value)
        if (actingFor) withEvidence.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value
        else withEvidence
      }

      val fire  = derelict(Some(UninhabitableDetails.FireDamage))
      val flood = derelict(Some(UninhabitableDetails.FloodDamage))
      val other = derelict(Some(UninhabitableDetails.Other))

      def next(page: Page, answers: UserAnswers) = navigator.nextPage(page, NormalMode, answers)
      def back(page: Page, answers: UserAnswers) = navigator.backLink(page, NormalMode, Some(answers))

      "must start from the deletion options and sign in at the formal review" in {
        next(deletionOptionsPage, derelict()) mustBe routes.PropertyUninhabitableController.onPageLoad(NormalMode)
        next(PropertyUninhabitablePage, derelict()) mustBe routes.ReviewOverviewController.formalReview()
        next(FormalReviewPage, derelict()) mustBe routes.UninhabitableDetailsController.onPageLoad(NormalMode)
        navigator.changesReviewType(derelict()) mustBe Some(ReviewType.Derelict)
      }

      "must not go past the property choice until one is made" in {
        val unanswered = UserAnswers("id").set(deletionOptionsPage, deletionOptions.Demolished).success.value
        next(PropertyUninhabitablePage, unanswered) mustBe routes.JourneyRecoveryController.onPageLoad()
        navigator.changesReviewType(unanswered) mustBe None
      }

      "must ask the same questions for fire damage and flood damage, ending at the evidence question" in {
        Seq(fire, flood).foreach { answers =>
          next(UninhabitableDetailsPage, answers) mustBe routes.UninhabitableDateController.onPageLoad(NormalMode)
          next(UninhabitableDatePage, answers) mustBe routes.UninhabitableDateLastOccupiedController.onPageLoad(NormalMode)
          next(UninhabitableDateLastOccupiedPage, answers) mustBe routes.UninhabitableDetailsPlanController.onPageLoad(NormalMode)
          next(UninhabitableDetailsPlanPage, answers) mustBe routes.UninhabitableDetailsPlanHabitableController.onPageLoad(NormalMode)
          next(UninhabitableDetailsPlanHabitablePage, answers) mustBe
            routes.UninhabitableDetailsPlanPlanningApplicationController.onPageLoad(NormalMode)
          next(UninhabitableDetailsPlanPlanningApplicationPage, answers) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
        }
      }

      "must send Other to its own details, reusing the date questions and skipping the plan questions" in {
        next(UninhabitableDetailsPage, other) mustBe routes.UninhabitableDetailsOtherController.onPageLoad(NormalMode)
        next(UninhabitableDetailsOtherPage, other) mustBe routes.UninhabitableDateController.onPageLoad(NormalMode)
        next(UninhabitableDatePage, other) mustBe routes.UninhabitableDateLastOccupiedController.onPageLoad(NormalMode)
        next(UninhabitableDateLastOccupiedPage, other) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
      }

      "must go from the evidence question to an upload or to continuing without evidence" in {
        next(HasEvidencePage, derelict(Some(UninhabitableDetails.FireDamage), Some(true))) mustBe
          routes.UploadEvidenceController.onPageLoad(NormalMode)
        next(HasEvidencePage, derelict(Some(UninhabitableDetails.FireDamage), Some(false))) mustBe
          routes.ContinueWithoutEvidenceController.onPageLoad()
      }

      "must finish at contact details, asking for payer details first when acting for someone" in {
        val own    = derelict(Some(UninhabitableDetails.FireDamage), Some(true))
        val acting = derelict(Some(UninhabitableDetails.FireDamage), Some(false), actingFor = true)

        next(UploadEvidencePage, own) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        next(ContinueWithoutEvidencePage, derelict(Some(UninhabitableDetails.FireDamage), Some(false))) mustBe
          routes.ContactDetailsController.onPageLoad(NormalMode)
        next(ContinueWithoutEvidencePage, acting) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
        next(CouncilTaxPayerDetailsPage, acting) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        next(ContactDetailsPage, own) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Deletion)
      }

      "must send the formal review and the questions back to the page before them" in {
        navigator.reviewOverviewBackLink(ReviewType.Derelict, Some(fire)) mustBe routes.PropertyUninhabitableController.onPageLoad(NormalMode)
        back(PropertyUninhabitablePage, fire) mustBe routes.deletionOptionsController.onPageLoad(NormalMode)
        back(UninhabitableDetailsPage, fire) mustBe routes.ReviewOverviewController.formalReview()
        back(UninhabitableDetailsOtherPage, other) mustBe routes.UninhabitableDetailsController.onPageLoad(NormalMode)
        back(UninhabitableDatePage, fire) mustBe routes.UninhabitableDetailsController.onPageLoad(NormalMode)
        back(UninhabitableDatePage, other) mustBe routes.UninhabitableDetailsOtherController.onPageLoad(NormalMode)
        back(UninhabitableDateLastOccupiedPage, fire) mustBe routes.UninhabitableDateController.onPageLoad(NormalMode)
        back(UninhabitableDetailsPlanPage, fire) mustBe routes.UninhabitableDateLastOccupiedController.onPageLoad(NormalMode)
        back(UninhabitableDetailsPlanHabitablePage, fire) mustBe routes.UninhabitableDetailsPlanController.onPageLoad(NormalMode)
        back(UninhabitableDetailsPlanPlanningApplicationPage, fire) mustBe
          routes.UninhabitableDetailsPlanHabitableController.onPageLoad(NormalMode)
      }

      "must send the evidence and contact questions back along the derelict path" in {
        val withUpload    = derelict(Some(UninhabitableDetails.FireDamage), Some(true))
        val withoutProof  = derelict(Some(UninhabitableDetails.FireDamage), Some(false))
        val otherUpload   = derelict(Some(UninhabitableDetails.Other), Some(true))
        val actingWithout = derelict(Some(UninhabitableDetails.FireDamage), Some(false), actingFor = true)

        back(HasEvidencePage, fire) mustBe routes.UninhabitableDetailsPlanPlanningApplicationController.onPageLoad(NormalMode)
        back(HasEvidencePage, other) mustBe routes.UninhabitableDateLastOccupiedController.onPageLoad(NormalMode)
        back(UploadEvidencePage, withUpload) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
        back(ContinueWithoutEvidencePage, withoutProof) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
        back(ContactDetailsPage, withUpload) mustBe routes.UploadEvidenceController.onPageLoad(NormalMode)
        back(ContactDetailsPage, otherUpload) mustBe routes.UploadEvidenceController.onPageLoad(NormalMode)
        back(ContactDetailsPage, withoutProof) mustBe routes.ContinueWithoutEvidenceController.onPageLoad()
        back(CouncilTaxPayerDetailsPage, actingWithout) mustBe routes.ContinueWithoutEvidenceController.onPageLoad()
        back(ContactDetailsPage, actingWithout) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
      }

      "in Check mode must ask for the first question the chosen path still needs" in {
        def check(answers: UserAnswers) = navigator.nextPage(UninhabitableDetailsPage, CheckMode, answers)

        // "Fire damage" changed to "Other": the extra details are asked, but not the plan questions
        check(derelict(Some(UninhabitableDetails.Other))) mustBe routes.UninhabitableDetailsOtherController.onPageLoad(CheckMode)
        check(derelict()) mustBe routes.UninhabitableDetailsController.onPageLoad(CheckMode)
        check(fire) mustBe routes.UninhabitableDateController.onPageLoad(CheckMode)
      }

      "in Check mode must return to the answers once the path is complete" in {
        val date = java.time.LocalDate.of(2025, 4, 27)
        val complete = derelict(Some(UninhabitableDetails.FireDamage), Some(false))
          .set(UninhabitableDatePage, date).success.value
          .set(UninhabitableDateLastOccupiedPage, date).success.value
          .set(UninhabitableDetailsPlanPage, "plan").success.value
          .set(UninhabitableDetailsPlanHabitablePage, "habitable").success.value
          .set(UninhabitableDetailsPlanPlanningApplicationPage, "planning").success.value

        // only the contact details are still missing
        navigator.nextPage(UninhabitableDatePage, CheckMode, complete) mustBe routes.ContactDetailsController.onPageLoad(CheckMode)
        navigator.nextPage(UninhabitableDatePage, CheckMode, complete
          .set(ContactDetailsPage, models.ContactDetails("A", "B", None, None, None, None, None, None, None, Set.empty)).success.value) mustBe
          routes.CheckYourAnswersController.onPageLoad(Journey.Deletion)
      }
    }

    "for a property that has been fully demolished" - {

      def demolished(
        details:   Option[UninhabitableDetails] = Some(UninhabitableDetails.FireDamage),
        evidence:  Option[Boolean]              = None,
        actingFor: Boolean                      = false
      ): UserAnswers = {
        val start        = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.Demolished).success.value
          .set(PropertyUninhabitablePage, PropertyUninhabitable.Demolished).success.value
        val withDetails  = details.fold(start)(d => start.set(UninhabitableDetailsPage, d).success.value)
        val withEvidence = evidence.fold(withDetails)(e => withDetails.set(HasEvidencePage, e).success.value)
        if (actingFor) withEvidence.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value
        else withEvidence
      }

      def next(page: Page, answers: UserAnswers) = navigator.nextPage(page, NormalMode, answers)
      def back(page: Page, answers: UserAnswers) = navigator.backLink(page, NormalMode, Some(answers))

      "must go from the property choice to its own formal review, then the reason" in {
        next(PropertyUninhabitablePage, demolished(None)) mustBe routes.ReviewOverviewController.formalReview()
        navigator.changesReviewType(demolished(None)) mustBe Some(ReviewType.Demolished)
        next(FormalReviewPage, demolished(None)) mustBe routes.UninhabitableDetailsController.onPageLoad(NormalMode)
      }

      "must ask the same questions whether the reason is fire, flood or other" in {
        Seq(UninhabitableDetails.FireDamage, UninhabitableDetails.FloodDamage, UninhabitableDetails.Other).foreach { reason =>
          val answers = demolished(Some(reason))
          next(UninhabitableDetailsPage, answers) mustBe routes.DemolishedDateController.onPageLoad(NormalMode)
          next(DemolishedDatePage, answers) mustBe routes.DemolishedDetailsController.onPageLoad(NormalMode)
          next(DemolishedDetailsPage, answers) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
        }
      }

      "must continue from evidence to the contact details, via the payer details when acting for someone" in {
        next(HasEvidencePage, demolished(evidence = Some(true))) mustBe routes.UploadEvidenceController.onPageLoad(NormalMode)
        next(HasEvidencePage, demolished(evidence = Some(false))) mustBe routes.ContinueWithoutEvidenceController.onPageLoad()
        next(UploadEvidencePage, demolished(evidence = Some(true))) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        next(UploadEvidencePage, demolished(evidence = Some(true), actingFor = true)) mustBe
          routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
      }

      "must have back links that follow the same path" in {
        val answers = demolished(evidence = Some(false))
        back(DemolishedDatePage, answers) mustBe routes.UninhabitableDetailsController.onPageLoad(NormalMode)
        back(DemolishedDetailsPage, answers) mustBe routes.DemolishedDateController.onPageLoad(NormalMode)
        back(HasEvidencePage, answers) mustBe routes.DemolishedDetailsController.onPageLoad(NormalMode)
        navigator.reviewOverviewBackLink(ReviewType.Demolished, Some(answers)) mustBe
          routes.PropertyUninhabitableController.onPageLoad(NormalMode)
      }

      "in Check mode must ask for the first demolished question still missing" in {
        val date = java.time.LocalDate.of(2025, 4, 27)
        val start = demolished(evidence = Some(false))
        navigator.nextPage(UninhabitableDetailsPage, CheckMode, start) mustBe routes.DemolishedDateController.onPageLoad(CheckMode)
        val withDate = start.set(DemolishedDatePage, date).success.value
        navigator.nextPage(DemolishedDatePage, CheckMode, withDate) mustBe routes.DemolishedDetailsController.onPageLoad(CheckMode)
        navigator.nextPage(DemolishedDetailsPage, CheckMode, withDate.set(DemolishedDetailsPage, "gone").success.value) mustBe
          routes.ContactDetailsController.onPageLoad(CheckMode)
      }
    }

    "for a property having major works or renovations done" - {

      def reno(evidence: Option[Boolean] = None, actingFor: Boolean = false): UserAnswers = {
        val start = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.Demolished).success.value
          .set(PropertyUninhabitablePage, PropertyUninhabitable.Reno).success.value
        val withEvidence = evidence.fold(start)(e => start.set(HasEvidencePage, e).success.value)
        if (actingFor) withEvidence.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value
        else withEvidence
      }

      def next(page: Page, answers: UserAnswers) = navigator.nextPage(page, NormalMode, answers)
      def back(page: Page, answers: UserAnswers) = navigator.backLink(page, NormalMode, Some(answers))

      "must go from the property choice to its own formal review, then the start date" in {
        next(PropertyUninhabitablePage, reno()) mustBe routes.ReviewOverviewController.formalReview()
        navigator.changesReviewType(reno()) mustBe Some(ReviewType.Reno)
        next(FormalReviewPage, reno()) mustBe routes.RenoStartDateController.onPageLoad(NormalMode)
      }

      "must ask for the dates and details, with no reason question, before the evidence" in {
        next(RenoStartDatePage, reno()) mustBe routes.RenoCompletionDateController.onPageLoad(NormalMode)
        next(RenoCompletionDatePage, reno()) mustBe routes.RenoDetailsController.onPageLoad(NormalMode)
        next(RenoDetailsPage, reno()) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
      }

      "must continue from evidence to the contact details, via the payer details when acting for someone" in {
        next(HasEvidencePage, reno(Some(true))) mustBe routes.UploadEvidenceController.onPageLoad(NormalMode)
        next(HasEvidencePage, reno(Some(false))) mustBe routes.ContinueWithoutEvidenceController.onPageLoad()
        next(UploadEvidencePage, reno(Some(true), actingFor = true)) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
      }

      "must have back links that follow the same path" in {
        back(RenoStartDatePage, reno()) mustBe routes.ReviewOverviewController.formalReview()
        back(RenoCompletionDatePage, reno()) mustBe routes.RenoStartDateController.onPageLoad(NormalMode)
        back(RenoDetailsPage, reno()) mustBe routes.RenoCompletionDateController.onPageLoad(NormalMode)
        back(HasEvidencePage, reno()) mustBe routes.RenoDetailsController.onPageLoad(NormalMode)
        navigator.reviewOverviewBackLink(ReviewType.Reno, Some(reno())) mustBe routes.PropertyUninhabitableController.onPageLoad(NormalMode)
      }

      "in Check mode must ask for the first reno question still missing" in {
        val date = java.time.LocalDate.of(2025, 4, 27)
        navigator.nextPage(PropertyUninhabitablePage, CheckMode, reno()) mustBe routes.RenoStartDateController.onPageLoad(CheckMode)
        val withStart = reno().set(RenoStartDatePage, date).success.value
        navigator.nextPage(RenoStartDatePage, CheckMode, withStart) mustBe routes.RenoCompletionDateController.onPageLoad(CheckMode)
        val withCompletion = withStart.set(RenoCompletionDatePage, date.plusDays(5)).success.value
        navigator.nextPage(RenoCompletionDatePage, CheckMode, withCompletion) mustBe routes.RenoDetailsController.onPageLoad(CheckMode)
      }
    }

    "for a property that has been partly demolished" - {

      def partDemo(evidence: Option[Boolean] = None): UserAnswers = {
        val start = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.Demolished).success.value
          .set(PropertyUninhabitablePage, PropertyUninhabitable.PartDemo).success.value
        evidence.fold(start)(e => start.set(HasEvidencePage, e).success.value)
      }

      def next(page: Page, answers: UserAnswers) = navigator.nextPage(page, NormalMode, answers)
      def back(page: Page, answers: UserAnswers) = navigator.backLink(page, NormalMode, Some(answers))

      "must go from the property choice to its own formal review, then the demolition date" in {
        next(PropertyUninhabitablePage, partDemo()) mustBe routes.ReviewOverviewController.formalReview()
        navigator.changesReviewType(partDemo()) mustBe Some(ReviewType.PartDemo)
        next(FormalReviewPage, partDemo()) mustBe routes.PartDemoDateController.onPageLoad(NormalMode)
      }

      "must ask for the date and details, with no reason question, before the evidence" in {
        next(PartDemoDatePage, partDemo()) mustBe routes.PartDemoDetailsController.onPageLoad(NormalMode)
        next(PartDemoDetailsPage, partDemo()) mustBe routes.HasEvidenceController.onPageLoad(NormalMode)
        next(HasEvidencePage, partDemo(Some(true))) mustBe routes.UploadEvidenceController.onPageLoad(NormalMode)
        next(HasEvidencePage, partDemo(Some(false))) mustBe routes.ContinueWithoutEvidenceController.onPageLoad()
      }

      "must have back links that follow the same path" in {
        back(PartDemoDatePage, partDemo()) mustBe routes.ReviewOverviewController.formalReview()
        back(PartDemoDetailsPage, partDemo()) mustBe routes.PartDemoDateController.onPageLoad(NormalMode)
        back(HasEvidencePage, partDemo()) mustBe routes.PartDemoDetailsController.onPageLoad(NormalMode)
        navigator.reviewOverviewBackLink(ReviewType.PartDemo, Some(partDemo())) mustBe routes.PropertyUninhabitableController.onPageLoad(NormalMode)
      }

      "in Check mode must ask for the first part demolished question still missing" in {
        val date = java.time.LocalDate.of(2025, 4, 27)
        navigator.nextPage(PropertyUninhabitablePage, CheckMode, partDemo()) mustBe routes.PartDemoDateController.onPageLoad(CheckMode)
        navigator.nextPage(PartDemoDatePage, CheckMode, partDemo().set(PartDemoDatePage, date).success.value) mustBe
          routes.PartDemoDetailsController.onPageLoad(CheckMode)
      }
    }

    "for a change of use to a home" - {

      def changedUse(use: Option[ChangeOfUse] = Some(ChangeOfUse.Domestic), actingFor: Boolean = false): UserAnswers = {
        val start = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.Changed).success.value
        val withUse = use.fold(start)(u => start.set(ChangeOfUsePage, u).success.value)
        if (actingFor) withUse.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value else withUse
      }

      def next(page: Page, answers: UserAnswers) = navigator.nextPage(page, NormalMode, answers)
      def back(page: Page, answers: UserAnswers) = navigator.backLink(page, NormalMode, Some(answers))

      "must ask how the use has changed, then go to the general formal review for a home" in {
        next(deletionOptionsPage, changedUse(None)) mustBe routes.ChangeOfUseController.onPageLoad(NormalMode)
        next(ChangeOfUsePage, changedUse()) mustBe routes.ReviewOverviewController.formalReview()
        navigator.changesReviewType(changedUse()) mustBe Some(ReviewType.Deletion)
        next(FormalReviewPage, changedUse()) mustBe routes.DomesticPartsController.onPageLoad(NormalMode)
      }

      "must ask the three home questions and go straight to the contact details, with no evidence" in {
        next(DomesticPartsPage, changedUse()) mustBe routes.DomesticDateController.onPageLoad(NormalMode)
        next(DomesticDatePage, changedUse()) mustBe routes.AddressChangedController.onPageLoad(NormalMode)
        next(AddressChangedPage, changedUse()) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        next(AddressChangedPage, changedUse(actingFor = true)) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
      }

      "must ask the business questions, then the shared address question, for a business" in {
        val business = changedUse(Some(ChangeOfUse.Business))
        next(ChangeOfUsePage, business) mustBe routes.ReviewOverviewController.formalReview()
        navigator.changesReviewType(business) mustBe Some(ReviewType.Deletion)
        next(FormalReviewPage, business) mustBe routes.BusinessPartsController.onPageLoad(NormalMode)
        next(BusinessPartsPage, business) mustBe routes.BusinessDateController.onPageLoad(NormalMode)
        next(BusinessDatePage, business) mustBe routes.AddressChangedController.onPageLoad(NormalMode)
        next(AddressChangedPage, business) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        back(BusinessDatePage, business) mustBe routes.BusinessPartsController.onPageLoad(NormalMode)
        back(AddressChangedPage, business) mustBe routes.BusinessDateController.onPageLoad(NormalMode)
      }

      "must not go past the use question until it is answered" in {
        next(ChangeOfUsePage, changedUse(None)) mustBe routes.JourneyRecoveryController.onPageLoad()
        navigator.changesReviewType(changedUse(None)) mustBe None
      }

      "must have back links that follow the same path" in {
        back(ChangeOfUsePage, changedUse()) mustBe routes.deletionOptionsController.onPageLoad(NormalMode)
        back(DomesticPartsPage, changedUse()) mustBe routes.ReviewOverviewController.formalReview()
        back(DomesticDatePage, changedUse()) mustBe routes.DomesticPartsController.onPageLoad(NormalMode)
        back(AddressChangedPage, changedUse()) mustBe routes.DomesticDateController.onPageLoad(NormalMode)
        back(ContactDetailsPage, changedUse()) mustBe routes.AddressChangedController.onPageLoad(NormalMode)
        navigator.reviewOverviewBackLink(ReviewType.Deletion, Some(changedUse())) mustBe routes.ChangeOfUseController.onPageLoad(NormalMode)
      }

      "in Check mode must ask for the first home question still missing" in {
        val date = java.time.LocalDate.of(2025, 4, 27)
        navigator.nextPage(ChangeOfUsePage, CheckMode, changedUse()) mustBe routes.DomesticPartsController.onPageLoad(CheckMode)
        val withParts = changedUse().set(DomesticPartsPage, "kitchen").success.value
        navigator.nextPage(DomesticPartsPage, CheckMode, withParts) mustBe routes.DomesticDateController.onPageLoad(CheckMode)
        val withDate = withParts.set(DomesticDatePage, date).success.value
        navigator.nextPage(DomesticDatePage, CheckMode, withDate) mustBe routes.AddressChangedController.onPageLoad(CheckMode)
      }
    }

    "for a split or merged property" - {

      def splitMerge(choice: Option[PropertySplitOrMerge] = Some(PropertySplitOrMerge.Split), actingFor: Boolean = false): UserAnswers = {
        val start = UserAnswers("id")
          .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
          .set(deletionOptionsPage, deletionOptions.Split).success.value
        val withChoice = choice.fold(start)(c => start.set(PropertySplitOrMergePage, c).success.value)
        if (actingFor) withChoice.set(CouncilTaxResponsiblePage, CouncilTaxResponsible.ActingFor).success.value else withChoice
      }

      def next(page: Page, answers: UserAnswers) = navigator.nextPage(page, NormalMode, answers)
      def back(page: Page, answers: UserAnswers) = navigator.backLink(page, NormalMode, Some(answers))

      "must ask the split or merge question, then the formal review, then the details" in {
        next(deletionOptionsPage, splitMerge(None)) mustBe routes.PropertySplitOrMergeController.onPageLoad(NormalMode)
        PropertySplitOrMerge.values.foreach { choice =>
          val answers = splitMerge(Some(choice))
          next(PropertySplitOrMergePage, answers) mustBe routes.ReviewOverviewController.formalReview()
          navigator.changesReviewType(answers) mustBe Some(ReviewType.SplitOrMerge)
          next(FormalReviewPage, answers) mustBe routes.PropertyChangeDetailsController.onPageLoad(NormalMode)
        }
      }

      "must ask the same questions for a split and a merge, with no evidence, ending at contact details" in {
        PropertySplitOrMerge.values.foreach { choice =>
          val answers = splitMerge(Some(choice))
          next(PropertyChangeDetailsPage, answers) mustBe routes.PropertyChangeDateController.onPageLoad(NormalMode)
          next(PropertyChangeDatePage, answers) mustBe routes.AddressChangedController.onPageLoad(NormalMode)
          next(AddressChangedPage, answers) mustBe routes.ContactDetailsController.onPageLoad(NormalMode)
        }
        next(AddressChangedPage, splitMerge(actingFor = true)) mustBe routes.CouncilTaxPayerDetailsController.onPageLoad(NormalMode)
      }

      "must have back links that follow the same path" in {
        val answers = splitMerge()
        back(PropertySplitOrMergePage, answers) mustBe routes.deletionOptionsController.onPageLoad(NormalMode)
        back(PropertyChangeDetailsPage, answers) mustBe routes.ReviewOverviewController.formalReview()
        back(PropertyChangeDatePage, answers) mustBe routes.PropertyChangeDetailsController.onPageLoad(NormalMode)
        back(AddressChangedPage, answers) mustBe routes.PropertyChangeDateController.onPageLoad(NormalMode)
        back(ContactDetailsPage, answers) mustBe routes.AddressChangedController.onPageLoad(NormalMode)
        navigator.reviewOverviewBackLink(ReviewType.SplitOrMerge, Some(answers)) mustBe routes.PropertySplitOrMergeController.onPageLoad(NormalMode)
      }

      "must not go past the split or merge question until it is answered" in {
        next(PropertySplitOrMergePage, splitMerge(None)) mustBe routes.JourneyRecoveryController.onPageLoad()
      }

      "in Check mode must ask for the first question still missing, then return to the answers" in {
        val date  = java.time.LocalDate.of(2025, 4, 27)
        val start = splitMerge()
        navigator.nextPage(PropertySplitOrMergePage, CheckMode, start) mustBe routes.PropertyChangeDetailsController.onPageLoad(CheckMode)
        val withDetails = start.set(PropertyChangeDetailsPage, "two flats").success.value
        navigator.nextPage(PropertyChangeDetailsPage, CheckMode, withDetails) mustBe routes.PropertyChangeDateController.onPageLoad(CheckMode)
        val withDate = withDetails.set(PropertyChangeDatePage, date).success.value
        navigator.nextPage(PropertyChangeDatePage, CheckMode, withDate) mustBe routes.AddressChangedController.onPageLoad(CheckMode)
        val complete = withDate.set(AddressChangedPage, "no").success.value
          .set(ContactDetailsPage, models.ContactDetails("A", "B", None, None, None, None, None, None, None, Set.empty)).success.value
        navigator.nextPage(PropertyChangeDatePage, CheckMode, complete) mustBe routes.CheckYourAnswersController.onPageLoad(Journey.Deletion)
      }
    }
  }
}
