package com.gabojait.gabojaitspring.api.service.develop;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DevelopService {

    @Value("${api.name}")
    private String serverName;

    /**
     * 서버명 조회
     * @return 서버명
     */
    public String getServerName() {
        return serverName;
    }
}
