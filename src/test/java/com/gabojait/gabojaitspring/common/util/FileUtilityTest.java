package com.gabojait.gabojaitspring.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// TODO
//@ActiveProfiles("test")
//@SpringBootTest
//class FileUtilityTest {
//
//    @MockBean private AmazonS3Client amazonS3Client;
//    @Autowired private FileUtility fileUtility;
//
//    @Test
//    @DisplayName("파일 업로드가 정상 작동한다")
//    void givenValid_whenUpload_thenReturn() throws MalformedURLException {
//        // given
//        String bucketName = "test";
//        String folderName = "";
//        String fileName = "file";
//        String contentType = "image/jpeg";
//        byte[] content = "a".getBytes();
//        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, contentType, content);
//        boolean isImage = true;
//
//        URL url = new URL("https://gabojait.com/test");
//
//        when(amazonS3Client.getUrl(anyString(), anyString()))
//                .thenReturn(url);
//
//        // when
//        String uploadedUrl = fileUtility.upload(bucketName, folderName, fileName, multipartFile, isImage);
//
//        // then
//        assertThat(uploadedUrl).isEqualTo(url.toString());
//    }
//
//    @Test
//    @DisplayName("null인 파일로 파일 업로드시 예외가 발생한다")
//    void givenNullMultipartFile_whenUpload_thenThrow() throws MalformedURLException {
//        // given
//        String bucketName = "test";
//        String folderName = "";
//        String fileName = "file";
//        MultipartFile multipartFile = null;
//        boolean isImage = true;
//
//        URL url = new URL("https://gabojait.com/test");
//
//        when(amazonS3Client.getUrl(anyString(), anyString()))
//                .thenReturn(url);
//
//        // when & then
//        assertThatThrownBy(() -> fileUtility.upload(bucketName, folderName, fileName, multipartFile, isImage))
//                .isInstanceOf(CustomException.class)
//                .extracting("errorCode")
//                .isEqualTo(FILE_FIELD_REQUIRED);
//    }
//
//    @Test
//    @DisplayName("빈 파일로 파일 업로드시 예외가 발생한다")
//    void givenEmptyMultipartFile_whenUpload_thenThrow() throws MalformedURLException {
//        // given
//        String bucketName = "test";
//        String folderName = "";
//        String fileName = "file";
//        String contentType = "image/jpeg";
//        byte[] content = new byte[0];
//        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, contentType, content);
//        boolean isImage = true;
//
//        URL url = new URL("https://gabojait.com/test");
//
//        when(amazonS3Client.getUrl(anyString(), anyString()))
//                .thenReturn(url);
//
//        // when & then
//        assertThatThrownBy(() -> fileUtility.upload(bucketName, folderName, fileName, multipartFile, isImage))
//                .isInstanceOf(CustomException.class)
//                .extracting("errorCode")
//                .isEqualTo(FILE_FIELD_REQUIRED);
//    }
//}