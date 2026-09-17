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

import controllers.actions._
import forms.PostcodeSearchFormProvider
import javax.inject.Inject
import models.Mode
import utils.UrlEncryptor
import navigation.Navigator
import pages.PostcodeSearchPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PostcodeSearchView

import scala.concurrent.{ExecutionContext, Future}

class PostcodeSearchController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository:        SessionRepository,
  urlEncryptor:             UrlEncryptor,
  sessionIdentify:          SessionIdentifierAction,   // ← session only, no auth
  getData:                  DataRetrievalAction,
  formProvider:             PostcodeSearchFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view:                     PostcodeSearchView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] =
    (sessionIdentify andThen getData) { implicit request =>
      val preparedForm = request.userAnswers.flatMap(_.get(PostcodeSearchPage)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, mode))
    }

  def onSubmit(mode: Mode): Action[AnyContent] =
    (sessionIdentify andThen getData).async { implicit request =>
      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

  value =>
    for {
      answers        <- Future.successful(
                          request.userAnswers.getOrElse(
                            models.UserAnswers(request.userId)
                          )
                        )
      updatedAnswers <- Future.fromTry(answers.set(PostcodeSearchPage, value))
      _              <- sessionRepository.set(updatedAnswers)
    } yield {
      val encoded =  urlEncryptor.encrypt(value)
      Redirect(routes.SearchResultsController.show(encoded, 1))
    }
      )
    }
}