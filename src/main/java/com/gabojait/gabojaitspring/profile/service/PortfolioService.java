package com.gabojait.gabojaitspring.profile.service;

import com.gabojait.gabojaitspring.common.util.FileProvider;
import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import com.gabojait.gabojaitspring.profile.dto.req.PortfolioLinkCreateReqDto;
import com.gabojait.gabojaitspring.profile.dto.req.PortfolioLinkUpdateReqDto;
import com.gabojait.gabojaitspring.profile.repository.PortfolioRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    @Value("${s3.bucket.portfolio-file}")
    private String bucketName;
    private final PortfolioRepository portfolioRepository;
    private final UtilityProvider utilityProvider;
    private final FileProvider fileProvider;

    /**
     * 전체 포트폴리오 링크 업데이트 | main |
     * 404(PORTFOLIO_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void updateLinkAll(ObjectId userId, List<PortfolioLinkUpdateReqDto> requests) {
        List<Portfolio> portfolios = new ArrayList<>();
        for (PortfolioLinkUpdateReqDto request: requests) {
            ObjectId id = utilityProvider.toObjectId(request.getPortfolioId());

            Portfolio portfolio = findOne(id);
            validateOwner(portfolio, userId);

            portfolios.add(portfolio);
        }

        for (int i = 0; i < portfolios.size(); i++) {
            portfolios.get(i).update(requests.get(i).getPortfolioName(),
                    requests.get(i).getUrl());

            save(portfolios.get(i));
        }
    }

    /**
     * 전체 포트폴리오 링크 업데이트 | main |
     * 400(FILE_FIELD_REQUIRED)
     * 404(PORTFOLIO_NOT_FOUND)
     * 415(FILE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    public void updateFileAll(User user,
                              List<String> portfolioIds,
                              List<String> portfolioNames,
                              List<MultipartFile> multipartFiles) {
        List<Portfolio> portfolios = new ArrayList<>();

        if (portfolioIds == null || portfolioNames == null || multipartFiles == null)
            return;

        for (String portfolioId : portfolioIds) {
            ObjectId id = utilityProvider.toObjectId(portfolioId);

            Portfolio portfolio = findOne(id);
            validateOwner(portfolio, user.getId());

            portfolios.add(portfolio);
        }

        for (int i = 0; i < multipartFiles.size(); i++) {
            String url = fileProvider.upload(bucketName,
                    user.getId().toString(),
                    portfolioNames.get(i) + "-" + UUID.randomUUID(),
                    multipartFiles.get(i),
                    false);

            portfolios.get(i).update(portfolioNames.get(i), url);
        }
    }

    /**
     * 전체 포트폴리오 링크 생성 | sub |
     * 500(SERVER_ERROR)
     */
    public List<Portfolio> createLinkAll(ObjectId userId, List<PortfolioLinkCreateReqDto> requests) {
        List<Portfolio> portfolios = new ArrayList<>();
        for (PortfolioLinkCreateReqDto request: requests) {
            Portfolio portfolio = save(request.toEntity(userId));
            portfolios.add(portfolio);
        }

        return portfolios;
    }

    /**
     * 전체 포트폴리오 파일 생성 | sub |
     * 400(FILE_FIELD_REQUIRED)
     * 415(FILE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    public List<Portfolio> createFileAll(User user, List<String> portfolioNames, List<MultipartFile> multipartFiles) {
        List<Portfolio> portfolios = new ArrayList<>();

        if (portfolioNames == null)
            return portfolios;

        for (int i = 0; i < multipartFiles.size(); i++) {
            String url = fileProvider.upload(bucketName,
                    user.getId().toString(),
                    portfolioNames.get(i) + "-" + UUID.randomUUID(),
                    multipartFiles.get(i),
                    false);

            Portfolio portfolio = Portfolio.builder()
                    .userId(user.getId())
                    .portfolioName(portfolioNames.get(i))
                    .url(url)
                    .media(Media.FILE)
                    .build();
            save(portfolio);
            portfolios.add(portfolio);
        }

        return portfolios;
    }

    /**
     * 전체 포트폴리오 삭제 | sub |
     * 403(REQUEST_FORBIDDEN)
     * 404(PORTFOLIO_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<Portfolio> deleteAll(ObjectId userId, List<String> portfolioIds) {
        List<Portfolio> portfolios = new ArrayList<>();

        if (portfolioIds == null)
            return portfolios;

        for (String portfolioId: portfolioIds) {
            ObjectId id = utilityProvider.toObjectId(portfolioId);

            Portfolio portfolio = findOne(id);
            validateOwner(portfolio, userId);

            portfolios.add(portfolio);
        }

        for (Portfolio portfolio: portfolios)
            softDelete(portfolio);

        return portfolios;
    }

    /**
     * 소유자 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateOwner(Portfolio portfolio, ObjectId userId) {
        if (!portfolio.getUserId().equals(userId))
            throw new CustomException(null, REQUEST_FORBIDDEN);
    }

    /**
     * 포트폴리오 파일 전체 검증 | sub |
     * 400(PORTFOLIO_NAME_LENGTH_INVALID / CREATE_PORTFOLIO_CNT_MATCH_INVALID / UPDATE_PORTFOLIO_CNT_MATCH_INVALID)
     * 404(PORTFOLIO_NOT_FOUND)
     * 413(FILE_COUNT_EXCEED)
     */
    public void validateFilesPreAll(List<String> createPortfolioNames,
                                    List<MultipartFile> createPortfolioFiles,
                                    List<String> updatePortfolioIds,
                                    List<String> updatePortfolioNames,
                                    List<MultipartFile> updatePortfolioFiles,
                                    List<String> deletePortfolioIds) {
        if (createPortfolioNames != null && createPortfolioFiles != null) {
            isExceedingLengthName(createPortfolioNames);
            isExceedingSizeFile(createPortfolioFiles);
            if (isUnmatchedCntNamesAndFiles(createPortfolioNames, createPortfolioFiles))
                throw new CustomException(null, CREATE_PORTFOLIO_CNT_MATCH_INVALID);
        }

        if (updatePortfolioIds != null && updatePortfolioNames != null && updatePortfolioFiles != null) {
            isExistingPortfolios(updatePortfolioIds);
            isExceedingLengthName(updatePortfolioNames);
            if (isUnmatchedCntIdsAndNamesAndFiles(updatePortfolioIds, updatePortfolioNames, updatePortfolioFiles))
                throw new CustomException(null, UPDATE_PORTFOLIO_CNT_MATCH_INVALID);
        }

        if (deletePortfolioIds != null)
            isExistingPortfolios(deletePortfolioIds);
    }

    /**
     * 포트폴리오 전체 존재 여부 검증 |
     * 404(PORTFOLIO_NOT_FOUND)
     */
    private void isExistingPortfolios(List<String> portfolioIds) {
        if (portfolioIds == null)
            return;

        for (String portfolioId : portfolioIds) {
            ObjectId id = utilityProvider.toObjectId(portfolioId);

            findOne(id);
        }
    }

    /**
     * 포트폴리오명 글자 수 검증 |
     * 400(PORTFOLIO_NAME_LENGTH_INVALID)
     */
    private void isExceedingLengthName(List<String> portfolioNames) {
        for (String portfolioName : portfolioNames)
            if (portfolioName.length() > 10)
                throw new CustomException(null, PORTFOLIO_NAME_LENGTH_INVALID);
    }

    /**
     * 포트폴리오 파일 수 검증 |
     * 413(FILE_COUNT_EXCEED)
     */
    private void isExceedingSizeFile(List<MultipartFile> portfolioFiles) {
        if (portfolioFiles == null)
            return;

        if (portfolioFiles.size() > 5)
            throw new CustomException(null, FILE_COUNT_EXCEED);
    }

    /**
     * 포트폴리오 식별자, 포트폴리오명, 파일 수 일치 검증
     */
    private boolean isUnmatchedCntIdsAndNamesAndFiles(List<String> portfolioIds,
                                                      List<String> portfolioNames,
                                                      List<MultipartFile> multipartFiles) {
        return portfolioIds.size() != portfolioNames.size() || portfolioIds.size() != multipartFiles.size();
    }

    /**
     * 포트폴리오명과 파일 수 일치 검증
     */
    private boolean isUnmatchedCntNamesAndFiles(List<String> portfolioNames, List<MultipartFile> multipartFiles) {
        return portfolioNames.size() != multipartFiles.size();
    }

    /**
     * 포트폴리오 단건 조회 |
     * 404(PORTFOLIO_NOT_FOUND)
     */
    private Portfolio findOne(ObjectId portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> {
                    throw new CustomException(null, PORTFOLIO_NOT_FOUND);
                });
    }

    /**
     * 포트폴리오 저장 |
     * 500(SERVER_ERROR)
     */
    public Portfolio save(Portfolio portfolio) {
        try {
            return portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    public void softDelete(Portfolio portfolio) {
        portfolio.delete();

        save(portfolio);
    }

    /**
     * 포트폴리오 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    private void hardDelete(Portfolio portfolio) {
        try {
            portfolioRepository.delete(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
