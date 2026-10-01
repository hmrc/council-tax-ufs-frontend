package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class RequestCouncilTaxBandReviewSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "RequestCouncilTaxBandReview" - {

    "must deserialise valid values" in {

      val gen = arbitrary[RequestCouncilTaxBandReview]

      forAll(gen) {
        requestCouncilTaxBandReview =>

          JsString(requestCouncilTaxBandReview.toString).validate[RequestCouncilTaxBandReview].asOpt.value mustEqual requestCouncilTaxBandReview
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!RequestCouncilTaxBandReview.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[RequestCouncilTaxBandReview] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[RequestCouncilTaxBandReview]

      forAll(gen) {
        requestCouncilTaxBandReview =>

          Json.toJson(requestCouncilTaxBandReview) mustEqual JsString(requestCouncilTaxBandReview.toString)
      }
    }
  }
}
