package choi.jpa.basic;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain3 {

    public static void main(String[] args) {
        // 일대다 관계 예제 생략 (이전 예제에서 실습하였던 항목이기 때문에 생략)
        // ex1();

        // 다대일 관계 예제 생략 (이전 예제에서 실습하였던 항목이기 때문에 생략)
        // ex2();

        // 일대일 관계
        ex3();
    }

    static void ex3() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Locker locker = new Locker();
            locker.setName("111번 락커");
            em.persist(locker);

            Player player = new Player();
            player.setName("player-111");
            player.setLocker(locker);
            em.persist(player);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

}
