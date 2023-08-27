package com.gabojait.gabojaitspring.config;

import com.gabojait.gabojaitspring.log.InterceptorLogging;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions.BeforeSendCallback;
import io.sentry.protocol.SentryId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SentryConfig implements BeforeSendCallback {

    @Value("${api.name}")
    private String serverName;

    @Override
    public SentryEvent execute(SentryEvent event, Hint hint) {
        event.setServerName(serverName);
        event.setEventId(new SentryId(InterceptorLogging.getRequestId()));
        return event;
    }
}
