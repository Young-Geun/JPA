package choi.jpa.basic1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain4 {

    public static void main(String[] args) {
        /** 상속관계 매핑 예제 */

        // 조인 전략
        // ex1();

        // 단일테이블 전략
        // ex2();

        // 구현 클래스마다 테이블 전략
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
            Movie movie = new Movie();
            movie.setDirector("감독 A");
            movie.setActor("배우 A");
            movie.setName("스프링부트");
            movie.setPrice(10_000);

            em.persist(movie);
            /*
                테이블 조회 시, 아래와 같이 데이터가 생성되어있음
                - ITEM 테이블
                    DTYPE  	ID  	NAME  	PRICE
                    Movie	1	    스프링부트	10000


                - MOVIE 테이블
                    ACTOR  	DIRECTOR  	ID
                    배우 A	감독 A	    1
             */

            // 1차 캐시 삭제
            em.flush();
            em.clear();

            // 조회 시, 콘솔창의 쿼리를 확인해보면 조인을 통해서 가져오는 것을 확인할 수 있다.
            Movie findMovie = em.find(Movie.class, movie.getId());
            /*
                Hibernate:
                    select
                        movie0_.id as id1_2_0_,
                        movie0_1_.name as name2_2_0_,
                        movie0_1_.price as price3_2_0_,
                        movie0_.actor as actor1_5_0_,
                        movie0_.director as director2_5_0_
                    from
                        Movie movie0_
                    inner join
                        Item movie0_1_
                            on movie0_.id=movie0_1_.id
                    where
                        movie0_.id=?
             */

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
            Movie movie = new Movie();
            movie.setDirector("감독 A");
            movie.setActor("배우 A");
            movie.setName("스프링부트");
            movie.setPrice(10_000);

            em.persist(movie);
            /*
                테이블 조회 시, 아래와 같이 데이터가 생성되어있음
                - ITEM 테이블 (모든 상속관계가 1개의 테이블로 관리되어서 아래와 같은 형태를 가진다.)
                    DTYPE  	ID  	NAME  	PRICE  	ARTIST  	AUTHOR  	ISBN  	ACTOR  	DIRECTOR
                    Movie	1	    스프링부트	10000	null	    null	    null	배우 A	감독 A
             */

            // 1차 캐시 삭제
            em.flush();
            em.clear();

            // 조회 시, 콘솔창의 쿼리를 확인해보면 한 개의 테이블에서 가져오는 것을 확인할 수 있다. (성능상 장점이 있음)
            Movie findMovie = em.find(Movie.class, movie.getId());
            /*
                Hibernate:
                    select
                        movie0_.id as id2_0_0_,
                        movie0_.name as name3_0_0_,
                        movie0_.price as price4_0_0_,
                        movie0_.actor as actor8_0_0_,
                        movie0_.director as director9_0_0_
                    from
                        Item movie0_
                    where
                        movie0_.id=?
                        and movie0_.DTYPE='Movie'
             */

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
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
            Movie movie = new Movie();
            movie.setDirector("감독 A");
            movie.setActor("배우 A");
            movie.setName("스프링부트");
            movie.setPrice(10_000);

            em.persist(movie);
            /*
                테이블 조회 시, 아래와 같이 데이터가 생성되어있음
                - ITEM 테이블 (해당 전략에서는 ITEM 테이블이 생성되지 않는다.)

                - MOVIE 테이블
                    ID  	NAME  	PRICE  	ACTOR  	DIRECTOR
                    1	    스프링부트	10000	배우 A	감독 A
             */


            // 1차 캐시 삭제
            em.flush();
            em.clear();

            // 부모 클래스를 기준으로 조회 시, 콘솔창의 쿼리를 확인해보면 union all을 사용해서 가져오는 것을 확인할 수 있다. (성능상 좋지 않음)
            // Item이 아닌 자식 객체(Ex. Movie)를 기준으로 조회하면 해당 테이블(Ex. MOVIE)만 조회한다.
            Item findItem = em.find(Item.class, movie.getId());
            /*
                Hibernate:
                        select
                            item0_.id as id1_2_0_,
                            item0_.name as name2_2_0_,
                            item0_.price as price3_2_0_,
                            item0_.artist as artist1_0_0_,
                            item0_.author as author1_1_0_,
                            item0_.isbn as isbn2_1_0_,
                            item0_.actor as actor1_5_0_,
                            item0_.director as director2_5_0_,
                            item0_.clazz_ as clazz_0_
                        from
                            ( select
                                id,
                                name,
                                price,
                                artist,
                                null as author,
                                null as isbn,
                                null as actor,
                                null as director,
                                1 as clazz_
                            from
                                Album
                            union
                            all select
                                id,
                                name,
                                price,
                                null as artist,
                                author,
                                isbn,
                                null as actor,
                                null as director,
                                2 as clazz_
                            from
                                Book
                            union
                            all select
                                id,
                                name,
                                price,
                                null as artist,
                                null as author,
                                null as isbn,
                                actor,
                                director,
                                3 as clazz_
                            from
                                Movie
                        ) item0_
                    where
                        item0_.id=?
             */

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

}
