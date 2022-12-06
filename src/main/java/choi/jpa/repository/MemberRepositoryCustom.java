package choi.jpa.repository;

import choi.jpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findMemberCustom();

}
