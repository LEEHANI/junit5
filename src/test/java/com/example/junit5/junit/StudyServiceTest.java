package com.example.junit5.junit;

import com.example.junit5.domain.Member;
import com.example.junit5.domain.Study;
import com.example.junit5.member.MemberService;
import com.example.junit5.study.StudyRepository;
import com.example.junit5.study.StudyService;
import com.example.junit5.domain.StudyStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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

//        when(memberService.findById(1L)).thenReturn(Optional.of(member));

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

    @Test
    void stubbing_연습() {
        StudyService studyService  = new StudyService(memberService, studyRepository);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("e@email.com");

        Study study = new Study(10, "test");
        study.setOwnerId(member.getId());

        // memberService 객체에 findById 메소드를 1L 값으로 호출하면 Optional.of(member) 객체를 리턴하도록 Stubbing
        when(memberService.findById(1L)).thenReturn(Optional.of(member));

        studyService.createNewStudy(1L, study);

        assertNotNull(study.getOwnerId());
        assertEquals(member.getId(), study.getOwnerId());

    }

    @Test
    void 호출됐는지_확인() {
        verify(memberService, never()).validate(any());
    }

    @DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
    @Test
    void openStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "더 자바, 테스트");

        given(studyRepository.save(study)).willReturn(study);

        // When
        Study result = studyService.openStudy(study);

        // study의 status가 OPENED로 변경됐는지 확인
        Assertions.assertEquals(StudyStatus.OPENED, result.getStatus());
        // study의 openedDataTime이 null이 아닌지 확인
        assertNotNull(study.getOpenedDateTime());
        // memberService의 notify(study)가 호출 됐는지 확인.
        then(memberService).should(times(1)).notify(study);
    }
}