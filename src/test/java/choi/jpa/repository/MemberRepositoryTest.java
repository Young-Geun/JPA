package choi.jpa.repository;

import choi.jpa.dto.MemberDto;
import choi.jpa.entity.Member;
import choi.jpa.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberB");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(saveMember.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(saveMember.getUsername());
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        Assertions.assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(10);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(10);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result) {
            System.out.println(s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto dto : result) {
            System.out.println(dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("CCC", 20);
        Member m4 = new Member("DDD", 20);
        Member m5 = new Member("EEE", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findMembersByUsername("AAA");
        for (Member member : list) {
            System.out.println("member = " + member);
        }

        Member member1 = memberRepository.findMemberByUsername("AAA");
        System.out.println("member1 = " + member1); // member1 = Member(id=1, username=AAA, age=10)

        Optional<Member> member2 = memberRepository.findOptionalByUsername("AAA");
        System.out.println("member2 = " + member2); // member2 = Optional[Member(id=1, username=AAA, age=10)]

        Member member3 = memberRepository.findMemberByUsername("AAAD");
        System.out.println("member3 = " + member3); // member3 = null

        Optional<Member> member4 = memberRepository.findOptionalByUsername("AAAD");
        System.out.println("member4 = " + member4); // member4 = Optional.empty
    }

    @Test
    public void page() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        // then
        List<Member> content = page.getContent(); // 조회된 데이터
        Assertions.assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5); // 전체 데이터 수
        Assertions.assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 번호
        Assertions.assertThat(page.isFirst()).isTrue(); // 첫번째 항목인가?
        Assertions.assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?
    }

    @Test
    public void slice() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Slice<Member> page = memberRepository.findSliceByAge(10, pageRequest);

        // then
        List<Member> content = page.getContent(); // 조회된 데이터
        Assertions.assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
        Assertions.assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        Assertions.assertThat(page.isFirst()).isTrue(); // 첫번째 항목인가?
        Assertions.assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        // em.flush();
        // em.clear();

        List<Member> members = memberRepository.findByUsername("member5");
        System.out.println("member = " + members.get(0));
        /*
            콘솔 로그 결과

            1. em.flush(); 와 em.clear();를 실행시켰을 경우
               : member = Member(id=5, username=member5, age=41)

            2. em.flush(); 와 em.clear();를 실행하지 않았을 경우
               : member = Member(id=5, username=member5, age=40)

            ==> 벌크 연산은 영속성 컨텍스트를 무시하고 실행하기 때문에,
                영속성 컨텍스트에 있는 엔티티의 상태와 DB에 엔티티 상태가 달라질 수 있다.

            ** em.flush(); 와 em.clear(); 사용 대신에
               영속성 컨텍스트 초기화 옵션[@Modifying(clearAutomatically = true)]으로 대체 가능하다. (이 옵션의 기본값은 false)
         */

        //then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        /*
            @EntityGraph 예제

            - 현재 코드에 대해서는 @EntityGraph가 적용되어있어서
              아래의 코드를 실행할 때 주석과 다르게 1번의 쿼리를 통해 데이터를 가지고 온다.
              하지만 @EntityGraph를 사용하지 않을 경우, 아래의 주석처럼 동작한다.
              이는 중간에서 볼 수 있듯이 N+1 문제를 야기할 수도 있으며,
              이를 해결하고자 fetch조인을 사용하였다.
              하지만 매 코드를 fetch조인으로 사용할 수 없으니, @EntityGraph를 사용하여 해결하였다.
         */

        //given
        //member1는 teamA와 연관
        //member2는 teamB와 연관
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
        }
        /*
            select
                member0_.member_id as member_i1_0_,
                member0_.age as age2_0_,
                member0_.team_id as team_id4_0_,
                member0_.username as username3_0_
            from
                member member0_

            ==> 사용자를 가져오는 쿼리 1번 수행
         */


        for (Member member : members) {
            System.out.println("member.team = " + member.getTeam().getName());
        }
        /*
            select
                team0_.team_id as team_id1_1_0_,
                team0_.name as name2_1_0_
            from
                team team0_
            where
                team0_.team_id=?

            ==> 팀을 가져오는 쿼리 2번 수행(위의 쿼리 2번 실행됨) --> N + 1 문제 발생
                * 팀을 실제 가져올 때(=member.getTeam().getName()) 쿼리가 수행됨

                :
         */


        List<Member> members2 = memberRepository.findMemberFetchJoin();
        for (Member member : members2) {
            System.out.println("#2 member.team = " + member.getTeam().getName());
        }
        /*
            select
                member0_.member_id as member_i1_0_0_,
                team1_.team_id as team_id1_1_1_,
                member0_.age as age2_0_0_,
                member0_.team_id as team_id4_0_0_,
                member0_.username as username3_0_0_,
                team1_.name as name2_1_1_
            from
                member member0_
            left outer join
                team team1_
                    on member0_.team_id=team1_.team_id

            ==> fetch조인을 사용하면 위의 케이스와 다르게 한 번의 조회로 가져온다.
         */
    }

    @Test
    public void queryHint() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");
        em.flush(); //Update Query 실행X
    }

    @Test
    public void lock() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findLockByUsername("member1");
        /*
            select
                member0_.member_id as member_i1_0_,
                member0_.age as age2_0_,
                member0_.team_id as team_id4_0_,
                member0_.username as username3_0_
            from
                member member0_
            where
                member0_.username=? for update


           ===> 'for update' : lock 걸리는 것을 확인할 수 있다.
         */
    }

    @Test
    public void callCustom() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));

        List<Member> members = memberRepository.findMemberCustom();
        System.out.println("members = " + members);
    }

}