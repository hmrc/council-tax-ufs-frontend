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
import services.submission.PageCatalogue.Entry
import models.submission.SubmissionPayload
import play.api.Configuration

import javax.inject.{Inject, Singleton}

/** Every journey the service can submit, read from conf/journeys.conf.
  *
  * The journey is chosen from the user's selections, not guessed from the codes: journeys 5/6/7/8 all carry
  * exactly S01, P02 and I11, and 9/10 both carry S01 and P02, so only the premise (J05...J10) tells them apart.
  * The codes are used afterwards to check that what was built really is the journey that was selected.
  *
  * A config that is invalid (see `JourneyRegistry.problems`) stops the application starting.
  */
@Singleton
class JourneyRegistry @Inject()(configuration: Configuration) {

  val all: Seq[JourneyDefinition] = JourneyConfig.read(configuration.underlying)

  JourneyRegistry.problems(all) match {
    case Seq() => ()
    case found => throw new IllegalStateException(s"Invalid journeys.conf: ${found.mkString("; ")}")
  }

  /** The one journey the user's selections lead to. */
  def select(answers: UserAnswers): Either[String, JourneyDefinition] =
    all.filter(_.selected(answers)) match {
      case Seq(only) => Right(only)
      case Seq()     => Left("No journey matches the selections made")
      case several   => Left(s"Selections match more than one journey: ${several.map(_.key).mkString(", ")}")
    }

  /** The journey a built payload belongs to, judged only by its premise and the codes it carries. */
  def identify(payload: SubmissionPayload): Either[String, JourneyDefinition] =
    payload.premises match {
      case Seq(premise) => identify(premise.premise.code, payload.codes)
      case other        => Left(s"Expected one premise, found ${other.size}")
    }

  /** The same, from the premise code and the codes of everything else sent with it. */
  def identify(premiseCode: String, codes: Seq[String]): Either[String, JourneyDefinition] = {
    val others = codes.toSet - premiseCode
    all.filter(d => d.premise.code == premiseCode && d.requiredCodes.subsetOf(others) && others.subsetOf(d.allCodes)) match {
      case Seq(only) => Right(only)
      case Seq()     => Left(s"No journey has premise $premiseCode with codes ${others.toSeq.sorted.mkString(", ")}")
      case several   => Left(s"Codes fit more than one journey: ${several.map(_.key).mkString(", ")}")
    }
  }
}

object JourneyRegistry {

  private def conditionProblems(key: String, condition: Condition): Seq[String] =
    condition.requirements.toSeq.flatMap { case (page, values) =>
      PageCatalogue.entries.get(page) match {
        case None                                     => Seq(s"$key: unknown page '$page'")
        case Some(Entry(_, _, Some(options)))         => values.diff(options).toSeq.map(v => s"$key: '$v' is not an option of page '$page'")
        case Some(_)                                  => Seq(s"$key: page '$page' has no fixed options to match on")
      }
    }

  private val questionCode = "[SIP][0-9]{2}"

  /** What is wrong with a set of journeys, if anything. */
  def problems(journeys: Seq[JourneyDefinition]): Seq[String] = {
    val duplicateKeys = journeys.map(_.key).diff(journeys.map(_.key).distinct).distinct.map(k => s"journey $k is defined twice")

    val perJourney = journeys.flatMap { j =>
      Seq(
        Option.when(!j.premise.code.matches("J[0-9]{2}"))(s"${j.key}: premise code ${j.premise.code} must look like J01"),
        Option.when(j.selectors.requirements.isEmpty)(s"${j.key}: has no selectors, so it would match every user")
      ).flatten ++
        j.questions.filterNot(_.code.matches(questionCode)).map(q => s"${j.key}: question code ${q.code} must look like S01, I01 or P01") ++
        j.questions.filter(_.when == Condition.always).groupBy(_.code).collect { case (code, qs) if qs.size > 1 => s"${j.key}: code $code is used more than once" }
    }

    // two journeys with the same premise whose codes could be the same would be indistinguishable in a payload
    val ambiguous = for {
      a <- journeys
      b <- journeys if a.key < b.key && a.premise.code == b.premise.code
      if (a.requiredCodes ++ b.requiredCodes).subsetOf(a.allCodes.intersect(b.allCodes))
    } yield s"${a.key} and ${b.key} share premise ${a.premise.code} and could carry the same codes"

    // every page and option named in the config must exist, and be used as the type of answer it holds
    val unknown = journeys.flatMap { j =>
      (j.premise +: j.questions).flatMap { q =>
        PageCatalogue.entries.get(q.page) match {
          case None                                      => Seq(s"${j.key}: ${q.code} uses unknown page '${q.page}'")
          case Some(e) if e.answerType != q.answerType => Seq(s"${j.key}: ${q.code} reads page '${q.page}' as ${q.answerType} but it holds ${e.answerType}")
          case Some(_)                                   => conditionProblems(j.key, q.when)
        }
      } ++ conditionProblems(j.key, j.selectors)
    }.distinct

    // two journeys that one set of answers could select would be a coin toss
    val overlapping = for {
      a <- journeys
      b <- journeys if a.key < b.key
      shared = a.selectors.requirements.keySet.intersect(b.selectors.requirements.keySet)
      if shared.forall(page => a.selectors.requirements(page).intersect(b.selectors.requirements(page)).nonEmpty)
    } yield s"${a.key} and ${b.key} can both be selected by the same answers"

    duplicateKeys ++ perJourney ++ ambiguous ++ unknown ++ overlapping
  }
}
