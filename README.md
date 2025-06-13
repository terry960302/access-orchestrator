# Access Orchestrator

## 프로젝트 소개

Access Orchestrator는 복잡한 권한 관리 요구사항을 처리할 수 있는 정책 기반의 권한 관리 시스템입니다.
RBAC(Role-Based Access Control)를 넘어서 필드 단위 권한 제어, 컨텍스트 기반 제한, 리소스 상태 기반 제어 등
다양한 권한 관리 시나리오를 지원합니다.

## 개발 배경

### 기존 권한 처리 구조의 핵심 요약(ABAC 군 시스템 인가처리)

#### 구조적 특성

- 권한 판단에 필요한 속성 데이터는 모두 `DB 기반`
- 소속, 직책, 계급, 인원의 타입, 임시권한, 부대 등 판단 로직은 자바 코드 내 전략 객체로 고정(정적)
- 도메인별 authorize() 메서드에서 조건 분기 처리
- 필드 단위 제어까지 존재
- 단순 “리소스 접근 가능 여부”만 아니라, 내부 엔티티의 개별 필드에 대한 읽기/쓰기 제어도 포함
- 그래서 권한 판단은 비즈니스 로직과 밀접하게 얽히게 됨
- 실제 서비스 로직 내부에서 strategy.authorize(user, resource) 호출이 불가피
- 로직 자체를 분리하려 노력했지만, 인가 판단은 비즈니스 흐름의 일부로 존재할 수밖에 없었음

#### 그러한 구조의 본질적 한계

- **인가 판단이 코드 안에 묶여 있었기 때문에, 정책 변경 시 로직 수정과 배포가 불가피**
- **권한 정책의 선언적 가시성이 떨어져**, 외부에서 전체 권한 구조를 파악하거나 시각화하기 어려움
- **비즈니스 흐름 내부에서의 인가 판단이 필수적**이었기에, 인가 로직을 완전히 분리하는 것도 불가능


## Access Orchestrator로의 전환 이유 요약
그래서 완전히 '인가 판단 로직을 서비스 레이어 밖으로 추출하자'는 건 현실적으로 불가능했지만, 정책 정의 방식과 표현을 선언형 데이터 기반으로 전환하면,

- 코드 변경 없이 정책 수정 가능
- 정책 시각화 및 시뮬레이션 가능
- 다양한 리소스/필드/컨텍스트 조건을 일관된 방식으로 평가 가능

→ 결국 인가 로직은 존재하되, `정책 중심 아키텍처`로 전환 가능하다고 판단.




## 주요 기능

### 1. 필드 단위 권한 제어

- 리소스의 특정 필드에 대한 세밀한 접근 제어
- 예: 예산 필드는 PM만 수정 가능, 디자이너는 조회만 가능

### 2. 컨텍스트 기반 제한

- 사용자의 역할, 직책, 도메인 등 다양한 속성 기반 접근 제어
- 프로젝트별, 팀별 차별화된 권한 정책 적용

### 3. 리소스 상태 기반 제어

- 리소스의 상태에 따른 동적 권한 제어
- 예: 승인된 문서는 수정 불가

### 4. 선언형 정책 구성

- JSON 기반의 정책 정의
- 조건과 효과를 명확하게 표현

## 기술 스택

- Java 17
- Spring Boot
- JUnit 5
- Gradle

## 프로젝트 구조

```
src/
├─ main/java/com/example/access/core/
│  ├─ policy/          # 정책 관련 클래스
│  ├─ context/         # 컨텍스트 관리
│  ├─ attribute/       # 속성 관리
│  └─ resource/        # 리소스 정의
└─ test/              # 테스트 코드
```

## 시작하기

### 요구사항

- Java 17 이상
- Gradle 7.0 이상

### 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew build

# 테스트 실행
./gradlew test
```

## 사용 예시

### 필드 정책 정의

```java
FieldPolicy budgetPolicy = FieldPolicy.builder()
    .resourceType("Document")
    .fieldName("budget")
    .effect(Policy.Effect.ALLOW)
    .conditions(List.of(
        Condition.builder()
            .attributeId("domain")
            .operator(Condition.Operator.EQUALS)
            .value("FINANCE")
            .build()
    ))
    .build();
```

### 접근 권한 확인

```java
boolean canAccess = policyEvaluator.canAccessField(
    "pm1",      // 주체 ID
    "doc1",     // 리소스 ID
    "budget"    // 필드명
);
```

## 테스트

프로젝트는 다양한 시나리오에 대한 테스트 케이스를 포함하고 있습니다:

- 기본 정책 평가 테스트
- 필드 정책 관리 테스트
- 통합 테스트
- 시나리오 기반 테스트

## 라이선스

MIT License

