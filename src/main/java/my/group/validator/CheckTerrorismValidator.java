package my.group.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckTerrorismValidator implements ConstraintValidator<CheckTerrorism, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }

        String str = s.toLowerCase();

        return !(str.equals("russia")
                || str.equals("zov")
                || str.equals("rus")
                || str.equals("dnr")
                || str.equals("lnr"));
    }


}
