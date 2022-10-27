package choi.jpa.basic1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain4 {

    public static void main(String[] args) {
        /** 상속관계 매핑 예제 */

        // 조인전략
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

}
