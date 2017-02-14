package org.jboss.forge.arquillian.util;

import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.util.Strings;

public class MavenCoordinatesUIValidator implements UIValidator {
    @Override
    public void validate(UIValidationContext context) {
        InputComponent<?, ?> currentInputComponent = context.getCurrentInputComponent();
        Object value = InputComponents.getValueFor(currentInputComponent);
        if (value != null) {
            String mavenCoordinates = value.toString();

            final int numberOfColons = Strings.count(mavenCoordinates, ':');
            //Checks that minimum format G:A:V or maximum format G:A:P:C:V fits for colons
            if (numberOfColons < 2 || numberOfColons > 4) {
                context.addValidationError(currentInputComponent, String.format("Maven coordinates %s has not valid format.", mavenCoordinates));
            }
        }
    }
}
