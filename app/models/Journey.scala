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

package models

import pages.ReasonForReviewPage
import play.api.mvc.PathBindable

/** Which journey the user is on, taken from the reason chosen on the review-reason page. */
enum Journey(val urlSegment: String) {
  case Challenge extends Journey("challenge")
  case Deletion  extends Journey("deletion")
}

object Journey {

  /** Challenge unless the user chose the property-changes reason; also the default before a reason is chosen. */
  def from(answers: UserAnswers): Journey =
    answers.get(ReasonForReviewPage) match {
      case Some(ReasonForReview.Changesaffectedproperty) => Deletion
      case _                                             => Challenge
    }

  /** The journey the URL names, or None when the reason hasn't been chosen yet. */
  def chosen(answers: UserAnswers): Option[Journey] =
    answers.get(ReasonForReviewPage).map(_ => from(answers))

  implicit val pathBindable: PathBindable[Journey] = new PathBindable[Journey] {
    override def bind(key: String, value: String): Either[String, Journey] =
      Journey.values.find(_.urlSegment == value).toRight("Invalid journey")

    override def unbind(key: String, value: Journey): String = value.urlSegment
  }
}
