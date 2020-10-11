# Junit5

- java 8이상, Spring Boot 2.2+
- public 안해도됌

## Junit5 실행 순서
- @BeforeAll
- @BeforeEach
- @Test
- @AfterEach
- @AfterAll

## 테스트 이름 표기하기
- @DisplayNameGeneration
  + Method와 Class 레퍼런스를 사용해서 테스트 이름 표기
- @DisplayName
  + @DisplayNameGeneration보다 우선순위 높다

## Assertion 
- assertEqulas(expected, actual)
  + assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 DRAFT여야 한다");
  + 성능을 신경쓴다면, 람다식을 쓰자. 람다는 실패했을 때만 연산을 하기 때문이다. 
  + assertEquals(StudyStatus.DRAFT, study.getStatus(), ()->"스터디를 처음 만들면 상태값이 DRAFT여야 한다");
- assertNotNull(actual)
- assertTrue(boolean)
- assertAll(executables..)
  + ```
    assertAll(
            () -> assertNotNull(study),
            () -> assertEquals(StudyStatus.DRAFT, study.getStatus(),
                    () -> "스터디를 처음 만들면 상태값이 DRAFT여야 한다"),
            () -> assertTrue(study.getLimit()>0, "스터디 최대 참 가능 인원은 0보다 커야한다")
    );
    ```
- assertThrows(expectedType, executable)
- assertTimeout(duration, executable)
- AssertJ, Hemcrest, Truth 등의 라이브러리도 있다 

## 조건에 따라 테스트 실행
- assumeTrue(조건)
- assumeTrue(조건, 테스트)
- @EnabledOnJre(특정 jre)
- @EnabledOnOs(특정 OS)

## Tag
- @Tag로 원하는 테스트 그룹을 만들어서 실행할 수 있다 
- 어노테이션을 조합하여 커스텀 태그를 만들 수 있다. 
```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Tag("fast")
@Test
public @interface FastTest {
}
```
```
@FastTest
@DisplayName("스터디 만들기 fast")
void create_new_study() {

@SlowTest
@DisplayName("스터디 만들기 slow")
void create_new_study_again() {
```

## 테스트 반복하기
- @Repeatedest 
  + 반복 횟수와 테스트 이름을 설정할 수 있다.
  ```
  @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetition}")
  void repeatTest(RepetitionInfo repetitionInfo) {
      System.out.println("test " + repetitionInfo.getCurrentRepetition() + "/" +
                     repetitionInfo.getTotalRepetitions() );
  }
  ``` 
- @ParameterizedTest 
  + 테스트에 여러 매개변수를 대입해가며 반복 실행 
  ```
  @DisplayName("스터디 만들기")
  @ParameterizedTest(name = "{index} {displayName} message={0}")
  @ValueSource(strings = {"날씨가", "많이", "추워지고", "있네요."})
  void parameterizedTest(String message) {
      System.out.println(message);
  }
  ```
- SimpleArgumentConverter 1개의 인자 값 명시적 변환
  + @ValueSource를 custom convert 해줌 
  ```
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
  ```
- ArgumentsAccessor 여러 인자 값을 조합해서 만듦
  ```
  @DisplayName("스터디 만들기")
  @ParameterizedTest(name = "{index} {displayName} message={0}")
  @CsvSource({"10, '자바 스터디'", "20, 스프링"})
  void parameterizedTest(@AggregateWith(StudyAggregator.class) Study study) {
      System.out.println(study);
  }

  static class StudyAggregator implements ArgumentsAggregator {
      @Override
      public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
          return new Study(accessor.getInteger(0), accessor.getString(1));
      }
  }
  ```   
     


