package choi.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpqlMain {

    public static void main(String[] args) {
        test();
    }

    static void test() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("유저1");
            member.setAge(30);

            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            // 자원반환
            em.close();
        }

        // 자원반환
        emf.close();
    }

}