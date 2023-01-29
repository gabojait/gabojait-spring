package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.file.service.FileService;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.dto.req.PortfolioFileSaveReqDto;
import com.inuappcenter.gabojaitspring.profile.dto.req.PortfolioFileUpdateReqDto;
import com.inuappcenter.gabojaitspring.profile.dto.req.PortfolioLinkDefaultReqDto;
import com.inuappcenter.gabojaitspring.profile.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    @Value(value = "${s3.portfolioFileBucketName}")
    private String bucketName;

    private final PortfolioRepository portfolioRepository;
    private final FileService fileService;

    /**
     * 포트폴리오 파일 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public Portfolio savePortfolioFile(ObjectId userId, String username, PortfolioFileSaveReqDto request) {

        String url = fileService.upload(bucketName,
                username + "@" + userId.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                request.getFile());

        try {
            return portfolioRepository.save(request.toEntity(userId, url));
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 식별자 포트폴리오 조회 |
     * 404(PORTFOLIO_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Portfolio findOnePortfolio(String portfolioId) {

        try {
            return portfolioRepository.findById(new ObjectId(portfolioId))
                    .orElseThrow(() -> {
                        throw new CustomException(PORTFOLIO_NOT_FOUND);
                    });
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 파일 업데이터 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void updatePortfolioFile(ObjectId userId,
                                    String username,
                                    Portfolio portfolio,
                                    PortfolioFileUpdateReqDto request) {

        String url = fileService.upload(bucketName,
                username + "@" + userId.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                request.getFile());

        try {
            portfolio.update(request.getPortfolioName(), url);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 링크 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public Portfolio savePortfolioLink(ObjectId userId, PortfolioLinkDefaultReqDto request) {

        try {
            return portfolioRepository.save(request.toEntity(userId));
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 링크 업데이터 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void updatePortfolioLink(ObjectId userId,
                                    Portfolio portfolio,
                                    PortfolioLinkDefaultReqDto request) {

        try {
            portfolio.update(request.getPortfolioName(), request.getUrl());
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void deletePortfolio(Portfolio portfolio) {

        try {
            portfolio.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
