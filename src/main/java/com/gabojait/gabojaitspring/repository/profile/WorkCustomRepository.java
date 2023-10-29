package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Work;

import java.util.List;

public interface WorkCustomRepository {

    List<Work> findAll(long userId);
}
