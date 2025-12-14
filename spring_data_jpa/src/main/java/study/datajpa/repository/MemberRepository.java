package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.hibernate.annotations.processing.SQL;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.MemberProjection;
import study.datajpa.dto.UserNameOnlyDto;
import study.datajpa.entity.Member;

import java.nio.channels.Pipe;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member>{

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

    @Query("select m from Member m join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly" , value = "true"))
    Member findReadOnlyByUserName(@Param("username") String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUserName(String username);

    List<UserNameOnly> findInterfaceBasedProjectionsByUserName(@Param("userName") String userName);
    List<UserNameOnlyDto> findClassBasedProjectionsByUserName(@Param("userName") String userName);
    <T> List<T> findClassBasedWithTypeProjectionsByUserName(@Param("userName") String userName,Class<T> type);

    @Query(value = "select * from member where user_name = ?", nativeQuery = true)
    Member findByNativeQuery(String userName);

    @Query(value = "select m.member_id as id, m.user_name as userName, t.name as teamName from member m left join team t"
    ,countQuery = "select count(*) from member", nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
