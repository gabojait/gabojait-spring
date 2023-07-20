package com.gabojait.gabojaitspring.config;

import com.gabojait.gabojaitspring.log.P6spyLogging;
import com.p6spy.engine.spy.P6SpyOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class P6spyConfig {

    @PostConstruct
    public void logFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6spyLogging.class.getName());
    }
}
