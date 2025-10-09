# Hotel Booking System - AI Coding Agent Instructions

## Architecture Overview

This is a **Spring Boot 3.2.0 REST API** for hotel room booking with JWT authentication, built for test automation interviews. The system follows a layered architecture:

- **Entities**: JPA entities with auditing (`@EntityListeners(AuditingEntityListener.class)`)
- **DTOs**: Request/Response objects with Jakarta validation (`@Valid`, `@NotNull`, `@Email`)
- **Repositories**: Spring Data JPA with custom query methods
- **Services**: Business logic layer (inject repositories, implement CRUD)
- **Controllers**: REST endpoints with OpenAPI docs (`@Operation`, `@Tag`)
- **Security**: JWT-based auth with role-based access (`ADMIN`/`USER`)

## Critical Patterns & Conventions

### Package Structure
Follow the established pattern: `com.hotel.booking.{layer}` where layer is `entity`, `dto`, `repository`, `service`, `controller`, `security`, `exception`, `config`.

### Entity Patterns
```java
@Entity
@Table(name = "plural_table_name")
@EntityListeners(AuditingEntityListener.class)
@Data @NoArgsConstructor @AllArgsConstructor
public class EntityName {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
}
```

### DTO Validation
Use Jakarta validation extensively. Examples: `@NotBlank`, `@Email`, `@Future` for dates, `@Min(1)` for counts. Builder pattern is standard: `@Builder`.

### Repository Queries
Custom queries follow Spring Data naming: `findByRoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual`. See `RoomRepository` for availability checking patterns.

### Controller Structure
```java
@RestController
@RequestMapping("/endpoint")
@RequiredArgsConstructor
@Tag(name = "Feature Name", description = "Description")
public class FeatureController {
    private final FeatureService service;
    
    @PostMapping
    @Operation(summary = "Create", description = "Detailed description")
    public ResponseEntity<ApiResponse<Response>> create(@Valid @RequestBody Request request) {
        Response response = service.create(request);
        return ResponseEntity.ok(ApiResponse.success("Success message", response));
    }
}
```

### Security Configuration
- **Public endpoints**: GET `/rooms/**`, POST `/bookings`, POST `/messages`, `/auth/**`
- **Admin only**: PUT/DELETE operations, `/messages` management, payment refunds
- **JWT required**: All other endpoints
- Use `@PreAuthorize("hasRole('ADMIN')")` for method-level security

## Development Workflows

### Build & Run
```powershell
mvn clean install          # Build project
mvn spring-boot:run        # Start server on :8080/api/v1
```

### Database Access
- **H2 Console**: http://localhost:8080/api/v1/h2-console
- **Credentials**: JDBC URL: `jdbc:h2:file:./data/hotelbookingdb`, User: `sa`, Password: `password`
- **Default Admin**: username: `admin`, password: `admin123`

### API Testing
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **Get JWT**: POST `/auth/login` with admin credentials
- **Use JWT**: Add `Authorization: Bearer <token>` header

## Data Flow & Integration Points

### Booking Flow
1. Check room availability: `GET /rooms/available?checkIn=date&checkOut=date`
2. Create booking: `POST /bookings` (creates guest if new, generates booking number)
3. Process payment: `POST /payments` (links to booking)
4. Admin confirms: `POST /bookings/{id}/confirm`

### Entity Relationships
- `Booking` ↔ `Room` (ManyToOne, lazy fetch)
- `Booking` ↔ `Guest` (ManyToOne, optional)
- `Payment` ↔ `Booking` (ManyToOne)
- `Message` ↔ `User` (ManyToOne for responses)

### Exception Handling
Use `@RestControllerAdvice` pattern. Throw `ResourceNotFoundException` for 404s, `BadRequestException` for 400s. All exceptions return `ApiResponse.error()` format.

## Project-Specific Guidelines

### Status Management
Entities use enums: `BookingStatus` (PENDING, CONFIRMED, CANCELLED), `RoomStatus` (AVAILABLE, OCCUPIED, MAINTENANCE), `PaymentStatus` (PENDING, COMPLETED, FAILED, REFUNDED).

### Audit Fields
All entities except `User` have `@CreatedDate` and `@LastModifiedDate`. Enable with `@EntityListeners(AuditingEntityListener.class)`.

### Booking Numbers
Auto-generated with format `BK-{timestamp}`. Implement in service layer, not entity.

### Configuration Keys
- JWT secret: `jwt.secret` (min 256 bits)
- DB path: `jdbc:h2:file:./data/hotelbookingdb`
- Context path: `/api/v1`
- Logging: Debug level for `com.hotel.booking` package

## Common Implementation Patterns

When adding new features:
1. **Entity first**: Add to `entity/` with proper annotations
2. **Repository**: Extend `JpaRepository<Entity, Long>`, add custom queries if needed
3. **DTOs**: Create `Request` and `Response` DTOs with validation
4. **Service**: Inject repository, implement business logic, throw appropriate exceptions
5. **Controller**: Inject service, add OpenAPI annotations, return `ApiResponse` wrapper
6. **Security**: Configure in `SecurityConfig` if access control needed

### Testing Strategy
H2 database auto-populates with sample data (rooms 101, 201, 301) via `DataInitializer`. Use Swagger UI for manual testing, curl examples in README.md for automation.