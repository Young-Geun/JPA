package choi.jpa.basic1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain7 {

    public static void main(String[] args) {
        /** 객체지향 쿼리 언어 - 기본 문법 */

        // JPQL 기본
        ex1();
    }

    static void ex1() {
        // 선언
        EntityManagerFactory emf
                 = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /*
                JPQL
                - 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리이다.
             */
            List<Player> players = em.createQuery("SELECT p FROM Player p WHERE p.name LIKE '%c%'", Player.class).getResultList();

            for (Player player : players) {
                System.out.println("player = " + player.getName());
            }

            tx.commit();

            /*
                실행 쿼리
                 select
                    player0_.id as id1_11_,
                    player0_.createdBy as createdB2_11_,
                    player0_.modifiedBy as modified3_11_,
                    player0_.LOCKER_ID as LOCKER_I5_11_,
                    player0_.USERNAME as USERNAME4_11_,
                    player0_.TEAM_ID as TEAM_ID6_11_
                from
                    Player player0_
                where
                    player0_.USERNAME like '%c%'
             */
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
