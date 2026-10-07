package forms

import forms.behaviours.OptionFieldBehaviours
import models.ReasonForReview
import play.api.data.FormError

class ReasonForReviewFormProviderSpec extends OptionFieldBehaviours {

  val form = new ReasonForReviewFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "reasonForReview.error.required"

    behave like optionsField[ReasonForReview](
      form,
      fieldName,
      validValues  = ReasonForReview.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
