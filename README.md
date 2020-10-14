https://docs.google.com/document/d/1j6mU7Q5gng1mAJZUKUVya4Rs0Jvn5wn_bCUp3rq41nQ/edit


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
- @RepeatedTest 
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

## 테스트 인스턴스 
- 기존에는 메서드당 클래스를 만들었음. 서로간의 의존성이 없어야 한다. 
- `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` 사용하면 클래스당 1개의 인스턴스를 만들도록 할 수 있음
  + 이 경우, @BeforeAll, @AfterAll이 static 아니여도 됨
   
## 테스트 순서
- 특정 정해져있는 로직대로 테스트 실행되지만, 순서를 알 수 없으니 이를 의존해서는 안됌
- 경우에 따라, 특정 순서대로 테스트를 실행하고 싶을 때도 있다. 
  + `@TestInstance(TestInstance.Lifecycle.PER_CLASS)`와 `@TestMethodOrder`를 사용할 수 있다.
    + Alphanumeric, OrderAnnotation, Random    

## 테스트 properties 설정 
- test/resources 생성 - file - Project structure - module - 해당폴더 Test Resources로 설정

## 확장 모델 
- JUnit 4의 확장 모델은 @RunWith(Runner), TestRule, MethodRule.
- JUnit 5의 확장 모델은 단 하나, Extension.

- 확장팩 등록 방법
  + 선언적인 등록 `@ExtendWith`
- 프로그래밍 등록 @RegisterExtension
  + 자동 등록 자바 ServiceLoader 이용

- 확장팩 만드는 방법
  + 테스트 실행 조건
  + 테스트 인스턴스 팩토리
  + 테스트 인스턴스 후-처리기
  + 테스트 매개변수 리졸버
  + 테스트 라이프사이클 콜백
  + 예외 처리

## Junit4 -> Junit5 마이그레이션 
- junit-vintage-engine을 의존성으로 추가하면, JUnit 5의 junit-platform으로 JUnit 3과 4로 작성된 테스트를 실행할 수 있다.
- @Ignore -> @Disable
- @Before -> @BeforeEach
- @BeforeClass -> @BeforeAll
- @After -> @AfterEach
- @After -> @AfterEach
- @AfterClass -> @AfterAll
- @Category(Class) -> @Tag(String)


## Mockito
- Mock 객체를 쉽게 만들고 관리하하고 검증할 수 있는 방법 
- 모든 의존성을 mock해야만 단위 테스트라고 대부분이 생각함 
- 클래스의 단위가 아닌 행위의 단위라고도 생각할 수 있음 => 팀마다 정하면 될듯 
- spring-boot-starter-test가 알아서 mockito 추가해줌 