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


**실행방법**<br>
코드를 IDEA에 import 시켜서 실행시켜도 상관없습니다.<br>
maven에서 pacake를 이용해 jar로 만든 후 실행시켜도 동작합니다.<br>
<<<<<<< HEAD
SMTP 동작을 원하지 않을 경우
<pre><code>java -jar choi-0.0.1-SNAPSHOT.jar</code></pre>
SMTP 서버의 동작을 원할 경우
<pre><code>java -jar choi-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev</code></pre>

**기존 local과 dev 환경에 따라 DB를 구분하였지만 배포시 DB를 내장시킬 수 있는 방법이 없는 것 같아 현재 환경을 embeded DB를 사용하는 것으로 통일 시킴**


#### 2024-03-26
Lombook 에러나고있음. 일단 푸쉬.