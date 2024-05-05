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

### 도메인 분석 설계
<img src="image/table_design.png">
<img src="image/table_entity.png">

- member 와 order를 설계할때 회원을 기준으로 order가 생성되는것으로 생성된다고 하는 것 보다 주문이 생성될때 회원이 필요하다고 보는 것이 옳다.

<img src="image/table_analyze.png">

### 엔티티 클래스 개발
- foreign key를 잡아주는 것은 시스템 마다 다르다.
  - 만약 시스템이 동작하는것이 더 중요하면 잡아줄 필요는 없다
  - 하지만 돈과 관련된 약간 중요한 부분들은 잡아주는 것이 좋다
- getter는 사용해도 무방하지만 setter는 고민을 해봐야 한다.
  - read와 write가 시스템에 미치는 영향은 굉장히 차이가 크다
- PK컬럼을 지정할때 컬럼명을 id라고 적어도 무방하지만 talbe name + _id로 생성하는 것이 조금 더 명확하다.(관례상 더 많이 사용한다.)
- 값 타입은 변경 불가능하게 설계해야 한다.
  - JPA 는 java reflection을 사용해야 하기 때문에 생성자가 필요하다.
### 엔티티 설계시 주의점
- setter 사용 금지 : 변경 포인트가 너무 많아서 유지보수가 어렵다.
- 모든 연관관계는 지연 로딩으로 설정
  - 즉시 로딩이란 하나의 데이터에 접근할 때 연관된 모든 데이터를 조회하는 것을 즉시 로딩이라고 한다.
  - 즉시 로딩은 예측이 어렵고 어떤 sql이 실행될지 추적하기 어렵다. 특히 JPQL을 실행할 때 N+1문제가 자주 발생한다.
- 만약 연관된 엔티티를 할께 DB에서 조회하려면 fetch join또는 엔티티 그래프 기능을 사용한다.
- @XToOne은 fetch= FetchType = lazy로 변경해야 한다. : default가 EAGER로 되어 있다.
- 컬렉션은 필드에서 초기화 하자
  - 하이버네이트는 엔티티를 영속화 할때 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬랙션으로 변경한다
  - getOrders(); 와 같이 임의의 메서드에서 컬렉션을 잘못 사용하면 하이버네이트 내부 메커니즘에 문제가 발생할 수 있다.
### 테이블,컬럼명 생성 전략
- 스프링 부트에서 하이버네이트 기본 매핑 전략을 변경해서 실제 테이블 필드명은 다르다.
  - 카멜 케이스 -> 언더 스코어 (memberPoint -> memeber_point)
  - .(점) -> 언더 스코어
  - 대문자 -> 소문자
- 적용 2단계
  - 논리명 생성 : 명시적으로 컬럼, 테이블 명을 직접 적지 않으면 ImplicitNamingStrategy생성
  - 물리명 적용 : 모든 논리명에 적용 됨, 실제 테이블에 적용

### JOQL 과 SQL의 차이
- 기능적으로는 동일하지만 쿼리하는 대상이 다름
- JPQL의 경우 Entity 대상으로 하기 때문에 alias를 사용해야 한다
  - JPQL : select m from member m;
  - SQL : select * from member;

### Annotation 우선순위
1. method 상단
2. class

### EntityManager DI
- EntityManager를 DI할때 @PersistenceContext(표준임)를 사용해야 한다.
- 근데 spring boot가 @AutoWired로 DI가 될 수 있도록 개선되었다.
- 따라서 생성자 주입을 사용할 수 있다.

### Test Case에서 rollback
- @Tranctional한 상황에서 Test Case를 작성하여 동작하면 DB에 commit되지 않는다.
  - Test Case에서는 Commit할 필요가 없음으로 자동으로 RollBack해준다.
- 따라서 insert query또한 생성되지 않는다.
- 만약 insert query 를 날라고 Roll Back을 하고싶은 경우
  - EntityManeger의 flash함수를 이용해서 insert query를 날리도록 한다.
