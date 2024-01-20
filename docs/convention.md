# Convention

## Table of Contents 📋

1. [Naming & Code](#naming--code)
2. [Test](#test)
3. [Git Message](#git-message)
4. [Branch](#branch)
5. [Versioning](#versioning)
6. [Etc](#etc)

## 1️⃣ Naming & Code<a id="naming--code"></a>

### Service

- 검증 - `validateXXX()`
- 단건조회 - `findXXX()`
- 다건조회 - `findPageXXX()`
- 메소드명과 발생할 수 있는 모든 예외 케이스 주석으로 작성

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Service {
    private final Repository repository;

    /**
     * 메소드명 |
     * 400(ERROR_ONE)
     * 409(ERROR_TWO)
     * @param request 매개변수명
     * @return 반환객체명
     */
    public Response doSomething(Request request) {
        if (case1)
            throw CustomException(ERROR_ONE);
        if (case2)
            throw CustomException(ERROR_TWO);
    }
}
```

### Repository

- 단건조회 - `findXXX()`
- 다건조회 - `findPageXXX()`

### Domain

- setter 사용 금지
- 메소드명과 발생할 수 있는 모든 예외 케이스 주석으로 작성

```java
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "domain_id")
    private Long id;

    /**
     * 메소드명 |
     * 400(ERROR_ONE)
     * 409(ERROR_TWO)
     * @param object 매개변수명
     */
    public doSomething(Object object) {
        if (case1)
            throw CustomException(ERROR_ONE);
        if (case2)
            throw CustomException(ERROR_TWO);
    }
}
```

## 2️⃣ Test<a id="test"></a>

정상 케이스 아래에 예외 케이스 작성

### ControllerTest

- 모든 정상 케이스 작성
- 모든 예외 케이스 작성

##### Example

- 정상
  - `givenValid_whenRegister_thenReturn200` - `회원 가입을 하면 201을 반환한다`
- 예외
  - `givenBlankUsername_whenRegister_thenThrow400` - `아이디 없이 회원 가입하면 400을 던진다`
  - `givenBlankPassword_whenRegister_thenThrow400` - `비밀번호 없이 회원 가입하면 400을 던진다`

```java
@ActiveProfile("test")
@WebMvcTest(Controller.class)
@AutoConfigureMockMvc(addFilters = false)
class ControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private Service service;
    
    @Test
    @DisplayName("XXX을 하면 2XX을 반환한다")
    void givenValid_whenXXX_thenReturn200() { }
    
    @Test
    @DisplayName("XXX로 XXX을 하면 4XX을 반환한다")
    void givenXXX_whenXXX_thenThrow4XX() { }
}
```

### ServiceTest

- 모든 정상 케이스 작성
- 모든 예외 케이스 작성

##### Example

- 정상
  - `givenValid_whenRegister_thenReturn` - `회원 가입을 하면 정상 작동한다`
- 예외
  - `givenExistingUsername_whenRegister_thenThrow` - `존재하는 회원 아이디로 회원 가입을 하면 예외가 발생한다`
  - `givenUnequalPasswordAndPaswordReEntered_whenRegister_thenThrow` - `동일하지 않은 비밀번호와 비밀번호 재입력으로 회원 가입을 하면 예외가 발생한다`

```java
@ActiveProfile("test")
@SpringBootTest
@Transactional
class ServiceTest {
    @Autowired private Service service;
    @Autowired private Repository repository;
    
    @Test
    @DisplayName("XXX을 하면 정상 작동한다")
    void givenValid_whenXXX_thenReturn() { }

    @Test
    @DisplayName("XXX로 XXX을 하면 예외가 발생한다")
    void givenXXX_whenXXX_thenThrow() { }
}
```

### RepositoryTest

- 모든 정상 케이스 작성
- 데이터베이스와 관련된 예외 케이스만 작성

##### Example

- 정상
  - `givenValid_whenSave_thenReturn` - `회원을 저장하면 정상 작동한다`
- 예외 
  - `givenExistingUsername_whenSave_thenThrow` - `존재하는 회원 아이디로 회원 저장을 하면 예외가 발생한다`

```java
@ActiveProfile("test")
@SpringBootTest
@Transactional
class RepositoryTest {
    @Autowired private Repository repository;

    @Test
    @DisplayName("XXX을 하면 정상 작동한다")
    void givenValid_whenXXX_thenReturn() { }
  
    @Test
    @DisplayName("XXX로 XXX을 하면 예외가 발생한다")
    void givenXXX_whenXXX_thenThrowXXX() { }
}
```

### DomainTest

- 모든 정상 케이스 작성
- 모든 예외 케이스 작성

```java
class DomainTest {

}
```

## 3️⃣ Git Message<a id="git-message"></a>

### Commit Type

- FEAT: 새로운 기능 추가 
- FIX: 버그 수정 
- DOCS: 문서 수정 
- STYLE: 코드 포맷팅 또는 코드 변경이 없는 경우 
- REFACTOR: 코드 리펙토링 
- TEST: 테스트 코드, 리펙토링 테스트 코드 추가 
- CHORE: 빌드 업무 수정, 패키지 매니저 수정

### Subject

- 너무 길게 작성하지 않는다
- 맨 뒤에 Issue 번호를 함께 작성한다

##### Example

```
git commit -m "[FEAT] 새로운 기능 추가 #1"
```

## 4️⃣ Branch<a id="branch"></a>

### Strategy

[Git-flow](https://techblog.woowahan.com/2553/)

##### Example

- master
- develop
- feat/1
- feat/2

## 5️⃣ Versioning<a id="versioning"></a>

[Semantic Versioning](https://semver.org/lang/ko/)

## 6️⃣ Etc<a id="etc"></a>

- Intellij 상에 수직선(visual guide) 내로 코드 작성