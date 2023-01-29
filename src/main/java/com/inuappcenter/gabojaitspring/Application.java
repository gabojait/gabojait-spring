package com.inuappcenter.gabojaitspring;

import com.inuappcenter.gabojaitspring.profile.repository.EducationRepository;
import com.inuappcenter.gabojaitspring.profile.repository.PortfolioRepository;
import com.inuappcenter.gabojaitspring.profile.repository.SkillRepository;
import com.inuappcenter.gabojaitspring.profile.repository.WorkRepository;
import com.inuappcenter.gabojaitspring.user.repository.ContactRepository;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = {
		ContactRepository.class, UserRepository.class,
		EducationRepository.class, WorkRepository.class, SkillRepository.class, PortfolioRepository.class
})
@EnableMongoAuditing
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
