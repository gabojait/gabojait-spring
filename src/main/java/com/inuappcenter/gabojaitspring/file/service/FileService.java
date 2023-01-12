package com.inuappcenter.gabojaitspring.file.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3Client amazonS3Client;

    /**
     * S3에 파일 업로드 |
     * MultipartFile 에서 File 로 형변환을 한 후 파일을 S3에 업로드한다. |
     * 500: S3에 파일 업로드 중 서버 에러
     */
    public String upload(String bucketName, String folderName, String fileName, MultipartFile file) {
        log.info("PROGRESS | FileService | save | " + bucketName + " | " + folderName + fileName);

        String url;
        String folderFileName = folderName + "/" + fileName;

        try {
            File convertedFile = convertMultiPartToFile(file);

            amazonS3Client.putObject(new PutObjectRequest(bucketName, folderFileName, convertedFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            url = amazonS3Client.getUrl(bucketName, folderFileName).toString();

            convertedFile.delete();
        } catch (AmazonServiceException e) {
            throw new CustomException(SERVER_ERROR);
        }

        return url;
    }

    /**
     * MultiPartFile 에서 File 로 형변환 |
     * MultiPartFile 에서 File 로 형변환을 하여 리턴한다. |
     * 500: 형변환 중 서버 에러 발생
     */
    private File convertMultiPartToFile(MultipartFile file) {
        log.info("PROGRESS | FileService | convertMultiPartToFile");

        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(convertFile);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            throw new CustomException(SERVER_ERROR);
        }
        return convertFile;
    }
}
