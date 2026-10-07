package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class ReasonForReviewSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "ReasonForReview" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(ReasonForReview.values.toSeq)

      forAll(gen) {
        reasonForReview =>

          JsString(reasonForReview.toString).validate[ReasonForReview].asOpt.value mustEqual reasonForReview
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!ReasonForReview.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[ReasonForReview] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(ReasonForReview.values.toSeq)

      forAll(gen) {
        reasonForReview =>

          Json.toJson(reasonForReview) mustEqual JsString(reasonForReview.toString)
      }
    }
  }
}
