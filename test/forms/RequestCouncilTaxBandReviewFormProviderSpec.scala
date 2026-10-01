package forms

import forms.behaviours.CheckboxFieldBehaviours
import models.RequestCouncilTaxBandReview
import play.api.data.FormError

class RequestCouncilTaxBandReviewFormProviderSpec extends CheckboxFieldBehaviours {

  val form = new RequestCouncilTaxBandReviewFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "requestCouncilTaxBandReview.error.required"

    behave like checkboxField[RequestCouncilTaxBandReview](
      form,
      fieldName,
      validValues  = RequestCouncilTaxBandReview.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
