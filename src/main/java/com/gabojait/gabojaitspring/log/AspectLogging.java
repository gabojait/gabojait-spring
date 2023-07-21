package com.gabojait.gabojaitspring.log;

import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
public class AspectLogging {

    @Pointcut("execution(public * com.gabojait.gabojaitspring..*(..)) " +
            "&& !execution(public * com.gabojait.gabojaitspring..*Repository.*(..))")
    private void global() {}

    @Pointcut("execution(public * com.gabojait.gabojaitspring..*Repository.find*(..))")
    private void repositoryFind() {}

    @Pointcut("execution(public * com.gabojait.gabojaitspring..*Repository.save(..))")
    private void repositorySave() {}

    @Pointcut("execution(public * com.gabojait.gabojaitspring..*Repository.delete(..))")
    private void repositoryDelete() {}

    @Before("global()")
    public void beforeGlobal(JoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final String className = signature.getDeclaringType().getSimpleName();
        final Method method = signature.getMethod();
        final String[] parameterNames = signature.getParameterNames();
        final Object[] args = joinPoint.getArgs();
        final String uuid = InterceptorLogging.getRequestId() == null ? "SYSTEM" : InterceptorLogging.getRequestId();

        StringBuilder argsLog = new StringBuilder();

        for(int i = 0; i < method.getParameters().length; i++)
            if (parameterNames[i] != null) {
                if (i != 0)
                    argsLog.append(" | ");
                argsLog.append(parameterNames[i]).append(" = ").append(args[i]);
            }

        if (!method.getName().contains("resetMasterPasswordScheduler")) {
            argsLog = new StringBuilder(argsLog.toString()
                    .replaceAll("(?<=password\\s?=\\s?)\\S+", "******"));
            argsLog = new StringBuilder(argsLog.toString()
                    .replaceAll("(?<=passwordReEntered\\s?=\\s?)\\S+", "******"));
        }

        log.info("[{} | PROGRESS] {} | {}({})", uuid, className, method.getName(), argsLog);
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

        log.info("[{} | SUCCESS] {} | {} | return = {}", uuid, className, methodName, result);
    }

    @Before("repositoryFind()")
    public void beforeRepositoryFind(JoinPoint joinPoint) {
        beforeRepositoryLog(joinPoint, "PROGRESS-FIND");
    }

    @Before("repositorySave()")
    public void beforeRepositorySave(JoinPoint joinPoint) {
        beforeRepositoryLog(joinPoint, "PROGRESS-SAVE");
    }

    @Before("repositoryDelete()")
    public void beforeRepositoryDelete(JoinPoint joinPoint) {
        beforeRepositoryLog(joinPoint, "PROGRESS-DELETE");
    }

    private void beforeRepositoryLog(JoinPoint joinPoint, String logTitle) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final String className = methodSignature.getDeclaringType().getSimpleName();
        final Method method = methodSignature.getMethod();
        final Parameter[] parameters = method.getParameters();
        final Object[] args = joinPoint.getArgs();
        final String uuid = InterceptorLogging.getRequestId() == null ? "SYSTEM" : InterceptorLogging.getRequestId();


        StringBuilder argsLog = new StringBuilder();
        for(int i = 0; i < parameters.length; i++) {
            if (i != 0)
                argsLog.append(" | ");

            if (!parameters[i].getName().isBlank())
                argsLog.append(parameters[i].getName()).append(" = ").append(args[i]);
        }

        if (!method.getName().contains("resetMasterPasswordScheduler")) {
            argsLog = new StringBuilder(argsLog.toString()
                    .replaceAll("(?<=password\\s?=\\s?)\\S+", "******"));
            argsLog = new StringBuilder(argsLog.toString()
                    .replaceAll("(?<=passwordReEntered\\s?=\\s?)\\S+", "******"));
        }

        log.info("[{} | {}] {} | {} ({})", uuid, logTitle, className, method.getName(), argsLog);
    }

    @AfterReturning(value = "repositoryFind()", returning = "result")
    public void afterRepositoryFind(JoinPoint joinPoint, Object result) {
        afterRepositoryLog((MethodSignature) joinPoint.getSignature(), result, "SUCCESS-FIND");
    }

    @AfterReturning(value = "repositorySave()", returning = "result")
    public void afterRepositorySave(JoinPoint joinPoint, Object result) {
        afterRepositoryLog((MethodSignature) joinPoint.getSignature(), result, "SUCCESS-SAVE");
    }

    @AfterReturning(value = "repositoryDelete()", returning = "result")
    public void afterRepositoryDelete(JoinPoint joinPoint, Object result) {
        afterRepositoryLog((MethodSignature) joinPoint.getSignature(), result, "SUCCESS-DELETE");
    }

    private void afterRepositoryLog(MethodSignature signature, Object result, String logTitle) {
        final String className = signature.getDeclaringType().getSimpleName();
        final String methodName = signature.getMethod().getName();
        final String uuid = InterceptorLogging.getRequestId() == null ? "SYSTEM" : InterceptorLogging.getRequestId();

        if (result != null && !methodName.contains("resultMasterPasswordScheduler")) {
            result = result.toString().replaceAll("(?<=password\\s?=\\s?)\\S+", "******");
            result = result.toString().replaceAll("(?<=passwordReEntered\\s?=\\s?)\\S+", "******");
        }

        log.info("[{} | {}] {} | {} return = {}", uuid, logTitle, className, methodName, result);
    }

    @AfterThrowing(value = "global()", throwing = "exception")
    public void globalError(JoinPoint joinPoint, CustomException exception) {
        exceptionLog(joinPoint, exception, "ERROR");
    }

    @AfterThrowing(value = "repositoryFind()", throwing = "exception")
    public void errorRepositoryFind(JoinPoint joinPoint, CustomException exception) {
        exceptionLog(joinPoint, exception, "ERROR-FIND");
    }

    @AfterThrowing(value = "repositorySave()", throwing = "exception")
    public void errorRepositorySave(JoinPoint joinPoint, CustomException exception) {
        exceptionLog(joinPoint, exception, "ERROR-SAVE");
    }

    @AfterThrowing(value = "repositoryDelete()", throwing = "exception")
    public void errorRepositoryDelete(JoinPoint joinPoint, CustomException exception) {
        exceptionLog(joinPoint, exception, "ERROR-DELETE");
    }

    public void exceptionLog(JoinPoint joinPoint, CustomException exception, String logTitle) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final String className = signature.getDeclaringType().getSimpleName();
        final String methodName = signature.getMethod().getName();
        final String errorName = exception.getExceptionCode().name();
        final String uuid = InterceptorLogging.getRequestId() == null
                ? "SYSTEM" : InterceptorLogging.getRequestId();

        log.error("========== [{} | API-FINISH | {}] {} | {} | code = {} ==========",
                uuid, logTitle, className, methodName, errorName);

        if (exception.getExceptionCode().getHttpStatus().equals(HttpStatus.INTERNAL_SERVER_ERROR))
            if (exception.getThrowable() != null)
                log.error("========== [{} | ERROR DESCRIPTION] ==========", uuid, exception.getThrowable());
    }
}
