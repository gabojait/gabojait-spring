package com.gabojait.gabojaitspring.common.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gabojait.gabojaitspring.common.exception.CustomException;
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
public class FileUtility {

    private final AmazonS3Client amazonS3Client;

    /**
     * 파일 업로드 |
     * 400(FILE_FIELD_REQUIRED)
     * 415(IMAGE_TYPE_UNSUPPORTED / FILE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     * @param bucketName 버켓명
     * @param folderName 폴더명
     * @param fileName 파일명
     * @param multipartFile 파일
     * @param isImage 이미지 여부
     * @return 이미지 주소
     */
    public String upload(String bucketName,
                         String folderName,
                         String fileName,
                         MultipartFile multipartFile,
                         boolean isImage) {
        validateFileType(multipartFile, isImage);

        File file = convertMultipartToFile(multipartFile);
        String folderFileName = folderName + "/" + fileName;
        String url;

        try {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, folderFileName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            url = amazonS3Client.getUrl(bucketName, folderFileName).toString();
        } catch (AmazonServiceException e) {
            throw new CustomException(SERVER_ERROR, e.getCause());
        }

        file.delete();

        return url;
    }

    /**
     * 파일 타입 변환 |
     * 500(SERVER_ERROR)
     * @param file MultipartFile
     * @return File
     */
    private File convertMultipartToFile(MultipartFile file) {

        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(convertFile);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            throw new CustomException(SERVER_ERROR, e.getCause());
        }

        return convertFile;
    }

    /**
     * 파일 타입 검증 |
     * 400(FILE_FIELD_REQUIRED)
     * 415(IMAGE_TYPE_UNSUPPORTED / FILE_TYPE_UNSUPPORTED)
     * @param multipartFile 파일
     * @param isImage 이미지 여부
     */
    private void validateFileType(MultipartFile multipartFile, boolean isImage) {
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
