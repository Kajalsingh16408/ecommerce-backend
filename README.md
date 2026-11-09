# E-Commerce Backend System

A RESTful E-Commerce Backend Application developed using Java, Spring Boot, Spring Security, JWT Authentication, and MongoDB.

This project provides APIs for user authentication, product management, shopping cart operations, order processing, and administrative management.

## Tech Stack

- **Language:** Java 21+
- **Framework:** Spring Boot 4
- **Database:** MongoDB
- **Security:** Spring Security, JWT
- **Build Tool:** Maven
- **API Documentation:** Swagger / OpenAPI
- **Testing:** JUnit, Mockito
- **Validation:** Jakarta Bean Validation

## Features

### 1. User Authentication
- User registration and login.
- JWT-based authentication.
- Password encryption using BCrypt.
- Role-based access control (CUSTOMER and ADMIN).

### 2. Product Management
- Create, view, update, and delete products.
- Search products by name.
- Filter products by category.
- Product validation and stock management.

### 3. Shopping Cart
- Add products to cart.
- Update product quantities.
- Remove cart items.
- Calculate cart totals.
- Validate product availability and stock.

### 4. Order Management
- Place orders using cart items.
- View customer orders.
- Cancel eligible orders.
- Track order status.
- Restore product stock on cancellation.

### 5. Admin Management
- Admin dashboard.
- Manage products.
- View all orders.
- Update order status with transition validation.

### 6. Exception Handling
- Global exception handling.
- Request validation.
- Meaningful error responses.

## Project Structure

```text
src/main/java/com/ecommerce/
├── config/
├── controller/
├── dto/
├── exception/
├── model/
├── repository/
├── security/
├── service/
└── EcommerceBackendApplication.java

src/main/resources/
└── application.properties

src/test/java/
└── com/ecommerce/
```

## Prerequisites

- Java 21 or higher
- Maven
- MongoDB
- VS Code or IntelliJ IDEA

## Installation and Setup

### 1. Clone the repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
cd ecommerce-backend
```

### 2. Configure MongoDB

Make sure MongoDB is running locally.

Configure the database connection in `src/main/resources/application.properties`.

Example:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/ecommerce_db
server.port=8081
```

### 3. Configure JWT Secret

Set a strong, private JWT secret through an environment variable. Do not commit real secrets to GitHub.

### 4. Build the project

```bash
mvn clean package
```

### 5. Run the application

```bash
java -jar target/ecommerce-backend-0.0.1-SNAPSHOT.jar
```

## API Documentation

After starting the application, open Swagger UI:

http://localhost:8081/swagger-ui/index.html

## API Endpoints

| Module | Method | Endpoint |
|---|---|---|
| Authentication | POST | `/api/auth/register` |
| Authentication | POST | `/api/auth/login` |
| Products | GET | `/api/products` |
| Products | GET | `/api/products/{id}` |
| Products | POST | `/api/products` |
| Products | PUT | `/api/products/{id}` |
| Products | DELETE | `/api/products/{id}` |
| Cart | GET | `/api/cart` |
| Cart | POST | `/api/cart/items/{productId}` |
| Cart | PUT | `/api/cart/items/{productId}` |
| Cart | DELETE | `/api/cart/items/{productId}` |
| Orders | POST | `/api/orders` |
| Orders | GET | `/api/orders` |
| Orders | GET | `/api/orders/{id}` |
| Orders | PUT | `/api/orders/{id}/cancel` |
| Admin | GET | `/api/admin/dashboard` |
| Admin | GET | `/api/admin/orders` |

For complete endpoint details, refer to Swagger UI.

## Security

- Public access is allowed for registration and login.
- Authenticated users can access customer operations.
- Admin-only operations are protected through role-based authorization.
- JWT tokens are required for protected endpoints.
- Passwords are stored using BCrypt hashing.

## Testing

Run tests using:

```bash
mvn clean test
```

Build the executable JAR using:

```bash
mvn clean package
```

## Future Improvements

- Payment gateway integration.
- Email notifications.
- Pagination and sorting.
- Advanced product filtering.
- Automated integration testing.
- MongoDB transaction support for order and stock operations.

## Author

Kajal Singh

Java Backend Developer | BCA Graduate