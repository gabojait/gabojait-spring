package com.gabojait.gabojaitspring.offer.service;

import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.offer.dto.req.OfferDefaultReqDto;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final UtilityProvider utilityProvider;

    /**
     * 회원 또는 팀에 제안 | main |
     * 400(ID_CONVERT_INVALID)
     * 500(SERVER_ERROR)
     */
    public ObjectId offer(OfferDefaultReqDto request, String userId, String teamId, boolean isOfferedByUser) {
        ObjectId uId = utilityProvider.toObjectId(userId);
        ObjectId tId = utilityProvider.toObjectId(teamId);

        Offer offer = request.toEntity(uId, tId, isOfferedByUser ? OfferedBy.USER : OfferedBy.TEAM);
        save(offer);

        return offer.getId();
    }

    /**
     * 제안 저장 |
     * 500(SERVER_ERROR)
     */
    private Offer save(Offer offer) {
        try {
            return offerRepository.save(offer);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
