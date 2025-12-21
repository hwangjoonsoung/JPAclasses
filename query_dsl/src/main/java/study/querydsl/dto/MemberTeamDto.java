package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.net.Inet4Address;

@Data
public class MemberTeamDto {

    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teanName;

    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teanName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teanName = teanName;
    }
}
