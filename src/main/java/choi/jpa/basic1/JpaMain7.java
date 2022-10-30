package choi.jpa.basic1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain7 {

    public static void main(String[] args) {
        /** 객체지향 쿼리 언어 - 기본 문법 */

        // JPQL 기본
        // ex1();

        // Criteria 기본
        ex2();
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
                - 실행 쿼리
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

    static void ex2() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /*
                - Criteria
                  JPQL은 결국 문자열로 된 쿼리를 만들기 때문에 오류 가능성이 높다.
                  이에 반해 Criteria를 사용하면 컴파일 시점에서 오류를 잡을 수 있다는 장점이 있다.
                  또한 동적 쿼리를 보다 안전하게 작성할 수 있다.
                  단, 직관적이지 않아서 실용성이 없다.
             */
            // Criteria 사용 준비
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Player> query = cb.createQuery(Player.class);

            // 루트 클래스 (조회를 시작할 클래스)
            Root<Player> p = query.from(Player.class);

            // 쿼리 생성
            CriteriaQuery<Player> cq = query.select(p).where(cb.equal(p.get("name"), "choi"));
            List<Player> players = em.createQuery(cq).getResultList();

            for (Player player : players) {
                System.out.println("player = " + player.getName());
            }

            tx.commit();

            /*
                - 실행 쿼리
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
                    player0_.USERNAME=?
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