- 이렇게 되면 자동적으로 transectional한 상황에서 insert query는 날라가지만 rool back이 되는 상황으로 만들어 진다
```java
    @Test
        public void 회원가입() throws Exception{

        //given
        Member member = new Member();
        member.setName("hwang");
        //when
        Long joinedId = memberService.join(member);
        em.flush();

        //then
        Assertions.assertEquals(member , memberRepository.findOne(joinedId));
        }
```
### 와 persist를 한번만 날릴까?
```java
    @Transactional
    public Long order(Long memberId , Long itemId , int count) {
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        Order order = Order.createOrder(member, delivery, orderItem);
        orderRepository.save(order);
        return order.getId();
    }
```
- 위 코드를 보면 delivery와 orderItem을 persist한다음 가져오는게 정성이다.
- 하지만 우리는 orderRepository를 한번만 날렸다.
- 이게 가능한 이유는 cascade 옵션을 줬기 때문이다.
- 따라서 한번만 저장을 해줘도 delivery와 orderItem은 자동으로 persist 된다.

### JPA dirty checking

### 도메인 모델 패턴 vs 트렌잭션 스크립트 패턴

### Illegal mix of collations (latin1_swedish_ci,IMPLICIT) and (utf8mb4_general_ci,COERCIBLE) for operation '='] [n/a]
```sql
ALTER TABLE member CONVERT TO CHARACTER SET utf8 COLLATE 'utf8_general_ci';
```
### 엔티티 주의점
- 화면에 대한 로직은 별도의 DTO를 만들어서 사용해야 하며 엔티티는 순수하게 유지해야 한다.
- 여기서 순수하게란 validation check를 한다던가 엔티티 데이터를 맞추기 위한 로직은 클라이언트 DTO에 위치해야 한다.
- 외부에는 엔티티를 API로 노출시키면 안된다.
  - 컬럼을 하나 추가했을때 그 정보가 민감한 정보면 그대로 노출이 되어버리는 문제가 발생할 수 있다.
  - API의 스팩이 변경된다. -> API가 반환하는 데이터가 달라진다.
  - template engine에서는 노출시키는 것이 가능하다.

### 변경 감지와 병합
- entity를 가져와서 setter로 값을 변경하여 commit을 하면 자동으로 DB에 저장된다. -> dirty checking
- 준영속 엔티티
  - 영속성 컨텍스트가 더는 관리하지 않는 엔티티를 말한다.
  - itemController의 updateItem()를 보면 해당 로직이 정상적으로 동작하는 것을 볼 수 있다.
  - 그 이유는 book이 새로 만들어진 인스턴스이지만 BookForm.class 로 넘어온 id값을 통해 새로 만든 인스턴스의 id를 setting한 후 커밋하면
  - 새로운 객체가 insert 되는 것이 아닌 해당 id가 update된다.
  - 이것을 보고 우리는 임의로 만들어낸 엔티티도 DB에 식별자가 있는경우 JPA가 관리하고 있지는 않지만 commit을 하면 자동적으로 update query가
  - 날라갈수 있는 엔티티를 준영속 엔티티라고 한다.
- 문제는 준영속 엔티티는 JPA가 관리하지 않기 때문에 dirty checking이 일어나지 않음으로 DB update가 안된다.
#### 변경감지를 이용한 update
```java
    ItemService.java
    @Transactional
    public void updateItem(Long itemId , Book book){
        Item item = itemRepository.findOne(itemId);
        item.setPrice(book.getPrice());
        item.setName(book.getName());
        item.setStockQuantity(book.getStockQuantity());
    }
```
#### 병합(merge)을 이용한 update
```java
    ItemRepository.java
    em.merge(item);
```
<img src="image/mergeProcess.png">

#### 변경감지와 머지의 차이
1. 변경강지는 setter하나가 update query 라고 볼수 있다. 따라서 컬럼 하나하나 update이 가능하다.
2. 머지는 모든 컬럼을 한번에 변경하는 이치와 비슷하다. 따라서 식별자를 통해 DB에서 영속적인 엔티티를 찾아서 domain이 가진 모든 컬럼에 대해 update 한번에 날린다.
   - 이게 문제가 해당 domain에 없으면 null update가 된다. 

#### 무엇을 어떻게 사용해야 할까?
- 변경감지를 사용해야 한다. null update가 너무 치명적이다.
- update를 할때 service에서 setter를 사용하는것은 지양해야 한다.
- 따라서 별도의 메서드를 생성하여 update를 진행하는것이 더 좋은 코드라고 할 수 있다.

#### 코드 주의사항
- 컨트롤러에서 어섪츠게 엔티티를 생성하지 않기
- 트랜잭션이 있는 서비스 계층에서 식별자와 변경할 데이터를 명확하게 전달
- 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경

#### naming 규칙 변경

https://velog.io/@hyun-jii/JPA-Naming-Strategy-To-upper-snake-case

#### 연관관계 편의 메소드