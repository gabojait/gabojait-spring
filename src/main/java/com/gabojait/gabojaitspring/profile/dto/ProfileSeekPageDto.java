package com.gabojait.gabojaitspring.profile.dto;

import com.gabojait.gabojaitspring.profile.dto.res.ProfileSeekResDto;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class ProfileSeekPageDto {

    private final List<ProfileSeekResDto> profileSeekResDtos;
    private final int totalPages;

    public ProfileSeekPageDto(List<ProfileSeekResDto> profileSeekResDtos, int totalPages) {
        this.profileSeekResDtos = profileSeekResDtos;
        this.totalPages = totalPages;
    }
}
