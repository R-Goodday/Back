# 🔥 Project Convention
> `src/main` 하위에 `resources` 디렉토리와 `application.properties`를 생성해주세요!
## 🛠️ Build Info
- **Language** : Java 21
- **Framework** : Spring boot 3.5.6
- **Database** : MySQL
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
## 📌 Git Branch Strategy
| branch    | role                                   |
|-----------|----------------------------------------|
| `main`    | - 최종 배포용 브랜치<br>- dev 브랜치에서 안정화 버전만 병합 |
| `develop` | - 개발용 브랜치<br>- 자유롭게 병합                 |
