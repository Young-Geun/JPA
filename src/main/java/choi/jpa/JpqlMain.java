package choi.jpa;

import javax.persistence.*;
import java.util.List;

public class JpqlMain {

    public static void main(String[] args) {
        // TypeQuery, Query 예제
        //ex1();

        // 결과조회 예제
        ex2();
    }

    static void ex1() {
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

            // 반환 타입이 명확할 때는 TypedQuery를 사용할 수 있다.
            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);

            // 반환 타입이 명확하지 않을 때는 TypedQuery를 사용할 수 없다.(username은 문자열인 반면에 age는 숫자이므로 사용 불가.)
            // --> TypedQuery 대신 Query를 사용해야한다.
            // TypedQuery<String> query3 = em.createQuery("select m.username, m.age from Member m", String.class);
            Query query3 = em.createQuery("select m.username, m.age from Member m");

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

    static void ex2() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member1 = new Member();
            member1.setUsername("유저1");
            member1.setAge(30);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("유저2");
            member2.setAge(40);
            em.persist(member2);

            // 결과가 2개 이상일 때는 getResultList() 사용한다.
            TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
            List<Member> resultList = query.getResultList();
            for (Member m : resultList) {
                System.out.println(m.getUsername());
            }
            /*
                - 실행결과 (만약 결과가 없으면 빈 리스트 반환)
                유저1
                유저2
             */

            System.out.println("=============");

            // 결과가 2개 이상일 때는 getResultList() 사용한다.
            query = em.createQuery("select m from Member m where m.username = '유저2'", Member.class);
            Member result = query.getSingleResult();
            System.out.println(result.getUsername());
            /*
                - 실행결과 (만약 값이 없거나, 둘 이상이면 오류 발생)
                유저2
             */

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