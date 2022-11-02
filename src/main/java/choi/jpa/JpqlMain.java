package choi.jpa;

import javax.persistence.*;
import java.util.Collection;
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
        //ex9();

        // 기본함수 예제
        //ex10();

        // 경로 표현식 예제
        //ex11();

        // 페치조인 예제
        //ex12();

        // 페치조인 - DISTINCT 예제
        //ex13();

        // 페치조인의 한계 예제
        //ex14();

        // 엔티티 직접 사용 - 기본키
        //ex15();

        // Named 쿼리
        ex16();
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
            List<String> list = em.createQuery(query, String.class).getResultList();
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

    static void ex10() {
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

            for (int i = 0; i < 10; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                member.setTeam(team);
                em.persist(member);
            }

            em.flush();
            em.clear();

            /** JPQL 기본함수 */
            String query = "select m.username || ':' || m.age  from Member m";
            List<String> list = em.createQuery(query, String.class).getResultList();
            System.out.println(list);
            // [유저0:0, 유저1:1, 유저2:2, 유저3:3, 유저4:4, 유저5:5, 유저6:6, 유저7:7, 유저8:8, 유저9:9]
            // 기본 함수 종류 : CONCAT, SUBSTRING, TRIM, LOWER, UPPER, LENGTH, LOCATE 등등



            /** JPA 제공 함수 */
            int memberSize = em.createQuery("select size(t.members) from Team t", Integer.class).getSingleResult().intValue();
            System.out.println(memberSize);
            // 10
            // JPA 제공 함수 종류 : size, index



            /** 사용자 정의 함수 (*group_concat = 다수의 컬럼 값을 하나의 문자열로 합치는 함수) */
            List<String> memberList = em.createQuery("select function('group_concat', m.username) from Member m", String.class).getResultList();
            System.out.println(memberList.size()); // 1
            for (String s : memberList) {
                System.out.println(s);
            }
            // 유저0,유저1,유저2,유저3,유저4,유저5,유저6,유저7,유저8,유저9

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

    static void ex11() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("TEAM-A");
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

            /*
                - 상태 필드
                Ex) m.username
                : 경로의 끝(username 다음에 .을 찍고 더 이상 들어갈 곳이 없음)
             */
            String query = "select m.username from Member m";
            List<String> list1 = em.createQuery(query, String.class).getResultList();

            /*
                - 단일 값 연관 경로
                Ex) m.team
                : 경로의 끝이 아닌 탐색 가능(team 다음에 .을 찍고 더 이상 들어갈 곳이 있음. m.team.name 등 )
                : 주의! 묵시적 내부 조인이 발생한다.(실제 쿼리는 Member과 Team 테이블의 조인이 발생한다.)
             */
            query = "select m.team from Member m";
            List<Team> list2 = em.createQuery(query, Team.class).getResultList();


            /*
                - 컬렉션 값 연관 관계
                Ex) t.members
                : 경로의 끝(.을 찍고 더 이상 들어갈 곳이 없음)
                : 주의! 묵시적 내부 조인이 발생한다.(실제 쿼리는 Member과 Team 테이블의 조인이 발생한다.)
             */
            query = "select t.members from Team t";
            Collection result = em.createQuery(query, Collection.class).getResultList();
            System.out.println(result);


            /**
             *  정리 : 묵시적 방법은 코드와 실제 쿼리를 명확하게 알 수 없기 때문에 좋은 방법이 아니다.
             *        join 키워드를 사용하여 명시적 조인을 하는게 바람직한 코딩 방식이다.
             */

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // 자원반환
            em.close();
        }

        // 자원반환
        emf.close();
    }

    static void ex12() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("TEAM-A");
            em.persist(teamA);
            for (int i = 0; i < 3; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                member.setTeam(teamA);
                em.persist(member);
            }

            Team teamB = new Team();
            teamB.setName("TEAM-B");
            em.persist(teamB);
            for (int i = 3; i < 5; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                member.setTeam(teamB);
                em.persist(member);
            }

            em.flush();
            em.clear();

            /** 일반적인 조회 방식 */
            String query = "select m from Member m";
            List<Member> memberList = em.createQuery(query, Member.class).getResultList();
            for (Member m : memberList) {
                System.out.println("member.name = " + m.getUsername() + ", team.name = " + m.getTeam().getName());
            }
            /*
                Hibernate:
                    select
                        member0_.id as id1_0_,
                        member0_.age as age2_0_,
                        member0_.TEAM_ID as TEAM_ID5_0_,
                        member0_.type as type3_0_,
                        member0_.username as username4_0_
                    from
                        Member member0_

                Hibernate:
                    select
                        team0_.id as id1_3_0_,
                        team0_.name as name2_3_0_
                    from
                        Team team0_
                    where
                        team0_.id=?

                Hibernate:
                    select
                        team0_.id as id1_3_0_,
                        team0_.name as name2_3_0_
                    from
                        Team team0_
                    where
                        team0_.id=?


                ===> 쿼리가 총 3번 수행된다.
             */


            /** fetch join 사용 방식 */
            System.out.println("============== fetch join 사용 ==============");
            em.flush();
            em.clear();

            query = "select m from Member m join fetch m.team";
            memberList = em.createQuery(query, Member.class).getResultList();
            for (Member m : memberList) {
                System.out.println("member.name = " + m.getUsername() + ", team.name = " + m.getTeam().getName());
            }
            /*
                Hibernate:
                    select
                        member0_.id as id1_0_0_,
                        team1_.id as id1_3_1_,
                        member0_.age as age2_0_0_,
                        member0_.TEAM_ID as TEAM_ID5_0_0_,
                        member0_.type as type3_0_0_,
                        member0_.username as username4_0_0_,
                        team1_.name as name2_3_1_
                    from
                        Member member0_
                    inner join
                        Team team1_
                            on member0_.TEAM_ID=team1_.id

                ===> 쿼리가 총 1번 수행된다.
             */

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // 자원반환
            em.close();
        }

        // 자원반환
        emf.close();
    }

    static void ex13() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("TEAM-A");
            em.persist(teamA);
            for (int i = 0; i < 3; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                member.setTeam(teamA);
                em.persist(member);
            }

            Team teamB = new Team();
            teamB.setName("TEAM-B");
            em.persist(teamB);
            for (int i = 3; i < 5; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                member.setTeam(teamB);
                em.persist(member);
            }

            em.flush();
            em.clear();

            String query = "select t from Team t join fetch t.members";
            List<Team> teamList = em.createQuery(query, Team.class).getResultList();
            for (Team t : teamList) {
                System.out.println("team.name = " + t.getName() + ", member.size = " + t.getMembers().size());
                for (Member m : t.getMembers()) {
                    System.out.println("-----> member.name = " + m.getUsername());
                }
            }
            /*
                team.name = TEAM-A, member.size = 3
                -----> member.name = 유저0
                -----> member.name = 유저1
                -----> member.name = 유저2
                team.name = TEAM-A, member.size = 3
                -----> member.name = 유저0
                -----> member.name = 유저1
                -----> member.name = 유저2
                team.name = TEAM-A, member.size = 3
                -----> member.name = 유저0
                -----> member.name = 유저1
                -----> member.name = 유저2
                team.name = TEAM-B, member.size = 2
                -----> member.name = 유저3
                -----> member.name = 유저4
                team.name = TEAM-B, member.size = 2
                -----> member.name = 유저3
                -----> member.name = 유저4


                ==> 중복 데이터 존재. (JOIN 하면서 데이터가 뻥튀기 됨)
                    JPA의 DISTINCT를 사용하면,
                    1. SQL에 DISTINCT도 추가되고, (위의 경우에는 SQL에 DISTINCT가 추가되어도 데이터가 제거되지 않음. 실제 값이 다르게 조회되기 때문)
                    2. 중복 엔티티도 제거된다.
             */


            query = "select distinct t from Team t join fetch t.members";
            teamList = em.createQuery(query, Team.class).getResultList();
            for (Team t : teamList) {
                System.out.println("team.name = " + t.getName() + ", member.size = " + t.getMembers().size());
                for (Member m : t.getMembers()) {
                    System.out.println("-----> member.name = " + m.getUsername());
                }
            }
            /*
                team.name = TEAM-A, member.size = 3
                -----> member.name = 유저0
                -----> member.name = 유저1
                -----> member.name = 유저2
                team.name = TEAM-B, member.size = 2
                -----> member.name = 유저3
                -----> member.name = 유저4


               ==> 위의 데이터와 다르게 중복이 제거된 것을 볼 수 있다.
             */

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // 자원반환
            em.close();
        }

        // 자원반환
        emf.close();
    }

    static void ex14() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("TEAM-A");
            em.persist(teamA);
            for (int i = 0; i < 3; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                member.setTeam(teamA);
                em.persist(member);
            }

            Team teamB = new Team();
            teamB.setName("TEAM-B");
            em.persist(teamB);
            for (int i = 3; i < 5; i++) {
                Member member = new Member();
                member.setUsername("유저" + i);
                member.setAge(i);
                member.setTeam(teamB);
                em.persist(member);
            }

            em.flush();
            em.clear();

            // fetch 조인의 한계 - 페이징 사용 못함
            String query = "select distinct t from Team t join fetch t.members";
            List<Team> teamList = em.createQuery(query, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getResultList();

            for (Team t : teamList) {
                System.out.println("team.name = " + t.getName());
            }
            /*
                WARN: HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
                Hibernate:
                        select
                            distinct team0_.id as id1_3_0_,
                                    members1_.id as id1_0_1_,
                            team0_.name as name2_3_0_,
                                    members1_.age as age2_0_1_,
                            members1_.TEAM_ID as TEAM_ID5_0_1_,
                                    members1_.type as type3_0_1_,
                            members1_.username as username4_0_1_,
                                    members1_.TEAM_ID as TEAM_ID5_0_0__,
                            members1_.id as id1_0_0__
                        from
                            Team team0_
                        inner join
                            Member members1_
                                on team0_.id=members1_.TEAM_ID


               ==> fetch 조인 시 페이징을 사용할 경우, 아래의 경고 메시지 발생
                   - WARN: HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!

                   : 또한 쿼리를 보면 페이징을 하지 않고 있다.
                     메모리에서 페이징을 하기 모든 데이터를 메모리로 올리기 때문에 장애를 초래할 수 있다.
             */


            /**
             *  기타 한계점
             *
             *  1. 페치 조인 대상에는 별칭을 줄 수 없다. (하이버네이트에서는 가능하지만 가급적 사용x)
             *  2. 둘 이상의 컬렉션은 페치 조인할 수 있다. (가능할 수도 있으나 데이터 정합성을 보장할 수 없음)
             */

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // 자원반환
            em.close();
        }

        // 자원반환
        emf.close();
    }

    static void ex15() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("유저 1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            /*
                - JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 기본 키 값을 사용
                  : 따라서 아래 두 개의 JPQL은 같은 SQL이 실행된다.
                    JPQL 1. slect count(m.id) from Member m
                    JPQL 2. slect count(m) from Member m

                    ==> 실제 수행 쿼리 : select count(m.id) as cnt from member m
             */
            // 테스트 코드 생략



            /*
                - 파라미터로 전달 예제
                  : 아래의 두 코드 모두 같은 쿼리가 실행된다.
                    select
                        member0_.id as id1_0_,
                        member0_.age as age2_0_,
                        member0_.TEAM_ID as TEAM_ID5_0_,
                        member0_.type as type3_0_,
                        member0_.username as username4_0_
                    from
                        Member member0_
                    where
                        member0_.id=?

             */
            String query = "select m from Member m where m = :member";
            List<Member> memberList = em.createQuery(query, Member.class)
                    .setParameter("member", member)
                    .getResultList();
            System.out.println("memberList #1 = " + memberList); // memberList #1 = [Member{id=1, username='유저 1', age=10}]

            em.flush();
            em.clear();

            query = "select m from Member m where m.id = :memberId";
            memberList = em.createQuery(query, Member.class)
                    .setParameter("memberId", member.getId())
                    .getResultList();
            System.out.println("memberList #2= " + memberList); // memberList #2= [Member{id=1, username='유저 1', age=10}]

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // 자원반환
            em.close();
        }

        // 자원반환
        emf.close();
    }

    static void ex16() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member1 = new Member();
            member1.setUsername("유저 1");
            member1.setAge(10);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("유저 2");
            member2.setAge(10);
            em.persist(member2);

            em.flush();
            em.clear();

            /*
                - Named 쿼리
                  : 미리 정의해서 이름을 부여해두고 사용하는 JPQL
                  : 동적 쿼리를 만들지 못한다.
                  : 로딩 시점에 초기화 후 재사용한다.
                  : 애플리케이션 로딩 시점에 쿼리를 검증한다.
                  : 어노테이션 또는 XML에 정의 가능하며, XML이 우선권을 가진다. (xml에 정의할 경우, 배포방식에 따라 분기처리 할 수 있는 장점이 있다.)
             */
            List<Member> memberList = em.createNamedQuery("Member.findByUsername")
                    .setParameter("username", "유저 1")
                    .getResultList();
            System.out.println(memberList); // [Member{id=1, username='유저 1', age=10}]

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // 자원반환
            em.close();
        }

        // 자원반환
        emf.close();
    }

}