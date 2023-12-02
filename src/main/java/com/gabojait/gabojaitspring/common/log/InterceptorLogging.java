package com.gabojait.gabojaitspring.common.log;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Getter
public class InterceptorLogging implements HandlerInterceptor {

    private static final ThreadLocal<String> uuidThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<StopWatch> stopWatchThreadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @Nullable HttpServletResponse response,
                             @Nullable Object handler) {
        final String uuid = UUID.randomUUID().toString();
        final String method = request.getMethod();
        final String uri = request.getRequestURI();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        log.info("========== [{} | START] {} {} ==========", uuid, method, uri);

        uuidThreadLocal.set(uuid);
        stopWatchThreadLocal.set(stopWatch);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           @Nullable HttpServletResponse response,
                           @Nullable Object handler,
                           ModelAndView modelAndView) {
        StopWatch stopWatch = stopWatchThreadLocal.get();
        stopWatch.stop();

        final long time = stopWatch.getTotalTimeMillis();
        final String uuid = uuidThreadLocal.get();
        final String method = request.getMethod();
        final String uri = request.getRequestURI();

        log.info("========== [{} | FINISH] {} {} | time={}ms ==========", uuid, method, uri, time);

        uuidThreadLocal.remove();
        stopWatchThreadLocal.remove();
    }

    public static String getRequestId() {
        return uuidThreadLocal.get();
    }
}
