package jpabook.jpshop.controller;

import jakarta.security.auth.message.callback.PrivateKeyCallback;
import jpabook.jpshop.domin.Item;
import jpabook.jpshop.domin.item.Book;
import jpabook.jpshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createFrom(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String creat(BookForm bookForm) {
        Book book = new Book();
        book.setName(bookForm.getName());
        book.setPrice(bookForm.getPrice());
        book.setStockQuantity(bookForm.getStockQuantity());
        book.setAuthor(bookForm.getAuthor());
        book.setIsbn(bookForm.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String itemList(Model model) {
        List<Item> all = itemService.findAll();
        model.addAttribute("items", all);
        return "/items/itemList";
    }

    @GetMapping("items/{id}/edit")
    public String itemEdit(@PathVariable Long id, Model model) {
        Book book = (Book) itemService.findOne(id);

        BookForm bookForm = new BookForm();
        bookForm.setId(book.getId());
        bookForm.setAuthor(book.getAuthor());
        bookForm.setName(book.getName());
        bookForm.setIsbn(book.getIsbn());
        bookForm.setPrice(book.getPrice());
        bookForm.setStockQuantity(book.getStockQuantity());

        model.addAttribute("form", bookForm);
        return "/items/updateItemForm";
    }

    @PostMapping("items/{id}/edit")
    public String updateItem(@PathVariable Long id, @ModelAttribute("form") BookForm bookForm){
        Book book = new Book();

        book.setId(bookForm.getId());
        book.setName(bookForm.getName());
        book.setPrice(bookForm.getPrice());
        book.setStockQuantity(bookForm.getStockQuantity());
        book.setAuthor(bookForm.getAuthor());
        book.setIsbn(bookForm.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }
}
