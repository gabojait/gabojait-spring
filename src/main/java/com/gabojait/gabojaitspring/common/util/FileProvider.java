package com.gabojait.gabojaitspring.common.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class FileProvider {

    private final AmazonS3Client amazonS3Client;

    /**
     * S3에 파일 업로드 |
     * 400(FILE_FIELD_REQUIRED)
     * 415(IMAGE_TYPE_UNSUPPORTED / FILE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    public String upload(String bucketName,
                         String folderName,
                         String filename,
                         MultipartFile multipartFile,
                         boolean isImage) {
        validateFileType(multipartFile, isImage);

        File file = convertMultipartToFile(multipartFile);
        String folderFileName = folderName + "/" + filename;
        String url;

        try {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, folderFileName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            url = amazonS3Client.getUrl(bucketName, folderFileName).toString();
        } catch (AmazonServiceException e) {
            throw new CustomException(e, SERVER_ERROR);
        }

        file.delete();

        return url;
    }

    /**
     * MultipartFile에서 File로 변환 |
     * 500(SERVER_ERROR)
     */
    private File convertMultipartToFile(MultipartFile file) {

        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(convertFile);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            throw new CustomException(e, SERVER_ERROR);
        }

        return convertFile;
    }

    /**
     * 파일 타입 검증 |
     * 400(FILE_FIELD_REQUIRED)
     * 415(IMAGE_TYPE_UNSUPPORTED / FILE_TYPE_UNSUPPORTED)
     */
    public void validateFileType(MultipartFile multipartFile, boolean isImage) {
        if (multipartFile == null || multipartFile.isEmpty())
            throw new CustomException(FILE_FIELD_REQUIRED);

        String fileType = multipartFile.getContentType().split("/")[1];

        if (isImage) {
            if (!fileType.equals("jpg") && !fileType.equals("jpeg") && !fileType.equals("png"))
                throw new CustomException(IMAGE_TYPE_UNSUPPORTED);
        } else {
            if (!fileType.equals("pdf") && !fileType.equals("jpg") && !fileType.equals("jpeg") && !fileType.equals("png"))
                throw new CustomException(FILE_TYPE_UNSUPPORTED);
        }
    }
}
