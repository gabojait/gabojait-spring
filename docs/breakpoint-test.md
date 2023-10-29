# 중단점 테스트

## 환경 🖥️
![EC2](https://img.shields.io/badge/EC2-t2.micro-FF9900?logo=amazonec2&style=flat)
![RDS](https://img.shields.io/badge/RDS-db.t3.micro-527FFF?logo=amazonrds&style=flat)

### 웹 서버 & 웹 애플리케이션 서버
- 1 vCPU
- 1 GiB Memory + 1 GiB Virtual Memory
- 30 GiB Storage

### 데이터베이스 서버
- 2 vCPU
- 1 GiB Memory
- 20 GiB Storage

### 요구 사항
- Think Time = 1초
- Response Time = 5초 이내

## 목차 📋
1. [회원](#user)
    - [본인조회](#get/api/v1/user)
    - [로그인](#post/api/v1/user/login)
    - [로그아웃](#post/api/v1/user/logout)
    - [닉네임 중복여부 확인](#get/api/v1/user/nickname)
    - [알림 여부 업데이트](#patch/api/v1/user/notified)
    - [비밀번호 업데이트](#patch/api/v1/user/password)
    - [비밀번호 검증](#post/api/v1/user/password/verify)
    - [토큰 재발급](#post/api/v1/user/token)
    - [아이디 중복여부 확인](#get/api/v1/user/username)
2. [프로필](#profile)
    - [프로필 단건 조회](#get/api/v1/user/user-id/profile)
    - [자기소개 업데이트](#patch/api/v1/user/description)
    - [프로필 사진 삭제](#delete/api/v1/user/image)
    - [본인 프로필 조회](#get/api/v1/user/profile)
    - [프로필 업데이트](#post/api/v1/user/profile)
    - [팀을 찾는 회원 페이징 조회](#get/api/v1/user/seeking-team)
    - [팀 찾기 여부 수정](#patch/api/v1/user/seeking-team)
3. [팀](#team)
    - [팀 수정](#put/api/v1/team)
    - [팀 단건 조회](#get/api/v1/team/team-id)
    - [팀원을 찾는 팀 페이징 조회](#get/api/v1/team/recruiting)
    - [팀원 모집 여부 업데이트](#patch/api/v1/team/recruiting)
    - [본인 현재 팀 조회](#get/api/v1/user/team)
4. [찜](#favorite)
    - [찜한 팀 페이징 조회](#get/api/v1/favorite/team)
    - [찜한 회원 페이징 조회](#get/api/v1/favorite/user)
5. [제안](#offer)
    - [팀이 받은 제안 페이징 조회](#get/api/v1/team/offer/received)
    - [팀이 보낸 제안 페이징 조회](#get/api/v1/team/offer/sent)
    - [회원이 받은 제안 페이징 조회](#get/api/v1/user/offer/received)
    - [회원이 보낸 제안 페이징 조회](#get/api/v1/user/offer/sent)
6. [알림](#notification)
   - [알림 페이징 조회](#get/api/v1/user/notification)
7. [리뷰](#review)
    - [회원 리뷰 페이징 조회](#get/api/v1/user/user-id/review)
    - [리뷰 작성 가능한 팀 전체 조회](#get/api/v1/user/team/review)
 8. [개발](#develop)
    - [헬스 체크](#get/api/v1/health)

---

## 1️⃣ 회원<a id="user"></a>
### 본인 조회<a id="get/api/v1/user"></a>

![](img/breakpoint-test/user/api_v1_user_GET-1.png)

![](img/breakpoint-test/user/api_v1_user_GET-2.png)

### 로그인<a id="post/api/v1/user/login"></a>

![](img/breakpoint-test/user/api_v1_user_login_POST-1.png)

![](img/breakpoint-test/user/api_v1_user_login_POST-2.png)

### 로그아웃<a id="post/api/v1/user/logout"></a>

![](img/breakpoint-test/user/api_v1_user_logout_POST-1.png)

![](img/breakpoint-test/user/api_v1_user_logout_POST-2.png)

### 닉네임 중복여부 확인<a id="get/api/v1/user/nickname"></a>

![](img/breakpoint-test/user/api_v1_user_nickname_GET-1.png)

![](img/breakpoint-test/user/api_v1_user_nickname_GET-2.png)

### 알림 여부 업데이트<a id="patch/api/v1/user/notified"></a>

![](img/breakpoint-test/user/api_v1_user_notified_PATCH-1.png)

![](img/breakpoint-test/user/api_v1_user_notified_PATCH-2.png)

### 비밀번호 업데이트<a id="patch/api/v1/user/password"></a>

![](img/breakpoint-test/user/api_v1_user_password_PATCH-1.png)

![](img/breakpoint-test/user/api_v1_user_password_PATCH-2.png)

### 비밀번호 검증<a id="post/api/v1/user/password/verify"></a>

![](img/breakpoint-test/user/api_v1_user_password_verify_POST-1.png)

![](img/breakpoint-test/user/api_v1_user_password_verify_POST-2.png)

### 토큰 재발급<a id="post/api/v1/user/token"></a>

![](img/breakpoint-test/user/api_v1_user_token_POST-1.png)

![](img/breakpoint-test/user/api_v1_user_token_POST-2.png)

### 아이디 중복여부 확인<a id="get/api/v1/user/username"></a>

![](img/breakpoint-test/user/api_v1_user_username_GET-1.png)

![](img/breakpoint-test/user/api_v1_user_username_GET-2.png)

---

## 2️⃣ 프로필 <a id="profile"></a>
### 프로필 단건 조회<a id="get/api/v1/user/user-id/profile"></a>

![](img/breakpoint-test/profile/api_v1_user_user_id_profile_GET-1.png)

![](img/breakpoint-test/profile/api_v1_user_user_id_profile_GET-2.png)

### 자기소개 업데이트<a id="patch/api/v1/user/description"></a>

![](img/breakpoint-test/profile/api_v1_user_description_PATCH-1.png)

![](img/breakpoint-test/profile/api_v1_user_description_PATCH-2.png)

### 프로필 사진 삭제<a id="delete/api/v1/user/image"></a>

![](img/breakpoint-test/profile/api_v1_user_image_DELETE-1.png)

![](img/breakpoint-test/profile/api_v1_user_image_DELETE-2.png)

### 본인 프로필 조회<a id="get/api/v1/user/profile"></a>

![](img/breakpoint-test/profile/api_v1_user_profile_GET-1.png)

![](img/breakpoint-test/profile/api_v1_user_profile_GET-2.png)

### 프로필 업데이트<a id="post/api/v1/user/profile"></a>

![](img/breakpoint-test/profile/api_v1_user_profile_POST-1.png)

![](img/breakpoint-test/profile/api_v1_user_profile_POST-2.png)

### 팀을 찾는 회원 페이징 조회<a id="get/api/v1/user/seeking-team"></a>

![](img/breakpoint-test/profile/api_v1_user_seeking_team_GET-1.png)

![](img/breakpoint-test/profile/api_v1_user_seeking_team_GET-2.png)

### 팀 찾기 여부 수정<a id="patch/api/v1/user/seeking-team"></a>

![](img/breakpoint-test/profile/api_v1_user_seeking_team_PATCH-1.png)

![](img/breakpoint-test/profile/api_v1_user_seeking_team_PATCH-2.png)

---

## 3️⃣ 팀<a id="team"></a>
### 팀 수정<a id="put/api/v1/team"></a>

![](img/breakpoint-test/team/api_v1_team_PUT-1.png)

![](img/breakpoint-test/team/api_v1_team_PUT-2.png)

### 팀 단건 조회<a id="get/api/v1/team/team-id"></a>

![](img/breakpoint-test/team/api_v1_team_team_id_GET-1.png)

![](img/breakpoint-test/team/api_v1_team_team_id_GET-2.png)

### 팀원을 찾는 팀 페이징 조회<a id="get/api/v1/team/recruiting"></a>

![](img/breakpoint-test/team/api_v1_team_recruiting_GET-1.png)

![](img/breakpoint-test/team/api_v1_team_recruiting_GET-2.png)

### 팀원 모집 여부 업데이트<a id="patch/api/v1/team/recruiting"></a>

![](img/breakpoint-test/team/api_v1_team_recruiting_PATCH-1.png)

![](img/breakpoint-test/team/api_v1_team_recruiting_PATCH-2.png)

### 본인 현재 팀 조회<a id="get/api/v1/user/team"></a>

![](img/breakpoint-test/team/api_v1_user_team_GET-1.png)

![](img/breakpoint-test/team/api_v1_user_team_GET-2.png)

---

## 4️⃣ 찜<a id="favorite"></a>
### 찜한 팀 페이징 조회<a id="get/api/v1/favorite/team"></a>

![](img/breakpoint-test/favorite/api_v1_favorite_team_GET-1.png)

![](img/breakpoint-test/favorite/api_v1_favorite_team_GET-2.png)

### 찜한 회원 페이징 조회<a id="get/api/v1/favorite/user"></a>

![](img/breakpoint-test/favorite/api_v1_favorite_user_GET-1.png)

![](img/breakpoint-test/favorite/api_v1_favorite_user_GET-2.png)

---

## 5️⃣ 제안<a id="offer"></a>
### 팀이 받은 제안 페이징 조회<a id="get/api/v1/team/offer/received"></a>

![](img/breakpoint-test/offer/api_v1_team_offer_received_GET-1.png)

![](img/breakpoint-test/offer/api_v1_team_offer_received_GET-2.png)

### 팀이 보낸 제안 페이징 조회<a id="get/api/v1/team/offer/sent"></a>

![](img/breakpoint-test/offer/api_v1_team_offer_sent_GET-1.png)

![](img/breakpoint-test/offer/api_v1_team_offer_sent_GET-2.png)

### 회원이 받은 제안 페이징 조회<a id="get/api/v1/user/offer/received"></a>

![](img/breakpoint-test/offer/api_v1_user_offer_received_GET-1.png)

![](img/breakpoint-test/offer/api_v1_user_offer_received_GET-2.png)

### 회원이 보낸 제안 페이징 조회<a id="get/api/v1/user/offer/sent"></a>

![](img/breakpoint-test/offer/api_v1_user_offer_sent_GET-1.png)

![](img/breakpoint-test/offer/api_v1_user_offer_sent_GET-2.png)

---

## 6️⃣ 알림<a id="notification"></a>
### 알림 페이징 조회<a id="get/api/v1/user/notification"></a>

![](img/breakpoint-test/notification/api_v1_user_notification_GET-1.png)

![](img/breakpoint-test/notification/api_v1_user_notification_GET-2.png)

---

## 7️⃣ 리뷰<a id="review"></a>
### 회원 리뷰 페이징 조회<a id="get/api/v1/user/user-id/review"></a>

![](img/breakpoint-test/review/api_v1_user_user_id_review_GET-1.png)

![](img/breakpoint-test/review/api_v1_user_user_id_review_GET-2.png)

### 리뷰 작성 가능한 팀 전체 조회<a id="get/api/v1/user/team/review"></a>

![](img/breakpoint-test/review/api_v1_user_team_review_GET-1.png)

![](img/breakpoint-test/review/api_v1_user_team_review_GET-2.png)

---

## 8️⃣ 개발<a id="develop"></a>
### 헬스 체크<a id="get/api/v1/health"></a>

![](img/breakpoint-test/develop/api_v1_health_GET-1.png)

![](img/breakpoint-test/develop/api_v1_health_GET-2.png)