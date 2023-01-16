package com.inuappcenter.gabojaitspring;

import com.inuappcenter.gabojaitspring.profile.repository.*;
import com.inuappcenter.gabojaitspring.project.repository.ApplyRepository;
import com.inuappcenter.gabojaitspring.project.repository.ProjectRepository;
import com.inuappcenter.gabojaitspring.project.repository.RecruitRepository;
import com.inuappcenter.gabojaitspring.user.repository.ContactRepository;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = {
		ContactRepository.class, UserRepository.class,
		ProfileRepository.class, EducationRepository.class, SkillRepository.class, WorkRepository.class,
		PortfolioRepository.class,
		ProjectRepository.class, ApplyRepository.class, RecruitRepository.class
})
@EnableMongoAuditing
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
