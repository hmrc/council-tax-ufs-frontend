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

package pages

import base.SpecBase
import models._

import java.time.LocalDate

class ChangesPagesCleanupSpec extends SpecBase {

  private val date = LocalDate.of(2025, 4, 27)

  private val derelictFire = derelictAnswers()
    .set(UninhabitableDatePage, date).success.value
    .set(UninhabitableDateLastOccupiedPage, date).success.value
    .set(UninhabitableDetailsPlanPage, "plan").success.value
    .set(UninhabitableDetailsPlanHabitablePage, "habitable").success.value
    .set(UninhabitableDetailsPlanPlanningApplicationPage, "planning").success.value
    .set(HasEvidencePage, false).success.value

  "Changing the deletion option" - {
    "must drop the derelict answers, including evidence, when the property is no longer derelict" in {
      val result = derelictFire.set(deletionOptionsPage, deletionOptions.AnotherReason).success.value

      Seq[QuestionPage[?]](
        PropertyUninhabitablePage, UninhabitableDetailsPage, UninhabitableDatePage, UninhabitableDateLastOccupiedPage,
        UninhabitableDetailsPlanPage, UninhabitableDetailsPlanHabitablePage, UninhabitableDetailsPlanPlanningApplicationPage,
        HasEvidencePage
      ).foreach(page => result.data.value.contains(page.toString) mustBe false)
    }

    "must keep the derelict answers when the option is unchanged" in {
      val result = derelictFire.set(deletionOptionsPage, deletionOptions.Demolished).success.value
      result.get(UninhabitableDatePage) mustBe Some(date)
      result.get(HasEvidencePage) mustBe Some(false)
    }

    "must drop the change details when the property is demolished" in {
      val result = emptyUserAnswers
        .set(WhatHasChangedPage, "text").success.value
        .set(WhenDidChangeHappenPage, date).success.value
        .set(deletionOptionsPage, deletionOptions.Demolished).success.value

      result.get(WhatHasChangedPage) mustBe None
      result.get(WhenDidChangeHappenPage) mustBe None
    }
  }

  "Changing why the property cannot be lived in" - {
    "must drop the plan answers when it becomes Other, keeping the shared date answers" in {
      val result = derelictFire.set(UninhabitableDetailsPage, UninhabitableDetails.Other).success.value

      result.get(UninhabitableDetailsPlanPage) mustBe None
      result.get(UninhabitableDetailsPlanHabitablePage) mustBe None
      result.get(UninhabitableDetailsPlanPlanningApplicationPage) mustBe None
      result.get(UninhabitableDatePage) mustBe Some(date)
      result.get(UninhabitableDateLastOccupiedPage) mustBe Some(date)
    }

    "must drop the Other details when it becomes fire or flood damage" in {
      val other  = derelictAnswers(Some(UninhabitableDetails.Other)).set(UninhabitableDetailsOtherPage, "details").success.value
      val result = other.set(UninhabitableDetailsPage, UninhabitableDetails.FloodDamage).success.value

      result.get(UninhabitableDetailsOtherPage) mustBe None
    }
  }

  "Choosing that part of the property was demolished" - {
    "must drop the derelict, demolished and reno questions, including the shared reason, but keep evidence" in {
      val answers = derelictFire
        .set(DemolishedDatePage, date).success.value
        .set(RenoStartDatePage, date).success.value
        .set(PartDemoDatePage, date).success.value
      val result = answers.set(PropertyUninhabitablePage, PropertyUninhabitable.PartDemo).success.value

      result.get(UninhabitableDetailsPage) mustBe None
      result.get(UninhabitableDatePage) mustBe None
      result.get(DemolishedDatePage) mustBe None
      result.get(RenoStartDatePage) mustBe None
      result.get(PartDemoDatePage) mustBe Some(date)
      result.get(HasEvidencePage) mustBe Some(false)
    }

    "must drop the part demolished answers when another choice is made, or the deletion option changes" in {
      val answers = partDemoAnswers.set(PartDemoDatePage, date).success.value.set(PartDemoDetailsPage, "works").success.value
      answers.set(PropertyUninhabitablePage, PropertyUninhabitable.Derelict).success.value.get(PartDemoDatePage) mustBe None
      answers.set(deletionOptionsPage, deletionOptions.AnotherReason).success.value.get(PartDemoDetailsPage) mustBe None
    }
  }

  "Changing the reason for review" - {
    "must clear the derelict answers when moving to the band challenge, but leave its evidence" in {
      val result = derelictFire.set(ReasonForReviewPage, ReasonForReview.Bandiswrong).success.value

      result.get(deletionOptionsPage) mustBe None
      result.get(PropertyUninhabitablePage) mustBe None
      result.get(UninhabitableDatePage) mustBe None
      result.get(HasEvidencePage) mustBe Some(false)
    }

    "must keep the derelict answers when the property changes reason is chosen again" in {
      val result = derelictFire.set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value

      result.get(UninhabitableDatePage) mustBe Some(date)
      result.get(HasEvidencePage) mustBe Some(false)
    }
  }

