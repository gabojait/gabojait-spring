package com.inuappcenter.gabojaitspring.common;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.user.repository.ContactRepository;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import com.inuappcenter.gabojaitspring.profile.repository.EducationRepository;
import com.inuappcenter.gabojaitspring.profile.repository.PortfolioRepository;
import com.inuappcenter.gabojaitspring.profile.repository.SkillRepository;
import com.inuappcenter.gabojaitspring.profile.repository.WorkRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.SERVER_ERROR;

@Api(tags = "개발용")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dev")
public class DevelopController {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final WorkRepository workRepository;
    private final SkillRepository skillRepository;
    private final PortfolioRepository portfolioRepository;

    // TODO: Must remove before service deployment
    @ApiOperation(value = "데이터베이스 초기화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping
    public ResponseEntity<DefaultResDto<Object>> deleteAll() {

        try {
            userRepository.deleteAll();
            contactRepository.deleteAll();
            educationRepository.deleteAll();
            workRepository.deleteAll();
            skillRepository.deleteAll();
            portfolioRepository.deleteAll();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        return ResponseEntity.status(204)
                .body(DefaultResDto.builder()
                        .build());
    }
}
