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
