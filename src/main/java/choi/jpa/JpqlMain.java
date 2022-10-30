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
        //ex6();

        // 서브쿼리 예제
        //ex7();

        // 타입표현 및 표현식 예제
        //ex8();

        // 조건식 예제
        ex9();
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

    static void ex7() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /** 쿼리 예시 */
            /*
                - 나이가 평균보다 많은 회원
                  : select m from Member m where m.age > (select avg(m2.age) from Member m2)

                - 한 건 이라도 주문한 고객
                  : select m from Member m where (select count(o) from Order o where m = o.member) > 0

                - 나이가 평균보다 많은 회원
                  : select m from Member m where m.age > (select avg(m2.age) from Member m2)
             */


            /** 서브 쿼리 지원 함수 */
            /*
                - [NOT] EXISTS (subquery): 서브쿼리에 결과가 존재하면 참
                - {ALL | ANY | SOME} (subquery)
                    : ALL = 모두 만족하면 참
                    : ANY, SOME = 같은 의미, 조건을 하나라도 만족하면 참

                 ------------------------------ 예시 ------------------------------

                - 팀A 소속인 회원
                  : select m from Member m where exists (select t from m.team t where t.name = ‘팀A')

                - 전체 상품 각각의 재고보다 주문량이 많은 주문들
                  : select o from Order o where o.orderAmount > ALL (select p.stockAmount from Product p)

                - 어떤 팀이든 팀에 소속된 회원
                  : select m from Member m where m.team = ANY (select t from Team t)
             */


            /** 한계점 */
            /*
                - JPA 서브 쿼리의 한계
                    1. JPA는 WHERE, HAVING 절에서만 서브쿼리 사용 가능 (JPA 표준 스펙)
                    2. 하지만 SELECT절도 가능(하이버네이트에서 지원해주기 때문)
                    3. FROM 절의 서브 쿼리는 현재 JPQL에서 불가능(2022-10-30 기준)
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

    static void ex8() {
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
                if (i % 2 == 0) {
                    member.setUsername("유저" + i);
                    member.setType(MemberType.USER);
                } else {
                    member.setUsername("관리자" + i);
                    member.setType(MemberType.ADMIN);
                }
                member.setAge(i);
                member.setTeam(team);

                em.persist(member);
            }

            em.flush();
            em.clear();

            /** ENUM 사용 예제 */
            List<Member> adminList = em.createQuery("select m from Member m where m.type = choi.jpa.MemberType.ADMIN", Member.class).getResultList();
            System.out.println(adminList);
            /*
                [
                Member{id=3, username='관리자1', age=1},
                Member{id=5, username='관리자3', age=3},
                Member{id=7, username='관리자5', age=5},
                Member{id=9, username='관리자7', age=7},
                Member{id=11, username='관리자9', age=9}
                ]
             */

            /** 그 외에 사용할 수 있는 타입 : 문자, 숫자, Boolean, 엔티티 타입 */
            // 생략


            /**
             *  - JPQL 기타 표현식
             *    : SQL과 문법식과 동일하게 EXISTS, IN, AND, OR, NOT, =, >, >=, BETWEEN, LIKE, IS NULL 등을 사용 가능하다.
             */
            List<Member> betweenList = em.createQuery("select m from Member m where m.age between 5 and 7", Member.class).getResultList();
            System.out.println(betweenList);
            // [Member{id=7, username='관리자5', age=5}, Member{id=8, username='유저6', age=6}, Member{id=9, username='관리자7', age=7}]

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

    static void ex9() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName(null);
            em.persist(team);

            for (int i = 5; i < 15; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                if (i > 13) {
                    member.setTeam(team);
                }
                em.persist(member);
            }

            em.flush();
            em.clear();

            /* CASE식 기본 */
            String query = "select " +
                    "case when m.age <= 7 then '유치원생' " +
                    "     when m.age >= 14 then '중학생' " +
                    "     else '초등학생' " +
                    "end " +
                    "from Member m";
            List<String> list = em.createQuery(query, String.class).getResultList(); // inner 대신 다른 조인방법 사용가능
            System.out.println(list);
            // [유치원생, 유치원생, 유치원생, 초등학생, 초등학생, 초등학생, 초등학생, 초등학생, 초등학생, 중학생]


            /* COALEASE 기본 */
            List<Object[]> resultList = em.createQuery("select m.username, coalesce(m.team.name, '무소속') from Member m").getResultList();
            for (Object[] objects : resultList) {
                System.out.println(objects[0] + " : " + objects[1]);
            }
            // 유저14 : 무소속

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