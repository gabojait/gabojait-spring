package com.inuappcenter.gabojaitspring.test.inject;

import com.inuappcenter.gabojaitspring.test.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInjector implements ApplicationRunner {

    private final TestService testService;

    @Override
    public void run(ApplicationArguments args) {

        testService.resetDatabase();

    }
}
