package com.inuappcenter.gabojaitspring.team.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.team.domain.Offer;
import com.inuappcenter.gabojaitspring.team.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfferService {

    private final OfferRepository offerRepository;

    /**
     * 오퍼 저장 |
     * 500(SERVER_ERROR)
     */
    public Offer save(Offer offer) {

        try {
            return offerRepository.save(offer);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
