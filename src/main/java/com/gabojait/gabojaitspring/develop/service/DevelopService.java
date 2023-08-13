package com.gabojait.gabojaitspring.develop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DevelopService {

    @Value("${api.name}")
    private String serverName;

    /**
     * 서버명 반환
     */
    public String getServerName() {
        return serverName;
    }
}
