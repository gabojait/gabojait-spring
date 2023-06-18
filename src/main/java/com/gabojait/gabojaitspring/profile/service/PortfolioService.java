package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.common.util.FileProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.dto.req.PortfolioLinkCreateReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.PortfolioLinkDefaultReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.PortfolioLinkUpdateReqDto;
import com.gabojait.gabojaitspring.profile.repository.PortfolioRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioService {

    @Value("${s3.bucket.portfolio-file}")
    private String bucketName;

    private final PortfolioRepository portfolioRepository;
    private final FileProvider fileProvider;

    /**
     * 링크 포트폴리오 생성, 수정, 및 삭제 |
     * 404(PORTFOLIO_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void createUpdateDeleteLink(User user, PortfolioLinkDefaultReqDto request) {
        for(PortfolioLinkCreateReqDto createReq : request.getCreateLinkPortfolios())
            createLink(user, createReq);
        for(PortfolioLinkUpdateReqDto updateReq : request.getUpdateLinkPortfolios())
            updateLink(user, updateReq);
        for(Long deleteReqId : request.getDeletePortfolioIds())
            delete(user, deleteReqId);
    }

    /**
     * 파일 포트폴리오 생성, 수정, 및 삭제 |
     * 400(PORTFOLIO_NAME_LENGTH_INVALID / CREATE_PORTFOLIO_CNT_MATCH_INVALID / UPDATE_PORTFOLIO_CNT_MATCH_INVALID /
     * FILE_FIELD_REQUIRED)
     * 404(PORTFOLIO_NOT_FOUND)
     * 415(FILE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    public void createUpdateDeleteFile(User user,
                                       List<String> createPortfolioNames,
                                       List<MultipartFile> createPortfolioFiles,
                                       List<Long> updatePortfolioIds,
                                       List<String> updatePortfolioNames,
                                       List<MultipartFile> updatePortfolioFiles,
                                       List<Long> deletePortfolioIds) {
        validatePortfolioNameLength(createPortfolioNames, updatePortfolioNames);
        validateCreateFileCnt(createPortfolioNames.size(), createPortfolioFiles.size());
        validateUpdateFileCnt(updatePortfolioIds.size(), updatePortfolioNames.size(), updatePortfolioFiles.size());

        for(int i = 0; i < createPortfolioNames.size(); i++)
            createFile(user, createPortfolioNames.get(i), createPortfolioFiles.get(i));
        for(int i = 0; i < updatePortfolioIds.size(); i++)
            updateFile(user, updatePortfolioIds.get(i), updatePortfolioNames.get(i), updatePortfolioFiles.get(i));
        for(Long deleteReqId : deletePortfolioIds)
            delete(user, deleteReqId);
    }

    /**
     * 링크 포트폴리오 생성 |
     * 500(SERVER_ERROR)
     */
    private void createLink(User user, PortfolioLinkCreateReqDto request) {
        Portfolio portfolio = request.toEntity(user);

        savePortfolio(portfolio);
    }

    /**
     * 파일 포트폴리오 생성 |
     * 400(FILE_FIELD_REQUIRED)
     * 415(FILE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    private void createFile(User user, String portfolioName, MultipartFile portfolioFile) {
        String portfolioUrl = fileProvider.upload(bucketName,
                user.getId().toString(),
                portfolioName + "-" + UUID.randomUUID(),
                portfolioFile,
                false);

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .portfolioName(portfolioName)
                .portfolioUrl(portfolioUrl)
                .build();

        savePortfolio(portfolio);
    }


    /**
     * 링크 포트폴리오 수정 |
     * 404(PORTFOLIO_NOT_FOUND)
     */
    private void updateLink(User user, PortfolioLinkUpdateReqDto request) {
        Portfolio portfolio = findOnePortfolio(request.getPortfolioId(), user);

        portfolio.update(request.getPortfolioName(), request.getPortfolioUrl());
    }

    /**
     * 파일 포트폴리오 수정 |
     * 400(FILE_FIELD_REQUIRED)
     * 404(PORTFOLIO_NOT_FOUND)
     * 415(FILE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    private void updateFile(User user, Long portfolioId, String portfolioName, MultipartFile portfolioFile) {
        Portfolio portfolio = findOnePortfolio(portfolioId, user);

        String url = fileProvider.upload(bucketName,
                user.getId().toString(),
                portfolioName + "-" + UUID.randomUUID(),
                portfolioFile,
                false);

        portfolio.update(portfolioName, url);
    }

    /**
     * 포트폴리오 삭제 |
     * 404(PORTFOLIO_NOT_FOUND)
     */
    private void delete(User user, Long portfolioId) {
        Portfolio portfolio = findOnePortfolio(portfolioId, user);

        softDeletePortfolio(portfolio);
    }

    /**
     * 포트폴리오 저장 |
     * 500(SERVER_ERROR)
     */
    private void savePortfolio(Portfolio portfolio) {
        try {
            portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 기술 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    private void softDeletePortfolio(Portfolio portfolio) {
        portfolio.delete();
    }

    /**
     * 식별자와 회원으로 포트폴리오 단건 조회 |
     * 404(PORTFOLIO_NOT_FOUND)
     */
    private Portfolio findOnePortfolio(Long portfolioId, User user) {
        return portfolioRepository.findByIdAndUserAndIsDeletedIsFalse(portfolioId, user)
                .orElseThrow(() -> {
                    throw new CustomException(PORTFOLIO_NOT_FOUND);
                });
    }

    /**
     * 포트폴리오명 길이 검증 |
     * 400(PORTFOLIO_NAME_LENGTH_INVALID)
     */
    private void validatePortfolioNameLength(List<String> createPortfolioNames, List<String> updatePortfolioNames) {
        for(String portfolioName : createPortfolioNames)
            if (portfolioName.length() < 1  || portfolioName.length() > 15)
                throw new CustomException(PORTFOLIO_NAME_LENGTH_INVALID);

        for(String portfolioName : updatePortfolioNames)
            if (portfolioName.length() < 1  || portfolioName.length() > 15)
                throw new CustomException(PORTFOLIO_NAME_LENGTH_INVALID);

    }

    /**
     * 파일 생성 수 검증 |
     * 400(CREATE_PORTFOLIO_CNT_MATCH_INVALID)
     */
    private void validateCreateFileCnt(int portfolioNameSize, int portfolioFileSize) {
        if (portfolioNameSize != portfolioFileSize)
            throw new CustomException(CREATE_PORTFOLIO_CNT_MATCH_INVALID);
    }

    /**
     * 파일 업데이트 수 검증 |
     * 400(UPDATE_PORTFOLIO_CNT_MATCH_INVALID)
     */
    private void validateUpdateFileCnt(int portfolioIdSize, int portfolioNameSize, int portfolioFileSize) {
        if (portfolioIdSize != portfolioNameSize || portfolioNameSize != portfolioFileSize)
            throw new CustomException(UPDATE_PORTFOLIO_CNT_MATCH_INVALID);
    }
}
