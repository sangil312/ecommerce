# Ecommerce 토이 프로젝트

## 기술 스택
- Java 21, Spring Boot (3.5.X)
- Gradle (Groovy)
- JPA / Hibernate
- MySQL / H2 (테스트)
- Spring REST Docs

## 모듈 구조 (멀티 모듈)
- core:core-api
    - 애플리케이션 계층과 프레젠테이션(Web API) 계층을 포함합니다.
    - 주요 내용: Spring MVC Controller, 요청/응답 DTO, 도메인 서비스, 공용 응답/에러/인증 지원 코드.

- core:core-enum
    - 프로젝트 전반에서 공통으로 사용하는 열거형 타입 모음 모듈입니다.

- support:logging
   - 로깅 관련 공통 설정 모듈입니다. logback 설정 리소스를 포함합니다.

- clients
  - 외부 연동/클라이언트 모듈입니다.

## 의존성 및 빌드 공통 설정
- bootJar는 core-api 모듈만 활성화하고, 이 외 모듈은 일반 jar를 생성합니다. 즉, 각 모듈은 라이브러리로 패키징됩니다.
- 테스트 Tasks: `unitTest`, `integrationTest`, `restDocsTest` 태스크 제공 (JUnit5 기반)

## API 문서
- 테스트 대상: `core/core-api`
- API 문서 생성 경로: `ecommerce/docs/index.html`

## 테스트 실행
- 전체 테스트: `./gradlew test`
- 컨트롤러 테스트(rest docs 문서 생성)만: `./gradlew restDocsTest`
- 단위 테스트만: `./gradlew unitTest`
- 통합 테스트만: `./gradlew integrationTest`

## 레이어 및 트랜잭션 범위
- 레이어 구조: Presentation, Business, Implement(비지니스 구현), Data Access의 4계층 분리.
- 참조 규칙: 순방향 참조만 허용하며, 하위 레이어를 건너뛰지 않는다.
- 트랜잭션: Implement Layer에만 `@Transactional`을 사용한다.

## 명명 규칙 및 컨벤션
- 공통 응답 스팩(`ApiResponse`)과 에러 핸들링 정책(core-api/support/error) 확인
- 엔드포인트: 소문자-하이픈, 복수형 리소스명 사용 (`/v1/cart/items`)
- 엔티티: 단수형 리소스명 사용 (Order, Payment 등)
- DTO: `XxxRequest`, `XxxResponse`
- 서비스: `XxxService`
  - 비지니스 조율
- 구현체: `XxxWriter`, `XxxReader`, `XxxValidator`, `XxxProcessor` 등
  - 개념이 분산되지 않도록 접미사 팀 컨벤션 필요 (예시)
  - `Writer`: 생명주기 관리
  - `Reader`: 조회 전담
  - `Validator`: 정책 검증
  - `Processor`: 특정 비지니스 흐름 처리
- 열거형: 개념 의미가 드러나는 명사형 (`OrderStatus` 등)