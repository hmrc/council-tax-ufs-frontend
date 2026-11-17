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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.RenoCompletionDateFormProvider
import models.Mode
import navigation.Navigator
import pages.{RenoCompletionDatePage, RenoStartDatePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.RenoCompletionDateView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RenoCompletionDateController @Inject()(
                                override val messagesApi: MessagesApi,
                                sessionRepository: SessionRepository,
                                navigator: Navigator,
                                identify: IdentifierAction,
                                getData: DataRetrievalAction,
                                requireData: DataRequiredAction,
                                formProvider: RenoCompletionDateFormProvider,
                                val controllerComponents: MessagesControllerComponents,
                                view: RenoCompletionDateView
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def backLink(mode: Mode, answers: models.UserAnswers): Option[String] =
    Some(navigator.backLink(RenoCompletionDatePage, mode, Some(answers)).url)

  private def form(answers: models.UserAnswers)(implicit messages: play.api.i18n.Messages) =
    formProvider(answers.get(RenoStartDatePage))

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(RenoCompletionDatePage) match {
        case None        => form(request.userAnswers)
        case Some(value) => form(request.userAnswers).fill(value)
      }

      Ok(view(preparedForm, mode, backLink(mode, request.userAnswers)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form(request.userAnswers).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, backLink(mode, request.userAnswers)))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(RenoCompletionDatePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(RenoCompletionDatePage, mode, updatedAnswers))
      )
  }
}
