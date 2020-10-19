package com.example.junit5.member;

import com.example.junit5.domain.Member;
import com.example.junit5.domain.Study;

import java.util.Optional;

public interface MemberService {
    void validate(Long memberId) throws InvalidMemberException;

    Optional<Member> findById(Long memberId) throws MemberNotFoundException;

    void notify(Study newstudy);
}