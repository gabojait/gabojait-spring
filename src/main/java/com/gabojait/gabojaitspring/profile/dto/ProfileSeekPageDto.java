package com.gabojait.gabojaitspring.profile.dto;

import com.gabojait.gabojaitspring.profile.dto.res.ProfileSeekResDto;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class ProfileSeekPageDto {

    private List<ProfileSeekResDto> profileSeekResDtos = new ArrayList<>();
    private int totalPage;

    public ProfileSeekPageDto(List<ProfileSeekResDto> profileSeekResDtos, int totalPage) {
        this.profileSeekResDtos = profileSeekResDtos;
        this.totalPage = totalPage;
    }
}
