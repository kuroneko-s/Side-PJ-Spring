# 프로젝트 설명

1. service 모듈
기본적으로 공통된 모듈이나 Entity 및 Repository 정도 나열.       
> 외부 모듈에서 공통적으로 사용할 수 있는 기능이면 정의하는 용도.      
> 대표적으로 Entity 구조는 공통적으로 적용. Mail 기능은 공통으로 사용 가능.

2. web 모듈
웹 서비스 기능 제공을 위한 모듈.     
웹에 해당하는 설정이나 Controller, Service 등을 정의. 

`의존 관계 (의도)`       
- Controller - Service        
- Service - Repository or Entity      

3. REST API 모듈
정의 예정

4. 스케쥴러 모듈
정의 예정


---

### 기존 프로젝트에 작성되어있던 내용.

자바스크립트 관련 라이브러리 추가를 위해 npm을 사용했습니다.<br>

**JS library list** <br>
npm version - 6.14.4 <br>
1. @yaireo/tagify<br>
2. summernote<br>
3. @fontawesome-free<br>
4. jdention<br>
5. bootstrap<br>
6. jquery & jquery-form<br>
7. moment<br>
8. tooltipster<br>

_NPM 미포함 예정이었지만 배포를 위해 포함시킴_

프론트 설계 및 코드는 bootstrap을 사용했습니다.<br>
백엔드 설계 및 코드는 spring MVC를 기반으로 한 프레임 워크들을 사용했습니다.<br>

사용된 프레임워크<br>
1. Spring Web MVC Ver.5.2.2
2. Spring Boot Ver.2.2.6
3. Spring Data API(JPA)
4. Spring Security

DB
postgreSQL (dev)<br>
embeded (local)

Video 영상 데이터들은 프로젝트 내 /static/video에 위치하게 작성하였습니다.

**_참고한 사이트_**<br>
https://www.inflearn.com/ 인프런


#### 24-05-17
local 에서 h2 db로 실행은 되는 상태로 업로드.

#### 24-05-24
URL 엔드포인트 관리좀 해야할듯..

---

# 코드 규칙

---

## 웹 모듈
### 컨트롤러

| 접근자    | 규칙       | 설명                              |
|:-------|:---------|:--------------------------------|
| GET    | get~View | 페이지에 접근하는 용도일 경우                |
| GET    | get~     | 일반 GET 요청일 경우                   |
| POST   | post~    | 일반 POST의 처리인 경우                 |
| POST   | reg~     | POST 중에서 무언가를 등록하거나 추가하는 동작일 경우 |
| PATCH  | mod~     | 일부 수정 요청인 경우                    |
| PUT    | modAll~  | 모든 자원의 수정 요청인 경우                |
| DELETE | del~     | 삭제 요청인 경우                       |

### 서비스

컨트롤러와 서비서 모듈의 어댑터 역할.      
웹 서비스에 종속된 비지니스 로직 및 기능만을 정의.       
서비스 모듈의 Repository 나 서비스 패키지의 접근을 통한 비지니스 로직을 구현.       

## 서비스 모듈

### 서비스

외부 API 호출과 같은 현 프로젝트 외의 모듈과 통신하는 용도로만을 사용.      
필요에 의한 정의.       
기본적인 서비스는 각 외부 모듈 내에서 정의하게끔.

### Repository

JPA 로 저장하는 용도.

### SQLMapper (Mybatis)

JPA 로 처리가 안되는 경우 사용할 예정.        
기본 설정만 추가.

---

반환하는 타입을 따로 VO를 정해서 그놈을 쓸까...
그러면 서비스에서도 여러군데 안쓰고 그냥 한놈만 호출해서 통합해버리면 되는데...
그게 나은거같기도 하고
