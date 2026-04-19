
# StayEase

StayEase is a backend REST API that streamlines hotel room booking for a hotel management aggregator application.  
It supports user registration/login with **JWT-based stateless authentication**, **role-based authorization**, hotel management, and booking workflows — all exposed through clean REST endpoints.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen?logo=springboot)  
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)  
![Gradle](https://img.shields.io/badge/Gradle-8.8-02303A?logo=gradle)

---

## Key Features

| Feature                          | Description                                                         |
|----------------------------------|---------------------------------------------------------------------|
| **User Registration & Login**    | Sign up and login using email/password, BCrypt hashed passwords     |
| **JWT Authentication**           | Stateless authentication using Bearer tokens                        |
| **Role-Based Access Control**    | Roles: `USER`, `HOTEL_MANAGER`, `ADMIN`                             |
| **Hotel Management**             | Browse hotels publicly; Admin can create/delete; Manager can update |
| **Booking Management**           | Users can book rooms; Managers can cancel bookings                  |
| **Room Availability Protection** | Hotels track `availableRooms` to prevent overbooking                |
| **Validation & Error Handling**  | Input validation + consistent HTTP error responses                  |
| **DTO + MapStruct**              | Clean separation between API contracts and entities                 |
| **Logging**                      | SLF4J/Logback logging across layers                                 |
| **Unit Tests**                   | JUnit5 + Mockito + MockMvc (minimum 3 tests)                        |

---

## Assumptions (as per problem statement by Crio)

- Only **one type of room** exists, and all bookings are for **two guests**.
- Any hotel manager can update any hotel (no manager-hotel ownership tracking).
- Another service handles check-in/check-out workflows (this service only manages bookings).

---

## Tech Stack

- **Language:** Java 17 (OpenJDK 17.0.18)
- **Framework:** Spring Boot 3.3.3
- **Security:** Spring Security + JWT
- **ORM:** Spring Data JPA / Hibernate
- **Database:** MySQL 8.0.43
- **Build Tool:** Gradle Wrapper 8.8
- **Mapping:** MapStruct
- **Utilities:** Lombok, Jakarta Validation
- **Testing:** JUnit 5, Mockito, MockMvc

---

## Roles & Access Rules

| Role            | Permissions                                    |
|-----------------|------------------------------------------------|
| `USER`          | Register/Login; Browse hotels; Create bookings |
| `HOTEL_MANAGER` | Update hotel details; Cancel bookings          |
| `ADMIN`         | Create hotels; Delete hotels                   |

**Business rules**
1. Users can be `USER`, `HOTEL_MANAGER`, or `ADMIN`.
2. Only admins can create/delete hotels.
3. Only managers can update hotels and cancel bookings.
4. Users cannot cancel their own bookings.
5. Hotels track available rooms to prevent overbooking.
6. Dates must be in `YYYY-MM-DD`.
7. Check-in must be a future date.
8. Check-out must be after check-in.

---

## ER Diagram (Mermaid)

Use the below `erDiagram` code in [mermaid.live](https://mermaid.live) to visualize the schema.

```erDiagram
erDiagram
    USER {
        BIGINT id PK
        VARCHAR email UK
        VARCHAR password
        VARCHAR first_name
        VARCHAR last_name
        ENUM role
        TIMESTAMP created_at
    }

    HOTEL {
        BIGINT id PK
        VARCHAR name
        VARCHAR location
        TEXT description
        INT total_rooms
        INT available_rooms
        TIMESTAMP created_at
    }

    BOOKING {
        BIGINT id PK
        BIGINT user_id FK
        BIGINT hotel_id FK
        DATE check_in_date
        DATE check_out_date
        ENUM status
        TIMESTAMP created_at
    }

    USER ||--o{ BOOKING : "makes"
    HOTEL ||--o{ BOOKING : "has"
```

---

## Getting Started

### Prerequisites

- Java 17
- MySQL 8.0+
- Git

> No need to install Gradle separately — the project uses the Gradle Wrapper.

---

## Configuration

Update `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.application.name=stayease
jwt.secret=MyVerySecretKeyThatShouldBeLongAndRandom
jwt.expiration=3600000
  
# Database Configuration  
server.port=8081
#Database Configuration  
spring.datasource.url=jdbc:mysql://localhost:3306/stayease_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=Test@1234
  
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
  
logging.level.com.takehome.stayease=DEBUG
logging.level.org.springframework.security=INFO
  
server.error.include-message=always
```

---

## Running the Application

```bash
# Linux / macOS
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

The API will start at:

- Base URL: `http://localhost:8081/`

---

## Running Tests

```bash
./gradlew test
```

---
## Postman collection

A ready-to-use Postman collection is included: [`StayEase_API.postman_collection.json`](StayEase_API.postman_collection.json).

The collection includes:
1. Post-response scripts that automatically extract and set collection variables:
   customerToken — from Register Customer, Login Successfully
   managerToken — from Register Hotel Manager
   adminToken — from Register Admin
   hotelId — from Admin Create Hotel, Create Hotel with Limited Rooms
   bookingId — from Book a Room
2. Collection has description detailing the required execution order and dependencies between requests.

---
## API Reference

### Base URL

`http://localhost:8081/`

### Authentication

Private endpoints require:

- `Authorization: Bearer <JWT_TOKEN>`

JWT is returned by register/login endpoints.

---

## Endpoints

### 1) Register User (Public)

`POST /api/users/register`

Request:
```json
{
  "email": "user@example.com",
  "password": "Test@123!",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

Response (`200 OK`):
```json
{
  "token": "jwt-token-here"
}
```

Errors:
- `400` Invalid input (email/password format)
- `404` User already exists

---

### 2) Login (Public)

`POST /api/users/login`

Request:
```json
{
  "email": "user@example.com",
  "password": "Test@123!"
}
```

Response (`200 OK`):
```json
{
  "token": "jwt-token-here"
}
```

Errors:
- `404` Invalid credentials

---

### 3) Create Hotel (Admin Only)

`POST /api/hotels`

Headers:
- `Authorization: Bearer {adminToken}`

Request:
```json
{
  "name": "Hotel Name",
  "location": "Hotel Location",
  "description": "Hotel Description",
  "totalRooms": 10,
  "availableRooms": 10
}
```

Response (`200 OK`):
```json
{
  "id": 1,
  "name": "Hotel Name",
  "location": "Hotel Location",
  "description": "Hotel Description",
  "totalRooms": 10,
  "availableRooms": 10
}
```

Errors:
- `401` Unauthorized
- `403` Forbidden

---

### 4) Get All Hotels (Public)

`GET /api/hotels`

Response (`200 OK`):
```json
[
  {
    "id": 1,
    "name": "Hotel Name",
    "location": "Location",
    "description": "Description",
    "availableRooms": 10
  }
]
```

---

### 5) Update Hotel (Manager Only)

`PUT /api/hotels/{hotelId}`

Headers:
- `Authorization: Bearer {managerToken}`

Request:
```json
{
  "name": "Updated Name",
  "availableRooms": 15
}
```

Response (`200 OK`):
```json
{
  "id": 1,
  "name": "Updated Name",
  "location": "Location",
  "description": "Description",
  "totalRooms": 10,
  "availableRooms": 15
}
```

Errors:
- `401` Unauthorized
- `404` Hotel not found

---

### 6) Delete Hotel (Admin Only)

`DELETE /api/hotels/{hotelId}`

Headers:
- `Authorization: Bearer {adminToken}`

Response:
- `204 No Content`

Errors:
- `401` Unauthorized
- `404` Hotel not found

---

### 7) Create Booking (User Only)

`POST /api/bookings/{hotelId}`

Headers:
- `Authorization: Bearer {userToken}`

Request:
```json
{
  "checkInDate": "2026-05-20",
  "checkOutDate": "2026-05-22"
}
```

Response (`200 OK`):
```json
{
  "bookingId": 1,
  "hotelId": 1,
  "checkInDate": "2026-05-20",
  "checkOutDate": "2026-05-22"
}
```

Errors:
- `401` Unauthorized
- `404` Hotel not found / No rooms available

---

### 8) Get Booking Details (Authenticated)

`GET /api/bookings/{bookingId}`

Headers:
- `Authorization: Bearer {token}`

Response (`200 OK`):
```json
{
  "bookingId": 1,
  "hotelId": 1,
  "checkInDate": "2026-05-20",
  "checkOutDate": "2026-05-22"
}
```

Errors:
- `401` Unauthorized
- `404` Booking not found

---

### 9) Cancel Booking (Manager Only)

`DELETE /api/bookings/{bookingId}`

Headers:
- `Authorization: Bearer {managerToken}`

Response:
- `204 No Content`

Errors:
- `401` Unauthorized
- `404` Booking not found

---

## Notes

- Passwords are stored as **BCrypt hashes**.
- Room availability is enforced using the `availableRooms` field (prevents overbooking).
- The service is stateless: authentication relies on JWT for each request.

---