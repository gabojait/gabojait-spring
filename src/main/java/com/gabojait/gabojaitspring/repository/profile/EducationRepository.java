package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Education;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<Education, Long>, EducationCustomRepository {
}
