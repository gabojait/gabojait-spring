package com.inuappcenter.gabojaitspring.profile.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.file.service.FileService;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.repository.PortfolioRepository;
import com.inuappcenter.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    @Value(value = "${s3.bucketName.portfolioFile}")
    private String bucketName;

    private final PortfolioRepository portfolioRepository;
    private final FileService fileService;

    /**
     * 포트폴리오 파일 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public Portfolio save(Portfolio portfolio) {

        try {
            return portfolioRepository.save(portfolio);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 포트폴리오 S3 업로드
     */
    public String uploadToS3(ObjectId userId, String username, MultipartFile multipartFile) {

        return fileService.upload(bucketName,
                username + "-" + userId.toString(),
                UUID.randomUUID().toString(),
                multipartFile);
    }

    /**
     * 식별자 포트폴리오 조회 |
     * 404(PORTFOLIO_NOT_FOUND)
     */
    public Portfolio findOne(String portfolioId) {

        return portfolioRepository.findById(new ObjectId(portfolioId))
                .orElseThrow(() -> {
                    throw new CustomException(PORTFOLIO_NOT_FOUND);
                });
    }

    /**
     * 권한 검증 |
     * 403(ROLE_NOT_ALLOWED)
     */
    public void validateOwner(Portfolio portfolio, User user) {

        if (user.getPortfolios().contains(portfolio))
            throw new CustomException(ROLE_NOT_ALLOWED);
    }

    /**
     * 파일 타입 검증 |
     * 415(FILE_TYPE_UNSUPPORTED)
     */
    public void validateFileType(MultipartFile file) {

        String type = file.getContentType().split("/")[1];

        if (!type.equals("pdf") && !type.equals("jpg") && !type.equals("jpeg") && !type.equals("png")) {
            throw new CustomException(FILE_TYPE_UNSUPPORTED);
        }
    }

    /**
     * 포트폴리오 업데이터 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void update(Portfolio portfolio, String portfolioName, String portfolioUrl) {

        try {
            portfolio.update(portfolioName, portfolioUrl);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(portfolio);
    }

    /**
     * 포트폴리오 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void delete(Portfolio portfolio) {

        try {
            portfolio.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(portfolio);
    }
}
