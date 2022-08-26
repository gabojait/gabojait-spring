package com.inuappcenter.gabojaitspring.email.service;

import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    /**
     * 이메일 전송 |
     * 이메일 받는자, 이메일 제목, 이메일 내용을 받아서 gabojait.help@gmail.com으로부터 보낸다. 전송 중 오류가 발생하면 500(Internal Server
     * Error)을 던진다.
     */
    @Async
    public void sendEmail(String receiver, String title, String content) {
        log.info("IN PROGRESS | 이메일 전송 At " + LocalDateTime.now() +
                " | receiver = " + receiver  + " title = " + title);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            mimeMessageHelper.setTo(receiver);
            mimeMessageHelper.setSubject(title);
            mimeMessageHelper.setText(content, true);
            mimeMessageHelper.setFrom("gabojait.help@gmail.com");

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new InternalServerErrorException("이메일 전송 중 에러", e);
        }
        log.info("COMPLETE | 이메일 전송 완료 At " + LocalDateTime.now() +
                " | receiver = " + receiver  + " title = " + title);
    }
}
