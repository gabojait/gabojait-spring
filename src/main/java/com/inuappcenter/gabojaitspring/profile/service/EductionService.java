package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.dto.req.EducationUpdateReqDto;
import com.inuappcenter.gabojaitspring.profile.repository.EducationRepository;
import com.inuappcenter.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EductionService {

    private final EducationRepository educationRepository;

    /**
     * 학력 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public Education save(Education education) {

        try {
            return educationRepository.save(education);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 식별자 학력 조회 |
     * 404(EDUCATION_NOT_FOUND)
     */
    public Education findOne(String educationId) {

        return educationRepository.findById(new ObjectId(educationId))
                .orElseThrow(() -> {
                    throw new CustomException(EDUCATION_NOT_FOUND);
                });
    }

    /**
     * 학력 업데이트 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void update(Education education, EducationUpdateReqDto request) {

        try {
            education.update(request.getInstitutionName(),
                    request.getStartedDate(),
                    request.getEndedDate(),
                    request.getIsCurrent());
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(education);
    }

    /**
     * 권한 검증 |
     * 403(ROLE_NOT_ALLOWED)
     */
    public void validateOwner(Education education, User user) {

        if (!user.getEducations().contains(education))
            throw new CustomException(ROLE_NOT_ALLOWED);
    }

    /**
     * 기간 검증 |
     * 400(EDUCATION_DATE_INVALID)
     */
    public void validateDate(LocalDate startedDate, LocalDate endedDate) {

        if (startedDate.isAfter(endedDate))
            throw new CustomException(EDUCATION_DATE_INVALID);
    }

    /**
     * 학력 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void delete(Education education) {

        try {
            education.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(education);
    }
}
