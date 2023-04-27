package my.group.validator;

import my.group.good.Good;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyValidatorTest {
    MyValidator validator = new MyValidator();
    @Test
    void validateGood() {
        assertFalse(validator.validateGood(new Good("rus","shit")));
        assertFalse(validator.validateGood(new Good("russia","fucking")));
        assertFalse(validator.validateGood(new Good("fuck","zov")));
        assertFalse(validator.validateGood(new Good("shit","lamp")));
        assertFalse(validator.validateGood(new Good("q","plate")));
        assertFalse(validator.validateGood(new Good("Udss","p")));
        assertFalse(validator.validateGood(new Good("Udss","")));
        assertFalse(validator.validateGood(new Good("","pork")));
        assertFalse(validator.validateGood(new Good("qwercfdwergcbhfy","pork")));
    }
}