package jpabook.jpshop.controller;

import jakarta.validation.Valid;
import jpabook.jpshop.domin.Address;
import jpabook.jpshop.domin.Member;
import jpabook.jpshop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm , BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "members/createMemberForm";
        }

        Member member = new Member();
        member.setName(memberForm.getName());
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipCode());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("members")
    public String listForm(Model model){
        List<Member> all = memberService.findAll();

        model.addAttribute("members", all);
        return "members/memberList";
    }
}
