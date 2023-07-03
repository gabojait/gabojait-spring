package com.gabojait.gabojaitspring.profile.dto.res;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ProfileSeekPageResDto {

    private List<ProfileSeekResDto> profileSeekResDtos = new ArrayList<>();
    private int totalPage;
}
