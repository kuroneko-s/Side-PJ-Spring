# 프로젝트 설명

사용된 프레임워크       
1. [Spring Web MVC](https://docs.spring.io/spring-framework/docs/5.2.2.RELEASE/spring-framework-reference/)
2. [Spring Boot](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/)
3. [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/2.2.6.RELEASE/reference/html/#reference)
4. [Spring Security](https://docs.spring.io/spring-security/site/docs/5.2.2.RELEASE/reference/html5/)
5. [Spring RESTDOCS](https://docs.spring.io/spring-restdocs/docs/2.0.4.RELEASE/reference/html5/#documenting-your-api-customizing)
6. [Spring HATEOAS](https://docs.spring.io/spring-hateoas/docs/current/reference/html/#preface)
7. [PostgreSQL](https://www.postgresql.org/)
8. [h2 Database](https://www.h2database.com/html/main.html)

--- 

1. service 모듈       
공통된 기능이나 Entity 및 Repository, Service 정의       
> 외부 모듈에서 공통적으로 사용할 수 있는 기능이면 정의하는 용도.      
> 대표적으로 Entity 구조는 공통적으로 적용. Mail 기능은 공통으로 사용 가능.

2. web 모듈       
웹 서비스 기능 제공을 위한 모듈.     
웹에 해당하는 설정이나 Controller, Service 등을 정의.
port - 8080(기본) 사용.

3. REST API 모듈       
web 모듈에서 사용되는 기본 컨트롤러의 일부 기능들을 REST API로써 지원하기 위한 모듈.
port - 8081(기본) 사용.          
Google OAUTH 사용하여 로그인.       
인증은 JWT 사용

4. 스케쥴러 모듈       
정의 예정

--- 

### 프로젝트 내부 내용
web 모듈에서 JS 라이브러리를 사용하기 위해서 npm 을 사용하고 있음.          
DB는 JPA를 메인으로 사용하지만 SQLMapper도 사용가능하도록 설정.          
기존 Bootstrap 사용했던 코드를 CSS3로 변경. (정적 디자인으로 변경).       
기본 샘플 데이터 위치 - web/resources/sampleData             
기본 업로드 위치 - web/resources/uploadFiles           
기본 샘플 데이터를 만들기 위해서 AppRunner 를 사용했음.        

### 실행
1. PJ-Java (clean - compile)       
2. service (install)       
3. web (test 제거 후 install)     
4. rest_api (test 별도 실행 후 install)

#### 기본 주소
web (http://localhost:8080/)        
rest_api (http://localhost:8081/docs/index.html)        

---

# 코드 규칙

| 접근자    | 규칙       | 설명                              |
|:-------|:---------|:--------------------------------|
| GET    | get~View | 페이지에 접근하는 용도일 경우                |
| GET    | get~     | 일반 GET 요청일 경우                   |
| POST   | post~    | 일반 POST의 처리인 경우                 |
| POST   | reg~     | POST 중에서 무언가를 등록하거나 추가하는 동작일 경우 |
| PATCH  | mod~     | 일부 수정 요청인 경우                    |
| PUT    | modAll~  | 모든 자원의 수정 요청인 경우                |
| DELETE | del~     | 삭제 요청인 경우                       |

---

# 특이사항 및 작업 예정

### maven install 관련 문제

1. web 모듈에서 maven install 동작시 test 패키지 쪽 코드들이 lombok이나 다른 모듈의 참조를 가져오지 못하고있음. 해결방안은 찾지 못했고 임시방편으로 test 폴더 외부에 코드들을 옮겨두고 install 후 다시 원상복구하는 방식이면 동작함.
2. maven의 test 기능하지 않아서 rest-api 모듈 동작시킬 때 PJ-Java (clean-compile) 후 rest-api의 test 실행 후 install 동작 필요.        

### 확인해야하는 것
rest_api 에서 maven 으로 test 돌아가는 plugin 설정 알아봐야함.