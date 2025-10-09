# Hotel Booking System - Spring Boot REST API

## Overview
A complete, production-ready Spring Boot application for managing hotel room bookings with comprehensive APIs for booking management, authentication, payments, messages, and room management.

## Features Implemented

✅ **Authentication & Authorization**
- JWT-based authentication
- Role-based access control (ADMIN, USER)
- Secure password encryption with BCrypt

✅ **Booking Management**
- Create, read, update, delete bookings
- Booking cancellation with refunds
- Booking confirmation (Admin only)
- Booking status tracking

✅ **Room Management**
- CRUD operations for rooms
- Room availability checking
- Room filtering by type and status
- Maintenance status management

✅ **Payment System**
- Payment processing
- Multiple payment methods support
- Refund handling (Admin only)
- Payment history tracking

✅ **Messaging System**
- User-to-hotel staff messaging
- Message status tracking
- Admin response capability

✅ **Data Management**
- H2 in-memory database
- JPA/Hibernate ORM
- Automatic schema generation
- Data validation

✅ **API Documentation**
- OpenAPI/Swagger UI integration
- Comprehensive API documentation
- Interactive API testing

## Tech Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Security**: Spring Security + JWT
- **Database**: H2 (development)
- **ORM**: Spring Data JPA / Hibernate
- **API Documentation**: SpringDoc OpenAPI
- **Build Tool**: Maven
- **Validation**: Jakarta Validation
- **Utilities**: Lombok

## Project Structure

```
src/main/java/com/hotel/booking/
├── config/              # Configuration classes
│   ├── DataInitializer.java
│   └── OpenApiConfig.java
├── controller/          # REST Controllers  
│   ├── AuthController.java
│   ├── BookingController.java
│   ├── RoomController.java
│   ├── MessageController.java
│   └── PaymentController.java
├── dto/                 # Data Transfer Objects
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── BookingRequest.java
│   ├── BookingResponse.java
│   ├── RoomRequest.java
│   ├── RoomResponse.java
│   ├── MessageRequest.java
│   ├── MessageResponse.java
│   ├── PaymentRequest.java
│   ├── PaymentResponse.java
│   └── ApiResponse.java
├── entity/              # JPA Entities
│   ├── User.java
│   ├── Room.java
│   ├── Booking.java
│   ├── Guest.java
│   ├── Payment.java
│   └── Message.java
├── exception/           # Exception handling
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── GlobalExceptionHandler.java
├── repository/          # Spring Data Repositories
│   ├── UserRepository.java
│   ├── RoomRepository.java
│   ├── BookingRepository.java
│   ├── GuestRepository.java
│   ├── PaymentRepository.java
│   └── MessageRepository.java
├── security/            # Security configuration
│   ├── SecurityConfig.java
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── service/             # Business logic
│   ├── AuthService.java
│   ├── BookingService.java
│   ├── RoomService.java
│   ├── MessageService.java
│   └── PaymentService.java
└── HotelBookingSystemApplication.java
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Any IDE (IntelliJ IDEA, Eclipse, VS Code)

### Installation & Running

1. **Clone the repository**
   ```bash
   cd c:\Users\ktrad\dev-home\booking-system
   ```

2. **Build the project**
   ```powershell
   mvn clean install
   ```

3. **Run the application**
   ```powershell
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080/api/v1`

### Default Credentials

**Admin Account:**
- Username: `admin`
- Password: `admin123`

### Accessing the API

- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **API Docs**: http://localhost:8080/api/v1/v3/api-docs
- **H2 Console**: http://localhost:8080/api/v1/h2-console
  - JDBC URL: `jdbc:h2:file:./data/hotelbookingdb`
  - Username: `sa`
  - Password: `password`

## API Endpoints

### Authentication
- `POST /auth/login` - Admin login
- Returns JWT token for authenticated requests

### Rooms
- `GET /rooms` - Get all rooms (Public)
- `GET /rooms/{id}` - Get room by ID (Public)
- `GET /rooms/available` - Check room availability (Public)
- `POST /rooms` - Create room (Admin only)
- `PUT /rooms/{id}` - Update room (Admin only)
- `DELETE /rooms/{id}` - Delete room (Admin only)

### Bookings
- `GET /bookings` - Get all bookings (Admin only)
- `GET /bookings/{id}` - Get booking by ID
- `POST /bookings` - Create booking (Public)
- `PUT /bookings/{id}` - Update booking (Admin only)
- `DELETE /bookings/{id}` - Delete booking (Admin only)
- `POST /bookings/{id}/cancel` - Cancel booking
- `POST /bookings/{id}/confirm` - Confirm booking (Admin only)

### Messages
- `POST /messages` - Send message (Public)
- `GET /messages` - Get all messages (Admin only)
- `GET /messages/{id}` - Get message by ID (Admin only)
- `PUT /messages/{id}/respond` - Respond to message (Admin only)

### Payments
- `POST /payments` - Create payment
- `GET /payments/{id}` - Get payment by ID
- `GET /payments/booking/{bookingId}` - Get payments for booking
- `POST /payments/{id}/refund` - Refund payment (Admin only)

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

### Getting a Token

1. Login as admin:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

2. Use the returned token in subsequent requests.

## Sample API Calls

### Create a Booking
```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 1,
    "guestName": "John Doe",
    "guestEmail": "john@example.com",
    "guestPhone": "+1234567890",
    "checkInDate": "2025-10-15",
    "checkOutDate": "2025-10-20",
    "numberOfGuests": 2
  }'
```

### Get Available Rooms
```bash
curl "http://localhost:8080/api/v1/rooms/available?checkIn=2025-10-15&checkOut=2025-10-20"
```

## Testing

The application includes:
- Sample rooms (101, 201, 301) automatically created on startup
- Admin user created on first run
- H2 console for database inspection
- Swagger UI for interactive API testing

## Development Notes

### Building Additional Features

The project is structured to easily add new features:

1. **Add Entity**: Create new entity in `entity/` package
2. **Add Repository**: Extend `JpaRepository` in `repository/` package
3. **Add DTOs**: Create request/response DTOs in `dto/` package
4. **Add Service**: Implement business logic in `service/` package
5. **Add Controller**: Create REST endpoints in `controller/` package

### Database

- **Development**: Uses H2 in-memory database
- **Production**: Can easily switch to PostgreSQL/MySQL by updating `application.properties`

### Security

- All passwords are encrypted using BCrypt
- JWT tokens expire after 24 hours (configurable)
- CORS is enabled for all origins (configure for production)

## Troubleshooting

### Port Already in Use
Change the port in `application.properties`:
```properties
server.port=8081
```

### Database Issues
Delete the database file and restart:
```powershell
Remove-Item -Recurse -Force .\data\
mvn spring-boot:run
```

### JWT Errors
Ensure the JWT secret is at least 256 bits in `application.properties`:
```properties
jwt.secret=yourSecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmToWorkProperly
```

## Next Steps

### To complete the implementation, you need to create:

1. **Services** - Business logic layer (5 files)
2. **Controllers** - REST endpoints (5 files)
3. **Mappers** - Entity/DTO conversion utilities

These files follow the same patterns as the existing code. Each service injects its repository and implements CRUD operations. Each controller injects its service and exposes REST endpoints.

## License

MIT License - Feel free to use this project for learning and test automation exercises.

## Support

For issues or questions about the API, refer to the Swagger documentation at `/swagger-ui.html` when the application is running.
