# 꿈틀 Back

꿈틀 서비스의 백엔드 서비스입니다.  
동화 생성 요청, 게임 진행, 음성 처리, 단어 추출, 인증을 담당하는 Spring Boot 서버입니다.

## 🛠️ Build Info
- Language: Java 21
- Framework: Spring Boot 3.5.6
- Database: MySQL
- Cache: Redis
- Message Broker: Kafka
- Storage: AWS S3
- Auth: JWT
- Media: FFmpeg

## 📁 Project Structure
```
src/main/java/com/capstone/kkumteul
    ├── domain
    │     ├── auth
    │     ├── fairytale
    │     ├── game
    │     ├── kafka
    │     ├── user
    │     ├── vocab
    │     └── voice
    ├── global
    │     ├── client
    │     ├── config
    │     ├── constant
    │     ├── entity
    │     ├── exception
    │     ├── jwt
    │     ├── response
    │     └── security
    └── KkumteulApplication.java
```

## 🧩 Service Modules

- `auth`: 회원가입, 로그인, JWT 인증
- `user`: 프로필 조회 및 수정
- `fairytale`: 동화 생성, 조회, SSE 구독
- `game`: 동화 기반 게임 진행
- `voice`: 음성 업로드 및 변환
- `vocab`: 단어 추출 및 저장
- `kafka`: 비동기 이벤트 처리
  
## 🏗️ Architecture
<img width="100%" alt="image" src="https://github.com/user-attachments/assets/f620bd41-49d1-4751-bc2c-c90f2425230d" />


## 🗂️ ERD

<img width="100%" alt="image" src="https://github.com/user-attachments/assets/f5273cec-e12e-4c5f-b556-d6726604706b" />

<br>

------

# 🚀 Local Setup

`src/main/resources` 디렉토리와 `application.properties` 설정 파일을 생성한 뒤 필요한 값을 설정합니다.

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/kkumteul
spring.datasource.username=root
spring.datasource.password=your-password

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT
jwt.secret=your-jwt-secret-key
jwt.expiration=86400000

# Kafka
KAFKA_URL=localhost:9092
FAIRYTALE_GENERATION=fairytale_generate
TTS_MODELING=tts_modeling
VOCAB_EXTRACTED_GROUP_ID=kkumteul-vocab

# FastAPI
fastapi.base-url=http://localhost:8000

# AWS S3
AWS_S3_BUCKET_NAME=your-s3-bucket-name

# Vocab fallback
vocab.fallback-threshold-seconds=300
```
서버 실행:
```bash
./gradlew bootRun
```

테스트 실행:

```bash
./gradlew test
./gradlew kafkaBrokerTest
```

## 🐳 Docker

`docker-compose.yml` 기준 서비스:

- `backend`
- `redis`
- `fastapi`
- `nginx`

환경변수:

- `BACKEND_IMAGE`
- `FASTAPI_IMAGE`
<br>

------

# 🔥 Project Convention

## 📋 Commit Convention
| type       | name                    | description     |
|------------|-------------------------|-----------------|
| `feat`     | `feat/#ISSUE_NUM￼`     | ⚡️ 새로운 기능 추가     |
| `fix`      | `fix/#ISSUE_NUM￼`      | 🐛 버그 수정         |
| `docs`     | `docs/#ISSUE_NUM￼`     | 📝 문서 수정         |
| `refactor` | `refactor/#ISSUE_NUM￼` | 💫 리팩토링          |
| `test`     | `test/#ISSUE_NUM￼`     | 🧪 테스트 코드 작성     |
| `chore`    | `chore/#ISSUE_NUM￼`    | 🛠️ 빌드, 패키지 관련 수정 |
| `perf`     | `perf/#ISSUE_NUM￼`     | 🪄 성능 개선         |
| `ci`       | `ci/#ISSUE_NUM￼`       | 🔄 CI 관련 수정      |
| `cd`       | `cd/#ISSUE_NUM￼`       | 🔄 CD 관련 수정      |
| `revert`   | `revert/#ISSUE_NUM￼`   | ⚠️ 특정 커밋으로 되돌리기  |
| `docker`   | `docker/#ISSUE_NUM￼`   | 🐳 도커 파일 관련 작업 |

## 📌 Git Branch Strategy
| branch    | role                                   |
|-----------|----------------------------------------|
| `main`    | - 최종 배포용 브랜치<br>- dev 브랜치에서 안정화 버전만 병합 |
| `develop` | - 개발용 브랜치<br>- 자유롭게 병합                 |
