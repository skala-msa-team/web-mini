# Frontend

Vue 3와 Vite 기반의 최소 Frontend 프로젝트입니다. Vue Router, Axios와 STOMP Client는 의존성만 설치했으며 Router, API, STOMP 설정과 기능 코드는 아직 추가하지 않았습니다. 상태 관리 Library는 미정입니다.

```bash
npm install
npm run dev
npm run build
```

## Frontend 브랜치 운영 규칙

Frontend 작업은 공용 브랜치인 `frontend`를 기준으로 진행합니다. 각 팀원은 담당 역할이나 기능에 맞는 작업 브랜치를 생성하고, 작업이 완료되면 Pull Request를 통해 `frontend` 브랜치에 병합합니다.

브랜치 이름은 `frontend/{작업명}` 형식을 사용합니다. 아래는 예시입니다.

```text
frontend/login
frontend/home
frontend/trial-form
frontend/live-trial
frontend/result
```

### 작업 순서

1. 최신 `frontend` 브랜치를 가져옵니다.

   ```bash
   git switch frontend
   git pull origin frontend
   ```

2. 담당 역할이나 기능에 맞는 작업 브랜치를 생성합니다.

   ```bash
   git switch -c frontend/trial-form
   ```

3. 작업 내용을 커밋하고 원격 저장소에 올립니다.

   ```bash
   git add .
   git commit -m "feat(frontend): 재판 신청 폼 구현"
   git push -u origin frontend/trial-form
   ```

4. GitHub에서 `frontend/trial-form`을 `frontend`로 병합하는 Pull Request를 생성합니다.
5. 코드 리뷰와 충돌 확인이 끝난 뒤 `frontend` 브랜치에 병합합니다.
6. Frontend 기능을 모두 통합하고 검증한 뒤 `frontend`에서 `dev`로 Pull Request를 생성합니다.

### 주의 사항

- `frontend` 브랜치에는 직접 Commit하거나 Push하지 않습니다.
- 하나의 작업 브랜치에서는 하나의 역할 또는 기능만 처리합니다.
- 새로운 작업은 최신 `frontend`를 기준으로 브랜치를 생성합니다.
- 공용 코드가 변경된 경우 최신 `frontend`를 반영하고 충돌을 해결한 뒤 Pull Request를 갱신합니다.
- Frontend와 Backend 변경을 하나의 작업 브랜치나 Pull Request에 섞지 않습니다.

```text
frontend/{작업명}
        ↓ Pull Request
     frontend
        ↓ Frontend 통합 검증 후 Pull Request
        dev
        ↓ 최종 통합 검증
        main
```
