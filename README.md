<div align="center">

# ConnectChat

### Real-Time Full Stack Chat Application

A production-style real-time chat platform built with Spring Boot, WebSockets, JWT authentication, Docker, AWS EC2, PostgreSQL, and React.

<br/>

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-green?style=for-the-badge)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge)
![AWS](https://img.shields.io/badge/AWS-EC2-orange?style=for-the-badge)
![License](https://img.shields.io/badge/License-Educational-orange?style=for-the-badge)

Built with Spring Boot · WebSockets · JWT · PostgreSQL · Docker · AWS · React

</div>

---

# 📸 Screenshots

<p align="center">
  <img src="./screenshots/login-page.png" width="45%" />
  <img src="./screenshots/register-page.png" width="45%" />
</p>

<p align="center">
  <img src="./screenshots/chat.png" width="90%" />
</p>

<p align="center">
  <img src="./screenshots/search.png" width="45%" />
  <img src="./screenshots/search-user.png" width="45%" />
</p>

---

# 🔗 Live Links

| Resource | URL |
|---|---|
| Frontend | https://chat.karthiknarravula.dev |
| Backend API | https://api.karthiknarravula.dev |
| Swagger UI | https://api.karthiknarravula.dev/swagger-ui/index.html |
| OpenAPI JSON | https://api.karthiknarravula.dev/v3/api-docs |

---

# ✨ Features

## 🔐 Authentication & Security
- JWT-based authentication with access + refresh token flow
- Secure protected APIs using Spring Security
- Password validation and automatic token refresh handling

## ⚡ Real-Time Messaging
- Private real-time messaging using WebSocket + STOMP
- Online/offline user tracking
- Read receipts
- Optimistic UI updates
- Typing indicators *(planned for V2)*

## 🎨 Frontend
- Responsive React UI with modern chat experience
- Mobile-friendly sidebar with conversation previews
- Real-time updates without refresh

## 🛠 Backend
- REST APIs documented with Swagger / OpenAPI
- Layered Spring Boot architecture
- DTO-based responses
- Global exception handling
- PostgreSQL integration with JPA + Hibernate

## ☁️ DevOps & Cloud
- Dockerized backend deployed on AWS EC2
- AWS RDS PostgreSQL database
- Nginx reverse proxy with HTTPS
- Custom domain configuration
- GitHub Actions CI/CD *(in progress)*

---

# 🛠 Tech Stack

| Layer | Technologies |
|---|---|
| **Backend** | Java 25, Spring Boot 3, Spring Security, Spring WebSocket, Spring Data JPA, Hibernate, PostgreSQL, JWT |
| **Frontend** | React, React Router, SockJS, STOMP.js, date-fns |
| **DevOps / Cloud** | Docker, Docker Hub, AWS EC2, AWS RDS, Nginx, GitHub Actions, Vercel |

---

# 🏗 Architecture

```text
React Frontend  (Vercel)
       │
       ▼
Nginx Reverse Proxy
       │
       ▼
Spring Boot Backend  (Docker on EC2)
       │
       ▼
PostgreSQL Database  (AWS RDS)
```

---

# 🚀 Running Locally

## 1. Clone Repository

```bash
git clone https://github.com/Karthik0806/functional-chat-application.git
cd functional-chat-application
```

## 2. Configure Environment Variables

Create a `.env` file in the project root:

```env
SPRING_PROFILES_ACTIVE=prod

DB_URL=your_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret
```

## 3. Run Backend

```bash
./mvnw spring-boot:run
```

## 4. Run Frontend

```bash
npm install
npm start
```

---

# 🐳 Docker Deployment

## Build Docker Image

```bash
docker buildx build \
  --platform linux/amd64 \
  -t karthi2005/connectchat-backend:latest \
  --push .
```

## Deploy to EC2

```bash
./deploy.sh
```

---

# ⚙️ CI/CD Pipeline

The GitHub Actions workflow automatically:

1. Builds the Docker image
2. Pushes it to Docker Hub
3. Connects to EC2 via SSH
4. Deploys the latest backend version

---

# 🗺 Roadmap

- [ ] Redis for scalable online-user tracking
- [ ] Message pagination
- [ ] Group chat support
- [ ] Push notifications
- [ ] File and image sharing
- [ ] Voice / video calls
- [ ] Kubernetes deployment
- [ ] Monitoring with Prometheus & Grafana

---

# 📚 What I Learned

Building ConnectChat provided hands-on experience with:

- Designing real-time systems using WebSockets and STOMP
- Implementing secure JWT authentication flows
- Containerizing applications with Docker
- Configuring Nginx as a reverse proxy
- Deploying and debugging on AWS EC2 + RDS
- Setting up CI/CD pipelines with GitHub Actions
- Full-stack architecture design
- Production debugging and deployment troubleshooting

---

# 👤 Author

## Karthik Narravula

- GitHub: https://github.com/Karthik0806
- Portfolio: https://karthiknarravula.dev

---

# 📄 License

This project is for educational and portfolio purposes. Feel free to explore the code and architecture for learning.