package com.zuehlke.pgadmissions.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class and {@link SystemArchitecture} can be used to 
 * do some rudimentary performance measuring of the Spring Controllers.
 * It outputs the execution time of methods withing the controllers in milliseconds.
 * <p>
 * To enable this feature uncomment {@code <aop:aspectj-autoproxy />} in the {@code applicationContext.xml}.
 */
@Component
@Aspect
public class TimeExecutionProfiler {

    private final Logger logger = LoggerFactory.getLogger(TimeExecutionProfiler.class);

    @Around("com.zuehlke.pgadmissions.performance.SystemArchitecture.businessController()")
    public Object profileControlers(ProceedingJoinPoint pjp) throws Throwable {
        return profile(pjp);
    }
    
    @Around("com.zuehlke.pgadmissions.performance.SystemArchitecture.businessService()")
    public Object profileService(ProceedingJoinPoint pjp) throws Throwable {
        return profile(pjp);
    }

    private Object profile(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        logger.info(String.format("PROFILE [%s] [%s]", methodName, elapsedTime));
        return output;
    }
}