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
        //ex3();

        // 값 타입 컬렉션
        ex4();
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

    static void ex4() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Student student = new Student();
            student.setName("학생-1");
            student.setHomeAddress(new Address("서울", "구로구", "12345"));

            // 값 타입 컬렉션 예제 START
            student.getFavoriteFoods().add("치킨");
            student.getFavoriteFoods().add("피자");

            student.getAddressHistory().add(new Address("경기", "대왕판교로", "98765"));
            student.getAddressHistory().add(new Address("서울", "구로구", "12345"));
            // 값 타입 컬렉션 예제 END

            em.persist(student);

            em.flush();
            em.clear();

            Student findStudent = em.find(Student.class, student.getId());
            /*
                Hibernate:
                    select
                        student0_.STUDENT_ID as STUDENT_1_12_0_,
                        student0_.city as city2_12_0_,
                        student0_.street as street3_12_0_,
                        student0_.zipcode as zipcode4_12_0_,
                        student0_.name as name5_12_0_
                    from
                        Student student0_
                    where
                        student0_.STUDENT_ID=?

                ==> FavoriteFoods와 AddressHistory는 가져오지 않고 있음.
                    실제 값을 쓰게되면 그때 가져옴(값 타입 컬렉션은 지연로딩 방식임을 알 수 있다.)
             */

            findStudent.getAddressHistory().remove(new Address("경기", "대왕판교로", "98765"));
            findStudent.getAddressHistory().add(new Address("경기", "대왕판교로", "56789"));
            // -> 첫 번째 데이터의 값을 변경하고자할 때, 기존 데이터를 지우고 새로운 값을 넣는 방식으로 해야한다.
            //    : 단, remove() 내부적으로 equals()가 동작하여 true일 경우에만 삭제되므로 equals를 제대로 재정의하지 않으면 값이 지워지지 않는 현상이 발생할 수 있다.

            // * 업데이트 동작방식
            //   실제 실행되는 쿼리를 보면 첫 번째 데이터를 지우고 두 번째 데이터를 넣는 것이 아니라
            //   모든 데이터를 지운 후, new Address("서울", "구로구", "12345"), new Address("경기", "대왕판교로", "56789")에 해당하는 값을 넣게된다.




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
