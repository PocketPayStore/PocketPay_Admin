# PocketPay Admin

PocketPay의 결제 상태를 조회하고 장애 대응이 필요한 결제를 추적하는 운영자용 애플리케이션입니다. Redis Pub/Sub과 SSE로 상태 변경을 전달해 운영자가 최신 결제 상태와 처리 이력을 확인할 수 있습니다.

## 주요 기능

- 전체 결제 검색과 상태별 통계
- 결제 상세 정보와 상태 변경 이력
- FAILED·TIMEOUT_UNKNOWN 확인 필요 결제 분리
- 장기 미처리 결제 조회
- Redis Pub/Sub → SSE 실시간 화면 갱신
- Thymeleaf 기반 운영 화면

~~~mermaid
flowchart LR
    A[Core 결제 상태 변경] --> B[Redis Pub/Sub]
    B --> C[Admin 이벤트 수신]
    C --> D[SSE]
    D --> E[목록·통계·상세 재조회]
~~~

실시간 이벤트는 최종 데이터가 아니라 변경 신호로 사용합니다. 브라우저가 이벤트를 받으면 API를 다시 조회해 DB에 저장된 최신 상태를 표시합니다.

## 화면과 API

| 구분 | 경로 | 설명 |
|---|---|---|
| 화면 | /admin/payments | 전체 결제 관리 |
| 화면 | /admin/payments/attention | 확인 필요 결제 |
| API | GET /api/payments | 조건별 결제 목록 |
| API | GET /api/payments/statistics | 결제 통계 |
| API | GET /api/payments/attention | 확인 필요 목록 |
| API | GET /api/payments/{paymentId} | 결제 상세 |
| API | GET /api/payments/{paymentId}/histories | 상태 이력 |
| API | GET /api/payments/events | SSE 스트림 |

## 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Java 17, Spring Boot 4.1, Spring MVC |
| View | Thymeleaf, JavaScript, CSS |
| Persistence | Spring Data JPA, QueryDSL, MySQL |
| Event | Redis Pub/Sub, Server-Sent Events |
| Monitoring | Actuator, Micrometer, Prometheus |
| Test | JUnit 5, H2 |

## 실행

JDK 17, MySQL, Redis가 필요합니다. Admin은 스키마를 변경하지 않으며 Flyway가 비활성화돼 있으므로 Core에서 DB 마이그레이션을 먼저 적용해야 합니다.

~~~bash
./gradlew bootRun --args='--spring.profiles.active=local'
~~~

로컬 Redis 이벤트 채널은 payment:status-changed입니다.

## 테스트

~~~bash
./gradlew test
~~~

운영 화면 라우팅, 결제 조회, Redis 이벤트 수신과 SSE 연결을 테스트합니다.
