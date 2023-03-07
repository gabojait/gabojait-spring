package com.inuappcenter.gabojaitspring.review.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.review.domain.Question;
import com.inuappcenter.gabojaitspring.review.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * 리뷰 질문 저장 |
     * 500(SERVER_ERROR)
     */
    public Question save(Question question) {

        try {
            return questionRepository.save(question);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 현재 리뷰 질문 전체 조회 |
     * 500(SERVER_ERROR)
     */
    public List<Question> findAllCurrentQuestions() {

        try {
            return questionRepository.findQuestionsByIsDeletedIsFalseOrderByCreatedDateDesc();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
