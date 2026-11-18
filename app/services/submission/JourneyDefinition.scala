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

package services.submission

import models.UserAnswers
import play.api.i18n.Messages
import play.api.libs.json.JsString

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

enum AnswerType {
  case Text, Date, Choice
}

object AnswerFormat {

  private val submissionDate = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

  def date(date: LocalDate): String = date.atStartOfDay.format(submissionDate)
}

/** Answers that must have particular values, by page name, e.g. `deletionOptions = demolished`. */
final case class Condition(requirements: Map[String, Set[String]]) {

  def holds(answers: UserAnswers): Boolean =
    requirements.forall { case (page, allowed) => Condition.value(answers, page).exists(allowed.contains) }
}

object Condition {

  val always: Condition = Condition(Map.empty)

  // pages hold their answer under their own name, and every answer a condition looks at is a plain string
  def value(answers: UserAnswers, page: String): Option[String] =
    (answers.data \ page).asOpt[String]
}

/** A question the journey asks, read from conf/journeys.conf. */
final case class QuestionSpec(
  code: String,
  questionKey: String,
  page: String,
  answerType: AnswerType,
  prefix: String,
  required: Boolean,
  when: Condition
) {

  def applies(answers: UserAnswers): Boolean = when.holds(answers)

  /** The answer as it is submitted, or None if it has not been given. */
  def answer(answers: UserAnswers, messages: Messages): Option[String] =
    (answers.data \ page).asOpt[JsString].map(_.value).flatMap { raw =>
      answerType match {
        case AnswerType.Text   => Some(raw.trim).filter(_.nonEmpty)
        case AnswerType.Date   => Try(LocalDate.parse(raw)).toOption.map(AnswerFormat.date)
        case AnswerType.Choice => Some(messages(s"$prefix.$raw"))
      }
    }
}

/** One submittable journey: the premise it is identified by, the questions it carries, and the selections that
  * lead to it. `number` is the journey id (1-10); journey 4 has four shapes, each with its own `key`.
  */
final case class JourneyDefinition(
  key: String,
  number: Int,
  name: String,
  premise: QuestionSpec,
  questions: Seq[QuestionSpec],
  selectors: Condition
) {

  def selected(answers: UserAnswers): Boolean = selectors.holds(answers)

  val allCodes: Set[String] = questions.map(_.code).toSet

  // codes every submission of this journey must contain, whichever way the conditional questions go
  val requiredCodes: Set[String] = questions.filter(_.required).map(_.code).toSet
}
