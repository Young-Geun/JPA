package choi.jpa.basic1;

import javax.persistence.*;
import java.time.LocalDateTime;

public class JpaMain6 {

    public static void main(String[] args) {
        /** 값 타입 예제 */

        // 임베디드 타입
        //ex1();

        // 임베디드 타입 - 속성 재정의
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

}
