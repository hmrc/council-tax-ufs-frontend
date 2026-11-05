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
import models.{Mode, UploadedEvidence, UserAnswers}
import navigation.Navigator
import pages.UploadEvidencePage
import org.apache.pekko.stream.Materializer
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.UploadEvidenceView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadEvidenceController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  val controllerComponents: MessagesControllerComponents,
  view: UploadEvidenceView
)(implicit ec: ExecutionContext, mat: Materializer) extends FrontendBaseController with I18nSupport {

  private val fieldName = "file"

  // Rejects oversize requests while the body is being read, with headroom for multipart framing. The inner
  // parser's own limit is higher so that this one answers first and the user gets an error on the page.
  private val requestLimit = UploadedEvidence.maxSizeInBytes + 64 * 1024

  private val multipartParser: BodyParser[Either[MaxSizeExceeded, MultipartFormData[TemporaryFile]]] =
    parse.maxLength(requestLimit, parse.multipartFormData(requestLimit * 2))

  private def backLink(mode: Mode, answers: Option[UserAnswers]): String =
    navigator.backLink(UploadEvidencePage, mode, answers).url

  private def uploaded(answers: Option[UserAnswers]): Seq[UploadedEvidence] =
    answers.flatMap(_.get(UploadEvidencePage)).getOrElse(Seq.empty)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) { implicit request =>
    Ok(view(uploaded(request.userAnswers), None, mode, backLink(mode, request.userAnswers)))
  }

  /** The first problem with the chosen file, as a message key. */
  private def validate(file: MultipartFormData.FilePart[TemporaryFile], existing: Seq[UploadedEvidence]): Option[String] =
    if (existing.size >= UploadedEvidence.maxFiles) Some("uploadEvidence.error.tooMany")
    else if (!UploadedEvidence.isAllowedType(file.filename, file.contentType)) Some("uploadEvidence.error.invalidType")
    else if (file.fileSize > UploadedEvidence.maxSizeInBytes) Some("uploadEvidence.error.tooBig")
    else if (existing.exists(_.fileName.equalsIgnoreCase(file.filename))) Some("uploadEvidence.error.duplicate")
    else None

  /** Handles both buttons: "upload" adds the file and stays here, "continue" adds any chosen file and moves on. */
  def onSubmit(mode: Mode): Action[Either[MaxSizeExceeded, MultipartFormData[TemporaryFile]]] =
    (identify andThen getData).async(multipartParser) { implicit request =>
      val existing = uploaded(request.userAnswers)

      def failure(errorKey: String): Future[Result] =
        Future.successful(BadRequest(view(existing, Some(errorKey), mode, backLink(mode, request.userAnswers))))

      def continue: Result =
        Redirect(navigator.nextPage(UploadEvidencePage, mode, request.userAnswers.getOrElse(UserAnswers(request.userId))))

      request.body match {
        case Left(_)     => failure("uploadEvidence.error.tooBig")
        case Right(body) =>
          val continuing = body.dataParts.get("action").exists(_.contains("continue"))

          body.file(fieldName).filter(_.filename.nonEmpty) match {
            case None if continuing && existing.nonEmpty => Future.successful(continue)
            case None                                    => failure("uploadEvidence.error.required")
            case Some(file) =>
              validate(file, existing) match {
                case Some(errorKey) => failure(errorKey)
                case None =>
                  val evidence = UploadedEvidence(file.filename, file.contentType.getOrElse(""), file.fileSize)
                  val answers  = request.userAnswers.getOrElse(UserAnswers(request.userId))
                  for {
                    updatedAnswers <- Future.fromTry(answers.set(UploadEvidencePage, existing :+ evidence))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield
                    if (continuing) Redirect(navigator.nextPage(UploadEvidencePage, mode, updatedAnswers))
                    else Redirect(routes.UploadEvidenceController.onPageLoad(mode)).flashing("uploaded" -> evidence.fileName)
              }
          }
      }
    }
}
