package my.group.validator;

import my.group.good.Good;
import my.group.utilities.MyLogger;
import org.slf4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class MyValidator {
    private final Logger logger = new MyLogger().getLogger();
    public final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    public final Validator validator = factory.getValidator();

    public boolean validateGood(Good good) {
        Set<ConstraintViolation<Good>> violation = validator.validate(good);
        if (!violation.isEmpty()) {
            for (ConstraintViolation<Good> goodConstraintViolation : violation) {
                logger.error(goodConstraintViolation.getMessage());
            }
            return false;
        } else {
            return true;
        }
    }
}
