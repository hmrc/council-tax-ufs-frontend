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

      "must send why the band is wrong back to the formal review overview for a Notice of Alteration" in {
        val answers = UserAnswers("id")
          .set(scenarioResponsiblePage, scenarioResponsible.Morethan).success.value
          .set(noticeAlterationPage, noticeAlteration.Yes).success.value
        navigator.backLink(WhyBandIsWrongPage, NormalMode, Some(answers)) mustBe
          routes.ReviewOverviewController.formalReview()
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
        navigator.checkYourAnswersBackLink mustBe routes.WhyBandIsWrongController.onPageLoad(NormalMode)
      }

      "must send both pages back to Check Your Answers in Check mode" in {
        navigator.backLink(CouncilTaxLiabilityStartDatePage, CheckMode, None) mustBe routes.CheckYourAnswersController.onPageLoad()
        navigator.backLink(WhyBandIsWrongPage, CheckMode, None) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }

    "in Normal mode" - {

      "must route the liability start date to the reason textarea" in {
        navigator.nextPage(CouncilTaxLiabilityStartDatePage, NormalMode, UserAnswers("id")) mustBe
          routes.WhyBandIsWrongController.onPageLoad(NormalMode)
      }

      "must route the reason textarea to Check Your Answers" in {
        navigator.nextPage(WhyBandIsWrongPage, NormalMode, UserAnswers("id")) mustBe
          routes.CheckYourAnswersController.onPageLoad()
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

      "must go from a page that doesn't exist in the route map to postcode search" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.PostcodeSearchController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" - {

      "must return the reason textarea to Check Your Answers after editing" in {
        navigator.nextPage(WhyBandIsWrongPage, CheckMode, UserAnswers("id")) mustBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}