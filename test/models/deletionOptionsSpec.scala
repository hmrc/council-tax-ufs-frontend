package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class deletionOptionsSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "deletionOptions" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(deletionOptions.values.toSeq)

      forAll(gen) {
        deletionOptions =>

          JsString(deletionOptions.toString).validate[deletionOptions].asOpt.value mustEqual deletionOptions
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!deletionOptions.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[deletionOptions] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(deletionOptions.values.toSeq)

      forAll(gen) {
        deletionOptions =>

          Json.toJson(deletionOptions) mustEqual JsString(deletionOptions.toString)
      }
    }
  }
}
