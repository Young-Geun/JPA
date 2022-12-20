package choi.jpa.querydsl;

import choi.jpa.querydsl.dto.MemberDto;
import choi.jpa.querydsl.dto.UserDto;
import choi.jpa.querydsl.entity.Member;
import choi.jpa.querydsl.entity.QMember;
import choi.jpa.querydsl.entity.QTeam;
import choi.jpa.querydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static choi.jpa.querydsl.entity.QMember.member;
import static choi.jpa.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    @PersistenceUnit
    EntityManagerFactory emf;

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

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        Assertions.assertThat(member5.getUsername()).isEqualTo("member5");
        Assertions.assertThat(member6.getUsername()).isEqualTo("member6");
        Assertions.assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) //0부터 시작(zero index)
                .limit(2) //최대 2건 조회
                .fetch();

        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() {
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        Assertions.assertThat(queryResults.getTotal()).isEqualTo(4);
        Assertions.assertThat(queryResults.getLimit()).isEqualTo(2);
        Assertions.assertThat(queryResults.getOffset()).isEqualTo(1);
        Assertions.assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    /**
     * JPQL
     * select
     * COUNT(m), //회원수
     * SUM(m.age), //나이 합
     * AVG(m.age), //평균 나이
     * MAX(m.age), //최대 나이
     * MIN(m.age) //최소 나이
     * from Member m
     */
    @Test
    public void aggregation() throws Exception {
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);

        Assertions.assertThat(tuple.get(member.count())).isEqualTo(4);
        Assertions.assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        Assertions.assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        Assertions.assertThat(tuple.get(member.age.max())).isEqualTo(40);
        Assertions.assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라.
     */
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        Assertions.assertThat(teamA.get(team.name)).isEqualTo("teamA");
        Assertions.assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        Assertions.assertThat(teamB.get(team.name)).isEqualTo("teamB");
        Assertions.assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    public void join() throws Exception {
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * 세타 조인(연관관계가 없는 필드로 조인)
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    public void theta_join() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /**
     * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='teamA'
     */
    @Test
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
        /*
            tuple = [Member(id=3, username=member1, age=10), Team(id=1, name=teamA)]
            tuple = [Member(id=4, username=member2, age=20), Team(id=1, name=teamA)]
            tuple = [Member(id=5, username=member3, age=30), null]
            tuple = [Member(id=6, username=member4, age=40), null]
         */
    }

    /**
     * 2. 연관관계 없는 엔티티 외부 조인
     * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
     * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
     */
    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("t=" + tuple);
        }
        /*
            t=[Member(id=3, username=member1, age=10), null]
            t=[Member(id=4, username=member2, age=20), null]
            t=[Member(id=5, username=member3, age=30), null]
            t=[Member(id=6, username=member4, age=40), null]
            t=[Member(id=7, username=teamA, age=0), Team(id=1, name=teamA)]
            t=[Member(id=8, username=teamB, age=0), Team(id=2, name=teamB)]
         */
    }

    @Test
    public void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        /*
            select
                member0_.member_id as member_i1_1_,
                member0_.age as age2_1_,
                member0_.team_id as team_id4_1_,
                member0_.username as username3_1_
            from
                member member0_
            where
                member0_.username=?
         */

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        Assertions.assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    @Test
    public void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        /*
            select
                member0_.member_id as member_i1_1_0_,
                team1_.team_id as team_id1_2_1_,
                member0_.age as age2_1_0_,
                member0_.team_id as team_id4_1_0_,
                member0_.username as username3_1_0_,
                team1_.name as name2_2_1_
            from
                member member0_
            inner join
                team team1_
                    on member0_.team_id=team1_.team_id
            where
                member0_.username=?
         */

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        Assertions.assertThat(loaded).as("페치 조인 적용").isTrue();
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    public void subQuery() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions.select(memberSub.age.max()).from(memberSub)
                ))
                .fetch();

        Assertions.assertThat(result).extracting("age").containsExactly(40);
    }

    /**
     * 나이가 평균 나이 이상인 회원
     */
    @Test
    public void subQueryGoe() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions.select(memberSub.age.avg()).from(memberSub)
                ))
                .fetch();

        Assertions.assertThat(result).extracting("age").containsExactly(30, 40);
    }

    /**
     * 서브쿼리 여러 건 처리, in 사용
     */
    @Test
    public void subQueryIn() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions.select(memberSub.age).from(memberSub).where(memberSub.age.gt(10))
                ))
                .fetch();

        Assertions.assertThat(result).extracting("age").containsExactly(20, 30, 40);
    }

    /**
     * select 절에 subquery
     */
    @Test
    public void selectSubquery() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Tuple> fetch = queryFactory
                .select(member.username,
                        JPAExpressions.select(memberSub.age.avg()).from(memberSub)
                ).from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("username = " + tuple.get(member.username));
            System.out.println("age = " + tuple.get(JPAExpressions.select(memberSub.age.avg()).from(memberSub)));
        }
    }

    @Test
    public void basicCase() throws Exception {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String age : result) {
            System.out.println("age = " + age);
        }
        /*
            age = 열살
            age = 스무살
            age = 기타
            age = 기타
         */
    }

    @Test
    public void complexCase() throws Exception {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String age : result) {
            System.out.println("age = " + age);
        }
        /*
            age = 0~20살
            age = 0~20살
            age = 21~30살
            age = 기타
         */
    }

    /**
     * orderBy에서 Case 문 함께 사용하기 예제
     *
     * 출력조건
     *      1. 0 ~ 30살이 아닌 회원을 가장 먼저 출력
     *      2. 0 ~ 20살 회원 출력
     *      3. 21 ~ 30살 회원 출력
     * @throws Exception
     */
    @Test
    public void orderbyCase() throws Exception {
        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then(2)
                .when(member.age.between(21, 30)).then(1)
                .otherwise(3);

        List<Tuple> result = queryFactory
                .select(member.username, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc())
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            Integer rank = tuple.get(rankPath);
            System.out.println("username = " + username + " age = " + age + " rank = " + rank);
        }
        /*
            username = member4 age = 40 rank = 3
            username = member1 age = 10 rank = 2
            username = member2 age = 20 rank = 2
            username = member3 age = 30 rank = 1
         */
    }

    @Test
    public void constant() throws Exception {
        Tuple result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetchFirst();

        System.out.println("result = " + result);
        /*
            result = [member1, A]
         */
    }

    @Test
    public void concat() throws Exception {
        String result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        System.out.println("result = " + result);
        /*
            result = member1_10
         */
    }

    @Test
    public void simpleProjection() throws Exception {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("username = " + s);
        }
        /*
            username = member1
            username = member2
            username = member3
            username = member4
         */
    }

    @Test
    public void tupleProjection() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username=" + username + ", age=" + age);
        }
        /*
            username=member1, age=10
            username=member2, age=20
            username=member3, age=30
            username=member4, age=40
         */
    }

    @Test
    public void findDtoByJPQL() throws Exception {
        List<MemberDto> result = em.createQuery("select new choi.jpa.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class).getResultList();

        for (MemberDto dto : result) {
            System.out.println("dto = " + dto);
        }
        /*
            dto = MemberDto(username=member1, age=10)
            dto = MemberDto(username=member2, age=20)
            dto = MemberDto(username=member3, age=30)
            dto = MemberDto(username=member4, age=40)
         */
    }

    @Test
    public void findDtoBySetter() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto dto : result) {
            System.out.println("dto = " + dto);
        }
        /*
            dto = MemberDto(username=member1, age=10)
            dto = MemberDto(username=member2, age=20)
            dto = MemberDto(username=member3, age=30)
            dto = MemberDto(username=member4, age=40)
         */
    }

    @Test
    public void findDtoByField() throws Exception {
        // fields : getter, setter 필요없음
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto dto : result) {
            System.out.println("dto = " + dto);
        }
        /*
            dto = MemberDto(username=member1, age=10)
            dto = MemberDto(username=member2, age=20)
            dto = MemberDto(username=member3, age=30)
            dto = MemberDto(username=member4, age=40)
         */
    }

    @Test
    public void findUserDto() throws Exception {
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        member.age))
                .from(member)
                .fetch();

        for (UserDto dto : result) {
            System.out.println("dto = " + dto);
        }
        /*
            dto = UserDto(name=member1, age=10)
            dto = UserDto(name=member2, age=20)
            dto = UserDto(name=member3, age=30)
            dto = UserDto(name=member4, age=40)
         */
    }

    @Test
    public void findUserDto2() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                                member.username.as("name"),
                                ExpressionUtils.as(
                                        JPAExpressions
                                                .select(memberSub.age.max())
                                                .from(memberSub), "age")))
                .from(member)
                .fetch();

        for (UserDto dto : result) {
            System.out.println("dto = " + dto);
        }
        /*
            dto = UserDto(name=member1, age=40)
            dto = UserDto(name=member2, age=40)
            dto = UserDto(name=member3, age=40)
            dto = UserDto(name=member4, age=40)
         */
    }

    @Test
    public void findDtoByConstructor() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto dto : result) {
            System.out.println("dto = " + dto);
        }
        /*
            dto = MemberDto(username=member1, age=10)
            dto = MemberDto(username=member2, age=20)
            dto = MemberDto(username=member3, age=30)
            dto = MemberDto(username=member4, age=40)
         */
    }

}