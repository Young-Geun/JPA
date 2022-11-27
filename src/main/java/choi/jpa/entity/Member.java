package choi.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    protected Member() { // JPA는 기본 생성자가 필수. Access 레벨은 private 금지 (프록시에 의해 동작할 때, 접근을 할 수 없을수도)

    }

    public Member(String username) {
        this.username = username;
    }

}
