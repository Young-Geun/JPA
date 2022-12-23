package choi.jpa.querydsl.repository;

import choi.jpa.querydsl.dto.MemberSearchCondition;
import choi.jpa.querydsl.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchCondition condition);

}
