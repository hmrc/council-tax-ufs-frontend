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

package helpers

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.scalatest.{Assertion, BeforeAndAfterEach}
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{FakeRequest, Injecting}
import play.twirl.api.Html

trait ViewBaseSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with BeforeAndAfterEach with Matchers {
  def injector: Injector = app.injector
  implicit lazy val messages: Messages = MessagesImpl(Lang("en"), messagesApi)
  lazy val messagesApi: MessagesApi             = inject[MessagesApi]

  def element(cssSelector: String)(implicit document: Document): Element = {
    val elements = document.select(cssSelector)

    if(elements.size == 0) {
      fail(s"No element exists with the selector '$cssSelector'")
    }

    document.select(cssSelector).first()
  }

  lazy implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()


  def elementText(selector: String)(implicit document: Document): String = {
    element(selector).text()
  }

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  def elementExtinct(cssSelector: String)(implicit document: Document): Assertion = {
    val elements = document.select(cssSelector)

    if (elements.size == 0) {
      succeed
    } else {
      fail(s"Element with selector '$cssSelector' was found!")
    }
  }
}
