package jpabook.jpshop.service;

import jpabook.jpshop.domin.Member;
import jpabook.jpshop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.type.ErrorType;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member){
        validateDuplidateMember(member);
        memberRepository.save(member);
        return member.getId();
    }


    /**
     *회원 전체조회
     */
    public List<Member> findAll(){
        List<Member> all = memberRepository.findAll();
        return all;
    }


    public Member findMember(Long id) {
        Member member = memberRepository.findMember(id);
        return member;
    }


    private void validateDuplidateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

    }
}
