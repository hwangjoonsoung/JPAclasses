package jpabook.jpshop.controller;

import jpabook.jpshop.domin.Item;
import jpabook.jpshop.domin.Member;
import jpabook.jpshop.domin.Order;
import jpabook.jpshop.domin.OrderSearch;
import jpabook.jpshop.service.ItemService;
import jpabook.jpshop.service.MemberService;
import jpabook.jpshop.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.dialect.IExpressionObjectDialect;

import java.sql.RowIdLifetime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){

        List<Member> members = memberService.findAll();
        List<Item> items = itemService.findAll();

        model.addAttribute("members", members);
        model.addAttribute("items", items);
        return "order/orderForm";

    }

    @PostMapping("/order")
    public String createOrder(@RequestParam("memberId") Long memberId, @RequestParam("itemId") Long itemId , @RequestParam("count") int count){
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderListForm(@ModelAttribute("orderSearch")OrderSearch orderSearch , Model model){
        List<Order> order = orderService.findOrder(orderSearch);
        model.addAttribute("orders", order);
        return "order/orderList";
    }

    @PostMapping("orders/{id}/cancel")
    public String cancelOrder(@PathVariable("id")Long id){
        orderService.cancelOrder(id);
        return "redirect:/orders";
    }

}


