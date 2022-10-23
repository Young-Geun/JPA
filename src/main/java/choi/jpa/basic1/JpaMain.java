package choi.jpa.basic1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        /*
            - 주의사항
            1. 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유한다.
            2. EntityManager는 쓰레드 간에 공유해서는 안된다.
            3. JPA의 모든 데이터 변경은 트랜잭션 안에서 실행한다.

         */

        // 사용자 저장
        //saveMember();

        // 사용자 수정
        //editMember();

        // 사용자 삭제
        //removeMember();

        // 사용자 전체조회 (JPQL)
        //findMembers();

        // 영속과 비영속
        //managedAndTransient();

        // 1차 캐시
        //findCashEx1();

        // 1차 캐시
        //findCashEx2();

        // 1차 캐시
        //findCashEx3();

        // 쓰기지연
        // writeBehind();

        // 변경감지
        //dirtyChecking();

        // flush
        flush();
    }

    static void saveMember() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 사용자 저장
            Member member = new Member();
            member.setId(1L);
            member.setName("user-1");
            em.persist(member);

            // 트랜잭션 커밋
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

    static void editMember() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 사용자 검색
            Member member = em.find(Member.class, 1L);
            System.out.println("id = " + member.getId());
            System.out.println("name = " + member.getName());

            // 이름변경
            member.setName("user-rename");

            // em.persist(member); // 별도 저장없이 객체의 값만 변경해도 업데이트 쿼리가 실행된다.

            // 트랜잭션 커밋
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

    static void removeMember() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 사용자 검색
            Member member = em.find(Member.class, 1L);
            System.out.println("id = " + member.getId());
            System.out.println("name = " + member.getName());

            // 사용자 삭제
            em.remove(member);

            // 트랜잭션 커밋
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

    static void findMembers() {
        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 사용자 검색
            List<Member> list = em.createQuery("select m from Member as m", Member.class)
                    .getResultList();

            System.out.println(list);

            // 트랜잭션 커밋
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

    static void managedAndTransient() {
        /**
         *
         * 영속성과 비영속성
         *
         * - 영속성 컨텍스트 : '엔티티를 영구 저장하는 환경'이라는 뜻
         * - 엔티티의 생명주기
         *      1. 비영속(new/transient)
         *          : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
         *      2. 영속(managed)
         *          : 영속성 컨텍스트에 관리되는 상태
         *      3. 준영속(detached)
         *          : 영속성 컨텍스트에 저장되었다가 분리된 상태
         *      4. 삭제(removed)
         *          : 삭제된 상태
         *
         */


        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 비영속 상태
            Member member = new Member();
            member.setId(101L);
            member.setName("member101");

            // 영속 상태(객체를 저장한 상태로 실제 디비에 쿼리가 날라가는 시점은 아니다. 실제 실행 쿼리는 커밋하는 시점에 수행된다.)
            System.out.println("==== before ====");
            em.persist(member);
            System.out.println("==== after ====");
            /**
             * 콘솔창 로그
             *
             * ==== before ====
             * ==== after ====
             * Hibernate:
             *   insert into Member (name, id)
             *   values ( ?, ?)
             *
             * ====> 콘솔창에 before후에 바로 after가 찍힌 후
             *       그 다음에 Insert 쿼리가 수행된다.
             *       즉, em.persist(member); 코드에서 Insert쿼리가 수행되는 것이 아닌 tx.commit() 코드에서 수행되는 것을 알 수 있다.
             *
             */



            // 준영속 상태(회원 엔티티를 영속성 컨텍스트에서 분리)
            em.detach(member);

            // 삭제(객체를 삭제한 상태)
            em.remove(member);

            // 트랜잭션 커밋
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

    static void findCashEx1() {
        /**
         *
         * 1차 캐시
         *
         * - 영속성 컨텍스트 내부에 1차 캐시가 존재하며
         *   조회 시, DB를 조회하기 전에 1차 캐시를 우선적으로 탐색한다.         *
         */


        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 영속 컨텍스트의 1차 캐시에 저장
            Member member = new Member();
            member.setId(201L);
            member.setName("member201");
            em.persist(member);

            Member findMember = em.find(Member.class, 201L);
            System.out.println(findMember);

            // 트랜잭션 커밋
            tx.commit();

            /**
             * 콘솔창 로그
             *
             * member201(201)
             *
             * ====> 콘솔창을 확인해보면 Insert 쿼리만 수행되었고,
             *       조회 쿼리는 확인할 수 없다.
             *       이는 id가 '201L'인 값을 조회할 때, DB가 아닌 1차 캐시에서 조회를 하기 때문이다.
             *
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

    static void findCashEx2() {
        /**
         *
         * 1차 캐시
         *
         * - 영속성 컨텍스트 내부에 1차 캐시가 존재하며
         *   조회 시, DB를 조회하기 전에 1차 캐시를 우선적으로 탐색한다.
         */


        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member findMember1 = em.find(Member.class, 201L);
            System.out.println(findMember1);

            Member findMember2 = em.find(Member.class, 201L);
            System.out.println(findMember2);

            // 트랜잭션 커밋
            tx.commit();

            /**
             * 콘솔창 로그
             *
             * Hibernate:
             *     select
             *         member0_.id as id1_0_0_,
             *         member0_.name as name2_0_0_
             *     from
             *         Member member0_
             *     where
             *         member0_.id=?
             *
             * member101(201)
             * member101(201)
             *
             * ====> 콘솔창을 확인해보면 최초 한 번만 DB에서 조회하고, 두 번째는 1차 캐시에서 조회하는 것을 확인할 수 있다.
             *
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

    static void findCashEx3() {
        /**
         *
         * 영속 엔티티의 동일성 보장
         *
         * - 1차 캐시로 인하여 동일한 객체임을 보장한다.
         *
         */


        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member findMember1 = em.find(Member.class, 201L);
            Member findMember2 = em.find(Member.class, 201L);

            System.out.println("findMember1 == findMember2 : " +  (findMember1 == findMember2));
            // findMember1 == findMember2 : true
            // ==> equals() 비교가 아니지만 true 반환

            // 트랜잭션 커밋
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

    static void writeBehind() {
        /**
         *
         * 쓰기지연(transactional write-behind)
         *
         * - 영속성 컨텍스트에 변경이 발생했을 때,
         *   바로 데이터베이스로 쿼리를 보내지 않고 SQL 쿼리를 버퍼에 모아놨다가,
         *   영속성 컨텍스트가 flush 하는 시점에 모아둔 SQL 쿼리를 데이터베이스로 보내는 기능
         *
         */


        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member301 = new Member();
            member301.setId(301L);
            member301.setName("Member_301");
            em.persist(member301);
            System.out.println("사용자 301 등록");

            Member member302 = new Member();
            member302.setId(302L);
            member302.setName("Member_302");
            em.persist(member302);
            System.out.println("사용자 302 등록");

            // 트랜잭션 커밋
            tx.commit();

            /**
             * 콘솔창 로그
             *
             * 사용자 301 등록
             * 사용자 302 등록
             * Hibernate:
             *  insert into Member (name, id) values (?, ?)
             * Hibernate:
             *  insert into Member (name, id) values (?, ?)
             *
             * ====> 콘솔창을 확인해보면 쿼리수행이 '사용자 301 등록', '사용자 302 등록' 문구 이후에 있는 것을 확인해볼 수 있다.
             *       * hibernate.jdbc.batch_size 옵션을 사용하여 버퍼를 지정할 수 있다.
             *
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

    static void dirtyChecking() {
        /**
         * 변경감지(transactional write-behind)
         *
         * - JPA는 영속성 컨텍스트에 Entity를 보관할 때 최초의 상태를 저장한다. (이것을 스냅샷이라고 한다.)
         *   영속성 컨텍스트가 Flush되는 시점에 스냅샷과 Entity의 현재 값을 비교하여 달라진 필드를 업데이트한다.
         *
         */


        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member1 = em.find(Member.class, 1L);
            System.out.println("member1 = " + member1); // member1 = user-1(1)

            member1.setName("사용자-1");
            // em.persist(member1); // 주석처리해도 UPDATE 쿼리가 수행된다.

            // 트랜잭션 커밋
            tx.commit();

            /**
             * DB를 조회해보면 변경된 것을 확인할 수 있다.
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

    static void flush() {
        /**
         * flush
         *
         * - 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화 시키는 작업이다.
         * - [주의!] 영속성 컨텍스트를 비우는 것은 아니다.
         *
         */

        // 선언
        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("hello"); // persistence.xml의 persistence-unit의 name
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 선언 및 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setId(401L);
            member.setName("사용자-401");
            em.persist(member);

            em.flush(); // flush()를 호출하는 시점에 Insert쿼리가 수행된다.

            // 트랜잭션 커밋
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
