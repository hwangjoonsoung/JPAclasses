# 

### 프로젝트 환경설정
@PersistenceContext : springboot에 해당 어노테이션이 있으면 자동으로 DI를 진행한다.

trouble shutting
1. Test Class에서 @RunWith가 사용이 안되는 이유
   - 스프링부트 2.x에서는 RunWith로 SpringRunner.class 를 넣어줬어야 했다.
   - 이는 JUnit4를 사용하겠다는 의미를 내포한다. (SpringRunner는 SpringJunit4ClassRunner의 alias다. )
   - 근데 부트 3.x에서는 JUnit5를 사용한다.
   - 즉 JUnit4는 부트 3.x에서 사용할 수 없다.
   - 그러면 어떻게 JUnit5를 사용할까?
   - @SpringBootTest안에 이미 @ExtendWith에 SpringExtension.class가 있다.
   - SpringExtension.class는 JUnit5를 통합한 클래스다.
   - 따라서 @SpringBootTest만 선언하면 JUnit을 자동으로 사용할 수 있다.
