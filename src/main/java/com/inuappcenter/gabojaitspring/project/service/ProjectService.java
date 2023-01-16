package com.inuappcenter.gabojaitspring.project.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.project.domain.Project;
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
     * 403: 프로젝트 리더 권한 에러
     * 500: 프로젝트 정보 저장 중 서버 에러
     */
    public void start(Project project, Profile profile) {
        log.info("INITIALIZE | ProjectService | start | " + project.getId() + " | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        if (project.getLeader() != profile.getId()) {
            throw new CustomException(NON_LEADER);
        }

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
     * 403: 프로젝트 리더 권한 에러
     * 500: 프로젝트 정보 저장 중 서버 에러
     */
    public Project end(Project project, Profile profile) {
        log.info("INITIALIZE | ProjectService | end | " + project.getId() + " | " + profile.getId());
        LocalDateTime initTime = LocalDateTime.now();

        if (project.getLeader() != profile.getId()) {
            throw new CustomException(NON_LEADER);
        }

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
        log.info("INITIALIZE | ProfileService | findMany | " + projectIds.size());
        LocalDateTime initTime = LocalDateTime.now();

        List<Project> projects = new ArrayList<>();
        for(ObjectId projectId : projectIds)
            projects.add(projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        throw new CustomException(NON_EXISTING_PROFILE);
                    }));

        log.info("COMPLETE | ProfileService | findMany | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                projectIds.size());
        return projects;
    }
}
