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
import forms.RemoveEvidenceFormProvider
import models.{Mode, UploadedEvidence, UserAnswers}
import pages.UploadEvidencePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.RemoveEvidenceView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RemoveEvidenceController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  formProvider: RemoveEvidenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveEvidenceView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  private def backToUpload(mode: Mode): Result = Redirect(routes.UploadEvidenceController.onPageLoad(mode))

  private def find(answers: Option[UserAnswers], id: String): Option[UploadedEvidence] =
    answers.flatMap(_.get(UploadEvidencePage)).flatMap(_.find(_.id == id))

  def onPageLoad(id: String, mode: Mode): Action[AnyContent] = (identify andThen getData) { implicit request =>
    find(request.userAnswers, id).fold(backToUpload(mode))(file => Ok(view(form, file, mode)))
  }

  def onSubmit(id: String, mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    // stale link (file already removed, session expired) -> back to the upload page
    (find(request.userAnswers, id), request.userAnswers) match {
      case (Some(file), Some(answers)) =>
        form.bindFromRequest().fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, file, mode))),
          {
            case true =>
              val remaining = answers.get(UploadEvidencePage).getOrElse(Seq.empty).filterNot(_.id == id)
              for {
                updated <- Future.fromTry(answers.set(UploadEvidencePage, remaining))
                _       <- sessionRepository.set(updated)
              } yield backToUpload(mode)
            case false => Future.successful(backToUpload(mode))
          }
        )
      case _ => Future.successful(backToUpload(mode))
    }
  }
}
