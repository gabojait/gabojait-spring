package com.gabojait.gabojaitspring.config;

import com.gabojait.gabojaitspring.exception.CustomException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Configuration
public class FcmConfig {

    @Value("${firebase.project.id}")
    private String projectId;

    @Value("${firebase.private.key-id}")
    private String privateKeyId;

    @Value("${firebase.private.key}")
    private String privateKey;

    @Value("${firebase.client.email}")
    private String clientEmail;

    @Value("${firebase.client.id}")
    private String clientId;

    @Value("${firebase.client.x509.cert.url}")
    private String clientX509CertUrl;

    private ByteArrayInputStream firebaseAdminSdk() {
        String content = "{" +
                "\"type\": \"service_account\", " +
                "\"project_id\": \"" + projectId + "\", " +
                "\"private_key_id\": \"" + privateKeyId +"\", " +
                "\"private_key\": \"" + privateKey + "\", "+
                "\"client_email\": \"" + clientEmail + "\", " +
                "\"client_id\": \"" + clientId + "\", " +
                "\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\", " +
                "\"token_uri\": \"https://oauth2.googleapis.com/token\", " +
                "\"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\", " +
                "\"client_x509_cert_url\": \"" + clientX509CertUrl + "\", " +
                "\"universe_domain\": \"googleapis.com\"" +
                "}";

        return new ByteArrayInputStream(content.getBytes());
    }

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseAdminSdk()))
                    .build();

            return FirebaseApp.initializeApp(firebaseOptions);
        } catch (IOException e) {
            throw new CustomException(SERVER_ERROR, e.getCause());
        }
    }
}
