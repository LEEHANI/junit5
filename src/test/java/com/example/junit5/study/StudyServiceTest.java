package com.example.junit5.study;

import com.example.junit5.domain.Member;
import com.example.junit5.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    MemberService memberService;

    @Mock
    StudyRepository studyRepository;

    @Test
    void createStudy() {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("e@email.com");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);
    }

    @Test
    void 리턴타입이_void() {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("e@email.com");

        doThrow(new IllegalArgumentException()).when(memberService).validate(1L);
        assertThrows(IllegalArgumentException.class, () -> memberService.validate(1L));
    }

    @Test
    void 동일한_메서드_호출시마다_다른_행동() {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("e@email.com");

        when(memberService.findById(1L))
                .thenReturn(Optional.of(member))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.empty());

        Optional<Member> byId = memberService.findById(1L);
        assertEquals("e@email.com", byId.get().getEmail());

        assertThrows(RuntimeException.class, () -> {
            memberService.findById(1L);
        });

        assertEquals(Optional.empty(), memberService.findById(1L));
    }
}