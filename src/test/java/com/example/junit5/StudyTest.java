package com.example.junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class StudyTest {



    @DisplayName("스터디 만들기")
    @FastTest
//    @Test
//    @Tag("fast")
    void create_new_study() {
        Study study = new Study(10);

        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(),
                        () -> "스터디를 처음 만들면 상태값이 DRAFT여야 한다"),
                () -> assertTrue(study.getLimit()>0, "스터디 최대 참 가능 인원은 0보다 커야한다")
        );
    }

    @DisplayName("스터디 만들기 throw test")
    @FastTest
//    @Test
//    @Tag("fast")
    void create_study_throw() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit는 0보다 커야 한다", exception.getMessage());
    }

    @DisplayName("스터디 만들기 throw test")
    @SlowTest
//    @Test
//    @Tag("slow")
    void create_study_timeout() {
        assertTimeout(Duration.ofMillis(1000), () -> {
            new Study(10);
            Thread.sleep(300);
        });
    }

    @Test
    void assume_test() {
        String test_env = "LOCAL";
        System.out.println(test_env);
        assumeTrue("LOCAL".equalsIgnoreCase(test_env));

        Study study = new Study(10);
    }

    @Test
    @DisplayName("생성2")
    void create2() {
        System.out.println("create2");
    }

    @DisplayName("스터디 만들기")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetition}")
    void repeatTest(RepetitionInfo repetitionInfo) {
        System.out.println("test " + repetitionInfo.getCurrentRepetition() + "/" +
                       repetitionInfo.getTotalRepetitions() );
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(strings = {"날씨가", "많이", "추워지고", "있네요."})
    void parameterizedTest(String message) {
        System.out.println(message);
    }

    @Test
    @ValueSource(ints = {10,20,40})
    void parameterizedInt(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter {

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("after all");
    }

    @AfterEach
    void afterEach() {
        System.out.println("after each");
    }

    @BeforeEach
     void beforeEach() {
        System.out.println("Before each");
    }
}
