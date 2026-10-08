package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class noticeAlterationSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "noticeAlteration" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(noticeAlteration.values.toSeq)

      forAll(gen) {
        noticeAlteration =>

          JsString(noticeAlteration.toString).validate[noticeAlteration].asOpt.value mustEqual noticeAlteration
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!noticeAlteration.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[noticeAlteration] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(noticeAlteration.values.toSeq)

      forAll(gen) {
        noticeAlteration =>

          Json.toJson(noticeAlteration) mustEqual JsString(noticeAlteration.toString)
      }
    }
  }
}
