package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class WhichBandSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "WhichBand" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(WhichBand.values.toSeq)

      forAll(gen) {
        whichBand =>

          JsString(whichBand.toString).validate[WhichBand].asOpt.value mustEqual whichBand
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!WhichBand.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[WhichBand] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(WhichBand.values.toSeq)

      forAll(gen) {
        whichBand =>

          Json.toJson(whichBand) mustEqual JsString(whichBand.toString)
      }
    }
  }
}
