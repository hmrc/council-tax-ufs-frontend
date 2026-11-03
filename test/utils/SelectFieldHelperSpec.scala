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

package utils

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.Form
import play.api.data.Forms.*
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.test.Helpers.stubMessagesApi
import uk.gov.hmrc.govukfrontend.views.html.components.Text

class SelectFieldHelperSpec extends AnyFreeSpec with Matchers {

  private val messagesApi: MessagesApi = stubMessagesApi(
    Map("default" -> Map("prefix.band" -> "Band", "prefix.band.hint" -> "Choose a band", "prefix.status" -> "Status"))
  )
  private given Messages = messagesApi.preferred(Seq(Lang("en")))

  private val form = Form(tuple("band" -> optional(text), "status" -> optional(text)))
  private val options = Seq("" -> "All", "A" -> "Band A")

  "SelectFieldHelper.select" - {

    "must build a select with id, name, items, label and the bound value" in {
      val select = SelectFieldHelper.select(form.bind(Map("band" -> "A")), "prefix", "band", options)

      select.id mustBe "band"
      select.name mustBe "band"
      select.label.content mustBe Text("Band")
      select.value mustBe Some("A")
      select.items.map(i => i.value -> i.text) mustBe Seq(Some("") -> "All", Some("A") -> "Band A")
    }

    "must include a hint when a hint message exists" in {
      SelectFieldHelper.select(form, "prefix", "band", options).hint.map(_.content) mustBe Some(Text("Choose a band"))
    }

    "must omit the hint and value when there is no hint message or form value" in {
      val select = SelectFieldHelper.select(form, "prefix", "status", options)

      select.hint mustBe None
      select.value mustBe None
    }
  }
}
