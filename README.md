# GaelCravings Backend

A Spring Boot backend application for the GaelCravings food ordering platform, built with Java 21 and PostgreSQL (Supabase).

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Environment Configuration](#environment-configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Docker Deployment](#docker-deployment)
- [Development Workflow](#development-workflow)
- [Troubleshooting](#troubleshooting)

## 🔧 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** (JDK) - [Download here](https://adoptium.net/)
- **Gradle 8.14.3** (included via wrapper)
- **PostgreSQL** access (we use Supabase)
- **Docker** (optional, for containerized deployment)
- **Git** for version control

Verify your Java installation:
```bash
java -version
```

## 📁 Project Structure

```
GaelCravings_Backend/
├── src/
│   ├── main/
│   │   ├── java/com/gaelcraves/project3/GaelCravings_Backend/
│   │   │   ├── GaelCravingsBackendApplication.java  # Main application
│   │   │   ├── SecurityConfig.java                  # Security & CORS
│   │   │   ├── User.java                            # User entity
│   │   │   ├── UserRepository.java                  # Data access
│   │   │   ├── UserService.java                     # Business logic
│   │   │   └── UserController.java                  # REST endpoints
│   │   └── resources/
│   │       └── application.properties               # Configuration
│   └── test/                                        # Unit tests
├── build.gradle                                     # Dependencies
├── docker-compose.yml                               # Docker setup
├── Dockerfile                                       # Container image
├── .env.example                                     # Environment template
└── README.md                                        # This file
```

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd GaelCravings_Backend
```

### 2. Set Up Environment Variables

Copy the example environment file:

```bash
cp .env.example .env
```

Edit `.env` with your actual credentials (see [Environment Configuration](#environment-configuration)).

### 3. Build the Project

```bash
./gradlew clean build
```

On Windows:
```bash
gradlew.bat clean build
```

## 🔐 Environment Configuration

Create a `.env` file in the project root with the following variables:

```env
# Database Connection
DB_URL=jdbc:postgresql://db.cfmztcfqrvinpwxqqwxa.supabase.co:5432/postgres
DB_USER=postgres
DB_PASS=your-password-here

# Supabase Configuration
supaURL=https://cfmztcfqrvinpwxqqwxa.supabase.co
supaAPI=your-supabase-anon-key

# CORS Configuration
FRONTEND_ORIGIN=http://localhost:5173

# JWT Secret (must be Base64 encoded)
APP_JWT_SECRET=dGVzdC1qd3Qtc2VjcmV0LWtleS1mb3ItZGV2ZWxvcG1lbnQtb25seQ==
```

⚠️ **NEVER commit your `.env` file to Git!** It contains sensitive credentials.

### How to Get Supabase Credentials:

1. Go to your [Supabase Dashboard](https://app.supabase.com/)
2. Select your project
3. Navigate to **Settings** → **API**
4. Copy the **URL** and **anon public** key

## ▶️ Running the Application

### Option 1: Run with Gradle (Development)

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Option 2: Run with Environment Variables Directly

```bash
DB_URL="jdbc:postgresql://..." \
DB_USER="postgres" \
DB_PASS="your-password" \
supaURL="https://..." \
supaAPI="your-key" \
./gradlew bootRun
```

### Option 3: Run the JAR file

```bash
./gradlew bootJar
java -jar build/libs/GaelCraves_Backend-0.0.1-SNAPSHOT.jar
```

### Verify the Application is Running

Visit: `http://localhost:8080/api/users`

You should see an empty array `[]` if the database is empty, or a list of users.

## 📡 API Endpoints

### User Management

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `GET` | `/api/users` | Get all users | - |
| `GET` | `/api/users/{id}` | Get user by ID | - |
| `GET` | `/api/users/email?email=` | Get user by email | - |
| `GET` | `/api/users/security-question?email=` | Get security question | - |
| `POST` | `/api/users` | Register new user | `User` JSON |
| `POST` | `/api/users/login` | User login | `{email, password}` |
| `PUT` | `/api/users/reset-password?email=&newPassword=` | Reset password | - |
| `DELETE` | `/api/users/{id}` | Delete user | - |

### Example Requests

**Register a new user:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "securityQuestion": "What is your pet name?",
    "securityAnswer": "Fluffy"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

## 🐳 Docker Deployment

### Build and Run with Docker Compose

```bash
# Build the JAR first
./gradlew bootJar

# Start with Docker Compose
docker-compose up -d
```

### Build Docker Image Manually

```bash
# Build JAR
./gradlew bootJar

# Build Docker image
docker build -t gaelcravings-backend .

# Run container
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://..." \
  -e DB_USER="postgres" \
  -e DB_PASS="your-password" \
  -e supaURL="https://..." \
  -e supaAPI="your-key" \
  gaelcravings-backend
```

### Stop Docker Containers

```bash
docker-compose down
```

## 👥 Development Workflow

### For Team Members

1. **Pull latest changes:**
   ```bash
   git pull origin main
   ```

2. **Create a feature branch:**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make changes and test locally:**
   ```bash
   ./gradlew clean build
   ./gradlew bootRun
   ```

4. **Commit and push:**
   ```bash
   git add .
   git commit -m "Add: description of changes"
   git push origin feature/your-feature-name
   ```

5. **Create a Pull Request on GitHub**

### Code Style

- Follow Java naming conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep methods focused and small
- Write unit tests for new features

## 🔍 Troubleshooting

### Application won't start

**Error:** `The driver has not received any packets from the server`

**Solution:** Check your database credentials in `.env` file:
```bash
# Verify database connection
psql -h db.cfmztcfqrvinpwxqqwxa.supabase.co -U postgres -d postgres
```

---

**Error:** `Cannot resolve property 'app.jwt.secret'`

**Solution:** Ensure all environment variables are set. Run with explicit variables:
```bash
APP_JWT_SECRET="dGVzdC1qd3Qtc2VjcmV0LWtleS1mb3ItZGV2ZWxvcG1lbnQtb25seQ==" ./gradlew bootRun
```

---

**Error:** Port 8080 already in use

**Solution:** 
```bash
# Find and kill the process
lsof -ti:8080 | xargs kill -9

# Or change the port in application.properties
server.port=8081
```

### Gradle build fails

```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

### Database connection issues

1. Verify Supabase is running
2. Check firewall/network settings
3. Confirm database credentials
4. Test connection with `psql` or a database client

### CORS errors from frontend

Ensure `FRONTEND_ORIGIN` in `.env` matches your frontend URL:
```env
FRONTEND_ORIGIN=http://localhost:5173
```

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
- [Supabase Documentation](https://supabase.com/docs)
- [Gradle User Guide](https://docs.gradle.org)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## 📝 License

[Add your license here]

## 👨‍💻 Contributors

- [Add team members here]

---

**Need help?** Contact the team lead or open an issue in the repository.