package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Education;
import com.gabojait.gabojaitspring.domain.user.User;

import java.util.List;

public interface EducationCustomRepository {

    List<Education> findAll(long userId);
}
