package choi.jpa.querydsl;

import choi.jpa.querydsl.entity.Member;
import choi.jpa.querydsl.entity.QMember;
import choi.jpa.querydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static choi.jpa.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        //member1을 찾기.
        String qlString = "select m from Member m where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        //member1을 찾기.
        QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))//파라미터 바인딩 처리
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydslUsingStaticImport() {
        //member1을 찾기.
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10))
                )
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");

        /*
            검색조건 예시

            member.username.eq("member1") // username = 'member1'
            member.username.ne("member1") //username != 'member1'
            member.username.eq("member1").not() // username != 'member1'
            member.username.isNotNull() //이름이 is not null
            member.age.in(10, 20) // age in (10,20)
            member.age.notIn(10, 20) // age not in (10, 20)
            member.age.between(10,30) //between 10, 30
            member.age.goe(30) // age >= 30
            member.age.gt(30) // age > 30
            member.age.loe(30) // age <= 30
            member.age.lt(30) // age < 30
            member.username.like("member%") //like 검색
            member.username.contains("member") // like ‘%member%’ 검색
            member.username.startsWith("member") //like ‘member%’ 검색
         */
    }

    @Test
    public void searchAndParam() {
        /*
            search()에서 처럼 and()로 메서드 체인으로 연결할 수도 있지만,
            아래와 같이 파라미터로 추가하여 AND조건을 표현할 수도 있다. (이 경우, null값은 무시되므로 동적 쿼리를 깔끔하게 만들 수 있다는 장점이 있다)
         */
        List<Member> result1 = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetch();

        Assertions.assertThat(result1.size()).isEqualTo(1);
    }

    @Test
    public void resultFetch() {
        //List
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();
        System.out.println("### List = " + fetch);

        //단 건
        Member findMember1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        System.out.println("### 단 건 = " + findMember1);

        //처음 한 건 조회
        Member findMember2 = queryFactory
                .selectFrom(member)
                .fetchFirst();
        System.out.println("### 처음 한 건 조회 = " + findMember1);

        //페이징에서 사용
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();
        System.out.println("### 페이징에서 사용 = " + results);

        //count 쿼리로 변경
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();
        System.out.println("### count = " + count);
    }

}