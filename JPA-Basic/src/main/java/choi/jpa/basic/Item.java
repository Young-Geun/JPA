package choi.jpa.basic;

import javax.persistence.*;

@Entity
// @Inheritance(strategy = InheritanceType.JOINED) // 조인 전략
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 단일테이블 전략 (해당 전략에서는 @DiscriminatorColumn을 생략해도 DTYPE이 자동 생성된다.)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // 구현 클래스마다 테이블 전략
@DiscriminatorColumn // DTYPE컬럼 생성 옵션
public abstract class Item {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private int price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
