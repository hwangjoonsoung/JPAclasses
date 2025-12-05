package jpabook.jpshop.config;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class Hibernate5JakartaConfig {

    @Bean
    public Hibernate5JakartaModule hibernate5Module(){
        return new Hibernate5JakartaModule();
    }
}
