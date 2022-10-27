package choi.jpa.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    /*
        카테고리 관련 변수 Start
        - 계층형 형태(셀프 조인 형태)이기 때문에 아래와 같이 구현
    */
    @ManyToOne
    @JoinColumn(name = "PARENT_D")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
    /* 카테고리 관련 변수 End */

    @ManyToMany
    @JoinTable(name = "CATEGORY_ITEM",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private List<Item> items = new ArrayList<>();

}
