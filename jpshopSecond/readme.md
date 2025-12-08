# OSIV
- spring.jpa.open-in-view : true  -> 기본값
## on 하는 경우
- 기본적으로 persist context가 db connection을 가지고 있는 기간은 transaion이 시작해서 controller에서 reponse가 나갈때 까지 가지고 있다.
- 이렇게 하는 이유는 lazy loading 때문에 proxy 객체를 초기화 시켜줘야 하기 때문에 그렇다.
- 문제는 이 기간이 너무 길다는 것
- 만약 외부 api를 요청하는 중에 오류가 발생하는 경우 db connection을 계속 가지고 있을 가능성이 생긴다.
## off하는 경우
- transaction이 종료되면 DB connection을 반환한다.
- 이렇게 하는 경우 lazy loading을 할 수 없어 service, repository단에서 lazy loading까지 조회를 마쳐서 controller에 더져야 한다
- SLazyInitializationException발생 한다.("/api/v1/orders" 참고)
## off 했을때 해결하는 방법
1. fetch join을 사용하는 방법
2. transaction 안에서 로딩을 하는 방법
### admin과 사용자는 모듈을 분리해서 개발하는 것이 일반적이다. (멀티모듈을 통해 개발하는 방법을 채택)