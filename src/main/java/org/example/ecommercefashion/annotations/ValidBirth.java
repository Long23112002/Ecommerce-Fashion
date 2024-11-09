package org.example.ecommercefashion.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BirthValidator.class})
public @interface ValidBirth {
    String message() default "";

    int min() default 10;

    int max() default 120;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
