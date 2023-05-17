package com.gabojait.gabojaitspring.offer.service;

import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;

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
