package forms

import forms.behaviours.OptionFieldBehaviours
import models.CouncilTaxResponsible
import play.api.data.FormError

class CouncilTaxResponsibleFormProviderSpec extends OptionFieldBehaviours {

  val form = new CouncilTaxResponsibleFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "councilTaxResponsible.error.required"

    behave like optionsField[CouncilTaxResponsible](
      form,
      fieldName,
      validValues  = CouncilTaxResponsible.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
