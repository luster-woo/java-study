# SPA의 인증 처리

> 교재: 15기_자바전공_04_Vue_with_Jwt_0609 (전공) — 2. SPA의 인증 처리

> [← refresh token](10_refresh_token.md) · [day13 목록](../README.md)

---

## jwt-decode

### jwt token decode 라이브러리

- **jwt-decode**: JWT의 payload를 디코딩해 JS 객체로 돌려주는 라이브러리(npm `jwt-decode`)
  - 서명 **검증은 하지 않음** → 화면 표시용 정보(이름·이메일·권한)를 꺼낼 때 사용
- 설치

```bash
pnpm add jwt-decode
```

- 기본 사용법

```js
import { jwtDecode } from 'jwt-decode'

const token = 'eyJ0eXAiO...'          // jwt token
const decoded = jwtDecode(token)
// 결과(예): { sub: 'accessToken', email: '...', name: '...', role: 'USER', exp: 1780000000 }
```

---

## 토큰 관리

### store/member.js — 토큰 관리 (TODO: 21)

- Pinia store에 토큰을 보관하고 login·logout·refresh 처리

| 함수 | 하는 일 |
|---|---|
| `login(tokens)` | accessToken 디코딩 → member(name, email, role)로 로그인 상태 갱신 → 토큰 저장 |
| `logout()` | 로그인 상태 `null` → 토큰 비움 |
| `refresh(tokens)` | 새로 받은 토큰으로 교체 |

```js
const _tokens = ref({})
const tokens = computed(() => _tokens.value)

const login = (tokens) => {
  const decoded = jwtDecode(tokens.accessToken)
  const member = { name: decoded.name, email: decoded.email, role: decoded.role }
  updateLoginStatus(member)
  _tokens.value = tokens
}

const logout = () => {
  updateLoginStatus(null)
  _tokens.value = {}
}

const refresh = (tokens) => {
  _tokens.value = tokens
}
```

- `_tokens`는 내부용, 밖에는 읽기 전용 `computed`인 `tokens`만 노출

### restapi/index.js — 토큰 전달 (TODO: 22)

- 인증이 필요한 `ssafyapi`(axios 인스턴스) 요청에는 **요청 인터셉터**에서 Authorization 헤더를 자동으로 붙임
- login, refresh 등에는 access token이 **없어야** 함 → 요청에 `skipAuth` 헤더를 달면 건너뜀

```js
ssafyapi.interceptors.request.use(
  (config) => {
    console.log('[요청]', config)
    if (!config.headers['skipAuth']) {
      const memberStore = useMemberStore()
      if (memberStore.tokens?.accessToken) {
        config.headers['Authorization'] = `Bearer ${memberStore.tokens.accessToken}`
      }
    }
    return config
  },
  (error) => { /* ... */ }
)
```

---

## login · logout · refresh 처리

### login 처리 — useMemberApis.js (TODO: 23)

- `skipAuth: true` → Authorization 헤더 없이 요청
- 서버의 JWTAuthenticationFilter(UsernamePasswordAuthenticationFilter 확장)는 email·password를 **요청 파라미터**로 읽음 → JSON이 아니라 **`application/x-www-form-urlencoded`**로 보내야 함
- 응답(`{accessToken, refreshToken}`)을 store의 `login()`에 넘김

```js
const login = async (member) => {
  try {
    const res = await ssafyapi.post('/auth/login', member, {
      headers: {
        skipAuth: true,
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    })
    memberStore.login(res.data)
  } catch (err) {
    console.log('useLogin callApi error:', err)
    memberStore.setAlertMsg('로그인 처리 중 오류가 발생했습니다.')
  }
  router.push({ name: 'home' })
  return true
}
```

```java
// 서버 쪽 - 파라미터 이름이 맞아야 함
public JWTAuthenticationFilter(...) {
    // ...
    this.setFilterProcessesUrl("/api/v1/auth/login");
    this.setUsernameParameter("email");
    this.setPasswordParameter("password");
}
```

### logout 처리 — useMemberApis.js (TODO: 23)

- 목적은 **refresh token 전송**(서버 DB에서 삭제)
- access token은 이미 만료됐을 수도 있음 → `skipAuth: true`로 검증을 건너뜀
- 서버 로그아웃이 실패해도 무시하고, `finally`에서 클라이언트 토큰을 비우고 홈으로

