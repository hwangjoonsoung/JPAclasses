package jpabook.jpshop.repository;

import jakarta.validation.constraints.NotEmpty;
import jpabook.jpshop.domin.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByName(@NotEmpty String name);
}
