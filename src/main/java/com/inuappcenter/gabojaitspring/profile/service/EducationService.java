package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import com.inuappcenter.gabojaitspring.exception.http.NotFoundException;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.dto.EducationSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.EducationUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.repository.EducationRepository;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;

    /**
     * 학력 저장 |
     * 학력을 저장한다. 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public Education save(EducationSaveRequestDto request) {
        log.info("INITIALIZE | 학력 저장 At " + LocalDateTime.now() + " | " + request.getProfileId());
        try {
            Education education = educationRepository.save(request.toEntity());
            log.info("COMPLETE | 학력 저장 At " + LocalDateTime.now() + " | " + request.getProfileId());
            return education;
        } catch (Exception e) {
            throw new InternalServerErrorException("학력 저장 중 에러 발생", e);
        }
    }

    /**
     * 경력 조희 후 경력 엔티티 반환 |
     * 경력을 조회 하여 경력 엔티티로 반환한다. 조회가 되지 않을 경우 404(NotFound)를 던진다.
     */
    public Education findEducation(String id) {
        log.info("INITIALIZE | 경력 조회 At " + LocalDateTime.now() + " | " + id);
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException("존재 하지 않은 정보입니다");
                });
        log.info("COMPLETE | 경력 조회 At " + LocalDateTime.now() + " | " + id);
        return education;
    }

    /**
     * 학력 업데이트 |
     * 학력을 업데이트 한다. 업데이트 중 서버 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void update(EducationUpdateRequestDto request, String educationId) {
        log.info("INITIALIZE | 학력 업데이트 At " + LocalDateTime.now() + " | " + educationId);
        Education education = findEducation(educationId);

        try {
            education.update(request.getInstitutionName(),
                    request.getStartedDate(),
                    request.getEndedDate(),
                    request.getIsCurrent());
            educationRepository.save(education);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        log.info("COMPLETE | 학력 업데이트 At " + LocalDateTime.now() + " | " + educationId);
    }

    /**
     * 학력 삭제 |
     * 학력을 삭제한다. 삭제 중 에러가 발생하면 500(Internal Server Error)을 던진다.
     */
    public void delete(String id) {
        log.info("INITIALIZE | 학력 삭제 At " + LocalDateTime.now() + " | " + id);
        Education education = findEducation(id);
        try {
            education.setIsDeleted(true);
            educationRepository.save(education);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        log.info("COMPLETE | 학력 삭제 At " + LocalDateTime.now() + " | " + id);
    }
}
