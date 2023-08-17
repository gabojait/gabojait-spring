# 부하 테스트 결과

## 환경 🖥️
![EC2](https://img.shields.io/badge/EC2-t2.micro-FF9900?logo=amazonec2&style=flat)
![EC2](https://img.shields.io/badge/RDS-db.t3.micro-527FFF?logo=amazonrds&style=flat)

### 웹 서버 & 웹 애플리케이션 서버
- 1 vCPU
- 1 GiB Memory + 1 GiB Virtual Memory
- 30 GiB Storage

### 데이터베이스 서버
- 2 vCPU
- 1 GiB Memory
- 20 GiB Storage

## 목차 📋
1. [회원](#user)
2. [프로필](#profile)
3. [팀](#team)
4. [찜](#favorite)
5. [제안](#offer)
6. [리뷰](#review)
7. [개발](#develop)

---

## 1️⃣ 회원<a id="user"></a>
### 본인 조회
#### vUser 99
![](img/v1.0.0/user/api_v1_user_GET-99.png)

#### vUser 296
![](img/v1.0.0/user/api_v1_user_GET-296.png)

#### vUser 500
![](img/v1.0.0/user/api_v1_user_GET-500.png)

### 로그인
#### vUser 99
![](img/v1.0.0/user/api_v1_user_login_POST-99.png)

#### vUser 296
![](img/v1.0.0/user/api_v1_user_login_POST-296.png)

#### vUser 500
![](img/v1.0.0/user/api_v1_user_login_POST-500.png)

### 로그아웃
#### vUser 99
![](img/v1.0.0/user/api_v1_user_logout_POST-99.png)

#### vUser 296
![](img/v1.0.0/user/api_v1_user_logout_POST-296.png)

#### vUser 500
![](img/v1.0.0/user/api_v1_user_logout_POST-500.png)

### 닉네임 중복여부 확인
#### vUser 99
![](img/v1.0.0/user/api_v1_user_nickname_GET-99.png)

#### vUser 296
![](img/v1.0.0/user/api_v1_user_nickname_GET-296.png)

#### vUser 500
![](img/v1.0.0/user/api_v1_user_nickname_GET-500.png)

### 알림 업데이트
#### vUser 99
![](img/v1.0.0/user/api_v1_user_notified_PATCH-99.png)

#### vUser 296
![](img/v1.0.0/user/api_v1_user_notified_PATCH-296.png)

#### vUser 500
![](img/v1.0.0/user/api_v1_user_notified_PATCH-500.png)

### 비밀번호 업데이트
#### vUser 99
![](img/v1.0.0/user/api_v1_user_password_PATCH-99.png)

#### vUser 296
![](img/v1.0.0/user/api_v1_user_password_PATCH-296.png)

#### vUser 500
![](img/v1.0.0/user/api_v1_user_password_PATCH-500.png)

### 비밀번호 검증
#### vUser 99
![](img/v1.0.0/user/api_v1_user_password_verify_POST-99.png)

#### vUser 296
![](img/v1.0.0/user/api_v1_user_password_verify_POST-296.png)

#### vUser 500
![](img/v1.0.0/user/api_v1_user_password_verify_POST-500.png)

### 토큰 재발급
#### vUser 99
![](img/v1.0.0/user/api_v1_user_token_POST-99.png)

#### vUser 296
![](img/v1.0.0/user/api_v1_user_token_POST-296.png)

#### vUser 500
![](img/v1.0.0/user/api_v1_user_token_POST-500.png)

### 아이디 중복여부 확인
#### vUser 99
![](img/v1.0.0/user/api_v1_user_username_GET-99.png)

#### vUser 296
![](img/v1.0.0/user/api_v1_user_username_GET-296.png)

#### vUser 500
![](img/v1.0.0/user/api_v1_user_username_GET-500.png)

---

## 2️⃣ 프로필 <a id="profile"></a>
### 프로필 단건 조회
#### vUser 99
![](img/v1.0.0/profile/api_v1_user_user-id_profile_GET-99.png)

#### vUser 296
![](img/v1.0.0/profile/api_v1_user_user-id_profile_GET-296.png)

#### vUser 500
![](img/v1.0.0/profile/api_v1_user_user-id_profile_GET-500.png)

### 자기소개 업데이트
#### vUser 99
![](img/v1.0.0/profile/api_v1_user_description_PATCH-99.png)

#### vUser 296
![](img/v1.0.0/profile/api_v1_user_description_PATCH-296.png)

#### vUser 500
![](img/v1.0.0/profile/api_v1_user_description_PATCH-500.png)

### 프로필 사진 삭제
#### vUser 99
![](img/v1.0.0/profile/api_v1_user_image_DELETE-99.png)

#### vUser 296
![](img/v1.0.0/profile/api_v1_user_image_DELETE-296.png)

#### vUser 500
![](img/v1.0.0/profile/api_v1_user_image_DELETE-500.png)

### 본인 프로필 조회
#### vUser 99
![](img/v1.0.0/profile/api_v1_user_description_PATCH-99.png)

#### vUser 296
![](img/v1.0.0/profile/api_v1_user_description_PATCH-296.png)

#### vUser 500
![](img/v1.0.0/profile/api_v1_user_description_PATCH-500.png)

### 프로필 업데이트
#### vUser 99
![](img/v1.0.0/profile/api_v1_user_profile_POST-99.png)

#### vUser 296
![](img/v1.0.0/profile/api_v1_user_profile_POST-296.png)

#### vUser 500
![](img/v1.0.0/profile/api_v1_user_profile_POST-500.png)

### 팀을 찾는 회원 다건 조회
#### vUser 99
![](img/v1.0.0/profile/api_v1_user_seeking-team_GET-99.png)

#### vUser 296
![](img/v1.0.0/profile/api_v1_user_seeking-team_GET-296.png)

#### vUser 500
![](img/v1.0.0/profile/api_v1_user_seeking-team_GET-500.png)

### 팀 찾기 여부 수정
#### vUser 99
![](img/v1.0.0/profile/api_v1_user_seeking-team_PATCH-99.png)

#### vUser 296
![](img/v1.0.0/profile/api_v1_user_seeking-team_PATCH-296.png)

#### vUser 500
![](img/v1.0.0/profile/api_v1_user_seeking-team_PATCH-500.png)

---

## 3️⃣ 팀<a id="team"></a>
### 팀 정보 수정
#### vUser 99
![](img/v1.0.0/team/api_v1_team_PUT-99.png)

#### vUser 296
![](img/v1.0.0/team/api_v1_team_PUT-296.png)

#### vUser 500
![](img/v1.0.0/team/api_v1_team_PUT-500.png)

### 팀 단건 조회
#### vUser 99
![](img/v1.0.0/team/api_v1_team_tem-id_GET-99.png)

#### vUser 296
![](img/v1.0.0/team/api_v1_team_tem-id_GET-296.png)

#### vUser 500
![](img/v1.0.0/team/api_v1_team_tem-id_GET-500.png)

### 팀원 찾는 팀 다건 조회
#### vUser 99
![](img/v1.0.0/team/api_v1_team_recruiting_GET-99.png)

#### vUser 296
![](img/v1.0.0/team/api_v1_team_recruiting_GET-296.png)

#### vUser 500
![](img/v1.0.0/team/api_v1_team_recruiting_GET-500.png)

### 팀원 모집 여부 업데이트
#### vUser 99
![](img/v1.0.0/team/api_v1_team_recruiting_PATCH-99.png)

#### vUser 296
![](img/v1.0.0/team/api_v1_team_recruiting_PATCH-296.png)

#### vUser 500
![](img/v1.0.0/team/api_v1_team_recruiting_PATCH-500.png)

### 본인 현재 팀 조회
#### vUser 99
![](img/v1.0.0/team/api_v1_user_team_GET-99.png)

#### vUser 296
![](img/v1.0.0/team/api_v1_user_team_GET-296.png)

#### vUser 500
![](img/v1.0.0/team/api_v1_user_team_GET-500.png)

---

## 4️⃣ 찜<a id="favorite"></a>
### 팀이 찜한 회원 다건 조회
#### vUser 99
![](img/v1.0.0/favorite/api_v1_team_favorite_user_GET-99.png)

#### vUser 296
![](img/v1.0.0/favorite/api_v1_team_favorite_user_GET-296.png)

#### vUser 500
![](img/v1.0.0/favorite/api_v1_team_favorite_user_GET-500.png)

### 팀이 회원 찜하기 및 찜 취소하기
#### vUser 99
![](img/v1.0.0/favorite/api_v1_team_favorite_user_user-id_POST-99.png)

#### vUser 296
![](img/v1.0.0/favorite/api_v1_team_favorite_user_user-id_POST-296.png)

#### vUser 500
![](img/v1.0.0/favorite/api_v1_team_favorite_user_user-id_POST-500.png)

### 회원이 찜한 팀 다건 조회
#### vUser 99
![](img/v1.0.0/favorite/api_v1_user_favorite_team_GET-99.png)

#### vUser 296
![](img/v1.0.0/favorite/api_v1_user_favorite_team_GET-296.png)

#### vUser 500
![](img/v1.0.0/favorite/api_v1_user_favorite_team_GET-500.png)

### 회원이 팀 찜하기 및 찜 취소하기
#### vUser 99
![](img/v1.0.0/favorite/api_v1_user_favorite_team_team-id_POST-99.png)

#### vUser 296
![](img/v1.0.0/favorite/api_v1_user_favorite_team_team-id_POST-296.png)

#### vUser 500
![](img/v1.0.0/favorite/api_v1_user_favorite_team_team-id_POST-500.png)

---

## 5️⃣ 제안<a id="offer"></a>
### 팀이 받은 제안 다건 조회
#### vUser 99
![](img/v1.0.0/offer/api_v1_team_offer_received_GET-99.png)

#### vUser 296
![](img/v1.0.0/offer/api_v1_team_offer_received_GET-296.png)

#### vUser 500
![](img/v1.0.0/offer/api_v1_team_offer_received_GET-500.png)

### 팀이 보낸 제안 다건 조회
#### vUser 99
![](img/v1.0.0/offer/api_v1_team_offer_sent_GET-99.png)

#### vUser 296
![](img/v1.0.0/offer/api_v1_team_offer_sent_GET-296.png)

#### vUser 500
![](img/v1.0.0/offer/api_v1_team_offer_sent_GET-500.png)

### 팀이 회원에게 스카웃
#### vUser 99
![](img/v1.0.0/offer/api_v1_team_user_user-id_offer_POST-99.png)

#### vUser 296
![](img/v1.0.0/offer/api_v1_team_user_user-id_offer_POST-296.png)

#### vUser 500
![](img/v1.0.0/offer/api_v1_team_user_user-id_offer_POST-500.png)

### 회원이 받은 제안 다건 조회
#### vUser 99
![](img/v1.0.0/offer/api_v1_user_offer_received_GET-99.png)

#### vUser 296
![](img/v1.0.0/offer/api_v1_user_offer_received_GET-296.png)

#### vUser 500
![](img/v1.0.0/offer/api_v1_user_offer_received_GET-500.png)

### 회원이 보낸 제안 다건 조회
#### vUser 99
![](img/v1.0.0/offer/api_v1_user_offer_sent_GET-99.png)

#### vUser 296
![](img/v1.0.0/offer/api_v1_user_offer_sent_GET-296.png)

#### vUser 500
![](img/v1.0.0/offer/api_v1_user_offer_sent_GET-500.png)

### 회원이 팀에 지원
#### vUser 99
![](img/v1.0.0/offer/api_v1_user_team_team-id_offer_POST-99.png)

#### vUser 296
![](img/v1.0.0/offer/api_v1_user_team_team-id_offer_POST-296.png)

#### vUser 500
![](img/v1.0.0/offer/api_v1_user_team_team-id_offer_POST-500.png)

---

## 6️⃣ 리뷰<a id="review"></a>
### 회원 리뷰 다건 조회
#### vUser 99
![](img/v1.0.0/review/api_v1_user_user-id_review_GET-99.png)

#### vUser 296
![](img/v1.0.0/review/api_v1_user_user-id_review_GET-296.png)

#### vUser 500
![](img/v1.0.0/review/api_v1_user_user-id_review_GET-500.png)

### 본인 리뷰 작성 가능한 팀 전체 조회
#### vUser 99
![](img/v1.0.0/review/api_v1_user_team_review_GET-99.png)

#### vUser 296
![](img/v1.0.0/review/api_v1_user_team_review_GET-296.png)

#### vUser 500
![](img/v1.0.0/review/api_v1_user_team_review_GET-500.png)

---

## 7️⃣ 개발<a id="develop"></a>
### 헬스 체크
#### vUser 99
![](img/v1.0.0/develop/api_v1_health_GET-99.png)

#### vUser 296
![](img/v1.0.0/develop/api_v1_health_GET-296.png)

#### vUser 500
![](img/v1.0.0/develop/api_v1_health_GET-500.png)

