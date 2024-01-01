package com.gabojait.gabojaitspring.api.dto.profile.request;

import com.gabojait.gabojaitspring.domain.profile.Level;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "기술 업데이트 요청")
public class SkillUpdateRequest {

    @ApiModelProperty(position = 1, required = true, value = "기술 식별자")
    private Long skillId;

    @ApiModelProperty(position = 2, required = true, value = "기술명", example = "스프링")
    @Size(min = 1, max = 20, message = "기술명은 1~20자만 가능합니다.")
    private String skillName;

    @ApiModelProperty(position = 3, required = true, value = "경험 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "경험 여부는 필수 입력입니다.")
    private Boolean isExperienced;

    @ApiModelProperty(position = 4, required = true, value = "레벨", example = "LOW", allowableValues = "LOW, MID, HIGH")
    @Pattern(regexp = "^(LOW|MID|HIGH)", message = "레벨은 'LOW', 'MID', 또는 'HIGH' 중 하나여야 됩니다.")
    private String level;

    public Skill toEntity(User user) {
        return Skill.builder()
                .skillName(this.skillName.trim())
                .isExperienced(this.isExperienced)
                .level(Level.valueOf(this.level))
                .user(user)
                .build();
    }

    @Builder
    private SkillUpdateRequest(Long skillId, String skillName, Boolean isExperienced, String level) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillUpdateRequest that = (SkillUpdateRequest) o;
        return Objects.equals(skillId, that.skillId)
                && Objects.equals(skillName, that.skillName)
                && level == that.level
                && Objects.equals(isExperienced, that.isExperienced);
    }

    public int hashCode(User user) {
        return Objects.hash(skillId, user, skillName, Level.valueOf(level), isExperienced);
    }
}
