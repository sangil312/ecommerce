# Ecommerce 토이 프로젝트

## 개요
간단한 이커머스 서비스를 구현한 백엔드 토이 프로젝트입니다. 주문, 결제, 리뷰 등 핵심 도메인 흐름을
모듈화된 구조로 정리하고, 테스트와 문서화를 함께 관리하는 데 초점을 두었습니다.

## 목표
- 계층 구조(Controller -> Service -> Implement -> Repository)에서 비즈니스 로직 분리
- 검증 로직을 도메인/서비스 레이어에 집중
- REST Docs 기반의 API 문서화

## 기술 스택
- Java 21, Spring Boot
- Gradle (멀티 모듈)
- JPA / Hibernate
- MySQL / H2 (테스트)
- Spring REST Docs

## 모듈 구성
- core:core-api
    - 애플리케이션 계층과 프레젠테이션(Web API) 계층을 포함합니다.
    - 주요 내용: Spring MVC Controller, 요청/응답 DTO, 도메인 서비스, 공용 응답/에러/인증 지원 코드.

- core:core-enum
    - 시스템 전반에서 공통으로 사용하는 열거형 타입 모음 모듈입니다.

- support:logging
   - 로깅 관련 공통 설정 모듈입니다. logback 설정 리소스를 포함합니다.

- clients
  - 외부 연동/클라이언트 모듈입니다.

## 도메인
- 주문(Order): 주문 생성, 장바구니 주문, 상태 전이
- 결제(Payment): 결제 생성/확정, 실패 처리
- 리뷰(Review): 리뷰 생성 검증, 대상별 리뷰 조회

## API 문서
- 테스트 대상: `core/core-api`
- API 문서 생성 경로: `ecommerce/docs/index.html`

## 테스트 실행
- 컨트롤러 테스트(rest docs 문서 생성)만: `./gradlew restDocsTest`
- 전체 테스트: `./gradlew test`
- 단위 테스트만: `./gradlew unitTest`
- 통합 테스트만: `./gradlew integrationTest`


## 검토 포인트
- 검증 로직은 컨트롤러가 아니라 서비스/검증 클래스에서 처리합니다.
- REST Docs 테스트가 실행 가능한 API 문서 역할을 합니다.
- 중요한 제약(중복 리뷰 등)은 DB 유니크 제약으로 최종 방어합니다.
