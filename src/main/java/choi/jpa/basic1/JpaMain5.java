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

}
