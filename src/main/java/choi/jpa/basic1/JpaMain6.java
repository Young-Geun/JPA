package choi.jpa.basic1;

import javax.persistence.*;
import java.time.LocalDateTime;

public class JpaMain6 {

    public static void main(String[] args) {
        /** 값 타입 예제 */

        // 임베디드 타입
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

}
