# Spring AI RAG Pipeline

Spring AI와 pgvector를 활용한 RAG(Retrieval Augmented Generation) 파이프라인 구현 프로젝트입니다.

## 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.5 |
| AI Framework | Spring AI 1.1.2 |
| Chat Model | Gemini 2.5 Flash Lite (OpenAI 호환 엔드포인트) |
| Embedding Model | Gemini Embedding 001 (Google GenAI) |
| Vector Store | PostgreSQL + pgvector |
| Cache | Redis |
| Streaming | Spring WebFlux (SSE) |
| Security | Spring Security + JWT |
| Build | Gradle |
| Infra | Docker Compose |
| API Docs | Swagger UI (springdoc-openapi) |

## 주요 기능

- **RAG 파이프라인**: 문서 임베딩 → pgvector 저장 → 유사도 검색 → LLM 응답 생성
- **Redis 캐싱**: 동일 질문 반복 시 Redis에 캐싱된 답변을 반환하여 API 호출 비용 절감
- **SSE 스트리밍**: 실시간으로 타이핑되듯 응답 스트리밍
- **프롬프트 인젝션 방어**:
  - V1: 시스템 프롬프트로 역할 고정
  - V2: 커스텀 키워드 블랙리스트 필터링
  - V3: Spring AI 내장 SafeGuardAdvisor
  - Indirect: 문서 저장 전 악의적 내용 검증
- **멀티턴 컨텍스트**: 이전 대화 내용을 기억하는 연속 질문 지원
- **JWT 인증**: JWT 토큰 기반 인증으로 인증된 사용자만 API 접근 가능
- **토큰 추적**: 사용자별 API 호출 횟수를 Redis에 카운팅하여 비용 모니터링 가능

## 프로젝트 구조

```
src/main/java/roadmap/springai/
├── config/
│   ├── RedisConfig.java               # Redis 연결 설정
│   ├── RedisCacheConfig.java          # Spring Cache → Redis 연동
│   └── SecurityConfig.java            # Spring Security 필터 체인 설정
├── controller/
│   ├── AuthController.java            # JWT 토큰 발급 (/auth/login)
│   ├── MyController.java              # 단순 Chat API (/ai)
│   ├── RagController.java             # RAG API (/rag/*, /rag/safe/*, /rag/chat)
│   └── SearchController.java          # 유사도 검색 API (/search)
├── filter/
│   └── JwtFilter.java                 # JWT 검증 필터
├── service/
│   ├── DocumentIngestionService.java  # 문서 임베딩 → pgvector 저장
│   ├── DocumentSearchService.java     # 유사도 검색
│   ├── RagCacheService.java           # RAG 캐시 서비스
│   ├── RagService.java                # RAG 응답 생성
│   └── TokenTrackingService.java      # API 호출 횟수 추적
└── util/
    ├── DataInitializer.java           # 앱 시작 시 문서 자동 저장
    └── JwtUtil.java                   # JWT 생성 / 검증
```

## 실행 방법

### 사전 요구사항
- Docker Desktop
- Gemini API 키 (Google AI Studio에서 발급)

### 1. 환경변수 설정

`.env` 파일 생성:

```
GEMINI_API_KEY=your_gemini_api_key_here
DB_USERNAME=postgres
DB_PASSWORD=postgres
REDIS_HOST=redis
REDIS_PORT=6379
JWT_SECRET=your_jwt_secret_key_here
```

### 2. 전체 스택 실행

```bash
docker-compose up --build
```

Spring Boot + PostgreSQL + Redis가 한 번에 실행됩니다.

### 3. Swagger UI
http://localhost:8080/swagger-ui/index.html

## API 엔드포인트

> 🔒 `/auth/login`을 제외한 모든 엔드포인트는 JWT 토큰이 필요합니다.
> `Authorization: Bearer {token}` 헤더에 포함해서 요청하세요.

| 메서드 | 엔드포인트 | 설명 |
|---|---|---|
| POST | `/auth/login` | JWT 토큰 발급 |
| GET | `/ai?userInput=질문` | 단순 Chat (RAG 없음) |
| GET | `/search?query=질문` | 유사 문서 검색 |
| GET | `/rag/v1?question=질문` | 기본 RAG 응답 생성 |
| GET | `/rag/v2?question=질문` | RAG + Redis 캐싱 |
| GET | `/rag/v3?question=질문` | RAG + Redis 캐싱 + 토큰 추적 |
| GET | `/rag/stream?question=질문` | RAG SSE 스트리밍 |
| GET | `/rag/safe/v1?question=질문` | 시스템 프롬프트 방어 |
| GET | `/rag/safe/v2?question=질문` | 키워드 블랙리스트 방어 |
| GET | `/rag/safe/v3?question=질문` | SafeGuardAdvisor 방어 |
| GET | `/rag/chat?question=질문&conversationId=ID` | 멀티턴 대화 |

## 환경변수

| 변수명 | 설명 |
|---|---|
| `GEMINI_API_KEY` | Google AI Studio에서 발급한 Gemini API 키 |
| `DB_USERNAME` | PostgreSQL 사용자명 |
| `DB_PASSWORD` | PostgreSQL 비밀번호 |
| `REDIS_HOST` | Redis 호스트 (Docker: `redis`, 로컬: `localhost`) |
| `REDIS_PORT` | Redis 포트 (기본값: `6379`) |
| `JWT_SECRET` | JWT 서명에 사용할 비밀 키 |

## 주의사항

- `.env` 파일을 `.gitignore`에 추가하여 API 키가 GitHub에 노출되지 않도록 하세요.
- 앱 시작 시 `DataInitializer`가 `vector_store` 테이블을 초기화하고 샘플 문서를 다시 저장합니다.
- 테스트용 계정: `username: user`, `password: password` (실제 프로덕션에서는 DB 연동 필요)
- 브라우저에서 http://localhost:8080/index.html 로 RAG 테스트 가능
