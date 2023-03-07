package com.inuappcenter.gabojaitspring.review.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.review.domain.Question;
import com.inuappcenter.gabojaitspring.review.dto.req.ReviewSaveManyReqDto;
import com.inuappcenter.gabojaitspring.review.dto.req.ReviewSaveOneReqDto;
import com.inuappcenter.gabojaitspring.review.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
     * 현재 리뷰 질문 다건 조회 및 리뷰 타입 검증 |
     * 400(REVIEWTYPE_FORMAT_INVALID)
     * 400(REVIEW_RATING_FORMAT_INVALID)
     * 400(REVIEW_ANSWER_FORMAT_INVALID)
     * 404(QUESTION_NOT_FOUND)
     */
    public List<Question> findAndValidateCurrentQuestionAndReviewType(List<ReviewSaveOneReqDto> reviews) {

        List<Question> questions = new ArrayList<>();

        for (ReviewSaveOneReqDto review : reviews) {
            Question question = questionRepository.findByIdAndIsDeletedIsFalse(new ObjectId(review.getQuestionId()))
                    .orElseThrow(() -> {
                        throw new CustomException(QUESTION_NOT_FOUND);
                    });

            switch (question.getReviewType()) {
                case 'R':
                    if (review.getAnswer() != null || review.getRate() == null)
                        throw new CustomException(REVIEW_RATING_FORMAT_INVALID);
                    break;
                case 'A':
                    if (review.getRate() != null || review.getAnswer() == null)
                        throw new CustomException(REVIEW_ANSWER_FORMAT_INVALID);
                    break;
                default:
                    throw new CustomException(REVIEWTYPE_FORMAT_INVALID);
            }

            questions.add(question);
        }

        return questions;
    }
}
