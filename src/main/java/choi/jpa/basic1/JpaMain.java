package choi.jpa.basic1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        /*
            - 주의사항
            1. 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유한다.
            2. EntityManager는 쓰레드 간에 공유해서는 안된다.
            3. JPA의 모든 데이터 변경은 트랜잭션 안에서 실행한다.

         */

        // 사용자 저장
        //saveMember();

        // 사용자 수정
        //editMember();

        // 사용자 삭제
        //removeMember();

        // 사용자 전체조회 (JPQL)
        findMembers();

        // 영속과 비영속
        managedAndTransient();
    }

    static void saveMember() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 사용자 저장
            Member member = new Member();
            member.setId(1L);
            member.setName("user-1");
            em.persist(member);

            // 트랜잭션 커밋
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

    static void editMember() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 사용자 검색
            Member member = em.find(Member.class, 1L);
            System.out.println("id = " + member.getId());
            System.out.println("name = " + member.getName());

            // 이름변경
            member.setName("user-rename");

            // em.persist(member); // 별도 저장없이 객체의 값만 변경해도 업데이트 쿼리가 실행된다.

            // 트랜잭션 커밋
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

    static void removeMember() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 사용자 검색
            Member member = em.find(Member.class, 1L);
            System.out.println("id = " + member.getId());
            System.out.println("name = " + member.getName());

            // 사용자 삭제
            em.remove(member);

            // 트랜잭션 커밋
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

    static void findMembers() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 사용자 검색
            List<Member> list = em.createQuery("select m from Member as m", Member.class)
                    .getResultList();

            System.out.println(list);

            // 트랜잭션 커밋
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

    static void managedAndTransient() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 비영속 상태
            Member member = new Member();
            member.setId(100L);
            member.setName("new_member");

            // 영속 상태(객체를 저장한 상태로 실제 디비에 쿼리가 날라가는 시점은 아니다. 실제 실행 쿼리는 커밋하는 시점에 수행된다.)
            System.out.println("==== before ====");
            em.persist(member);
            System.out.println("==== after ====");

            /**
             * 콘솔창 로
             *
             * ==== before ====
             * ==== after ====
             * Hibernate:
             *   insert into Member (name, id)
             *   values ( ?, ?)
             *
             * ====> 콘솔창에 before후에 바로 after가 찍힌 후
             *       그 다음에 Insert 쿼리가 수행된다.
             *       즉, em.persist(member); 코드에서 Insert쿼리가 수행되는 것이 아닌 tx.commit() 코드에서 수행되는 것을 알 수 있다.
             *
             */

            // 트랜잭션 커밋
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
