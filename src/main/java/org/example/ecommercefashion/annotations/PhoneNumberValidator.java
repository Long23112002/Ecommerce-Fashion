package org.example.ecommercefashion.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

  private static final String PHONE_NUMBER_PATTERN = "^(84|0[3|5|7|8|9])[0-9]{8}$";

  @Override
  public void initialize(ValidPhoneNumber constraintAnnotation) {}

  @Override
  public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
    if (phoneNumber == null) {
      return false;
    }
    return Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber);
  }
}
