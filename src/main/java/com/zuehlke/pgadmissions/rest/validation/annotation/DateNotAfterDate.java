package com.zuehlke.pgadmissions.rest.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = DateNotAfterDateValidator.class)
public @interface DateNotAfterDate {

    String message() default "{dateBeforeDate}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String startDate();

    String endDate();

}
