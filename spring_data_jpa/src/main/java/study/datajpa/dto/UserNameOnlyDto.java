package study.datajpa.dto;

public class UserNameOnlyDto {

    private final String userName;

    public String getUserName() {
        return userName;
    }

    public UserNameOnlyDto(String userName) {
        this.userName = userName;
    }
}
