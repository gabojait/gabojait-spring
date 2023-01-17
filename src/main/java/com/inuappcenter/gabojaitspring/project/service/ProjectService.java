package com.inuappcenter.gabojaitspring.project.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.project.domain.Apply;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import com.inuappcenter.gabojaitspring.project.domain.Recruit;
import com.inuappcenter.gabojaitspring.project.dto.ProjectSaveRequestDto;
import com.inuappcenter.gabojaitspring.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * 새 프로젝트 생성 |
     * 프로젝트 생성 절차를 밟아서 정보를 저장한다. |
     * 500: 프로젝트 정보 저장 중 서버 에러
     */
    public Project save(ProjectSaveRequestDto request, Profile leader) {
        log.info("INITIALIZE | ProjectService | start | " + leader.getId());
        LocalDateTime initTime = LocalDateTime.now();

        if (leader.getCurrentProject() != null) {
            throw new CustomException(CURRENT_PROJECT_EXIST);
        }

        Project project = request.toEntity(leader.getId());

        try {
            project = projectRepository.save(project);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProjectService | start | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                project.getId() + " | " + leader.getId());
        return project;
    }

    /**
     * 프로젝트 시작 |
     * 프로젝트 시작 절차를 밟아서 정보를 저장한다. |
     * 500: 프로젝트 정보 저장 중 서버 에러
     */
    public void start(Project project, Profile profile) {
        log.info("INITIALIZE | ProjectService | start | " + project.getId() + " | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        validateLeader(project, profile);

        project.startProject(initTime);

        try {
            project = projectRepository.save(project);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProjectService | start | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                project.getId());
    }

    /**
     * 프로젝트 종료 |
     * 프로젝트 종료 절차를 밟아서 정보를 저장한다. |
     * 500: 프로젝트 정보 저장 중 서버 에러
     */
    public Project end(Project project, Profile profile) {
        log.info("INITIALIZE | ProjectService | end | " + project.getId() + " | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        validateLeader(project, profile);

        project.endProject(initTime);

        try {
            project = projectRepository.save(project);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProjectService | end | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                project.getId());
        return project;
    }

    /**
     * 프로젝트 단건 조회
     * 프로젝트 정보를 찾아 반환한다. |
     * 404: 존재하지 않은 프로젝트 에러
     */
    public Project findOne(ObjectId projectId) {
        log.info("INITIALIZE | ProjectService | findOne | " + projectId);
        LocalDateTime initTime = LocalDateTime.now();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    throw new CustomException(NON_EXISTING_PROJECT);
                });

        log.info("COMPLETE | ProjectService | findOne | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                projectId);
        return project;
    }

    /**
     * 프로젝트 다건 조회 |
     * 여러 프로젝트 정보를 찾아 반환한다. |
     * 404: 존재하지 않은 프로젝트 에러
     */
    public List<Project> findMany(List<ObjectId> projectIds) {
        log.info("INITIALIZE | ProjectService | findMany | " + projectIds.size());
        LocalDateTime initTime = LocalDateTime.now();

        List<Project> projects = new ArrayList<>();
        for(ObjectId projectId : projectIds)
            projects.add(projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        throw new CustomException(NON_EXISTING_PROFILE);
                    }));

        log.info("COMPLETE | ProjectService | findMany | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                projectIds.size());
        return projects;
    }

    /**
     * 프로젝트에 해당 포지션 여부 검증 |
     * 프로젝트에 해당 포지션에 자리가 있는 검증한다. |
     * 409: 포지션 인원 초과 에러
     * 500: 프로젝트에 해당 포지션 여부 검증 중 서버 에러
     */
    public void validatePositionAvailability(Project project, Position position) {
        log.info("PROGRESS | ProjectService | validatePosition | " + project.getId() + " | " + position.getType());

        if (position.getType() == 'B') {
            if (project.getBackendProfileIds().size() >= project.getTotalBackendCnt())
                throw new CustomException(POSITION_UNAVAILABLE);
        } else if (position.getType() == 'F') {
            if (project.getFrontendProfileIds().size() >= project.getTotalFrontendCnt())
                throw new CustomException(POSITION_UNAVAILABLE);

        } else if (position.getType() == 'D') {
            if (project.getDesignerProfileIds().size() >= project.getTotalDesignerCnt())
                throw new CustomException(POSITION_UNAVAILABLE);

        } else if (position.getType() == 'M') {
            if (project.getManagerProfileIds().size() >= project.getTotalManagerCnt())
                throw new CustomException(POSITION_UNAVAILABLE);

        } else {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 프로젝트에 지원정보 저장 |
     * 프로젝트에 지원 정보를 저장한다. |
     * 500: 프로젝트에 지원정보 저장 중 서버 에러
     */
    public void projectApply(Project project, Apply apply) {
        log.info("INITIALIZE | ProjectService | projectApply | " + project.getId() + " | " + apply.getId());
        LocalDateTime initTime = LocalDateTime.now();

        project.addApply(apply.getId());

        try {
            project = projectRepository.save(project);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProjectService | projectApply | " + Duration.between(initTime, LocalDateTime.now()) + " | "
                + project.getId() + " | " + project.getApplyIds().size());
    }

    /**
     * 프로젝트에 영입정보 저장 |
     * 프로젝트에 영입 정보를 저장한다. |
     * 500: 프로젝트에 영입정보 저장 중 서버 에러
     */
    public void projectRecruit(Project project, Recruit recruit) {
        log.info("INITIALIZE | ProjectService | projectRecruit | " + project.getId() + " | " + recruit.getId());
        LocalDateTime initTime = LocalDateTime.now();

        project.addRecruit(recruit.getId());

        try {
            project = projectRepository.save(project);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        log.info("COMPLETE | ProjectService | projectRecruit | " + Duration.between(initTime, LocalDateTime.now()) + " | "
                + project.getId() + " | " + project.getRecruitIds().size());
    }

    /**
     * 프로젝트 리더 검증 |
     * 프로젝트에 리더인지 검증한다.
     * 403: 프로젝트 리더 권한 에러
     */
    public void validateLeader(Project project, Profile profile) {
        log.info("PROGRESS | ProjectService | validateLeader | " + project.getId() + " | " + profile.getId());

        if (project.getLeaderProfileId() != profile.getId())
            throw new CustomException(NON_LEADER);
    }
}
