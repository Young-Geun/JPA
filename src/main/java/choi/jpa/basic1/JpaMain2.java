package choi.jpa.basic1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain2 {

    public static void main(String[] args) {
        // 객체를 테이블에 맞추어 모델링
        ex1();
    }

    static void ex1() {
        /*
            객체를 테이블에 맞추어 모델링
            : 협력 관계를 만들 수 없다.
         */


        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 팀 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            // 회원 저장
            Player player = new Player();
            player.setName("player-1");
            player.setTeamId(team.getId()); // 협력 관계 문제 1 : 외래키 식별자를 직접 다룬다.
            em.persist(player);

            // 협력 관계 문제 2 : 소속된 팀을 찾으려면 사용자를 찾은 후 팀ID를 구한 이후에 팀을 구해야한다.
            Player findPlayer = em.find(Player.class, player.getId());
            Team findTeam = em.find(Team.class, findPlayer.getTeamId());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

}
