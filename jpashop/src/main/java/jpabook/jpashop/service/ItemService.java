package jpabook.jpashop.service;

import jakarta.persistence.Inheritance;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Flow;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public Item findItem(Long id){
        return itemRepository.findOne(id);
    }

    @Transactional
    public void updateItemUsingDirtyChecking(Long itemId , Book book){
        Item item = itemRepository.findOne(itemId);
        item.setPrice(book.getPrice());
        item.setName(book.getName());
        item.setStockQuantity(book.getStockQuantity());
    }
    @Transactional
    public void updateItemUsingMerge(Long itemId , Book book){

    }

    public List<Item> findAll(){
        return itemRepository.findAll();
    }


}
