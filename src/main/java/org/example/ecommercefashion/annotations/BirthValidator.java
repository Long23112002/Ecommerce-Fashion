package org.example.ecommercefashion.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class BirthValidator implements ConstraintValidator<ValidBirth, Date> {

    private int min;
    private int max;
    private String message;

    @Override
    public void initialize(ValidBirth constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.message = constraintAnnotation.message();
    }

    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }

        LocalDate birthDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDate now = LocalDate.now();
        int age = Period.between(birthDate, now).getYears();

        if(age<min) {
            buildNewMessage(constraintValidatorContext,"Bạn chưa đủ "+min+" tuổi");
            return false;
        }
        if(age>max){
            buildNewMessage(constraintValidatorContext,"Tuổi không hợp lệ (không quá "+max+" tuổi)");
            return false;
        }
        return true;
    }

    private void buildNewMessage(ConstraintValidatorContext constraintValidatorContext, String newMessage) {
        if(!message.isBlank()){
            return;
        }
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(newMessage).addConstraintViolation();
    }
}
