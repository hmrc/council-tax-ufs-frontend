package forms

import forms.behaviours.OptionFieldBehaviours
import models.scenarioResponsible
import play.api.data.FormError

class scenarioResponsibleFormProviderSpec extends OptionFieldBehaviours {

  val form = new scenarioResponsibleFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "scenarioResponsible.error.required"

    behave like optionsField[scenarioResponsible](
      form,
      fieldName,
      validValues  = scenarioResponsible.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
