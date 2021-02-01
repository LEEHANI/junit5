package com.example.junit5.study;

import com.example.junit5.domain.Member;
import com.example.junit5.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Testcontainers
class DockerComposeTest {

    @Mock
    MemberService memberService;

    @Autowired StudyRepository studyRepository;

    @Container
    static DockerComposeContainer composeContainer = new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"));

    @BeforeEach
    void beforeEach() {
        studyRepository.deleteAll();
    }

    @Test
    void createStudy() {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("e@email.com");

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);
    }
}