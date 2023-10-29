package com.gabojait.gabojaitspring.repository.notification;

import java.util.List;

public interface FcmCustomRepository {

    List<String> findAllTeam(long teamId);

    List<String> findAllUser(long userId);

    List<String> findAllTeamExceptUser(long teamId, long userId);
}
