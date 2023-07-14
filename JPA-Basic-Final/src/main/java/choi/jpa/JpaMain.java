package choi.jpa;

import choi.jpa.domain.Book;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {
        test();
    }

    static void test() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("CHOI");

            em.persist(book);

            // 트랜잭션 커밋
            tx.commit();

            /** 데이터 조회 결과(ITEM 테이블) */
            /*
                DTYPE  	ITEM_ID  	NAME  	PRICE  	STOCKQUANTITY  	AUTHOR  	ISBN  	ACTOR  	DIRECTOR  	ARTIST  	ETC
                Book	1	        JPA	    0	    0	            CHOI	    null	null	null	    null	    null
            */

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