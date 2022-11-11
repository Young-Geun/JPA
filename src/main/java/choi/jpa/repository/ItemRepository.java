package choi.jpa.repository;

import choi.jpa.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            /**
             * 준영속 엔티티를 수정하는 방법 : merge 사용
             * - merge 사용 시 주의사항
             *   : 변경감지 기능을 사용하면 원하는 속성만 변경할 수 있지만,
             *     병합을 사용하면 모든 속성이 변경되기 때문에
             *     값이 없으면 null로 업데이트 될 수도 있다.
             */
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
