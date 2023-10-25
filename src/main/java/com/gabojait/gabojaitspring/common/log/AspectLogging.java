package com.gabojait.gabojaitspring.common.log;

import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class AspectLogging {

    @Pointcut("execution(public * com.gabojait.gabojaitspring.domain..*(..))" +
            "|| execution(public * com.gabojait.gabojaitspring.repository..*(..)) " +
            "|| execution(public * com.gabojait.gabojaitspring.api..*(..))")
    private void global() {}

    @Pointcut("execution(protected * com.gabojait.gabojaitspring.exception.CustomExceptionHandler..*(..))")
    private void exception() {}

    @Before("global()")
    public void beforeGlobal(JoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final String className = signature.getDeclaringType().getSimpleName();
        final Method method = signature.getMethod();
        final String uuid = InterceptorLogging.getRequestId() == null ? "SYSTEM" : InterceptorLogging.getRequestId();

        StringBuilder argsLog = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null) argsLog.append(arg);
            else argsLog.append("null");
        }

        log.info("[{} | BEFORE] {} | {} ({})", uuid, className, method.getName(), argsLog);
    }

    @AfterReturning(value = "global()", returning = "result")
    public void afterGlobal(JoinPoint joinPoint, Object result) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final String className = signature.getDeclaringType().getSimpleName();
        final String methodName = signature.getMethod().getName();
        final String uuid = InterceptorLogging.getRequestId() == null ? "SYSTEM" : InterceptorLogging.getRequestId();

        if (result != null && !methodName.contains("resultMasterPasswordScheduler")) {
            result = result.toString().replaceAll("(?<=password\\s?=\\s?)\\S+", "******");
            result = result.toString().replaceAll("(?<=passwordReEntered\\s?=\\s?)\\S+", "******");
        }

        log.info("[{} | AFTER] {} | {} | return={}", uuid, className, methodName, result);
    }

    @AfterThrowing(value = "exception()", throwing = "ex")
    public void afterThrowingGlobal(JoinPoint joinPoint, CustomException ex) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final String className = signature.getDeclaringType().getSimpleName();
        final String methodName = signature.getMethod().getName();
        final String errorName = ex.getErrorCode().name();
        final String uuid = InterceptorLogging.getRequestId() != null ? InterceptorLogging.getRequestId() : "SYSTEM";

        log.error("========== [{} | ERROR] {} | {} | code={} ==========", uuid, className, methodName, errorName);

        if (ex.getErrorCode().getHttpStatus().equals(HttpStatus.INTERNAL_SERVER_ERROR))
            if (ex.getThrowable() != null)
                log.error("========== [{} | DESCRIPTION] ==========", uuid, ex.getThrowable());
    }
}
