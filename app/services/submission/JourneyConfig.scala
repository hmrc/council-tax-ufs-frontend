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

import com.typesafe.config.{Config, ConfigValueType}

import scala.jdk.CollectionConverters._

/** Reads the journeys in conf/journeys.conf. A missing or malformed field throws, so a bad config stops the app
  * starting rather than failing a user's submission.
  */
object JourneyConfig {

  def read(config: Config): Seq[JourneyDefinition] =
    config.getConfigList("journeys.list").asScala.toSeq.map(journey)

  private def journey(c: Config): JourneyDefinition =
    JourneyDefinition(
      key       = c.getString("key"),
      number    = c.getInt("number"),
      name      = c.getString("name"),
      premise   = question(c.getConfig("premise")),
      questions = c.getConfigList("questions").asScala.toSeq.map(question),
      selectors = condition(c.getConfig("selectors"))
    )

  private def question(c: Config): QuestionSpec = {
    val page = c.getString("page")
    QuestionSpec(
      code        = c.getString("code"),
      questionKey = c.getString("question"),
      page        = page,
      answerType  = answerType(c.getString("type")),
      prefix      = if (c.hasPath("prefix")) c.getString("prefix") else page,
      required    = !c.hasPath("required") || c.getBoolean("required"),
      when        = if (c.hasPath("when")) condition(c.getConfig("when")) else Condition.always
    )
  }

  private def answerType(name: String): AnswerType =
    AnswerType.values.find(_.toString.equalsIgnoreCase(name))
      .getOrElse(throw new IllegalArgumentException(s"Unknown answer type '$name' in journeys.conf"))

  // each entry is a page name and either one value or a list of values it may have
  private def condition(c: Config): Condition =
    Condition(c.root.keySet.asScala.toSeq.map { page =>
      val value = c.getValue(page)
      page -> (value.valueType match {
        case ConfigValueType.LIST => c.getStringList(page).asScala.toSet
        case _                    => Set(c.getString(page))
      })
    }.toMap)
}
