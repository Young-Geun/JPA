package choi.jpa;

import javax.persistence.*;
import java.util.List;

public class JpqlMain {

    public static void main(String[] args) {
        // TypeQuery, Query 예제
        //ex1();

        // 결과조회 예제
        //ex2();

        // 파라미터 바인딩 예제
        //ex3();

        // 프로젝션 예제
        //ex4();

        // 페이징 예제
        //ex5();

        // 조인 예제
        ex6();
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

    static void ex3() {
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
            TypedQuery<Member> query = em.createQuery("select m from Member m where m.username = :username", Member.class);
            query.setParameter("username", "유저2");
            List<Member> resultList = query.getResultList();
            for (Member m : resultList) {
                System.out.println(m.getUsername());
            }
            /*
                - 실행결과
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

    static void ex4() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /** 영속성 컨텍스트 */
            Member member1 = new Member();
            member1.setUsername("유저1");
            member1.setAge(30);
            em.persist(member1);

            em.flush();
            em.clear();

            List<Member> resultList = em.createQuery("select m from Member m", Member.class).getResultList();
            resultList.get(0).setUsername("유저1_갱신");
            /*
                - 실행쿼리를 확인해보면
                 유저의 이름이 변경되는데, 이는 resultList도 영속성 컨텍스트로 관리된다는 뜻임을 알 수 있다.
             */




            /** 스칼라 타입 프로젝션 */
            List<MemberDTO> dtoList = em.createQuery("select new choi.jpa.MemberDTO(m.username, m.age) from Member m", MemberDTO.class).getResultList();
            for (MemberDTO dto : dtoList) {
                System.out.println(dto.getUsername() + " : " + dto.getAge());
            }
            // 유저1_갱신 : 30

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

    static void ex5() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(5)
                    .getResultList();

            System.out.println(resultList);
            // [Member{id=100, username='유저99', age=99}, Member{id=99, username='유저98', age=98}, Member{id=98, username='유저97', age=97}, Member{id=97, username='유저96', age=96}, Member{id=96, username='유저95', age=95}]

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

    static void ex6() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            for (int i = 0; i < 10; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                member.setTeam(team);

                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> resultList = em.createQuery("select m from Member m inner join m.team t", Member.class).getResultList(); // inner 대신 다른 조인방법 사용가능
            System.out.println(resultList);
            /*
                - 실행쿼리
                select
                    member0_.id as id1_0_,
                    member0_.age as age2_0_,
                    member0_.TEAM_ID as TEAM_ID4_0_,
                    member0_.username as username3_0_
                from
                    Member member0_
                inner join
                    Team team1_
                        on member0_.TEAM_ID=team1_.id
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