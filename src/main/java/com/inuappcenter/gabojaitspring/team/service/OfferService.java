package com.inuappcenter.gabojaitspring.team.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.team.domain.Offer;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfferService {

    private final OfferRepository offerRepository;

    /**
     * 제안 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public Offer save(Offer offer) {

        try {
            return offerRepository.save(offer);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 지원 여부 검증 |
     * 409(EXISTING_OFFER)
     */
    public void isExistingOffer(ObjectId applicantId, ObjectId teamId) {

        offerRepository.findByApplicantIdAndTeamId(applicantId, teamId)
                .ifPresent((offer) -> {
                    throw new CustomException(EXISTING_OFFER);
                });
    }

    /**
     * 팀과 포지션으로 제안 다건 조회 |
     * 500(SERVER_ERROR)
     */
    public Page<Offer> findManyByTeamAndPosition(ObjectId teamId,
                                                 Position position,
                                                 boolean isByApplicant,
                                                 Integer pageFrom,
                                                 Integer pageNum) {

        if (pageNum == null)
            pageNum = 20;

        try {
            return offerRepository
                    .findOffersByTeamIdAndPositionAndIsByApplicantAndIsDeletedIsFalseOrderByModifiedDateDesc(
                            teamId,
                            position.getType(),
                            isByApplicant,
                            PageRequest.of(pageFrom, pageNum)
                    );
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
