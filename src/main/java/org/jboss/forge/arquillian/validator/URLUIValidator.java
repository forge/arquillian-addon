package org.jboss.forge.arquillian.validator;

import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.util.Strings;

public class URLUIValidator implements UIValidator {

    @Override
    public void validate(UIValidationContext context) {
        InputComponent<?, ?> currentInputComponent = context.getCurrentInputComponent();
        Object value = InputComponents.getValueFor(currentInputComponent);
        if (value != null) {
            String url = value.toString();

            if (! Strings.isURL(url)) {
                context.addValidationError(currentInputComponent, String.format("URL %s has not valid format.", url));
            }
        }
    }
}
