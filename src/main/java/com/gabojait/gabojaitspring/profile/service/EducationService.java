package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.dto.req.EducationCreateReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.EducationUpdateReqDto;
import com.gabojait.gabojaitspring.profile.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final UtilityProvider utilityProvider;

    /**
     * 전체 학력 업데이트 | main |
     * 404(EDUCATION_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void updateAll(ObjectId userId, List<EducationUpdateReqDto> requests) {
        List<Education> educations = new ArrayList<>();
        for (EducationUpdateReqDto request: requests) {
            ObjectId id = utilityProvider.toObjectId(request.getEducationId());

            Education education = findOne(id);
            validateOwner(education, userId);

            educations.add(education);
        }

        for (int i = 0; i < educations.size(); i++) {
            educations.get(i).update(requests.get(i).getInstitutionName(),
                    requests.get(i).getStartedDate(),
                    requests.get(i).getEndedDate(),
                    requests.get(i).getIsCurrent());

            save(educations.get(i));
        }
    }

    /**
     * 전체 학력 생성 | main |
     * 500(SERVER_ERROR)
     */
    public List<Education> createAll(ObjectId userId, List<EducationCreateReqDto> requests) {
        List<Education> educations = new ArrayList<>();
        for (EducationCreateReqDto request: requests) {
            Education education = save(request.toEntity(userId));
            educations.add(education);
        }

        return educations;
    }

    /**
     * 전체 경력 삭제 | sub |
     * 404(EDUCATION_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<Education> deleteAll(ObjectId userId, List<String> educationIds) {
        List<Education> educations = new ArrayList<>();
        for (String educationId: educationIds) {
            ObjectId id = utilityProvider.toObjectId(educationId);

            Education education = findOne(id);
            validateOwner(education, userId);

            educations.add(education);
        }

        for (Education education: educations)
            softDelete(education);

        return educations;
    }

    /**
     * 소유자 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateOwner(Education education, ObjectId userId) {
        if (!education.getUserId().equals(userId))
            throw new CustomException(null, REQUEST_FORBIDDEN);
    }

    /**
     * 경력 단건 조회 |
     * 404(EDUCATION_NOT_FOUND)
     */
    private Education findOne(ObjectId educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> {
                    throw new CustomException(null, EDUCATION_NOT_FOUND);
                });
    }

    /**
     * 학력 저장 |
     * 500(SERVER_ERROR)
     */
    public Education save(Education education) {
        try {
            return educationRepository.save(education);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 학력 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    public void softDelete(Education education) {
        education.delete();

        save(education);
    }

    /**
     * 학력 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    private void hardDelete(Education education) {
        try {
            educationRepository.delete(education);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
