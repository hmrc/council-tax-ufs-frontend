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
import forms.PropertyChangeDetailsFormProvider
import models.{Mode, PropertyChangePrefix, UserAnswers}
import navigation.Navigator
import pages.PropertyChangeDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PropertyChangeDetailsView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/** "How have your properties been merged?" or "How has the property been split?", worded for a merged or a split property by the answer to the split or merge question. */
@Singleton
class PropertyChangeDetailsController @Inject()(
                                override val messagesApi: MessagesApi,
                                sessionRepository: SessionRepository,
                                navigator: Navigator,
                                identify: IdentifierAction,
                                getData: DataRetrievalAction,
                                requireData: DataRequiredAction,
                                formProvider: PropertyChangeDetailsFormProvider,
                                val controllerComponents: MessagesControllerComponents,
                                view: PropertyChangeDetailsView
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def prefix(answers: UserAnswers): String = PropertyChangePrefix.details(Some(answers))

  private def backLink(mode: Mode, answers: UserAnswers): Option[String] =
    Some(navigator.backLink(PropertyChangeDetailsPage, mode, Some(answers)).url)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val form         = formProvider(prefix(request.userAnswers))
      val preparedForm = request.userAnswers.get(PropertyChangeDetailsPage).fold(form)(form.fill)

      Ok(view(preparedForm, mode, backLink(mode, request.userAnswers), prefix(request.userAnswers)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      formProvider(prefix(request.userAnswers)).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, backLink(mode, request.userAnswers), prefix(request.userAnswers)))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PropertyChangeDetailsPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PropertyChangeDetailsPage, mode, updatedAnswers))
      )
  }
}
