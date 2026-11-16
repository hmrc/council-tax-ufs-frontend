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

package base

import controllers.actions._
import models.{ChangeOfUse, ReasonForReview, PropertyUninhabitable, UninhabitableDetails, UserAnswers, deletionOptions}
import pages.{ChangeOfUsePage, PropertyUninhabitablePage, ReasonForReviewPage, UninhabitableDetailsPage, deletionOptionsPage}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest

trait SpecBase
  extends AnyFreeSpec
    with Matchers
    with TryValues
    with OptionValues
    with ScalaFutures
    with IntegrationPatience {

  val userAnswersId: String = "id"

  def emptyUserAnswers : UserAnswers = UserAnswers(userAnswersId)

  /** Answers for someone partway through the "derelict" journey, so back links follow that path. */
  def derelictAnswers(details: Option[UninhabitableDetails] = Some(UninhabitableDetails.FireDamage)): UserAnswers = {
    val start = emptyUserAnswers
      .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
      .set(deletionOptionsPage, deletionOptions.Demolished).success.value
      .set(PropertyUninhabitablePage, PropertyUninhabitable.Derelict).success.value
    details.fold(start)(d => start.set(UninhabitableDetailsPage, d).success.value)
  }

  /** Answers for someone partway through the "fully demolished" journey. */
  def demolishedAnswers(details: Option[UninhabitableDetails] = Some(UninhabitableDetails.FireDamage)): UserAnswers = {
    val start = emptyUserAnswers
      .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
      .set(deletionOptionsPage, deletionOptions.Demolished).success.value
      .set(PropertyUninhabitablePage, PropertyUninhabitable.Demolished).success.value
    details.fold(start)(d => start.set(UninhabitableDetailsPage, d).success.value)
  }

  /** Answers for someone partway through the "major works or renovations" journey. */
  def renoAnswers: UserAnswers =
    emptyUserAnswers
      .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
      .set(deletionOptionsPage, deletionOptions.Demolished).success.value
      .set(PropertyUninhabitablePage, PropertyUninhabitable.Reno).success.value

  /** Answers for someone partway through the "part of the property demolished" journey. */
  def partDemoAnswers: UserAnswers =
    emptyUserAnswers
      .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
      .set(deletionOptionsPage, deletionOptions.Demolished).success.value
      .set(PropertyUninhabitablePage, PropertyUninhabitable.PartDemo).success.value

  /** Answers for someone partway through the "change of use: now a home" journey. */
  def changedUseAnswers: UserAnswers =
    emptyUserAnswers
      .set(ReasonForReviewPage, ReasonForReview.Changesaffectedproperty).success.value
      .set(deletionOptionsPage, deletionOptions.Changed).success.value
      .set(ChangeOfUsePage, ChangeOfUse.Domestic).success.value

  /** Answers for someone partway through the "change of use: now a business" journey. */
  def businessUseAnswers: UserAnswers =
    changedUseAnswers.set(ChangeOfUsePage, ChangeOfUse.Business).success.value

  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
      )
}
