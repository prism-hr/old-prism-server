package com.zuehlke.pgadmissions.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RUNTIME)
@Repeatable(TargetingCalibrationSources.class)
public @interface TargetingCalibrationSource {

    int subjectArea();

    String[] sources();

}
