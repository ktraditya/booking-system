# 🏨 Hotel Booking System - Complete Guide for Dummies

A comprehensive Spring Boot REST API for managing hotel room bookings with JWT authentication, built for learning and demonstration purposes.

## 📋 Table of Contents
- [🚀 Quick Start](#-quick-start)
- [🏗️ Project Structure](#️-project-structure)
- [📁 Folder Guide](#-folder-guide)
- [🔧 Key Components](#-key-components)
- [🛡️ Security & Authentication](#️-security--authentication)
- [🌐 API Endpoints](#-api-endpoints)
- [💾 Database](#-database)
- [📚 How It All Works Together](#-how-it-all-works-together)
- [🧪 Testing Guide](#-testing-guide)

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Any IDE (VS Code, IntelliJ, Eclipse)

### Running the Application
```bash
# 1. Clone the repository
git clone https://github.com/ktraditya/booking-system.git
cd booking-system

# 2. Build the project
mvn clean install

# 3. Run the application
mvn spring-boot:run

### Default Login Credentials
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: ADMIN (can access all endpoints)

## 🏗️ Project Structure

```
booking-system/
├── src/main/java/com/hotel/booking/          # Main application code
│   ├── HotelBookingSystemApplication.java    # 🚀 Application entry point
│   ├── config/                               # ⚙️ Configuration classes
│   ├── controller/                           # 🌐 REST API endpoints
│   ├── dto/                                  # 📦 Data Transfer Objects
│   ├── entity/                               # 🗃️ Database entities
│   ├── exception/                            # ❌ Error handling
│   ├── repository/                           # 💾 Database access layer
│   ├── security/                             # 🛡️ Authentication & security
│   └── service/                              # 🔧 Business logic
├── src/main/resources/                       # Configuration files
│   └── application.properties                # 📝 App settings
├── pom.xml                                   # 📋 Maven dependencies
└── target/                                   # 🔨 Compiled files (auto-generated)
```

## 📁 Folder Guide

### 🚀 `HotelBookingSystemApplication.java`
**What it does**: The main entry point of your Spring Boot application
```java
@SpringBootApplication  // This tells Spring Boot to start here
public class HotelBookingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelBookingSystemApplication.class, args);
    }
}
```

### ⚙️ `config/` - Configuration Classes
**What it does**: Sets up how the application behaves

- **`DataInitializer.java`**: Creates sample data when the app starts
  - Creates an admin user (admin/admin123)
  - Creates 3 sample rooms (101, 201, 301)
  
- **`OpenApiConfig.java`**: Configures Swagger UI documentation
  - Makes the pretty API documentation you see at `/swagger-ui.html`

### 🌐 `controller/` - REST API Endpoints
**What it does**: Handles HTTP requests from users/frontend

- **`AuthController.java`**: Login and authentication
  - `POST /auth/login` - User login
  
- **`RoomController.java`**: Room management
  - `GET /rooms` - List all rooms
  - `GET /rooms/available` - Check room availability
  - `POST /rooms` - Create new room (Admin only)
  - `PUT /rooms/{id}` - Update room (Admin only)
  
- **`BookingController.java`**: Booking management
  - `POST /bookings` - Create new booking
  - `GET /bookings` - List bookings
  - `POST /bookings/{id}/confirm` - Confirm booking (Admin only)
  
- **`PaymentController.java`**: Payment processing
  - `POST /payments` - Process payment
  - `POST /payments/{id}/refund` - Refund payment (Admin only)
  
- **`MessageController.java`**: Customer messages
  - `POST /messages` - Send message to hotel
  - `GET /messages` - View messages (Admin only)

### 📦 `dto/` - Data Transfer Objects
**What it does**: Defines the structure of data sent/received via API

**Think of DTOs as "forms"**:
- **`BookingRequest.java`**: Form to create a booking
- **`BookingResponse.java`**: Information returned after creating booking
- **`LoginRequest.java`**: Login form (username/password)
- **`AuthResponse.java`**: Login response (includes JWT token)
- **`ApiResponse.java`**: Standard wrapper for all API responses

### 🗃️ `entity/` - Database Entities
**What it does**: Represents database tables as Java classes

- **`User.java`**: Users table (admin, customers)
- **`Room.java`**: Rooms table (room details, pricing)
- **`Booking.java`**: Bookings table (reservation details)
- **`Guest.java`**: Guests table (customer information)
- **`Payment.java`**: Payments table (payment records)
- **`Message.java`**: Messages table (customer inquiries)

### ❌ `exception/` - Error Handling
**What it does**: Handles errors gracefully

- **`GlobalExceptionHandler.java`**: Catches all errors and returns user-friendly messages
- **`ResourceNotFoundException.java`**: When something isn't found (404 error)
- **`BadRequestException.java`**: When request is invalid (400 error)

### 💾 `repository/` - Database Access Layer
**What it does**: Talks to the database

Each repository handles database operations for one entity:
- **`UserRepository.java`**: Find users, check login credentials
- **`RoomRepository.java`**: Find rooms, check availability
- **`BookingRepository.java`**: Create/update bookings, check conflicts
- **`PaymentRepository.java`**: Record payments, handle refunds

### 🛡️ `security/` - Authentication & Security
**What it does**: Protects your API

- **`SecurityConfig.java`**: Defines which endpoints need authentication
- **`JwtUtil.java`**: Creates and validates JWT tokens
- **`JwtAuthenticationFilter.java`**: Checks JWT tokens on each request
- **`CustomUserDetailsService.java`**: Loads user details for authentication

### 🔧 `service/` - Business Logic
**What it does**: Contains the main business rules

- **`AuthService.java`**: Handles login, creates JWT tokens
- **`RoomService.java`**: Room management logic, availability checking
- **`BookingService.java`**: Booking creation, validation, confirmation
- **`PaymentService.java`**: Payment processing, refund logic
- **`MessageService.java`**: Message handling, admin responses

## 🔧 Key Components

### 🔐 JWT Authentication Flow
1. User sends username/password to `/auth/login`
2. System validates credentials
3. If valid, creates JWT token
4. User includes token in `Authorization: Bearer <token>` header
5. System validates token on each request

### 🏨 Booking Flow
1. **Check Availability**: `GET /rooms/available?checkIn=2025-10-15&checkOut=2025-10-17`
2. **Create Booking**: `POST /bookings` with room and guest details
3. **Process Payment**: `POST /payments` with booking ID
4. **Admin Confirms**: `POST /bookings/{id}/confirm` (optional)

### 💾 Database (H2)
- **File-based**: Stores data in `./data/hotelbookingdb.mv.db`
- **Web Console**: http://localhost:8080/api/v1/h2-console
- **Credentials**: URL: `jdbc:h2:file:./data/hotelbookingdb`, User: `sa`, Password: `password`

## 🛡️ Security & Authentication

### Public Endpoints (No Login Required)
```
POST /auth/login          # Login
GET  /rooms/**           # View rooms
POST /bookings           # Create booking
POST /messages           # Send message
GET  /swagger-ui.html    # API documentation
```

### Admin-Only Endpoints (Requires JWT Token)
```
POST   /rooms                    # Create room
PUT    /rooms/{id}              # Update room  
DELETE /rooms/{id}              # Delete room
POST   /bookings/{id}/confirm   # Confirm booking
GET    /messages                # View messages
POST   /payments/{id}/refund    # Process refund
```

### User Authentication Endpoints
```
GET    /bookings        # View own bookings
POST   /payments        # Make payment
```

## 🌐 API Endpoints

### Authentication
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin",
    "role": "ADMIN"
  }
}
```

### Room Management
```http
# Get available rooms
GET /api/v1/rooms/available?checkIn=2025-10-15&checkOut=2025-10-17

# Create room (Admin only)
POST /api/v1/rooms
Authorization: Bearer <token>
Content-Type: application/json

{
  "roomNumber": "102",
  "type": "SINGLE",
  "pricePerNight": 100.0,
  "capacity": 1,
  "description": "Cozy single room"
}
```

### Booking Management
```http
# Create booking
POST /api/v1/bookings
Content-Type: application/json

{
  "roomId": 1,
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "guestPhone": "+1234567890",
  "checkInDate": "2025-10-15",
  "checkOutDate": "2025-10-17",
  "numberOfGuests": 1
}
```

## 💾 Database

### Tables Structure
```sql
users       # System users (admin, staff)
guests      # Hotel guests (customers)
rooms       # Hotel rooms
bookings    # Reservations
payments    # Payment records  
messages    # Customer messages
```

### Sample Data (Auto-created)
- **Admin User**: username=`admin`, password=`admin123`
- **Rooms**: 101 (Single), 201 (Double), 301 (Suite)
- **All rooms available** by default

## 📚 How It All Works Together

### 1. Application Startup
```
1. Spring Boot starts (HotelBookingSystemApplication.java)
2. DataInitializer creates sample data
3. Security configuration loads
4. Database connections established
5. API endpoints become available
```

### 2. User Makes a Booking
```
1. Frontend/User calls GET /rooms/available
   ↓
2. RoomController receives request
   ↓  
3. RoomController calls RoomService.findAvailableRooms()
   ↓
4. RoomService calls RoomRepository.findAvailableRooms()
   ↓
5. Repository queries H2 database
   ↓
6. Results flow back: DB → Repository → Service → Controller → User
```

### 3. Admin Authentication
```
1. Admin sends POST /auth/login with credentials
   ↓
2. AuthController receives request
   ↓
3. AuthController calls AuthService.login()
   ↓
4. AuthService validates with UserRepository
   ↓
5. If valid, JwtUtil creates token
   ↓
6. Token returned to admin
   ↓
7. Admin uses token in Authorization header for protected endpoints
```

## 🧪 Testing Guide

### Using Swagger UI (Recommended)
1. Start application: `mvn spring-boot:run`
2. Open: http://localhost:8080/api/v1/swagger-ui.html
3. Click "Authorize" button
4. Enter: `Bearer <your-jwt-token>`
5. Test any endpoint directly in the browser

### Using curl/Postman
```bash
# 1. Login
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Copy the token from response

# 3. Test admin endpoint
curl -X GET "http://localhost:8080/api/v1/messages" \
  -H "Authorization: Bearer <your-token>"
```

### Database Inspection
1. Open: http://localhost:8080/api/v1/h2-console
2. JDBC URL: `jdbc:h2:file:./data/hotelbookingdb`
3. Username: `sa`, Password: `password`
4. Run SQL: `SELECT * FROM rooms;`

## 🎯 What Makes This Special

### Professional Patterns
- **Layered Architecture**: Clear separation of concerns
- **DTO Pattern**: Clean API contracts
- **Repository Pattern**: Abstracted data access
- **Service Layer**: Business logic isolation
- **Global Exception Handling**: Consistent error responses

### Security Best Practices
- **JWT Tokens**: Stateless authentication
- **Role-based Access**: Admin vs User permissions
- **Password Encryption**: BCrypt hashing
- **CORS Configuration**: Cross-origin support

### Documentation & Usability
- **OpenAPI/Swagger**: Interactive API documentation
- **Clear Naming**: Self-documenting code
- **Comprehensive Logging**: Debug and monitoring
- **Sample Data**: Ready to test immediately

## 🚀 Next Steps

Want to extend this project? Here are some ideas:
- **Frontend**: React/Vue.js user interface
- **Email**: Booking confirmation emails
- **File Upload**: Room photos
- **Reporting**: Analytics dashboard
- **Testing**: Unit and integration tests
- **Cloud Deploy**: AWS, Railway, or Render

---

**Built with Spring Boot 3.2.0 + JWT Authentication + H2 Database + OpenAPI Documentation**

*This project demonstrates modern Spring Boot development practices and enterprise-grade patterns in a beginner-friendly way.*
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
