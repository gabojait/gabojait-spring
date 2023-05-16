package com.gabojait.gabojaitspring.common.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
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
                         String fileName,
                         MultipartFile multipartFile,
                         boolean isImage) {
        validateFileExistenceAndType(multipartFile, isImage);

        String folderFileName = folderName + "/" + fileName;
        String url;
        try {
            File convertedFile = convertMultiPartToFile(multipartFile);

            amazonS3Client.putObject(new PutObjectRequest(bucketName, folderFileName, convertedFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            url = amazonS3Client.getUrl(bucketName, folderFileName).toString();

            convertedFile.delete();
        } catch (AmazonServiceException e) {
            throw new CustomException(e, SERVER_ERROR);
        }

        return url;
    }

    /**
     * MultiPartFile에서 File로 변환 |
     * 500(SERVER_ERROR)
     */
    private File convertMultiPartToFile(MultipartFile file) {

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
    private void validateFileExistenceAndType(MultipartFile multipartFile, boolean isImage) {
        if (multipartFile == null || multipartFile.isEmpty())
            throw new CustomException(null, FILE_FIELD_REQUIRED);

        String fileType = multipartFile.getContentType().split("/")[1];
        if (isImage) {
            if (!fileType.equals("jpg") && !fileType.equals("jpeg") && !fileType.equals("png"))
                throw new CustomException(null, IMAGE_TYPE_UNSUPPORTED);
        } else {
            if (!fileType.equals("pdf") && !fileType.equals("jpg") && !fileType.equals("jpeg") && !fileType.equals("png"))
                throw new CustomException(null, FILE_TYPE_UNSUPPORTED);
        }
    }
}
