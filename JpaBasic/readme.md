### JPA 실무에서 어려운 이유
- 실무는 수십 개 이상의 복잡한 객체와 테이블 사용
### 목표 - 객체와 테이블 설계 매핑
- 객체와 테이블을 제대로 설계하고 매핑하는 방법
- 기본 키와 외래 키 매핑
- 1:N, N:1, 1:1, N:N 매핑
- 실무 노하우 + 성능까지 고려
- 어떤 복잡한 시스템도 JPA로 설계가능
### 목표 - 내부 동작 방식 이해
- JPA 의 내부 동작 방식을 이해하지 못하고 사용
- JPA 내부 동작 방식을 그림과 코드로 자세히 설명
- JPA가 어떤 SQL을 만들어 내는지 이해
- JPA가 언제 SQL을 실행하는지 이해
### SQL 중심 개발의 문제
- SQL을 계속 작성해 줘야 한다는게 문제다...
- 문제는 간단한 select도 모두 작성을 해야 하는것이 문제다.
### JPA를 왜 사용해야 하는가?
- SQL중심적인 개발에서 객체 중심적인 개발로 변경
- 생산성
  - 메서드 하나로 DB 데이터의 빠른 수정 가능
- 유지보수
  - DB 유지보수 어려움, JPA는 필드만 추가해 주면 됨
- 패러다임의 불일치 해결
- 성능
- 데이터 접근 추상화와 벤더 독립성
- 표준
### 데이터 베이스 방언
<img src="image/DBdialect.png">
- 방언: SQL 표준을 지키지 않는 특정 DB만의 고유한 기능
- JPA는 특정 데이터베이스에 종속적이지 않다.
- 각각의 DB가 제공하는 SQL 문법과 함수는 조금씩 다름

### JPA구동방식
<img src="image/JPARunningFormula.png">

### JPA 사용 주의점
- 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유
- 엔티티 매니저는 쓰레드간에 공유하지 말아야 한다.
- JPA의 모든 데이터 변경은 트랜잭션 안에서 실행

### JPQL 
- JPA는 SQL을 추상화한 JPQL 이라는 객체 지향 쿼리 언어 제공
- SQL과 문법은 비슷하다
- JPQL은 엔티티 객체를 대상으로 쿼리
- SQL은 데이터베이스 테이블 대상으로 쿼리
### JPA에서 가장 중요한 2가지
- 객체와 관계형 데이터 베이스 매핑하기
- 영속성 컨텍스트
### 엔티티 매니저 팩토리와 엔티티 매니저
<img src="image/EntityManagerFactory.png">

### 영속성 컨텍스트
- JPA를 이해하는데 가장 중요한 용어
- 엔티티를 영구 저장하는 환경
- EntityManager.persist(entity) : 이는 엔티티를 DB에 저장하는 것이 아닌 영속성 컨텍스트에 저장하는 것을 의미한다.
### 엔티티 생명 주기
- 비영속
  - 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태 : JPA와 관계가 없음
- 영속
  - 영속성 컨텍스트에 관리되는 상태
  - em.persist(entity); 를 통해 엔티티를 영속성 컨텍스트 안에 저장하는 상태
- 준영속
  - 영속성 컨텍스트에 저장되어 있다가 분리
- 삭제
-  삭제된 상태
```java
    // 비영속
    Member sectionMember = new Member();
    sectionMember.setId(100L);
    sectionMember.setName("Lee");
    // 영속
    em.persist(sectionMember);
    // 준영속
    em.detach(sectionMember);
    // 삭제
    em.remove(sectionMember);
```
### 영속성 컨텍스트의 이점
- 1차 캐시
  - 1차 캐시를 사용함으로 서 만약 1차 캐시에 엔티티가 있으면 해당 엔티티를 가져오고 그렇지 않는 경우 DB에 접근해서 가져온다.
  - 이후 가져온 데이터를 1차 캐시에 넣는다.
  - <img src="image/cache.png">
- 동일성 보장
  - 같은 트랜잭션 안에서 동일한 엔티티를 찾으면 같은 데이터를 가져올 수 있다.
- 트랜잭션을 지원하는 쓰기 지연
  - persist한다고 해서 바로 sql을 보내는 것이 아닌 트랜잭션 커밋이 이루어 졌을때 sql이 나간다
  - <img src="image/LateWrite.png">
  - <img src="image/LateWrite2.png">
