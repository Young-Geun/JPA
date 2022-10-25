package choi.jpa.basic1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain2 {

    public static void main(String[] args) {
        // 객체를 테이블에 맞추어 모델링
        //ex1();

        // 객체 지향 모델링 - 단방향 연관관계
        //ex2();

        // 객체 지향 모델링 - 양방향 연관관계
        ex3();
    }

    static void ex1() {
//        /*
//            객체를 테이블에 맞추어 모델링
//            : 협력 관계를 만들 수 없다.
//         */
//
//
//        // 선언
//        EntityManagerFactory emf
//                = Persistence.createEntityManagerFactory("hello");
//        EntityManager em = emf.createEntityManager();
//
//        // 트랜잭션 선언 및 시작
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        try {
//            // 팀 저장
//            Team team = new Team();
//            team.setName("TeamA");
//            em.persist(team);
//
//            // 회원 저장
//            Player player = new Player();
//            player.setName("player-1");
//            player.setTeamId(team.getId()); // 협력 관계 문제 1 : 외래키 식별자를 직접 다룬다.
//            em.persist(player);
//
//            // 협력 관계 문제 2 : 소속된 팀을 찾으려면 사용자를 찾은 후 팀ID를 구한 이후에 팀을 구해야한다.
//            Player findPlayer = em.find(Player.class, player.getId());
//            Team findTeam = em.find(Team.class, findPlayer.getTeamId());
//
//            tx.commit();
//        } catch (Exception e) {
//            tx.rollback();
//        } finally {
//            em.close();
//        }
//
//        emf.close();
    }

    static void ex2() {
//        // 선언
//        EntityManagerFactory emf
//                = Persistence.createEntityManagerFactory("hello");
//        EntityManager em = emf.createEntityManager();
//
//        // 트랜잭션 선언 및 시작
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        try {
//            // 팀 저장
//            Team team = new Team();
//            team.setName("TeamB");
//            em.persist(team);
//
//            // 회원 저장
//            Player player = new Player();
//            player.setName("player-2");
//            player.setTeam(team);
//            em.persist(player);
//
//            // 조회
//            Player findPlayer = em.find(Player.class, player.getId());
//            Team findTeam = findPlayer.getTeam();
//
//            tx.commit();
//        } catch (Exception e) {
//            tx.rollback();
//        } finally {
//            em.close();
//        }
//
//        emf.close();
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
            // 팀 저장
            Team team = new Team();
            team.setName("TeamB");
            em.persist(team);

            // 회원 저장
            Player player = new Player();
            player.setName("player-2");
            player.setTeam(team);
            em.persist(player);

            em.flush();
            em.clear();

            // 조회
            Player findPlayer = em.find(Player.class, player.getId());
            List<Player> players = findPlayer.getTeam().getPlayers();

            for (Player p : players) {
                System.out.println("player name : " + p.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

}
