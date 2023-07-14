package choi.jpa.basic;

import javax.persistence.*;

@Entity
public class Player extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "USERNAME")
    private String name;

//    @Column(name = "TEAM_ID")
//    private Long teamId;

    // @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    // @OneToOne(fetch = FetchType.LAZY)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;

    /*
        모든 테이블에 생성자와 수정자 컬림이 있어야한다고 가정할 때,
        각각의 엔티티에서 선언하는 것이 아니라
        공통 항목(BaseEntity.java)으로 뽑아내서 활용하는 예제

        private String createdBy;

        private String modifiedBy;
    */

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

//    public Long getTeamId() {
//        return teamId;
//    }
//
//    public void setTeamId(Long teamId) {
//        this.teamId = teamId;
//    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }
}
