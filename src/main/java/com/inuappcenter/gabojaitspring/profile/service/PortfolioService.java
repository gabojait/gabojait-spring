package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.file.service.FileService;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.domain.PortfolioType;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.dto.PortfolioFileSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.PortfolioFileUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.PortfolioLinkSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.PortfolioLinkUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    @Value(value = "${s3.portfolioFileBucketName}")
    private String bucketName;
    private final PortfolioRepository portfolioRepository;
    private final FileService fileService;

    /**
     * 링크 포트폴리오 생성 |
     * 링크 포트폴리오 생성 절차를 밟아서 정보를 저장한다. |
     * 500: 링크 포트폴리오 정보 저장 중 서버 에러
     */
    public Portfolio saveLink(PortfolioLinkSaveRequestDto request, Profile profile) {
        log.info("INITIALIZE | PortfolioService | saveLink | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        PortfolioType portfolioType = PortfolioType.LINK;

        Portfolio portfolio = request.toEntity(profile.getId(), portfolioType);

        try {
            portfolio = portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | PortfolioService | saveLink | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + portfolio.getId());
        return portfolio;
    }

    /**
     * 파일 포트폴리오 생성 |
     * 파일 포트폴리오 생성 절차를 밟아서 정보를 저장한다. |
     * 500: 파일 포트폴리오 정보 저장 중 서버 에러
     */
    public Portfolio saveFile(PortfolioFileSaveRequestDto request,
                              String username,
                              Profile profile) {
        log.info("INITIALIZE | PortfolioService | saveFile | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        PortfolioType portfolioType = PortfolioType.FILE;
        validateFileType(request.getFile());

        String fileUrl = fileService.upload(bucketName,
                username + "-" + profile.getId().toString(),
                initTime.format(DateTimeFormatter.ISO_DATE_TIME),
                request.getFile());

        Portfolio portfolio = request.toEntity(profile.getId(), portfolioType, fileUrl);
        try {
            portfolio = portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | PortfolioService | saveFile | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                profile.getId() + " | " + portfolio.getId());
        return portfolio;
    }

    /**
     * 파일 타입 검증 |
     * 파일 타입이 .pdf, .png, .jpg, .jpeg 중 하나로 되어 있는지 확인한다. |
     * 415: 올바르지 않은 포맷
     */
    private void validateFileType(MultipartFile file) {
        log.info("PROGRESS | PortfolioService | validateFileType");

        String type = file.getContentType().split("/")[1];

        if (!(type.equals("pdf") || type.equals("jpg") || type.equals("jpeg") || type.equals("png"))) {
            throw new CustomException(PORTFOLIO_FILE_TYPE_UNSUPPORTED);
        }
    }

    /**
     * 링크 포트폴리오 업데이터 |
     * 링크 포트폴리오 정보를 조회하여 업데이트한다. |
     * 500: 링크 포트폴리오 정보 저장 중 서버 에러
     */
    public void updateLink(Profile profile, PortfolioLinkUpdateRequestDto request) {
        log.info("INITIALIZE | PortfolioService | update | " + profile.getId() + " | " + request.getPortfolioId());
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId portfolioId = new ObjectId(request.getPortfolioId());
        Portfolio portfolio = findOne(profile, portfolioId);

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
     * 파일 포트폴리오 업데이터 |
     * 파일 포트폴리오 정보를 조회하여 업데이트한다. |
     * 500: 파일 포트폴리오 정보 저장 중 서버 에러
     */
    public void updateFile(PortfolioFileUpdateRequestDto request,
                           String username,
                           Profile profile,
                           MultipartFile file) {
        log.info("INITIALIZE | PortfolioService | updateFile | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        ObjectId portfolioId = new ObjectId(request.getPortfolioId());
        Portfolio portfolio = findOne(profile, portfolioId);

        String fileUrl = fileService.upload(bucketName,
                username + "-" + profile.getId().toString(),
                initTime.format(DateTimeFormatter.ISO_DATE_TIME),
                file);
        portfolio.updatePortfolio(request.getName(), fileUrl);

        try {
            portfolio = portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | PortfolioService | updateFile | " + Duration.between(initTime, LocalDateTime.now()) + " | "
                + profile.getId() + " | " + portfolio.getId());
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
