package com.gabojait.gabojaitspring.api.controller.notification;

import com.gabojait.gabojaitspring.common.response.DefaultMultiResponse;
import com.gabojait.gabojaitspring.common.response.DefaultNoResponse;
import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.notification.response.NotificationPageResponse;
import com.gabojait.gabojaitspring.api.service.notification.NotificationService;
import com.gabojait.gabojaitspring.config.auth.JwtProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.gabojait.gabojaitspring.common.constant.code.SuccessCode.NOTIFICATIONS_FOUND;
import static com.gabojait.gabojaitspring.common.constant.code.SuccessCode.NOTIFICATION_READ;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "알림")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtProvider jwtProvider;

    @ApiOperation(
            value = "알림 페이징 조회",
            notes = "<검증>\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = NOTIFICATIONS_FOUND\n" +
                    "- 400 = PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema =  @Schema(implementation = NotificationPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/notification")
    public ResponseEntity<DefaultMultiResponse<Object>> findPageNotification(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = "page-from", required = false, defaultValue = "9223372036854775806")
            @Positive(message = "페이지 시작점은 양수만 가능합니다.")
            Long pageFrom,
            @RequestParam(value = "page-size", required = false, defaultValue = "20")
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            @Max(value = 100, message = "페이지 사이즈는 100까지의 수만 가능합니다.")
            Integer pageSize
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PageData<List<NotificationPageResponse>> responses = notificationService.findPageNotifications(userId,
                pageFrom, pageSize);

        return ResponseEntity.status(NOTIFICATIONS_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(NOTIFICATIONS_FOUND.name())
                        .responseMessage(NOTIFICATIONS_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }

    @ApiOperation(value = "알림 읽기",
            notes = "<응답 코드>\n" +
                    "- 200 = NOTIFICATION_READ\n" +
                    "- 400 = NOTIFICATION_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema =  @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/user/notification/{notification-id}")
    public ResponseEntity<DefaultNoResponse> readNotification(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "notification-id")
            @Positive(message = "알림 식별자는 양수만 가능합니다.")
            Long notificationId
    ) {
        long userId = jwtProvider.getUserId(authorization);

        notificationService.readNotification(userId, notificationId);

        return ResponseEntity.status(NOTIFICATION_READ.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(NOTIFICATION_READ.name())
                        .responseMessage(NOTIFICATION_READ.getMessage())
                        .build());
    }

}
