package choi.jpa.basic1;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    /*
        - mappedBy
        : 양방향 연관관계에서는 객체의 두 관계를 제어하는 연관관계의 주인을 결정해야한다. (Player or Team)
          연관관계의 주인만이 외래 키를 관리(등록, 수정)할 수 있다.
          주인이 아닌쪽에 mappedBy 속성을 이용하여 주인을 정한다.
          아래 mappedBy = "team"에서 team은 Player.java의 team 변수를 뜻한다.
     */
    @OneToMany(mappedBy = "team")
    private List<Player> players = new ArrayList<>();

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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
