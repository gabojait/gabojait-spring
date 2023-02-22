package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.dto.req.EducationDefaultReqDto;
import com.inuappcenter.gabojaitspring.profile.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Education saveEducation(ObjectId userId, EducationDefaultReqDto request) {

        try {
            return educationRepository.save(request.toEntity(userId));
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 식별자 학력 조회 |
     * 404(EDUCATION_NOT_FOUND)
     */
    public Education findOneEducation(String educationId) {

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
    public void updateEducation(Education education, EducationDefaultReqDto request) {

        try {
            education.update(request.getInstitutionName(),
                    request.getStartedDate(),
                    request.getEndedDate(),
                    request.getIsCurrent());
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 학력 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void deleteEducation(Education education) {

        try {
            education.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
