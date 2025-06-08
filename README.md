# Access Orchestrator

## 프로젝트 소개

Access Orchestrator는 복잡한 권한 관리 요구사항을 처리할 수 있는 정책 기반의 권한 관리 시스템입니다.
RBAC(Role-Based Access Control)를 넘어서 필드 단위 권한 제어, 컨텍스트 기반 제한, 리소스 상태 기반 제어 등
다양한 권한 관리 시나리오를 지원합니다.

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

