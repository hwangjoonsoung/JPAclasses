package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("save item")
    void save() throws Exception {
        //given
        Item item = new Item(1L ,null);
        //when
        itemRepository.save(item);

        //then
    }

    /**
     * 무슨 이유로 인해서 id가 세팅한 상태로 DB에 insert 하는 경우 persist가 아닌 merge를 사용한다.
     * 문제는 merge는 select쿼리를 한번 보낸 뒤 insert하는 과정을 거친다.
     * 이는 매우 비 효율적임으로 Persistable을 사용한다.
     * entity에서 Persistable을 implement받는 것으로 isNew()를 overriding할 수 있다.
     * 이때 생성일자를 null check하는 것으로 새로 생성하는 것인지를 확인할 수 있다.
     **/
}