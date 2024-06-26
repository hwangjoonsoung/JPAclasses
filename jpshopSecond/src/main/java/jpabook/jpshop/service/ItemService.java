package jpabook.jpshop.service;

import jpabook.jpshop.domin.Item;
import jpabook.jpshop.domin.item.Book;
import jpabook.jpshop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.rtf.RTFEditorKit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item> findAll(){
        List<Item> all = itemRepository.findAll();
        return all;
    }

    public Item findOne(Long id) {
        Item item = itemRepository.itemOne(id);
        return item;
    }

    @Transactional
    public void updateItem(Long itemId, Book param) {
        Item item = itemRepository.itemOne(itemId);
        item.setPrice(param.getPrice());
        item.setName(param.getName());
        item.setStockQuantity(param.getStockQuantity());
    }

}
