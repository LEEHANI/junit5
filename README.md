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

## Mock 객체 만들기 
- 기본 
  + `MemberService memberService = Mockito.mock(MemberService.class)`
- @Mock, @ExtendWith 사용 
  + ```
    @ExtendWith(MockitoExtension.class)
    class StudyServiceTest {
    
        @Mock MemberService memberService;
        @Mock StudyRepository studyRepository;
    ```
- 메서드에서만 @Mock 사용
  + ```
    @ExtendWith(MockitoExtension.class)
    class StudyServiceTest {
        
        @Test
        void createStudyService(@Mock MemberService memberService,
                                @Mock StudyRepository studyRepository) {
            StudyService studyService = new StudyService(memberService, studyRepository);
            assertNotNull(studyService);
        }
    
    }
    ```

## Mock 객체 Stubbing
- 기본
  + `when(memberService.findById(1L)).thenReturn(Optional.of(member));`
- 예외 발생 시키기 
  + `when(memberService.findById(1L)).thenThrow(IllegalArgumentException.class);`
- void 일때 예외 발생 시키기 
  + `doThrow(new IllegalArgumentException()).when(memberService).validate(1L);`
- 동일한 메서드 호출시 마다 다른 행동
  + ```
    when(memberService.findById(1L))
            .thenReturn(Optional.of(member))
            .thenThrow(new RuntimeException())
            .thenReturn(Optional.empty());
    ```  
    
## Mock 객체 확인 
- verify를 사용하면 몇 번 호출됐는지, 어떤 순서대로 호출했는지, 특정 시간 이내에 호출됐는지 등을 알 수 있다. 
- `verify(memberService, never()).validate(any())`

## Mockito BDD 스타일 API 
- BDD(behavior driven development): 애플리케이션이 어떻게 “행동”해야 하는지에 대한 공통된 이해를 구성하는 방법으로, TDD에서 창안했다.
- //given //when //then 
- Mockito를 BDDMockito 클래스로 BDD 스타일로 바꿀 수 있다. 
- Mockito.when -> BDDMockito.given
- Mockito.verify -> BDDMockito.then
- https://www.baeldung.com/bdd-mockito

## Testcontainers 소개 
- https://www.testcontainers.org/modules/databases/jdbc/
- 인메모리 디비를 사용하면 빠르지만, 인메모리 디비와 개발 디비가 다르면 차이점이 발생될 수 있다. 가령 isolation, 쿼리 등
- Docker를 이용하면 개발과 동일한 테스트 환경을 만들기 편하다.
- 도커를 사용하므로, DB 설정을 하거나 별도의 프로그램 또는 스크립트를 실행할 필요가 없다.  
- 도커를 띄우므로 테스트가 느려질 수 있다.

## Testcontainers 설치
- gradle
  ```
  implementation 'org.postgresql:postgresql'
  testCompile "org.testcontainers:postgresql:1.15.0-rc2"
  testImplementation "org.testcontainers:junit-jupiter:1.12.4"
  ```
- properties
  ```
  spring.datasource.url=jdbc:tc:postgresql:///studytest
  spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
  
  spring.jpa.hibernate.ddl-auto=create-drop
  ```
- ```
  static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
              .withDatabaseName("studytest");
  
  @BeforeAll
  static void beforeAll() {
      postgreSQLContainer.start();
      System.out.println(postgreSQLContainer.getJdbcUrl());
  }

  @AfterAll
  static void afterAll() {
      postgreSQLContainer.stop();
  }
  ```
- @Testcontainers로 @Container를 사용한 필드를 찾아서 컨테이너 라이프사이클 관련 메소드를 실행해준다
- @Container 모든 테스트마다 컨테이너를 재시작한다.
  + ```
    @Container
    PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
                .withDatabaseName("studytest");
    ```
  + 스태틱 필드에 사용하면 클래스 내부 모든 테스트에서 동일한 컨테이너를 재사용한다.
  + ```
    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
                .withDatabaseName("studytest");
    ```  
    
## Testcontainers 도커 Compose 사용하기 
- docker-compose는 도커 컨데이너를 여러 개 동시에 띄울 때, 서로 간의 의존성 및 네트워크 등을 설정할 수 있는 방법
- ```
  @Container
  static DockerComposeContainer composeContainer = new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"));
  ```
- 테스트에 사용하는 `src/test/resources/docker-compose`는 랜덤하게 가용한 포트를 사용해야 하므로 port mapping을 하지 않는 게 좋다
  + ```
    ports:
    - 5432
    ```

## JMeter
- 성능 측정 및 부하(load) 테스트 기능을 제공하는 오픈 소스 자바 애플리케이션 
- https://jmeter.apache.org/download_jmeter.cgi
- 주요 기능
  + Thread Group: 한 쓰레드 당 유저 한명
  + Sampler: 어떤 유저가 해야 하는 액션
  + Listener: 응답을 받았을 할 일 (리포팅, 검증, 그래프 그리기 등)
  + Configuration: Sampler 또는 Listener가 사용할 설정 값 (쿠키, JDBC 커넥션 등)
  + Assertion: 응답이 성공적인지 확인하는 방법 (응답 코드, 본문 내용 등)
- 로컬에서 하는 테스트는 웹 서버, jmeter를 동시에 실행하고 있으므로 정확하지 않다.
- 첫 번째 샘플데이터의 시간이 오래걸리는 이유는, 서블릿을 최초로 만들기 때문이다. 
![JMeter_view_results](images/JMeter_view_results.png) 
 
## Chaos Monkey
- 운영 중 이슈를 테스트 하는 툴. `https://codecentric.github.io/chaos-monkey-spring-boot/2.1.1/#_customize_watcher`
  + 공격대상(Watcher)
    + @RestController, @Controller, @Service, @Repository, @Component
  + 공격 유형(Assaults)
    + 응답 지연 (Latency Assault), 예외 발생 (Exception Assault), 애플리케이션 종료 (AppKiller Assault), 메모리 누수 (Memory Assault)
- 스프링 부트에서 카오스 멍키를 손쉽게 적용해 볼 수 있다
  + gradle
    ```
    compile group: 'de.codecentric', name: 'chaos-monkey-spring-boot', version: '2.3.0'
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-actuator', version: '2.4.2'
    ```
    properties
    ```
    spring.profiles.active=chaos-monkey
    
    management.endpoint.chaosmonkey.enabled=true
    management.endpoints.web.exposure.include=health,info,chaosmonkey
    
    chaos.monkey.watcher.repository=true
    ```
- JMeter를 실행 후 Chaos Monkey를 활성화시키면 테스트를 진행해 볼 수 있다.

## ArchUnit
- 애플리케이션의 아키텍처를 테스트 할 수 있는 오픈 소스 라이브러리로, 패키지, 클래스, 레이어, 슬라이스 간의 의존성을 확인할 수 있는 기능을 제공한다.
- Study -> Member, Study, Member -> Domain   