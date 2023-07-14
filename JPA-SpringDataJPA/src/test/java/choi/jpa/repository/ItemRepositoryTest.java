package choi.jpa.repository;

import choi.jpa.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save() {
        /*
            save를 호출하면 JPA 내부적으로 아래와 같은 코드로 되어있다.

            if (entityInformation.isNew(entity)) {
                em.persist(entity);
                return entity;
            } else {
                return em.merge(entity);
            }

            PK가 되는 식별자(여기서는 Item.id [Long타입])가 null이면
            isNew가 true가 되어 persist를 호출하고,
            아닐 경우 merge를 호출한다.
         */

        /*
            식별자를 @GeneratedValue로 채번할 경우
            아래의 코드는 persist를 호출하지만
            만약 식별자를 개발자가 넣어주게 된다면
            Ex) 자동채번을 사용하지않고, 아래와 같은 코드로 구현하게되면 merge를 호출한다.
                Item item = new Item();
                item.setId(1);

            * merge를 호출하면 select 쿼리가 추가적으로 실행되게 된다.

            만약 직접 넣을 수 밖에 없는 상황이면,
            Persistable을 구현하여 해결할 수 있다.
         */
        Item item = new Item();
        itemRepository.save(item);
    }

}