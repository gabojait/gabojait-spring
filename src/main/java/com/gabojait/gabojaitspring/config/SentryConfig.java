package com.gabojait.gabojaitspring.config;

import com.gabojait.gabojaitspring.common.log.InterceptorLogging;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import io.sentry.protocol.SentryId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SentryConfig implements SentryOptions.BeforeSendCallback {

    @Value("${api.name}")
    private String serverName;

    @Override
    public SentryEvent execute(SentryEvent event, Hint hint) {
        event.setServerName(serverName);
        event.setEventId(new SentryId(InterceptorLogging.getRequestId()));
        return event;
    }
}
