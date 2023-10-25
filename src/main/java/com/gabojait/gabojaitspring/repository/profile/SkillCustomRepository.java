package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.user.User;

import java.util.List;

public interface SkillCustomRepository {

    List<Skill> findAll(long userId);

    List<Skill> findAllInFetchUser(List<Long> userIds);
}
