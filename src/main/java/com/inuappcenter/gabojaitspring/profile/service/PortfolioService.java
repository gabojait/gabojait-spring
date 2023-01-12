package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.domain.PortfolioType;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.dto.PortfolioSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.PortfolioUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    /**
     * 포트폴리오 생성 |
     * 포트폴리오 생성 절차를 밟아서 정보를 저장한다. |
     * 500: 포트폴리오 정보 저장 중 서버 에러
     */
    public Portfolio save(PortfolioSaveRequestDto request, Profile profile) {
        log.info("INITIALIZE | PortfolioService | save | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        PortfolioType portfolioType = validatePortfolioType(request.getPortfolioType());

        Portfolio portfolio = request.toEntity(profile.getId(), portfolioType);

        try {
            portfolio = portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | PortfolioService | save | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + portfolio.getId());
        return portfolio;
    }

    /**
     * 포트폴리오 타입 검증 |
     * 포트폴리오 타입이 L 또는 F로 되어 있는지 확인한다. |
     * 400: 올바리즈 않은 포맷 에러
     */
    private PortfolioType validatePortfolioType(Character portfolioType) {
        log.info("PROGRESS | PortfolioService | validatePortfolioType | " + portfolioType);

        if (portfolioType.equals('L')) {
            return PortfolioType.LINK;
        } else if (portfolioType.equals('F')) {
            return PortfolioType.FILE;
        } else {
            throw new CustomException(PORTFOLIO_TYPE_INCORRECT_TYPE);
        }
    }

    /**
     * 포트폴리오 업데이터 |
     * 포트폴리오 정보를 조회하여 업데이트한다. |
     * 500: 기술 정보 저장 중 서버 에러
     */
    public void update(Profile profile, PortfolioUpdateRequestDto request) {
        log.info("INITIALIZE | PortfolioService | update | " + profile.getId() + " | " + request.getPortfolioId());
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId portfolioId = new ObjectId(request.getPortfolioId());
        Portfolio portfolio = findOne(profile, portfolioId);
        PortfolioType portfolioType = validatePortfolioType(request.getPortfolioType());

        portfolio.updatePortfolio(request.getName(), request.getUrl());

        try {
            portfolio = portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | PortfolioService | update | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + portfolio.getId());
    }

    /**
     * 포트폴리오 단건 조회
     * 포트폴리오 정보가 프로필 정보에 있는지 확인하고 반환한다. |
     * 404: 존재하지 않은 포트폴리오 정보 에러
     */
    public Portfolio findOne(Profile profile, ObjectId portfolioId) {
        log.info("PROGRESS | PortfolioService | findOne | " + profile.getId() + " | " + portfolioId);

        for (Portfolio portfolio : profile.getPortfolios()) {
            if (portfolio.getId().equals(portfolioId)) {
                return portfolio;
            }
        }

        throw new CustomException(NON_EXISTING_PORTFOLIO);
    }

    /**
     * 포트폴리오 제거 |
     * 포트폴리오 정보에 제거 표시를 한 후 기술을 반환한다. |
     * 500: 포트폴리오 정보 저장 중 서버 에러
     */
    public Portfolio delete(Profile profile, String portfolioId) {
        log.info("INITIALIZE | PortfolioService | delete | " + profile.getId() + " | " + portfolioId);
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId id = new ObjectId(portfolioId);
        Portfolio portfolio = findOne(profile, id);

        portfolio.deletePortfolio();

        try {
            portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | SkillService | delete | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                portfolio.getId() + " | " + portfolio.getIsDeleted());
        return portfolio;
    }
}