  "Moving between the derelict and demolished journeys" - {
    val demolished = demolishedAnswers()
      .set(DemolishedDatePage, date).success.value
      .set(DemolishedDetailsPage, "gone").success.value
      .set(HasEvidencePage, false).success.value

    "must drop the demolished questions when the property is derelict" in {
      val result = demolished.set(PropertyUninhabitablePage, PropertyUninhabitable.Derelict).success.value
      result.get(DemolishedDatePage) mustBe None
      result.get(DemolishedDetailsPage) mustBe None
      result.get(UninhabitableDetailsPage) mustBe Some(UninhabitableDetails.FireDamage)
    }

    "must drop the derelict questions, but keep the shared reason and evidence, when the property is demolished" in {
      val result = derelictFire.set(PropertyUninhabitablePage, PropertyUninhabitable.Demolished).success.value
      result.get(UninhabitableDatePage) mustBe None
      result.get(UninhabitableDetailsPlanPage) mustBe None
      result.get(UninhabitableDetailsPage) mustBe Some(UninhabitableDetails.FireDamage)
      result.get(HasEvidencePage) mustBe Some(false)
    }

    "must keep the demolished answers when the choice is unchanged" in {
      val result = demolished.set(PropertyUninhabitablePage, PropertyUninhabitable.Demolished).success.value
      result.get(DemolishedDatePage) mustBe Some(date)
    }

    "must drop the demolished answers when the deletion option changes" in {
      val result = demolished.set(deletionOptionsPage, deletionOptions.AnotherReason).success.value
      result.get(DemolishedDatePage) mustBe None
      result.get(DemolishedDetailsPage) mustBe None
    }
  }

  "Moving to or from the major works or renovations journey" - {
    val reno = renoAnswers
      .set(RenoStartDatePage, date).success.value
      .set(RenoCompletionDatePage, date.plusDays(5)).success.value
      .set(RenoDetailsPage, "works").success.value
      .set(HasEvidencePage, false).success.value

    "must drop the reno questions when the property is derelict or demolished" in {
      Seq(PropertyUninhabitable.Derelict, PropertyUninhabitable.Demolished).foreach { choice =>
        val result = reno.set(PropertyUninhabitablePage, choice).success.value
        Seq[QuestionPage[?]](RenoStartDatePage, RenoCompletionDatePage, RenoDetailsPage)
          .foreach(page => result.data.value.contains(page.toString) mustBe false)
        result.get(HasEvidencePage) mustBe Some(false)
      }
    }

    "must drop the derelict and demolished questions, but keep evidence, when the property is having works done" in {
      val result = derelictFire.set(PropertyUninhabitablePage, PropertyUninhabitable.Reno).success.value
      result.get(UninhabitableDetailsPage) mustBe None
      result.get(UninhabitableDatePage) mustBe None
      result.get(HasEvidencePage) mustBe Some(false)
    }

    "must keep the reno answers when the choice is unchanged" in {
      reno.set(PropertyUninhabitablePage, PropertyUninhabitable.Reno).success.value.get(RenoDetailsPage) mustBe Some("works")
    }

    "must drop the reno answers when the deletion option changes" in {
      val result = reno.set(deletionOptionsPage, deletionOptions.AnotherReason).success.value
      result.get(RenoStartDatePage) mustBe None
      result.get(RenoDetailsPage) mustBe None
    }
  }

  "Change of use" - {
    val changed = changedUseAnswers
      .set(DomesticPartsPage, "kitchen").success.value
      .set(DomesticDatePage, date).success.value
      .set(AddressChangedPage, "no").success.value

    "must drop the home questions, but keep the shared address answer, when the use is a business" in {
      val result = changed.set(ChangeOfUsePage, ChangeOfUse.Business).success.value
      result.get(DomesticPartsPage) mustBe None
      result.get(DomesticDatePage) mustBe None
      result.get(AddressChangedPage) mustBe Some("no")
    }

    "must drop the business questions when the use is a home" in {
      val business = businessUseAnswers.set(BusinessPartsPage, "salon").success.value.set(BusinessDatePage, date).success.value
      val result   = business.set(ChangeOfUsePage, ChangeOfUse.Domestic).success.value
      result.get(BusinessPartsPage) mustBe None
      result.get(BusinessDatePage) mustBe None
    }

    "must keep the home questions when the choice is unchanged" in {
      changed.set(ChangeOfUsePage, ChangeOfUse.Domestic).success.value.get(DomesticPartsPage) mustBe Some("kitchen")
    }

    "must drop the change of use answers when another deletion option is chosen" in {
      Seq(deletionOptions.Demolished, deletionOptions.Affected, deletionOptions.AnotherReason).foreach { option =>
        val result = changed.set(deletionOptionsPage, option).success.value
        Seq[QuestionPage[?]](ChangeOfUsePage, DomesticPartsPage, DomesticDatePage, BusinessPartsPage, BusinessDatePage, AddressChangedPage)
          .foreach(page => result.data.value.contains(page.toString) mustBe false)
      }
    }

    "must drop the demolished journey when the option is a change of use" in {
      val result = derelictFire.set(deletionOptionsPage, deletionOptions.Changed).success.value
      result.get(PropertyUninhabitablePage) mustBe None
      result.get(UninhabitableDatePage) mustBe None
      result.get(HasEvidencePage) mustBe None
    }
  }
}
