package com.zuehlke.pgadmissions.performance;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    private static final Logger logger = Logger.getLogger(TimeExecutionProfiler.class);

    @Around("com.zuehlke.pgadmissions.performance.SystemArchitecture.businessController()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        logger.info(String.format("ServicesProfiler.profile(): Going to call the method: %s", pjp.getSignature().getName()));
        Object output = pjp.proceed();
        logger.info("ServicesProfiler.profile(): Method execution completed.");
        long elapsedTime = System.currentTimeMillis() - start;
        logger.info("ServicesProfiler.profile(): Method execution time: " + elapsedTime + " milliseconds.");

        return output;
    }

    @After("com.zuehlke.pgadmissions.performance.SystemArchitecture.businessController()")
    public void profileMemory() {
        logger.info(String.format("JVM memory in use = %s", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())));
    }
}