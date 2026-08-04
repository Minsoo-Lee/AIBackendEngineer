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
| Streaming | Spring WebFlux (SSE) |
| Build | Gradle |

## 주요 기능

- **RAG 파이프라인**: 문서 임베딩 → pgvector 저장 → 유사도 검색 → LLM 응답 생성
- **SSE 스트리밍**: 실시간으로 타이핑되듯 응답 스트리밍
- **프롬프트 인젝션 방어**:
    - V1: 시스템 프롬프트로 역할 고정
    - V2: 커스텀 키워드 블랙리스트 필터링
    - V3: Spring AI 내장 SafeGuardAdvisor
    - Indirect: 문서 저장 전 악의적 내용 검증
- **멀티턴 컨텍스트**: 이전 대화 내용을 기억하는 연속 질문 지원

## 프로젝트 구조

```
src/main/java/roadmap/springai/
├── controller/
│   ├── MyController.java       # 단순 Chat API (/ai)
│   ├── RagController.java      # RAG API (/rag, /rag/stream, /rag/safe/*, /rag/chat)
│   └── SearchController.java   # 유사도 검색 API (/search)
├── service/
│   ├── DocumentIngestionService.java  # 문서 임베딩 → pgvector 저장
│   ├── DocumentSearchService.java     # 유사도 검색
│   └── RagService.java                # RAG 응답 생성
└── util/
    └── DataInitializer.java    # 앱 시작 시 문서 자동 저장
```

## 실행 방법

### 사전 요구사항
- Java 21
- Docker Desktop
- Gemini API 키 (Google AI Studio에서 발급)

### 1. PostgreSQL + pgvector 실행

```bash
docker-compose up -d
```

### 2. 환경변수 설정

IntelliJ Run/Debug Configurations → Environment variables에 추가:

```
GEMINI_API_KEY=your_gemini_api_key_here
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

## API 엔드포인트

| 메서드 | 엔드포인트 | 설명 |
|---|---|---|
| GET | `/ai?userInput=질문` | 단순 Chat (RAG 없음) |
| GET | `/search?query=질문` | 유사 문서 검색 |
| GET | `/rag?question=질문` | RAG 응답 생성 |
| GET | `/rag/stream?question=질문` | RAG SSE 스트리밍 |
| GET | `/rag/safe/v1?question=질문` | 시스템 프롬프트 방어 |
| GET | `/rag/safe/v2?question=질문` | 키워드 블랙리스트 방어 |
| GET | `/rag/safe/v3?question=질문` | SafeGuardAdvisor 방어 |
| GET | `/rag/chat?question=질문&conversationId=ID` | 멀티턴 대화 |

## 환경변수

| 변수명 | 설명 |
|---|---|
| `GEMINI_API_KEY` | Google AI Studio에서 발급한 Gemini API 키 |

## 주의사항

- `application.yml`에 API 키를 직접 입력하지 마세요. 반드시 환경변수로 관리하세요.
- 앱 시작 시 `DataInitializer`가 `vector_store` 테이블을 초기화하고 샘플 문서를 다시 저장합니다.