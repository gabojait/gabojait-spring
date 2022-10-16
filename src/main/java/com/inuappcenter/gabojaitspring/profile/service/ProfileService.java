package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileDefaultRequestDto;
import com.inuappcenter.gabojaitspring.profile.repository.ProfileRepository;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;

    /**
     * 프로필 저장 |
     * 프로필을 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void save(ProfileDefaultRequestDto request) {
        log.info("INITIALIZE | 프로필 저장 At " + LocalDateTime.now() + " | " + request.getUserId());
        userService.isExistingUser(request.getUserId());
        try {
            Profile profile = profileRepository.save(request.toEntity());
            userService.saveProfile(request.getUserId(), profile.getId());
            log.info("COMPLETE | 프로필 저장 At " + LocalDateTime.now() +
                    " | userId = " + profile.getUserId() + ", profileId = " + profile.getId());
        } catch (Exception e) {
            throw new InternalServerErrorException("프로필 저장 중 에러 발생", e);
        }
    }

}
