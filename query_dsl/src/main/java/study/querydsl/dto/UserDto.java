package study.querydsl.dto;

import lombok.*;

import javax.print.attribute.standard.PrinterURI;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String name;
    private int age;
}
