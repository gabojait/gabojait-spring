package com.gabojait.gabojaitspring.profile.dto;

import com.gabojait.gabojaitspring.profile.dto.res.ProfileSeekResDto;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ProfileSeekPageDto {

    private final List<ProfileSeekResDto> profileSeekResDtos;
    private final long pageSize;

    public ProfileSeekPageDto(List<ProfileSeekResDto> profileSeekResDtos, long pageSize) {
        this.profileSeekResDtos = profileSeekResDtos;
        this.pageSize = pageSize;
    }
}
