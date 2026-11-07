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

package controllers

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{CouncilTaxResponsible, Journey, ReasonForReview, scenarioResponsible}
import navigation.Navigator
import pages.{CouncilTaxResponsiblePage, ReasonForReviewPage, scenarioResponsiblePage}
import viewmodels.checkAnswers.{ContactDetailsSummary, CouncilTaxPayerDetailsSummary, CouncilTaxLiabilityStartDateSummary, EvidenceSummary, DateNoticeSummary, PropertyAddressSummary, ReasonForReviewSummary, WhichBandSummary, deletionOptionsSummary, noticeAlterationSummary, scenarioResponsibleSummary, WhyBandIsWrongSummary}
import play.api.i18n.{I18nSupport, MessagesApi}
import models.requests.DataRequest
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.govuk.summarylist._
import services.SubmissionService
import views.html.CheckYourAnswersView

import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                            navigator: Navigator,
                                            submissionService: SubmissionService
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  // A URL naming the other journey is sent to the page for the journey the user actually chose.
  private def wrongJourney(journey: Journey, answers: models.UserAnswers): Option[Result] =
    Journey.chosen(answers).filter(_ != journey).map(chosen => Redirect(routes.CheckYourAnswersController.onPageLoad(chosen)))

  def onPageLoad(journey: Journey): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      wrongJourney(journey, request.userAnswers).getOrElse(page(journey))
  }

  private def page(journey: Journey)(implicit request: DataRequest[AnyContent]): Result = {

      // Answers from a different path can linger in the session, so only show the rows for the journey being followed.
      val reason = request.userAnswers.get(ReasonForReviewPage)
      val bandChallenge = reason.contains(ReasonForReview.Bandiswrong)
      val propertyChanges = reason.contains(ReasonForReview.Changesaffectedproperty)
      val moreThanSixMonths = request.userAnswers.get(scenarioResponsiblePage).contains(scenarioResponsible.Morethan)

      val actingFor = request.userAnswers.get(CouncilTaxResponsiblePage).contains(CouncilTaxResponsible.ActingFor)

      val sections = Seq(
        "" -> Seq(
          PropertyAddressSummary.row(request.userAnswers),
          ReasonForReviewSummary.row(request.userAnswers),
          Option.when(bandChallenge)(scenarioResponsibleSummary.row(request.userAnswers)).flatten,
          Option.when(bandChallenge && moreThanSixMonths)(noticeAlterationSummary.row(request.userAnswers)).flatten,
          Option.when(propertyChanges)(deletionOptionsSummary.row(request.userAnswers)).flatten
        ).flatten,
        "checkYourAnswers.section.challengeDetails" -> Seq(
          CouncilTaxLiabilityStartDateSummary.row(request.userAnswers),
          DateNoticeSummary.row(request.userAnswers),
          WhyBandIsWrongSummary.row(request.userAnswers),
          EvidenceSummary.row(request.userAnswers),
          WhichBandSummary.row(request.userAnswers)
        ).flatten,
        "checkYourAnswers.section.councilTaxPayer" -> Option.when(actingFor)(CouncilTaxPayerDetailsSummary.rows(request.userAnswers)).toSeq.flatten,
        "checkYourAnswers.section.contactDetails" -> ContactDetailsSummary.rows(request.userAnswers),
        "checkYourAnswers.section.preferredContact" -> ContactDetailsSummary.preferredContactRows(request.userAnswers)
      ).collect {
        case (heading, rows) if rows.nonEmpty => heading -> SummaryListViewModel(rows)
      }

      Ok(view(sections, navigator.checkYourAnswersBackLink.url, actingFor, journey))
  }

  def onSubmit(journey: Journey): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      wrongJourney(journey, request.userAnswers) match {
        case Some(redirect) => Future.successful(redirect)
        case None =>
          submissionService.submit(request.userAnswers).map {
            case Right(_) => Redirect(routes.RequestSentController.onPageLoad())
            case Left(_)  => Redirect(routes.JourneyRecoveryController.onPageLoad())
          }
      }
  }
}
