# Smart Attendance System

A comprehensive Spring Boot application for managing student attendance records with role-based access control for teachers and students.

## 📋 Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

- **Role-Based Access Control**: Separate login and dashboard for teachers and students
- **Attendance Management**: Teachers can mark and manage student attendance
- **Student Dashboard**: Students can view their attendance records
- **User Authentication**: Secure login system with password management
- **Password Recovery**: Forgot password functionality for user account recovery
- **User Registration**: New user registration system
- **Responsive UI**: HTML/CSS/JavaScript frontend with Bootstrap styling

## 🛠️ Technologies Used

- **Backend**
  - Java 11+
  - Spring Boot 2.x/3.x
  - Spring Security
  - Spring Data JPA
  - Spring Web MVC

- **Database**
  - JPA/Hibernate ORM

- **Frontend**
  - HTML5
  - CSS3
  - JavaScript (Vanilla)

- **Build Tool**
  - Maven 3.6+

- **Others**
  - Lombok (optional, for boilerplate reduction)

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- Git
- A relational database (MySQL, PostgreSQL, or H2 for testing)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/smart-attendance.git
cd smart-attendance
```

### 2. Configure Database

Update the database configuration in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/smart_attendance
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 3. Build the Project

```bash
mvn clean install
```

Or using the Maven wrapper:

```bash
./mvnw clean install
```

## ⚙️ Configuration

### Application Properties

Key configuration properties in `application.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Logging
logging.level.root=INFO
logging.level.com.attendance.smartattendance=DEBUG

# Session Configuration
server.servlet.session.timeout=30m
```

### Security Configuration

Security settings are configured in `config/SecurityConfig.java`:
- Password encoding
- CORS settings
- Authentication providers
- URL security rules

## ▶️ Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

Or using the Maven wrapper on Windows:

```bash
mvnw.cmd spring-boot:run
```

### Using JAR File

```bash
java -jar target/smart-attendance-1.0.0.jar
```

The application will start on `http://localhost:8080`

## 📁 Project Structure

```
smart-attendance/
├── src/
│   ├── main/
│   │   ├── java/com/attendance/smartattendance/
│   │   │   ├── SmartAttendanceApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── api/                 # REST API controllers
│   │   │   │   └── page/                # Page controllers (MVC)
│   │   │   ├── dto/                     # Data Transfer Objects
│   │   │   ├── entity/                  # JPA Entities
│   │   │   ├── exception/               # Custom Exceptions
│   │   │   ├── repository/              # Data Access Layer
│   │   │   └── service/                 # Business Logic Layer
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/                  # CSS, JS, Images
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       └── templates/               # HTML Templates
│   └── test/
│       └── java/com/attendance/smartattendance/
│           └── SmartAttendanceApplicationTests.java
├── target/                              # Compiled output
├── pom.xml                              # Maven configuration
└── README.md
```

## 🔌 API Documentation

### Authentication Endpoints

- **POST** `/api/auth/login` - User login
- **POST** `/api/auth/register` - New user registration
- **POST** `/api/auth/forgot-password` - Password recovery request
- **POST** `/api/auth/reset-password` - Reset password with token

### Teacher Endpoints

- **GET** `/api/teacher/dashboard` - Get teacher dashboard data
- **GET** `/api/teacher/students` - Get list of students
- **POST** `/api/teacher/attendance/mark` - Mark attendance
- **GET** `/api/teacher/attendance/report` - Get attendance report

### Student Endpoints

- **GET** `/api/student/dashboard` - Get student dashboard data
- **GET** `/api/student/attendance` - Get student's attendance records

## 👥 Usage

### For Teachers

1. Navigate to `http://localhost:8080/teacher-login`
2. Enter teacher credentials
3. Access the teacher dashboard to manage attendance
4. View and generate attendance reports

### For Students

1. Navigate to `http://localhost:8080/role` to select your role
2. Enter student login credentials
3. View personal attendance records on the student dashboard

### New User Registration

1. Navigate to `http://localhost:8080/register`
2. Fill in the registration form
3. Select your role (Teacher/Student)
4. Complete the registration

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📧 Support

For support, email support@smartattendance.com or open an issue on GitHub.

## 🔗 Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Maven Documentation](https://maven.apache.org/guides/index.html)

---

**Last Updated**: February 2026

**Version**: 1.0.0
