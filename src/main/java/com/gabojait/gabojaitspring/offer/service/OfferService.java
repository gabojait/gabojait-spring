package com.gabojait.gabojaitspring.offer.service;

import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.offer.dto.req.OfferDefaultReqDto;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * 회원 식별자로 페이징 다건 조회 | main |
     * 500(SERVER_ERROR)
     */
    public Page<Offer> findPageByUserId(ObjectId userId, Integer pageFrom, Integer pageSize) {
        Pageable pageable = utilityProvider.validatePaging(pageFrom, pageSize, 20);
        try {
            return offerRepository.findAllByUserIdAndIsDeletedIsFalse(userId, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 팀 식별자로 페이징 다건 조회 | main |
     * 403(REQUEST_FORBIDDEN)
     * 500(SERVER_ERROR)
     */
    public Page<Offer> findPageByTeamId(Team team, ObjectId userId, Integer pageFrom, Integer pageSize) {
        if (!team.getLeaderUserId().toString().equals(userId.toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);

        Pageable pageable = utilityProvider.validatePaging(pageFrom, pageSize, 20);
        try {
            return offerRepository.findAllByTeamIdAndIsDeletedIsFalse(team.getId(), pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 제안 결정 | main |
     * 500(SERVER_ERROR)
     */
    public void decideOffer(Offer offer, boolean isAccepted) {
        if (isAccepted)
            offer.accept();
        else
            offer.decline();

        save(offer);
    }

    /**
     * 제안 취소 | main |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(OFFER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void cancel(String offerId, String id) {
        Offer offer = findOneById(offerId);

        if (offer.getOfferedBy().equals(OfferedBy.USER.getType())) {
            if (!offer.getUserId().toString().equals(id))
                throw new CustomException(null, REQUEST_FORBIDDEN);
        } else if (offer.getOfferedBy().equals(OfferedBy.TEAM.getType())) {
            if (!offer.getTeamId().toString().equals(id))
                throw new CustomException(null, REQUEST_FORBIDDEN);
        } else {
            throw new CustomException(null, SERVER_ERROR);
        }

        offer.delete();

        save(offer);
    }

    /**
     * 식별자로 단건 조회 | sub |
     * 404(OFFER_NOT_FOUND)
     */
    public Offer findOneById(String offerId) {
        ObjectId id = utilityProvider.toObjectId(offerId);

        return offerRepository.findByIdAndIsDeletedIsFalse(id)
                .orElseThrow(() -> {
                    throw new CustomException(null, OFFER_NOT_FOUND);
                });
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
