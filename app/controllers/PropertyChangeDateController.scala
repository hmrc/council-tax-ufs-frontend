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

import controllers.actions.{DataRetrievalAction, IdentifierAction}
import forms.PropertyChangeDateFormProvider
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.{PropertyMergedDatePage, PropertySplitDatePage, QuestionPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PropertyChangeDateView

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/** "When was the work to merge your properties completed?" and "When was the work to split your home completed?". */
@Singleton
class PropertyChangeDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  formProvider: PropertyChangeDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PropertyChangeDateView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoadMerged(mode: Mode): Action[AnyContent] =
    onPageLoad(PropertyMergedDatePage, routes.PropertyChangeDateController.onSubmitMerged(mode), mode)

  def onSubmitMerged(mode: Mode): Action[AnyContent] =
    onSubmit(PropertyMergedDatePage, routes.PropertyChangeDateController.onSubmitMerged(mode), mode)

  def onPageLoadSplit(mode: Mode): Action[AnyContent] =
    onPageLoad(PropertySplitDatePage, routes.PropertyChangeDateController.onSubmitSplit(mode), mode)

  def onSubmitSplit(mode: Mode): Action[AnyContent] =
    onSubmit(PropertySplitDatePage, routes.PropertyChangeDateController.onSubmitSplit(mode), mode)

  private def onPageLoad(page: QuestionPage[LocalDate], action: Call, mode: Mode): Action[AnyContent] =
    (identify andThen getData) { implicit request =>
      val form         = formProvider(page.toString)
      val preparedForm = request.userAnswers.flatMap(_.get(page)).fold(form)(form.fill)
      Ok(view(preparedForm, page.toString, action, navigator.backLink(page, mode, request.userAnswers).url))
    }

  private def onSubmit(page: QuestionPage[LocalDate], action: Call, mode: Mode): Action[AnyContent] =
    (identify andThen getData).async { implicit request =>
      formProvider(page.toString).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, page.toString, action, navigator.backLink(page, mode, request.userAnswers).url))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.getOrElse(UserAnswers(request.userId)).set(page, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(page, mode, updatedAnswers))
      )
    }
}
