# API 문서
요청의 객체와 응답의 종류별로 HTTP Method, URI, HTTP 코드, 응답 코드, 응답 메세지가 종류별로 정리되어 있습니다.
모든 응답의 Body에는 응답 코드, 응답 메세지, 응답 객체로 응답합니다.

---

### 표준 응답의 Body ✅

| 응답 코드 | 응답 메세지 | Dto |
|-------|--------|-----|

### 표준 에러 응답의 Body 🚫

| 응답 코드 | 응답 메세지 |
|-------|--------|


## 문서 템플릿 📝
### 1️⃣ 요청 

| HTTP Method | URI |
|-------------|-----|
|             |     |

| 타입  | 변수명 |
|-----|-----|
|     |     |

### 2️⃣ 응답

| HTTP 코드 | 응답 코드 | 응답 메세지 |
|---------|-------|--------|
|         |       |        |

| 타입  | 변수명 |
|-----|-----|
|     |     |

---

## Contact 생성 🛠
### 1️⃣ 요청

| HTTP Method | URI          |
|-------------|--------------|
| GET         | /contact/new |

| 타입     | 변수명   |
|--------|-------|
| String | email |

### 2️⃣ 응답

| HTTP 코드 | 응답 코드                 | 응답 메세지           |
|---------|-----------------------|------------------|
| 201     | CREATED               | 이메일 중복여부 확인 완료   |
| 400     | FIELD_REQUIRED        | 이메일 입력은 필수입니다    |
| 400     | EMAIL_FORMAT_INVALID  | 올바른 이메일 형식이 아닙니다 |
| 409     | CONFLICT              | 이미 가입된 이메일입니다    |
| 500     | INTERNAL_SERVER_ERROR | 서버 에러가 발생했습니다    |

| 타입            | 변수명              |
|---------------|------------------|
| String        | id               |
| String        | email            |
| String        | verificationCode |
| Boolean       | isVerified       |
| Boolean       | isRegistered     |
| LocalDateTime | createdDate      |
| LocalDateTime | modifiedDate     |
| String        | schemaVersion    |


## Contact 인증번호 확인 🛠
### 1️⃣ 요청

| HTTP Method | URI                   |
|-------------|-----------------------|
| PATCH       | /contact/verification |

| 타입     | 변수명              |
|--------|------------------|
| String | email            |
| String | verificationCode |

### 2️⃣ 응답

| HTTP 코드 | 응답 코드                 | 응답 메세지               |
|---------|-----------------------|----------------------|
| 200     | OK                    | 이메일 인증 완료            |
| 400     | FIELD_REQUIRED        | 이메일 또는 인증번호를 입력해 주세요 |
| 400     | EMAIL_FORMAT_INVALID  | 올바른 이메일 형식이 아닙니다     |
| 401     | UNAUTHORIZED          | 인증되지 않은 이메일입니다       |
| 401     | UNAUTHORIZED          | 인증번호가 틀렸습니다          |
| 500     | INTERNAL_SERVER_ERROR | 서버 에러가 발생했습니다        |

| 타입            | 변수명              |
|---------------|------------------|
| String        | id               |
| String        | email            |
| String        | verificationCode |
| Boolean       | isVerified       |
| Boolean       | isRegistered     |
| LocalDateTime | createdDate      |
| LocalDateTime | modifiedDate     |
| String        | schemaVersion    |


## User 중복 여부 확인 🛠
### 1️⃣ 요청

| HTTP Method | URI             |
|-------------|-----------------|
| POST        | /user/duplicate |

| 타입     | 변수명      |
|--------|----------|
| String | username |

### 2️⃣ 응답

| HTTP 코드 | 응답 코드                   | 응답 메세지                    |
|---------|-------------------------|---------------------------|
| 200     | OK                      | 아이디 중복 확인 완료              |
| 400     | FIELD_REQUIRED          | 아이디를 입력해 주세요              |
| 400     | USERNAME_LENGTH_INVALID | 아이디는 5~15자만 가능합니다         |
| 400     | USERNAME_FORMAT_INVALID | 아이디 형식은 영문과 숫자의 조합만 가능합니다 |
| 409     | CONFLICT                | 이미 사용중인 아이디입니다            |
| 500     | INTERNAL_SERVER_ERROR   | 서버 에러가 발생했습니다             |


## User 가입 🛠
### 1️⃣ 요청

| HTTP Method | URI       |
|-------------|-----------|
| POST        | /user/new |

| 타입        | 변수명       |
|-----------|-----------|
| String    | username  |
| String    | password  |
| String    | legalName |
| String    | nickname  |
| Character | gender    |
| LocalDate | birthdate |
| String    | email     |

### 2️⃣ 응답

| HTTP 코드 | 응답 코드                    | 응답 메세지                    |
|---------|--------------------------|---------------------------|
| 201     | CREATED                  | 회원가입 완료                   |
| 400     | FIELD_REQUIRED           | 필수 정보를 입력해 주세요            |
| 400     | USERNAME_LENGTH_INVALID  | 아이디는 5~15자만 가능합니다         |
| 400     | USERNAME_FORMAT_INVALID  | 아이디 형식은 영문과 숫자의 조합만 가능합니다 |
| 400     | PASSWORD_LENGTH_INVALID  | 비밀번호는 6~30자만 가능합니다        |
| 400     | LEGALNAME_LENGTH_INVALID | 실명은 2~5자만 가능합니다           |
| 400     | NICKNAME_LENGTH_INVALID  | 닉네임은 2~8자만 가능합니다          |
| 400     | BIRTHDATE_REQUIRED       | 생년월일 입력은 필수입니다            |
| 400     | EMAIL_INVALID_FORMAT     | 올바른 이메일 형식이 아닙니다          |
| 404     | NOT_FOUND                | 인증되지 않은 이메일입니다            | 
| 409     | CONFLICT                 | 이메일 인증을 해주세요              |
| 500     | INTERNAL_SERVER_ERROR    | 서버 에러가 발생했습니다             |

| 타입            | 변수명           |
|---------------|---------------|
| String        | id            |
| String        | username      |
| String        | legalName     |
| String        | nickname      |
| Character     | gender        |
| LocalDate     | birthdate     |
| String        | email         |
| LocalDateTime | createdDate   |
| LocalDateTime | modifiedDate  |
| String        | schemaVersion |

## User 로그인 🛠
### 1️⃣ 요청

| HTTP Method | URI          |
|-------------|--------------|
| PATCH       | /user/signIn |

| 타입     | 변수명      |
|--------|----------|
| String | username |
| String | password |

### 2️⃣ 응답

| HTTP 코드 | 응답 코드          | 응답 메세지               |
|---------|----------------|----------------------|
| 200     | OK             | 로그인 성공               |
| 400     | FIELD_REQUIRED | 아이디 또는 비밀번호를 입력해 주세요 |
| 401     | UNAUTHORIZED   | 로그인 정보가 틀렸습니다        |

| 타입            | 변수명           |
|---------------|---------------|
| String        | id            |
| String        | username      |
| String        | legalName     |
| String        | nickname      |
| Character     | gender        |
| LocalDate     | birthdate     |
| String        | email         |
| LocalDateTime | createdDate   |
| LocalDateTime | modifiedDate  |
| String        | schemaVersion |