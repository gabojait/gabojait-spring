package com.inuappcenter.gabojaitspring.review.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.review.domain.Question;
import com.inuappcenter.gabojaitspring.review.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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

    /**
     * 현재 리뷰 질문 단건 조회 |
     * 404(QUESTION_NOT_FOUND)
     */
    public Question findById(String questionId) {

        return questionRepository.findByIdAndIsDeletedIsFalse(new ObjectId(questionId))
                .orElseThrow(() -> {
                    throw new CustomException(QUESTION_NOT_FOUND);
                });
    }

    /**
     * 현재 리뷰 질문 다건 조회 및 리뷰 타입 검증 |
     * 400(REVIEW_RATING_FORMAT_INVALID)
     * 400(REVIEW_ANSWER_FORMAT_INVALID)
     * 500(SERVER_ERROR)
     */
    public void validateReviewType(Question question, Byte rate, String answer) {

        switch (question.getReviewType()) {
            case 'R':
                if (rate == null || answer != null)
                    throw new CustomException(REVIEW_RATING_FORMAT_INVALID);
                break;
            case 'A':
                if (answer == null || rate != null)
                    throw new CustomException(REVIEW_ANSWER_FORMAT_INVALID);
                break;
            default:
                throw new CustomException(SERVER_ERROR);

        }
    }
}
