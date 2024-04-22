package section1to4;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@SequenceGenerator(name = "member-seq-generator", sequenceName = "member_seq", initialValue = 1, allocationSize = 50)
public class SectionMember2 {

    @Id

//    @GeneratedValue(strategy = GenerationType.AUTO) // DB방언에 맞춰서 자동으로 생성
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 생성을 DB에 위임
//    @GeneratedValue(strategy = GenerationType.SEQUENCE) // oracle 기본키 생성을 시퀀스로 생성되도록 설정
    private int id;

    @Column(name = "name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;

    /*
    * 이건 메모리에서만 사용하겠다는 의미
    * */
    @Transient
    private String transientColumn;

    public SectionMember2() {
    }
}
