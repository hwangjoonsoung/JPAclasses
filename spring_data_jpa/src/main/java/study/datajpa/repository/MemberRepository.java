package study.datajpa.repository;

import org.hibernate.annotations.processing.SQL;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.nio.channels.Pipe;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    //query method
    List<Member> findByUserName(String username);

    List<Member> findByUserNameAndAgeGreaterThan(String userName, int ageIsGreaterThan);

    List<Member> findHelloBy();

    @Query("select m from Member m where m.userName = :userName and m.age > :age")
    List<Member> findUser(@Param("userName") String username, @Param("age") int age);

    @Query("select m.userName from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id  , m.userName , t.teamName) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUserName(String username);

    Member findMemberByUserName(String username);

    Optional<Member> findOptionalMemberByUserName(String username);

    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    /**
     * count query별도로 작성 가능
     **/
    Page<Member> findByAge(int age, Pageable pageRequest);
//    Slice<Member> findByAge(int age, Pageable pageRequest);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

}