- 변경 감지 dirty checking + 플러쉬
  - jpa로 데이터를 가져오 setter로 데이터를 저장후 commit 하게 되면 jpa는 자동으로 변경된 값을 update query를 만들어 보낸다
  - <img src="image/dirtyChecking.png">
  - 플러쉬란?
    - 영속성 컨텍스트의 변경 내용을 Db에 반영 한다.
    - 변경감지 -> 스정된 엔티티를 쓰지 지연 sql 저장소에 등록 -> 쓰기 지연 sql 저장소의 쿼리를 DB 에 전송(커밋은 되지 않는다.)
  - 플러쉬 하는 법
    - 직접 호출 : em.flash();
    - 트랜잭션 커밋 : flash 자동 호출
    - JPQL 커밋 : flash 자동 호출
### 엔티티 매핑 소개
- 객체와 테이블 매핑 : @Entity , @Table
- 필드와 컬럼 매핑 : @Column
- 기본 키 매핑 : @Id
- 연관관계 매핑 : @ManyToOne , @JoinColumn
### hibernate.hbm2ddl.auto option
- create : 새로만듬
- create-drop : application down시 drop
- update : 새로운 커럼 변경점 생성시 table update (alter로 컬럼생성)
- validate : entity와 table colmn 정상 매핑 확인
- none : 사용하지 않음
- 주의점
  - ※운영 장비에는 절대 create, create-drop, update 사용하면 안된다.※
  - 개발 초기 단계에는 create 또는 update
  - 테스트 서버는 update 또는 validate
  - 스테이징과 운영 서버는 validate 또는 none
### Table Annoatation
<img src="image/TableAnnotation.png">

### mapping Annotation
<img src="image/MappingAnnotation.png">

### column Annotation
<img src="image/ColumnAnnotation.png">

### table 전략
- 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
- 장점: 모든 데이터베이스에 적용가능
- 단점: 성능

### 권장하는 시별자 전략
- 기본 키 제약 조건 : null 아님, 유일, 변하면 안된다.
- 미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. 대리키(대체키)를 사용하자.
- 권장 : Long형 + 대채키 + 키 생성전략 사용

### IDENTITY 전략
- GeneratedValue전략에서 IDENTITY를 사용하게 되면 기본키 생성을 DB에가 위임한다.
- 즉, PK값을 DB에 들어가야 지만 알 수 있다.
- 이렇게 지금까지의 내용으로 영속성 컨텍스트가 될 수없다. em.persist를 1차 캐시에서 key가 PK로 들어가함으로.
- 때문에 IDENTITY를 사용하게 되면 persist할때 바로 flash 이뤄진다.

### 데이터 중심 설계의 문제점
- 현재 방식은 객체 설계를 테이블 설계에 맞춘 방식
- 테이블의 외래키를 객체에 그대로 가져옴
- 객체 그래프 탐색이 불가능
- 참조가 없음으료 UML도 잘못됨

## 연관관계 매핑 기초
### 목표
- 객체와 테이블 연관관계의 차이를 이해
- 객체의 참조와 테이블의 외래 키를 매핑
- 용어 이해
  - 방향 : 단방향, 양방향
  - 다중성 : 1:N, N:1, 1:1 ,N:N
  - 연관관계의 주인 : 객체 양방향 연관관계는 관리 주인이 필요
### 연관관계의 주인
- 양방향 매핑 규칙
  - 객체의 두 관계중 하나를 연관관계의 주인으로 지정
  - 연간관계의 주인만이 외래 키를 관리(등록,수정)
  - 주인이 아닌 쪽은 읽기만 가능(readonly 속성을 가짐)
  - 주인은 mappedBy 속성 사용하지 않는다.
  - 주인이 아니면 mappedBy 속석으로 주인 지정
- 누구를 주인으로 할꺼지?
  - 외래 키가 있는 곳을 주인으로 정해라.
### 연관관계 설정할 때 주의점.
1. readonly 주인이 아닌쪽에서 값을 add 하는 경우 발생하는 문제
   - ```java
            TwoWaySection5Member member = new TwoWaySection5Member();
            member.setName("member1");
            em.persist(member);

            TwoWayTeam twoWayTeam = new TwoWayTeam();
            twoWayTeam.setName("TwoWayTeam A");
            em.persist(twoWayTeam);
     ```
   - 위 같은 상황에서는 member의 team이 연관관계의 주인 속성을 가지고 있다.
   - 즉 team의 pk가 member.team_id로 이뤄지는데 위 코드는 team의 pk가 생성되지 않는 상태에서 member.team_id를 넣는 것과 같다.
   - 따라서,member.setTeam(team)을 해주는 방법을 선택하거나 아니면 team을 먼저 생성하고 member에 setTeam(team)을 해주는 방법을 채택한다.
2. 