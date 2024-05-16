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
