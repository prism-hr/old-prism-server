package com.zuehlke.pgadmissions.rest.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = DateNotFutureValidator.class)
public @interface DateNotFuture {

    String message() default "{dateNotFuture}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
