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
import forms.WhyBandIsWrongFormProvider
import models.Mode
import navigation.Navigator
import pages.WhyBandIsWrongPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.WhyBandIsWrongView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhyBandIsWrongController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  formProvider: WhyBandIsWrongFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhyBandIsWrongView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) { implicit request =>
    val form = formProvider()
    val preparedForm = request.userAnswers.flatMap(_.get(WhyBandIsWrongPage)).fold(form)(form.fill)
    Ok(view(preparedForm, mode, navigator.backLink(WhyBandIsWrongPage, mode, request.userAnswers).url))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val form = formProvider()

    form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, navigator.backLink(WhyBandIsWrongPage, mode, request.userAnswers).url))),
      value => {
        val answers = request.userAnswers.getOrElse(models.UserAnswers(request.userId))
        for {
          updatedAnswers <- Future.fromTry(answers.set(WhyBandIsWrongPage, value))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(WhyBandIsWrongPage, mode, updatedAnswers))
      }
    )
  }
}