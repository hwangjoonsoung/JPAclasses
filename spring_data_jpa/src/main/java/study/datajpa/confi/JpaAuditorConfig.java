package study.datajpa.confi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

@Configuration
public class JpaAuditorConfig {

    @Bean
    public AuditorAware<String> getCurrentAuditor(){
        /**
         * session에서 가져와서 optional of에 적용
         **/
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
