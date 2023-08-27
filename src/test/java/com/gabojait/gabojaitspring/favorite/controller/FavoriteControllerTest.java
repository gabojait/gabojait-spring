package com.gabojait.gabojaitspring.favorite.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.WebMvc;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.favorite.dto.req.FavoriteUpdateReqDto;
import com.gabojait.gabojaitspring.favorite.service.FavoriteTeamService;
import com.gabojait.gabojaitspring.favorite.service.FavoriteUserService;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest extends WebMvc {

    @MockBean
    private FavoriteUserService favoriteUserService;

    @MockBean
    private FavoriteTeamService favoriteTeamService;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        doReturn(1L)
                .when(this.jwtProvider)
                .getId(any());

        User tester = User.testBuilder()
                .id(1L)
                .build();

        Team team = Team.builder()
                .projectName("가보자잇")
                .projectDescription("가보자잇 프로젝트 설명입니다.")
                .designerCnt((byte) 2)
                .backendCnt((byte) 2)
                .frontendCnt((byte) 2)
                .managerCnt((byte) 2)
                .expectation("열정적인 팀원을 원합니다.")
                .openChatUrl("https://open.kakao.com/o")
                .build();

        FavoriteTeam favoriteTeam = FavoriteTeam.builder()
                .user(tester)
                .team(team)
                .build();

        Page<FavoriteTeam> favoriteTeams = new PageImpl<>(List.of(favoriteTeam));

        FavoriteUser favoriteUser = FavoriteUser.builder()
                .user(tester)
                .team(team)
                .build();

        Page<FavoriteUser> favoriteUsers = new PageImpl<>(List.of(favoriteUser));

        doReturn(favoriteTeams)
                .when(this.favoriteTeamService)
                .findManyFavoriteTeams(anyLong(), anyLong(), any());

        doReturn(favoriteUsers)
                .when(this.favoriteUserService)
                .findManyFavoriteUsers(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("회원이 팀 찜하기 및 찜 취소하기 | 올바른 찜하기 요청시 | 201반환")
    void addOrDeleteFavoriteTeam_givenValidAddReq_return201() throws Exception {
        // given
        Long teamId = getValidId();
        FavoriteUpdateReqDto reqDto = getValidFavoriteUpdateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/favorite/team/{team-id}", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FAVORITE_TEAM_ADDED.getHttpStatus().value());
        assertThat(response).contains(FAVORITE_TEAM_ADDED.name());
    }

    @Test
    @DisplayName("회원이 팀 찜하기 및 찜 취소하기 | 올바른 찜 취소하기 요청시 | 200반환")
    void addOrDeleteFavoriteTeam_givenValidDeleteReq_return200() throws Exception {
        // given
        Long teamId = getValidId();
        FavoriteUpdateReqDto reqDto = getValidFavoriteUpdateReqDto();
        reqDto.setIsAddFavorite(false);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/favorite/team/{team-id}", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FAVORITE_TEAM_DELETED.getHttpStatus().value());
        assertThat(response).contains(FAVORITE_TEAM_DELETED.name());
    }

    @Test
    @DisplayName("회원이 팀 찜하기 및 찜 취소하기 | 팀 식별자가 양수 아닐시 | 400반환")
    void addOrDeleteFavoriteTeam_givenTeamIdPositiveOnly_return400() throws Exception {
        // given
        Long teamId = 0L;
        FavoriteUpdateReqDto reqDto = getValidFavoriteUpdateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/favorite/team/{team-id}", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(TEAM_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("회원이 팀 찜하기 및 찜 취소하기 | 찜 추가 여부 미입력시 | 400반환")
    void addOrDeleteFavoriteTeam_givenIsAddFavoriteFieldRequired_return400() throws Exception {
        // given
        Long teamId = getValidId();
        FavoriteUpdateReqDto reqDto = getValidFavoriteUpdateReqDto();
        reqDto.setIsAddFavorite(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/favorite/team/{team-id}", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_ADD_FAVORITE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_ADD_FAVORITE_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원이 찜한 팀 다건 조회 | 올바른 요청시 | 200반환")
    void findAllFavoriteTeams_givenValidReq_return200() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/favorite/team")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FAVORITE_TEAMS_FOUND.getHttpStatus().value());
        assertThat(response).contains(FAVORITE_TEAMS_FOUND.name());
    }

    @Test
    @DisplayName("회원이 찜한 팀 다건 조회 | 페이지 시작점 미입력시 | 400반환")
    void findAllFavoriteTeams_givenPageFromFieldRequired_return400() throws Exception {
        // given
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/favorite/team")
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원이 찜한 팀 다건 조회 | 페이지 시작점이 양수 또는 0 아닐시 | 400반환")
    void findAllFavoriteTeams_givenPageFromPositiveOrZeroOnly_return400() throws Exception {
        // given
        Integer pageFrom = -1;
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/favorite/team")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.name());
    }

    @Test
    @DisplayName("회원이 찜한 팀 다건 조회 | 페이지 사이즈가 양수 아닐시 | 400반환")
    void findAllFavoriteTeams_givenPageSizePositiveOnly_return400() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = 0;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/favorite/team")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_SIZE_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_SIZE_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("팀이 회원 찜하기 및 찜 취소하기 | 올바른 찜하기 요청시 | 201반환")
    void addOrDeleteFavoriteUser_givenValidAddReq_return201() throws Exception {
        // given
        FavoriteUpdateReqDto reqDto = getValidFavoriteUpdateReqDto();
        String request = mapToJson(reqDto);
        Long userId = getValidId();

        //when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team/favorite/user/{user-id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FAVORITE_USER_ADDED.getHttpStatus().value());
        assertThat(response).contains(FAVORITE_USER_ADDED.name());
    }
    @Test
    @DisplayName("팀이 회원 찜하기 및 찜 취소하기 | 올바른 찜 취소 요청시 | 200반환")
    void addOrDeleteFavoriteUser_givenValidDeleteReq_return200() throws Exception {
        // given
        FavoriteUpdateReqDto reqDto = getValidFavoriteUpdateReqDto();
        reqDto.setIsAddFavorite(false);
        String request = mapToJson(reqDto);
        Long userId = getValidId();

        //when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team/favorite/user/{user-id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FAVORITE_USER_DELETED.getHttpStatus().value());
        assertThat(response).contains(FAVORITE_USER_DELETED.name());
    }

    @Test
    @DisplayName("팀이 회원 찜하기 및 찜 취소하기 | 회원 식별자가 양수 아닐시 | 400반환")
    void addOrDeleteFavoriteUser_givenUserIdPositiveOnly_return400() throws Exception {
        // given
        FavoriteUpdateReqDto reqDto = getValidFavoriteUpdateReqDto();
        reqDto.setIsAddFavorite(true);
        String request = mapToJson(reqDto);
        Long userId = 0L;

        //when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team/favorite/user/{user-id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(USER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("팀이 회원 찜하기 및 찜 취소하기 | 찜 추가 여부 미입력시 | 400반환")
    void addOrDeleteFavoriteUser_givenIsAddFavoriteFieldRequired_return400() throws Exception {
        // given
        FavoriteUpdateReqDto reqDto = getValidFavoriteUpdateReqDto();
        reqDto.setIsAddFavorite(null);
        String request = mapToJson(reqDto);
        Long userId = getValidId();

        //when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team/favorite/user/{user-id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_ADD_FAVORITE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_ADD_FAVORITE_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("찜한 회원 전체 조회 | 올바른 요청시 | 200반환")
    void findAllFavoriteUsers_givenValidReq_return200() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/favorite/user")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FAVORITE_USERS_FOUND.getHttpStatus().value());
        assertThat(response).contains(FAVORITE_USERS_FOUND.name());
    }

    @Test
    @DisplayName("찜한 회원 전체 조회 | 페이지 시작점 미입력시 | 400반환")
    void findAllFavoriteUsers_givenPageFromFieldRequired_return400() throws Exception {
        // given
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/favorite/user")
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("찜한 회원 전체 조회 | 페이지 시작점이 양수 또는 0 아닐시 | 400반환")
    void findAllFavoriteUsers_givenPageFromPositiveOrZeroOnly_return400() throws Exception {
        // given
        Integer pageFrom = -1;
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/favorite/user")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.name());
    }

    @Test
    @DisplayName("찜한 회원 전체 조회 | 페이지 사이즈가 양수 아닐시 | 400반환")
    void findAllFavoriteUsers_givenPageSizePositiveOnly_return400() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = 0;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/favorite/user")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_SIZE_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_SIZE_POSITIVE_ONLY.name());
    }

    private FavoriteUpdateReqDto getValidFavoriteUpdateReqDto() {
        return FavoriteUpdateReqDto.builder()
                .isAddFavorite(true)
                .build();
    }

    private Long getValidId() {
        return 1L;
    }

    private Integer getValidPageFrom() {
        return 0;
    }

    private Integer getValidPageSize() {
        return 20;
    }
}