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
    public List<Education> updateAll(ObjectId userId, List<EducationUpdateReqDto> requests) {
        List<Education> educations = new ArrayList<>();

        for (EducationUpdateReqDto request : requests) {
            Education education = findOneById(request.getEducationId());
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

        return educations;
    }

    /**
     * 전체 학력 생성 | main |
     * 500(SERVER_ERROR)
     */
    public List<Education> createAll(ObjectId userId, List<EducationCreateReqDto> requests) {
        List<Education> educations = new ArrayList<>();

        for (EducationCreateReqDto request : requests) {
            Education education = save(request.toEntity(userId));
            educations.add(education);
        }

        return educations;
    }

    /**
     * 전체 경력 삭제 | sub |
     * 403(REQUEST_FORBIDDEN)
     * 404(EDUCATION_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<Education> deleteAll(ObjectId userId, List<String> educationIds) {
        List<Education> educations = new ArrayList<>();

        for (String educationId : educationIds) {
            Education education = findOneById(educationId);
            validateOwner(education, userId);

            educations.add(education);
        }

        for (Education education: educations)
            delete(education);

        return educations;
    }

    /**
     * 학력 처리전 전체 검증 | sub |
     * 404(EDUCATION_NOT_FOUND)
     */
    public void validatePreAll(List<EducationUpdateReqDto> educationUpdateReqDtos,
                               List<String> deletePortfolioIds) {
        for (EducationUpdateReqDto request : educationUpdateReqDtos)
            findOneById(request.getEducationId());

        for (String deleteEducationId : deletePortfolioIds)
            findOneById(deleteEducationId);
    }

    /**
     * 식별자로 경력 단건 조회 |
     * 404(EDUCATION_NOT_FOUND)
     */
    private Education findOneById(String educationId) {
        ObjectId id = utilityProvider.toObjectId(educationId);

        return educationRepository.findByIdAndIsDeletedIsFalse(id)
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
    public void delete(Education education) {
        education.delete();

        save(education);
    }

    /**
     * 소유자 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateOwner(Education education, ObjectId userId) {
        if (!education.getUserId().toString().equals(userId.toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);
    }
}
