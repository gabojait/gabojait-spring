package com.gabojait.gabojaitspring.log;

import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Slf4j
@Aspect
@Component
public class AspectLogging {

    @Pointcut("execution(public * com.gabojait.gabojaitspring..*(..)) " +
            "&& !execution(public * com.gabojait.gabojaitspring..*Repository.*(..))")
    private void allExceptRepository() {}

    @Pointcut("execution(public * com.gabojait.gabojaitspring..*Repository.find*(..))")
    private void findFromRepository() {}

    @Pointcut("execution(public * com.gabojait.gabojaitspring..*Repository.save(..))")
    private void saveFromRepository() {}

    @Pointcut("execution(public * com.gabojait.gabojaitspring..*Repository.delete(..))")
    private void deleteFromRepository() {}

    @Pointcut("execution(public * com.gabojait.gabojaitspring..*Controller.*(..))")
    private void apiTimer() {}

    @Around("apiTimer()")
    public Object ApiExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final String className = signature.getDeclaringType().getSimpleName();
        final String methodName = signature.getMethod().getName();

        log.info("========== [API-START] {} {} ==========", className, methodName);

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object proceed = joinPoint.proceed();
        stopWatch.stop();

        long totalTimeMillis = stopWatch.getTotalTimeMillis();

        log.info("========== [API-FINISH] {} {} | {} ms ==========", className, methodName, totalTimeMillis);

        return proceed;
    }

    @Before("allExceptRepository()")
    public void beforeExceptRepository(JoinPoint joinPoint) {
        try {
            final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            final String className = signature.getDeclaringType().getSimpleName();
            final Method method = signature.getMethod();
            final String[] parameterNames = signature.getParameterNames();
            final Object[] args = joinPoint.getArgs();

            StringBuilder argsLog = new StringBuilder();

            for(int i = 0; i < method.getParameters().length; i++)
                if (parameterNames[i] != null) {
                    if (i != 0)
                        argsLog.append(" | ");
                    argsLog.append(parameterNames[i]).append(" = ").append(args[i]);
                }

            if (!method.getName().contains("resetMasterPasswordScheduler")) {
                argsLog = new StringBuilder(argsLog.toString()
                        .replaceAll("(?<=password = )\\S+", "******"));
                argsLog = new StringBuilder(argsLog.toString()
                        .replaceAll("(?<=passwordReEntered = )\\S+", "******"));
            }

            log.info("[PROGRESS] {} | {}({})", className, method.getName(), argsLog);
        } catch (Exception e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    @Before("findFromRepository()")
    public void beforeFindFromRepository(JoinPoint joinPoint) {
        beforeLogFromRepository(joinPoint, "PROGRESS-FIND");
    }

    @Before("saveFromRepository()")
    public void beforeSaveFromRepository(JoinPoint joinPoint) {
        beforeLogFromRepository(joinPoint, "PROGRESS-SAVE");
    }

    @Before("deleteFromRepository()")
    public void beforeDeleteFromRepository(JoinPoint joinPoint) {
        beforeLogFromRepository(joinPoint, "PROGRESS-DELETE");
    }

    private void beforeLogFromRepository(JoinPoint joinPoint, String logTitle) {
        try {
            final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            final String className = methodSignature.getDeclaringType().getSimpleName();
            final Method method = methodSignature.getMethod();
            final Parameter[] parameters = method.getParameters();
            final Object[] args = joinPoint.getArgs();

            StringBuilder argsLog = new StringBuilder();
            for(int i = 0; i < parameters.length; i++) {
                if (i != 0)
                    argsLog.append(" | ");

                if (!parameters[i].getName().isBlank())
                    argsLog.append(parameters[i].getName()).append(" = ").append(args[i]);
            }

            log.info("[{}] {} | {} ({})", logTitle, className, method.getName(), argsLog);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    @AfterReturning(value = "allExceptRepository()", returning = "result")
    public void afterMethodExceptRepository(JoinPoint joinPoint, Object result) {
        try {
            final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            final String className = signature.getDeclaringType().getSimpleName();
            final String methodName = signature.getMethod().getName();

            if (result != null && !methodName.contains("resultMasterPasswordScheduler")) {
                result = result.toString().replaceAll("(?<=password = )\\S+", "******");
                result = result.toString().replaceAll("(?<=passwordReEntered = )\\S+", "******");
            }

            log.info("[SUCCESS] {} | {} | return = {}", className, methodName, result);
        } catch (Exception e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    @AfterReturning(value = "findFromRepository()", returning = "result")
    public void afterFindFromRepository(JoinPoint joinPoint, Object result) {
        afterLogFromRepository((MethodSignature) joinPoint.getSignature(), result, "SUCCESS-FIND");
    }

    @AfterReturning(value = "saveFromRepository()", returning = "result")
    public void afterSaveFromRepository(JoinPoint joinPoint, Object result) {
        afterLogFromRepository((MethodSignature) joinPoint.getSignature(), result, "SUCCESS-SAVE");
    }

    @AfterReturning(value = "deleteFromRepository()", returning = "result")
    public void afterDeleteFromRepository(JoinPoint joinPoint, Object result) {
        afterLogFromRepository((MethodSignature) joinPoint.getSignature(), result, "SUCCESS-DELETE");
    }

    private void afterLogFromRepository(MethodSignature signature, Object result, String logTitle) {
        try {
            final String className = signature.getDeclaringType().getSimpleName();
            final String methodName = signature.getMethod().getName();

            log.info("[{}] {} | {} return = {}", logTitle, className, methodName, result);
        } catch (Exception e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    @AfterThrowing(value = "allExceptRepository()", throwing = "exception")
    public void errorMethodExceptRepository(JoinPoint joinPoint, CustomException exception) {
        exceptionLog(joinPoint, exception, "ERROR");
    }

    @AfterThrowing(value = "findFromRepository()", throwing = "exception")
    public void errorFindFromRepository(JoinPoint joinPoint, CustomException exception) {
        exceptionLog(joinPoint, exception, "ERROR-FIND");
    }

    @AfterThrowing(value = "saveFromRepository()", throwing = "exception")
    public void errorSaveFromRepository(JoinPoint joinPoint, CustomException exception) {
        exceptionLog(joinPoint, exception, "ERROR-SAVE");
    }

    @AfterThrowing(value = "deleteFromRepository()", throwing = "exception")
    public void errorDeleteFromRepository(JoinPoint joinPoint, CustomException exception) {
        exceptionLog(joinPoint, exception, "ERROR-DELETE");
    }

    public void exceptionLog(JoinPoint joinPoint, CustomException exception, String logTitle) {
        try {
            final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            final String className = signature.getDeclaringType().getSimpleName();
            final String methodName = signature.getMethod().getName();
            final String errorName = exception.getExceptionCode().name();

            log.error("[{}] {} | {} | code = {}", logTitle, className, methodName, errorName);

            if (exception.getExceptionCode().getHttpStatus().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                if (exception.getThrowable() != null)
                    log.error("########## ERROR DESCRIPTION ##########", exception.getThrowable());
        } catch (Exception e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
