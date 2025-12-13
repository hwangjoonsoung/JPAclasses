package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

}
