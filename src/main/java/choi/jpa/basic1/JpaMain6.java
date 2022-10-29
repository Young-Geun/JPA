package choi.jpa.basic1;

import javax.persistence.*;
import java.time.LocalDateTime;

public class JpaMain6 {

    public static void main(String[] args) {
        /** 값 타입 예제 */

        // 임베디드 타입
        //ex1();

        // 임베디드 타입 - 속성 재정의
        //ex2();

        // 임베디드 타입 - 오류 예제
        ex3();
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
            Employee employee = new Employee();
            employee.setName("choi");
            employee.setWordPeriod(new Period(LocalDateTime.now(), LocalDateTime.now().plusDays(365)));
            employee.setHomeAddress(new Address("서울", "천왕로", "12312"));

            em.persist(employee);

            tx.commit();
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
            Employee employee = new Employee();
            employee.setName("choi");
            employee.setWordPeriod(new Period(LocalDateTime.now(), LocalDateTime.now().plusDays(365)));
            employee.setHomeAddress(new Address("서울", "천왕로", "12312"));
            employee.setCorAddress(new Address("서울", "대왕판교로", "55555")); // Repeated column in mapping for entity 오류발 생
            /*
                한 엔티티에서 같은 인베디드 타입을 사용할 경우, 컬럼명이 중복되어 오류가 발생한다.
                이를 해결하기 위하여 @AttributeOverrides를 사용하여 오류를 해결할 수 있다.
             */

            em.persist(employee);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
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
            Address address = new Address("서울", "천왕로", "12312");

            Employee employee1 = new Employee();
            employee1.setName("choi");
            employee1.setWordPeriod(new Period(LocalDateTime.now(), LocalDateTime.now().plusDays(365)));
            employee1.setHomeAddress(address);
            em.persist(employee1);

            Employee employee2 = new Employee();
            employee2.setName("kim");
            employee2.setWordPeriod(new Period(LocalDateTime.now(), LocalDateTime.now().plusDays(365)));
            employee2.setHomeAddress(address);
            em.persist(employee2);

            /* 주의
                - 예상 : employee1의 우편코드를 바꿈.
                - 실제 : employee2의 우편코드까지 바뀜.
             */
            employee1.getHomeAddress().setZipcode("99999");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

}
