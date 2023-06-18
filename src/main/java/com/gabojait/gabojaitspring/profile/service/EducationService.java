package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.dto.req.EducationCreateReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.EducationUpdateReqDto;
import com.gabojait.gabojaitspring.profile.repository.EducationRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;

    /**
     * 학력 생성 |
     * 500(SERVER_ERROR)
     */
    public void create(User user, EducationCreateReqDto request) {
        Education education = request.toEntity(user);

        saveEducation(education);
    }

    /**
     * 학력 업데이트 |
     * 404(EDUCATION_NOT_FOUND)
     */
    public void update(User user, EducationUpdateReqDto request) {
        Education education = findOne(request.getEducationId(), user);

        education.update(request.getInstitutionName(),
                request.getStartedAt(),
                request.getEndedAt(),
                request.getIsCurrent());
    }

    /**
     * 학력 삭제 |
     * 404(EDUCATION_NOT_FOUND)
     */
    public void delete(User user, Long educationId) {
        Education education = findOne(educationId, user);

        softDelete(education);
    }

    /**
     * 학력 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveEducation(Education education) {
        try {
            educationRepository.save(education);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 학력 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    private void softDelete(Education education) {
        education.delete();
    }

    /**
     * 식별자와 회원으로 학력 단건 조회 |
     * 404(EDUCATION_NOT_FOUND)
     */
    private Education findOne(Long educationId, User user) {
        return educationRepository.findByIdAndUserAndIsDeletedIsFalse(educationId, user)
                .orElseThrow(() -> {
                    throw new CustomException(EDUCATION_NOT_FOUND);
                });
    }
}
