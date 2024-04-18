# 목차
1. [과제 구현 안내](#1-과제-구현-안내)   
   1-1. [서비스 설명](#1-1-서비스-설명)   
   1-2. [Model 설명](#1-2-Model-설명)   
   1-3. [요구 사항](#1-3-요구-사항)   
   1-4. [공통 API Response 안내](#1-4-공통-API-Response-안내)   
   1-5. [API별 상세 설명](#1-5-API별-상세-설명)   
2. [시나리오](#2-1-시나리오)

# 1. 과제 구현 안내
## 1-1. 서비스 설명
- 탁구 게임 서비스를 만들어 봅시다.
- 탁구 게임의 종류는 2가지가 존재합니다.
  - 단식(SINGLE) : 1대1로 게임을 진행합니다.
  - 복식(DOUBLE) : 2대2로 게임을 진행합니다.
- 유저들은 방을 생성하거나 만들어진 방에 참가해 게임을 진행 할 수 있습니다.
- 방에 참가한 유저들은 RED, BLUE 팀 중 한 개의 팀에 배정받습니다.
- 팀은 변경이 가능합니다.

## 1-2. Model 설명
서비스에서 사용되는 데이터 Model 은 User, Room, UserRoom 3가지 모델이 있습니다.
```
User
- id : integer
- fakerId : integer
- name : string
- email : string
- status : string
  - WAIT(대기), ACTIVE(활성), NON_ACTIVE(비활성)
- created_at : datetime
- updated_at : datetime
```
```
Room
- id : integer
- title : string
- host : integer
  - User.id
- room_type : string
  - SINGLE(단식), DOUBLE(복식)
- status : string
  - WAIT(대기), PROGRESS(진행중), FINISH(완료)
- created_at : datetime
- updated_at : datetime
```
```
UserRoom
- id : integer
- room_id : integer
- user_id : integer
- team : string
  - RED, BLUE
```

## 1-3. 요구 사항
**⭐️ port는 8080으로 설정해야 합니다.**   
**⭐️ 데이터베이스는 H2 Database를 이용합니다.**   
**⭐️ JPA를 활용하며, ddl-auto 속성은 create 를 사용합니다.**   
**⭐️ 각 Entity에 대한 delete는 soft-delete/hard-delete 에 대해 자유롭게 개발합니다.**   
**⭐️ 아래 총 10개의 API들을 구현 합니다.**   
각 API에 대한 상세 설명은 하단부에 별도로 안내됩니다.   
1. 헬스체크 API
2. 초기화 API
3. 유저 전체 조회 API
4. 방 생성 API
5. 방 전체 조회 API
6. 방 상세 조회 API
7. 방 참가 API
8. 방 나가기 API
9. 게임시작 API
10. 팀 변경 API
   
- ⭐️ 게임은 시작하고 1분 뒤 자동으로  종료됩니다. ⭐️
  - 오차 범위 30초 이내
  - 종료된 게임의 방 상태는 'FINISH(완료)' 상태로 변경됩니다.
- 방을 생성한 사람을 host 라고 명칭하고, host 가 방을 나가면 해당 방은 사라집니다.
- RED, BLUE 2개의 팀이 존재하고 서로 반/반 정확하게 인원이 나뉘어진 상태에서만 게임을 진행할 수 있습니다.
- 게임은 방에 인원이 모두 찬 상태에서만 시작 가능합니다.
  - 단식 2명, 복식 4명
- 방에 모든 인원이 가득찬 상태라면 참가 할 수 없습니다.
- 방에 참가한 인원에 대한 팀 배정 로직은 다음과 같습니다.
  - 한 쪽 팀에 인원이 모두 찬 경우, 반대팀으로 배정합니다.
  - 양쪽 팀에 모두 자리가 있는 경우, RED팀에 먼저 배정됩니다.
- 작성하신 어플리케이션은 하나의 서버에서 동작한다고 가정합니다.
  - 동시성 이슈에 대해서 고려하지 않아도 됩니다.
- 각 기능에 대한 단위테스트를 작성해야 합니다.
- API를 개발하며 필요한 유저 domain에 대한 초기화 방법은 "API 설명"을 참고하시길 바랍니다.
 
## 1-4. 공통 API Response 안내
- ApiResponse 는 아래 클래스를 활용합니다.
  - 요청이 성공했을 때, code = 200, message = "API 요청이 성공했습니다." 를 반환합니다.
  - 잘못된 API 요청은 code = 201, message = "불가능한 요청입니다." 를 반환합니다.
  - 그 외 서버 에러가 발생하는 케이스는 code = 500, message = "에러가 발생했습니다." 를 반환합니다.
 
```java
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T result;
}
```

## 1-5. API별 상세 설명
### 헬스체크 API
- 서버의 상태를 체크하는 API입니다.
- 모든 시나리오에 대해 최초 1회 호출됩니다.
> API 명세
```
GET /health
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다."
}
```
### 초기화 API
- seed와 quantity를 body에 담아서 요청합니다.
- 기존에 있던 모든 회원 정보 및 방 정보를 삭제합니다. **(즉, 모든 table의 모든 데이터를 삭제합니다.)**
- 이후 body로 전달받은 seed와 quantity정보를 통해 아래 API를 호출하여 서비스에 필요한 회원 정보를 저장합니다.
   - https://fakerapi.it/api/v1/users?_seed={seed}&_quantity={quantity}&_locale=ko_KR
- fakerapi의 응답 결과로 내려오는 데이터를 아래 규칙에 따라 세팅합니다.
   - 응답 값의 id필드는 fakerId로 저장합니다.
   - 응답 값의 id(fakerId)를 오름차순으로 정렬하여 데이터를 저장합니다.
   - username 필드는 name으로 저장합니다.
   - email 필드는 그대로 저장합니다.
   - uuid, firstname, lastname, password, ip, macAddress, website, image 필드는 사용하지 않습니다.
   - 회원 상태(status)는 응답 값의 id(fakerId)를 기반하여 아래 규칙에 따라 저장합니다.
      - 응답 값의 id(fakerId) 값이 30 이하의 회원은 활성(ACTIVE) 상태로 세팅합니다.
      - 응답 값의 id(fakerId) 값이 31 이상, 60 이하의 회원은 대기(WAIT) 상태로 세팅합니다.
      - 응답 값의 id(fakerId) 값이 61 이상인 회원은 비활성(NON_ACTIVE) 상태로 세팅합니다.
   - 데이터가 저장되는 시점에 따라 createdAt과 updatedAt을 저장합니다.
> API 명세
```
POST /init
body
{
   "seed" : int,
   "quantity" : int,
}
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다."
}
```
### 유저 전체 조회 API
- 페이징 처리를 위한 size, page 값을 RequestParameter로 받습니다.
- 모든 회원 정보를 응답합니다.
  - id 기준 오름차순으로 정렬해서 반환합니다.
> API 명세
```
GET /user?size={size}&page={page}
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다.",
  "result" : {
		"totalElements" : int,
		"totalPages" : int,
		"userList" : [
			{
				"id" : int,
				"fakerId" : int,
				"name" : string,
				"email" : string,
				"status" : string, // WAIT(대기), ACTIVE(활성), NON_ACTIVE(비활성)
				"createdAt" : string, // yyyy-MM-dd HH:mm:ss 형태로 반환합니다.
				"updatedAt" : string // yyyy-MM-dd HH:mm:ss 형태로 반환합니다.
			}, ...
		]
	}
}
```
### 방 생성 API
- userId, roomType, title 정보는 body에 담아서 요청합니다.
- 방을 생성하려고 하는 user(userId)의 상태가 활성(ACTIVE)상태일 때만, 방을 생성할 수 있습니다. 만약 활성상태가 아닐때는 201 응답을 반환합니다. ([공통 API Response 안내](#6-4-공통-API-Response-안내) 참고)
- 방을 생성하려고 하는 user(userId)가 현재 참여한 방이 있다면, 방을 생성할 수 없습니다. 만약 참여하고 있는 방이 있을때는 201 응답을 반환합니다. ([공통 API Response 안내](#6-4-공통-API-Response-안내) 참고)
- 방은 초기에 대기(WAIT) 상태로 생성됩니다.
- 데이터가 저장되는 시점에 따라 createdAt과 updatedAt을 저장합니다.
> API 명세
```
POST /room
body
{
   "userId" : int,
   "roomType" : String,
   "title" : String
}
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다."
}
```
### 방 전체 조회 API
- 페이징 처리를 위한 size, page 값을 RequestParameter로 받습니다.
- 모든 방에 대한 데이터를 반환합니다.
  - id 기준 오름차순으로 데이터를 반환합니다.
> API 명세
```
GET /room?size={size}&page={page}
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다.",
  "result" : {
		"totelElements" : int,
		"totalPages" : int,
		"roomList" : [
			{
				"id" : int,
				"title" : string,
				"hostId" : int,
				"roomType" : string, // SINGLE(단식), DOUBLE(복식)
				"status" : string, // WAIT(대기), PROGRESS(진행중), FINISH(완료)
			}, ...
		]
	}
}
```
### 방 상세 조회 API
- roomId를 받아 방에 대한 상세 조회를 합니다.
- createdAt, updatedAt을 함께 반환합니다.
- 존재하지 않는 id에 대한 요청이라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
> API 명세
```
GET /room/{roomId}
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다.",
  "result" : {
		"id" : int,
		"title" : string,
		"hostId" : int,
		"roomType" : string, // SINGLE(단식), DOUBLE(복식)
		"status" : string, // WAIT(대기), PROGRESS(진행중), FINISH(완료)
		"createdAt" : string, // yyyy-MM-dd HH:mm:ss 형태로 반환합니다.
		"updatedAt" : string // yyyy-MM-dd HH:mm:ss 형태로 반환합니다.
	}
}
```
### 방 참가 API
- 대기(WAIT) 상태인 방에만 참가가 가능합니다. 만약 대기상태가 아닌 방이라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 유저(userId)가 활성(ACTIVE) 상태일 때만, 방에 참가할 수 있습니다. 만약 활성상태가 아니라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 유저(userId)가 현재 참여한 방이 없을때만, 방에 참가할 수 있습니다. 만약 참여한 방이 있다면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 참가하고자 하는 방(roomId)의 정원이 미달일 때만, 참가가 가능합니다. 만약 방에 인원이 가득 찼다면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 존재하지 않는 id에 대한 요청이라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
> API 명세
```
POST /room/attention/{roomId}
body
{
   "userId" : int
}
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다."
}
```
### 방 나가기 API
- 유저(userId)가 현재 해당 방(roomId)에 참가한 상태일 때만, 나가기가 가능합니다. 만약 참가한 상태가 아니라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 이미 시작(PROGRESS) 상태인 방이거나 끝난(FINISH) 상태의 방은 나갈 수 없습니다. 만약 그러한 상태라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 호스트가 방을 나가게 되면 방에 있던 모든 사람도 해당 방에서 나가게 됩니다.
  - 해당 방은 끝난(FINISH) 상태가 됩니다.
- 존재하지 않는 id에 대한 요청이라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
> API 명세
```
POST /room/out/{roomId}
body
{
   "userId" : int
}
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다."
}
```
### 게임시작 API
- 호스트인 유저만 게임을 시작할 수 있습니다. 만약 호스트가 아니라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 방 정원이 방의 타입에 맞게 모두 꽉 찬 상태에서만 게임을 시작할 수 있습니다. 만약 그렇지 않다면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 현재 방의 상태가 대기(WAIT) 상태일 때만 시작할 수 있습니다. 만약 그렇지 않다면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 방의 상태를 진행중(PROGRESS) 상태로 변경합니다.
- 게임시작이 된 방은 1분 뒤 종료(FINISH) 상태로 변경됩니다.
- 존재하지 않는 id에 대한 요청이라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
> API 명세
```
PUT /room/start/{roomId}
body
{
   "userId" : int
}
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다."
}
```
### 팀 변경 API
- 유저(userId)가 현재 해당 방(roomId)에 참가한 상태에서만 팀 변경이 가능합니다. 만약 그렇지 않다면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 유저(userId)가 현재 속한 팀 기준 반대 팀으로 변경됩니다. (RED -> BLUE / BLUE -> RED)
- 변경되려는 팀의 인원이 이미 해당 방 정원의 절반과 같다면 팀이 변경되지 않고 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 현재 방의 상태가 대기(WAIT) 상태일 때만 팀을 변경할 수 있습니다. 만약 그렇지 않다면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
- 존재하지 않는 id에 대한 요청이라면 201 응답을 반환합니다. ([공통 API Response 안내](#1-4-공통-API-Response-안내) 참고)
> API 명세
```
PUT /team/{roomId}
body
{
   "userId" : int
}
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다."
}
```

# 2. 시나리오
헬스체크 API는 모든 시나리오 시작에 호출됩니다.
### 1. 헬스체크
> Request
```bash
curl -X GET http://localhost:8080/health
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다."
}
```
### 2. 유저 전체 조회
> Request
```bash
curl -X GET http://localhost:8080/user?size=100&page=0
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다.",
  "result" : {
		"totalElements" : 0,
		"totalPages" : 0,
		"userList" : []
	}
}
```
### 3. 초기화
> Request
```bash
curl -X POST http://localhost:8080/init -d '{"seed":123, "quantity":10}'
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다."
}
```
### 4. 유저 전체 조회
> Request
```bash
curl -X GET http://localhost:8080/user?size=10&page=0
```
> Response
```json
{
  "code" : 200,
  "message" : "API 요청이 성공했습니다.",
  "result" : {
      "totalElements" : 10,
      "totalPages" : 0,
      "userList" : [
         {
            "id": 1,
            "fakerId": 1,
            "name": "jungran.gwon",
            "email": "knam@yahoo.com",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         },
         {
            "id": 2,
            "fakerId": 2,
            "name": "myungho.lim",
            "email": "go.minji@hotmail.com",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         },
         {
            "id": 3,
            "fakerId": 3,
            "name": "jiwoo.ko",
            "email": "nkang@yu.com",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         },
         {
            "id": 4,
            "fakerId": 4,
            "name": "minseok.moon",
            "email": "junhyuk16@chung.biz",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         },
         {
            "id": 5,
            "fakerId": 5,
            "name": "yunyoung01",
            "email": "sungho.hwang@yahoo.com",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         },
         {
            "id": 6,
            "fakerId": 6,
            "name": "qheo",
            "email": "sangho12@jung.org",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         },
         {
            "id": 7,
            "fakerId": 7,
            "name": "myungho.han",
            "email": "xlim@lim.net",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         },
         {
            "id": 8,
            "fakerId": 8,
            "name": "naree.gwon",
            "email": "lbaek@ahn.net",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         },
         {
            "id": 9,
            "fakerId": 9,
            "name": "jo.hyunjun",
            "email": "miyoung11@gmail.com",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         },
         {
            "id": 10,
            "fakerId": 10,
            "name": "kwak.jumyoung",
            "email": "jeon.doyoon@hotmail.com",
            "status": "ACTIVE",
            "createdAt": "2024-01-14 02:22:45",
            "updatedAt": "2024-01-14 02:22:45"
         }
      ]
  }
}
```
