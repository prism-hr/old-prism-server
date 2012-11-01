package com.zuehlke.pgadmissions.performance;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SystemArchitecture {

    @Pointcut("execution(* com.zuehlke.pgadmissions.controllers..*.*(..))")
    public void businessController() {
    }
    
    @Pointcut("execution(* com.zuehlke.pgadmissions.services..*.*(..))")
    public void businessService() {
    }
}