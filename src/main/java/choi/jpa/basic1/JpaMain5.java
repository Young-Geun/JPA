package choi.jpa.basic1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain5 {

    public static void main(String[] args) {
        /** 프록시 예제 */

        // 프록시 기본 예제
        // ex1();

        // 준영속 상태일 떄 초기화 오류
        //ex2();

        // 지연로딩 (@ManyToOne(fetch = FetchType.LAZY))
        //ex3();

        // 즉시로딩 (@ManyToOne(fetch = FetchType.EAGER))
        /*
            실무에서는 가급적 지연로딩을 사용할 것.
            : 예기치 못한 SQL 발생 가능성이 있다.
            : 즉시로딩은 JPQL에서 N+1 문제를 일으킨다.
         */
        //ex4();

        /** CASCADE 예제 */
        ex5();
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
            Player player = new Player();
            player.setName("proxy-1");

            em.persist(player);

            em.flush();
            em.clear();

            /* em.find() 사용 */
//            Player findPlayer = em.find(Player.class, player.getId());
//            System.out.println(findPlayer.getName());
            /*
                Hibernate:
                    select
                        player0_.id as id1_6_0_,
                        player0_.createdBy as createdB2_6_0_,
                        player0_.modifiedBy as modified3_6_0_,
                        player0_.LOCKER_ID as LOCKER_I5_6_0_,
                        player0_.USERNAME as USERNAME4_6_0_,
                        player0_.TEAM_ID as TEAM_ID6_6_0_,
                        locker1_.id as id1_3_1_,
                        locker1_.name as name2_3_1_,
                        team2_.TEAM_ID as TEAM_ID1_7_2_,
                        team2_.createdBy as createdB2_7_2_,
                        team2_.modifiedBy as modified3_7_2_,
                        team2_.name as name4_7_2_
                    from
                        Player player0_
                    left outer join
                        Locker locker1_
                            on player0_.LOCKER_ID=locker1_.id
                    left outer join
                        Team team2_
                            on player0_.TEAM_ID=team2_.TEAM_ID
                    where
                        player0_.id=?다.
             */



            /* em.getReference() 사용 */
            Player findPlayer = em.getReference(Player.class, player.getId()); // em.getReference() : 가짜(프록시) 엔티티 객체 조회
            System.out.println(findPlayer.getId()); // 해당 시점에 쿼리 실행안됨. id는 이미 알고있으니 쿼리를 수행할 이유가 없음.
            System.out.println(findPlayer.getName()); // 해당 시점에 쿼리 수행됨. name값은 모르니 조회가 필요하기 때문. 이 시점에 프록시 객체가 진짜 객체로 변경되는 것은 아니다. 프록시는 유지가 되고 프록시가 진짜 객체의 값을 가져오게 되는 구조.

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
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
            Player player = new Player();
            player.setName("proxy-2");
            em.persist(player);

            em.flush();
            em.clear();

            Player findPlayer = em.getReference(Player.class, player.getId());
            em.detach(findPlayer); // close(), clear() 모두 동일한 오류 발생

            System.out.println(findPlayer.getName()); // ==> 오류 발생. em.getReference()는 준영속상태가 되면 오류가 발생한다.

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            /*
                org.hibernate.LazyInitializationException: could not initialize proxy [choi.jpa.basic1.Player#1] - no Session
                at org.hibernate.proxy.AbstractLazyInitializer.initialize(AbstractLazyInitializer.java:169)
                at org.hibernate.proxy.AbstractLazyInitializer.getImplementation(AbstractLazyInitializer.java:309)
                at org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor.intercept(ByteBuddyInterceptor.java:45)
                at org.hibernate.proxy.ProxyConfiguration$InterceptorDispatcher.intercept(ProxyConfiguration.java:95)
                at choi.jpa.basic1.Player$HibernateProxy$hdn9STcr.getName(Unknown Source)
                at choi.jpa.basic1.JpaMain5.ex2(JpaMain5.java:107)
                at choi.jpa.basic1.JpaMain5.main(JpaMain5.java:17)


                ==>
             */
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
            Team team = new Team();
            team.setName("Team A");
            em.persist(team);

            Player player = new Player();
            player.setName("proxy-3");
            player.setTeam(team);
            em.persist(player);

            em.flush();
            em.clear();

            /* Player만 조회 */
//            Player findPlayer = em.find(Player.class, player.getId());
//            System.out.println(findPlayer.getName());
//            /*
//                Hibernate:
//                    select
//                        player0_.id as id1_6_0_,
//                        player0_.createdBy as createdB2_6_0_,
//                        player0_.modifiedBy as modified3_6_0_,
//                        player0_.LOCKER_ID as LOCKER_I5_6_0_,
//                        player0_.USERNAME as USERNAME4_6_0_,
//                        player0_.TEAM_ID as TEAM_ID6_6_0_
//                    from
//                        Player player0_
//                    where
//                        player0_.id=?
//
//                ==> @ManyToOne(fetch = FetchType.LAZY) 사용으로 이전과 다르게 조인없이 Player 테이블만 조회해온다.
//             */


            /* Team 조회 */
            Player findPlayer = em.find(Player.class, player.getId());
            System.out.println(findPlayer.getTeam().getName());
            /*
                Hibernate:
                    select
                        player0_.id as id1_6_0_,
                        player0_.createdBy as createdB2_6_0_,
                        player0_.modifiedBy as modified3_6_0_,
                        player0_.LOCKER_ID as LOCKER_I5_6_0_,
                        player0_.USERNAME as USERNAME4_6_0_,
                        player0_.TEAM_ID as TEAM_ID6_6_0_
                    from
                        Player player0_
                    where
                        player0_.id=?
                Hibernate:
                    select
                        team0_.TEAM_ID as TEAM_ID1_7_0_,
                        team0_.createdBy as createdB2_7_0_,
                        team0_.modifiedBy as modified3_7_0_,
                        team0_.name as name4_7_0_
                    from
                        Team team0_
                    where
                        team0_.TEAM_ID=?

                 ==> TEAM 테이블의 데이터를 조회할 때 TEAM 테이블 관련 쿼리가 수행된다.(지연 실행)
             */

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
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
            Team team = new Team();
            team.setName("Team A");
            em.persist(team);

            Player player = new Player();
            player.setName("proxy-3");
            player.setTeam(team);
            em.persist(player);

            em.flush();
            em.clear();

            /* Player만 조회 */
            Player findPlayer = em.find(Player.class, player.getId());
            System.out.println(findPlayer.getName());
            /*
                Hibernate:
                    select
                        player0_.id as id1_6_0_,
                        player0_.createdBy as createdB2_6_0_,
                        player0_.modifiedBy as modified3_6_0_,
                        player0_.LOCKER_ID as LOCKER_I5_6_0_,
                        player0_.USERNAME as USERNAME4_6_0_,
                        player0_.TEAM_ID as TEAM_ID6_6_0_,
                        locker1_.id as id1_3_1_,
                        locker1_.name as name2_3_1_,
                        team2_.TEAM_ID as TEAM_ID1_7_2_,
                        team2_.createdBy as createdB2_7_2_,
                        team2_.modifiedBy as modified3_7_2_,
                        team2_.name as name4_7_2_
                    from
                        Player player0_
                    left outer join
                        Locker locker1_
                            on player0_.LOCKER_ID=locker1_.id
                    left outer join
                        Team team2_
                            on player0_.TEAM_ID=team2_.TEAM_ID
                    where
                        player0_.id=?

                ==> ex3과 다르게 Player만 조회하여도 관련 테이블을 모두 조회한다.
             */

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    static void ex5() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /*
                - CASCADE 사용 전
                  : parent, child1, child2를 모두 persist() 해줘야 한다.
            */
//            Child child1 = new Child();
//            Child child2 = new Child();
//
//            Parent parent = new Parent();
//            parent.addChild(child1);
//            parent.addChild(child2);
//
//            em.persist(parent);
//            em.persist(child1);
//            em.persist(child2);


            /*
                - CASCADE 사용 후
                  : parent만 persist() 해도 child1과 child2가 등록된다.
            */
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

}
