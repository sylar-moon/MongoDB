package my.group.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckForSwearsValidator  implements ConstraintValidator<CheckForSwears, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }

        String str = s.toLowerCase();

        return !(str.equals("nigger")
                || str.equals("faggot")
                || str.equals("cunt")
                || str.equals("bitch")
                || str.equals("retard")
                || str.equals("shit")
                || str.equals("fuck")
                || str.equals("fucking")
                || str.equals("whitey"));
    }
}
