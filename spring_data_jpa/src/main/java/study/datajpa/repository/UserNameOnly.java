package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

//projection
public interface UserNameOnly {

//    @Value("#{target.userName + ' '+ target.age}") // open projection을 사용하기 위함
    String getUserName(); // close projection을 사용하기 위함

}