```js
const logout = async () => {
  try {
    await ssafyapi.post('/auth/logout', null, {
      headers: {
        skipAuth: true,
        'Refresh-Token': memberStore.tokens.refreshToken,
      },
    })
  } catch (err) {
    console.log('useLogout callApi error:', err)   // 로그아웃 실패는 무시
  } finally {
    memberStore.logout()
    router.push({ name: 'home' })
  }
}
```

```
/api/v1/auth/logout (Refresh-Token 헤더)
  → JWTVerificationFilter : Authorization Header? NO → 통과
  → permitAll → @PostMapping("/api/v1/auth/logout")에서 Refresh-Token 확인
```

### refresh 처리 — useMemberApis.js (TODO: 24)

- `/auth/refresh`에 `Refresh-Token` 헤더만 실어 요청(`skipAuth: true`) → 받은 새 토큰을 store `refresh()`로 교체
- 프로젝트의 공통 composable `useApi(callApi)`로 감싸 `data`·`error`·`execute`를 받음

```js
export const useTokenRefresh = () => {
  const memberStore = useMemberStore()
  const callApi = async () => {
    const res = await ssafyapi.post('/auth/refresh', null, {
      headers: {
        skipAuth: true,
        'Refresh-Token': memberStore.tokens.refreshToken,
      },
    })
    memberStore.refresh(res.data)
    return res.data
  }
  const { data, error, execute } = useApi(callApi)
  return { newTokens: data, tokenRefreshError: error, tokenRefreshExecute: execute }
}
```

---

## 권한 관련 오류 처리

### 응답 인터셉터 — TOKEN_ERROR면 token refresh (TODO: 24)

- access token이 만료되면 서버(SecurityExceptionHandlingFilter)가 **401 + `"TOKEN_ERROR"`** 응답
- **응답 인터셉터**에서 이를 잡아 refresh → 성공하면 **원래 요청(`error.config`)을 재시도**
- refresh token까지 만료된 경우 → 다시 로그인하라고 알리고 logout

```
client                                         server
  │── 1. 만료 access token ────────────────────▶│
  │◀── 2. 401 Unauthorized, TOKEN_ERROR ────────│
  │── 3. 유효 refresh token ───────────────────▶│
  │◀── 4. new tokens ───────────────────────────│  → 원래 요청 재시도
  │
  │── 3'. 만료 refresh token ──────────────────▶│
  │◀── 4'. 401 Unauthorized, INVALID_REFRESH_TOKEN │  → "다시 로그인하세요", logout
```

```js
ssafyapi.interceptors.response.use(
  /* ... 정상 응답 처리 ... */
  async (error) => {
    console.error('[응답 에러]', error)
    const memberStore = useMemberStore()
    const { tokenRefreshExecute } = useTokenRefresh()

    if (error.status === 401) {
      const originalRequest = error.config
      if (error.response.data.message === 'TOKEN_ERROR') {
        const result = await tokenRefreshExecute()
        if (result) {
          return ssafyapi(originalRequest)   // 새 토큰으로 원래 요청 재시도
        }
      } else {                               // refresh도 만료된 경우
        memberStore.setAlertMsg('다시 로그인하세요')
        memberStore.logout()
      }
    } else {                                 // 401 이외의 오류
      return Promise.reject(error)
    }
  }
)
```

- 재시도 시 요청 인터셉터가 다시 돌므로 store에 바뀐 **새 access token**이 Authorization에 실림

---

## 참고

- 교재 코드의 `'/auth/refresh’`, `'다시 로그인하세요’` 등은 여는 따옴표와 닫는 따옴표가 달라(둥근 따옴표 섞임) 그대로 붙이면 문법 오류 → 노트에서는 모두 `'`로 통일
- axios 1.x는 `Content-Type`이 `application/x-www-form-urlencoded`이면 객체 data를 자동으로 폼 형식으로 직렬화함. 직접 하려면 `new URLSearchParams(member)`
- `error.status`는 최근 axios 버전의 `AxiosError`에 있는 속성 — 구버전 호환이 필요하면 `error.response?.status`가 안전
- 401 분기에서 아무것도 `return`하지 않으면 호출한 쪽은 에러 대신 `undefined`로 **정상 resolve**된 결과를 받음 → 호출부에서 실패를 알아야 하면 끝에 `return Promise.reject(error)`
- jwt-decode는 서명을 검증하지 않으므로 화면 표시용으로만 쓰고, 권한 판단은 항상 서버가 함
