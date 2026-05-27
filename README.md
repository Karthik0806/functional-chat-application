<div align="center">

<br/>

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=32&duration=2800&pause=2000&color=4F8EF7&center=true&vCenter=true&width=600&lines=ConnectChat+%F0%9F%92%AC;Real-Time+Chat+Platform" alt="ConnectChat" />

<br/>

**Real-time chat platform with WebSocket messaging, JWT authentication, and AWS deployment.**

<br/>

[![Live Demo](https://img.shields.io/badge/%F0%9F%9A%80%20Live%20Demo-chat.karthiknarravula.dev-4F8EF7?style=for-the-badge&labelColor=0d1117)](https://chat.karthiknarravula.dev)
[![API Docs](https://img.shields.io/badge/%F0%9F%93%96%20API%20Docs-Swagger%20UI-2ea44f?style=for-the-badge&labelColor=0d1117)](https://api.karthiknarravula.dev/swagger-ui/index.html)
[![Portfolio](https://img.shields.io/badge/%F0%9F%8C%90%20Portfolio-karthiknarravula.dev-9b59b6?style=for-the-badge&labelColor=0d1117)](https://karthiknarravula.dev)

<br/>

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React_18-20232A?style=flat-square&logo=react&logoColor=61DAFB)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white)

</div>

<br/>

---

## 📸 Screenshots

<div align="center">

<table>
  <tr>
    <td align="center"><b>Login</b></td>
    <td align="center"><b>Register</b></td>
  </tr>
  <tr>
    <td><img src="./screenshots/login_page.png" width="100%"/></td>
    <td><img src="./screenshots/register_page.png" width="100%"/></td>
  </tr>
</table>

<img src="./screenshots/chat.png" width="90%"/>
<br/><em>Chat Interface</em>

<br/><br/>

<table>
  <tr>
    <td align="center"><b>Search Users</b></td>
    <td align="center"><b>Search Results</b></td>
  </tr>
  <tr>
    <td><img src="./screenshots/search.png" width="100%"/></td>
    <td><img src="./screenshots/search_user.png" width="100%"/></td>
  </tr>
</table>

</div>

---

## 🔗 Live Links

| Resource | URL |
|:---|:---|
| 🌐 Frontend | [chat.karthiknarravula.dev](https://chat.karthiknarravula.dev) |
| ⚙️ Backend API | [api.karthiknarravula.dev](https://api.karthiknarravula.dev) |
| 📖 Swagger UI | [/swagger-ui/index.html](https://api.karthiknarravula.dev/swagger-ui/index.html) |
| 📄 OpenAPI JSON | [/v3/api-docs](https://api.karthiknarravula.dev/v3/api-docs) |

---

## ✨ Features

<table>
<tr>
<td width="50%">

### 🔐 Authentication & Security
- JWT access + refresh token flow
- Google OAuth2 login integration
- Protected APIs via Spring Security
- Password validation
- Automatic token refresh handling

### ⚡ Real-Time Messaging
- Private messaging via WebSocket + STOMP
- Online / offline user presence tracking
- Read receipts
- Optimistic UI updates
- Typing indicators *(V2 planned)*

</td>
<td width="50%">

### 🎨 Frontend
- Responsive React UI
- Modern chat interface
- Mobile-friendly sidebar
- Conversation previews
- Live updates without page refresh

### ☁️ DevOps & Cloud
- Dockerized backend on AWS EC2
- AWS RDS PostgreSQL database
- Nginx reverse proxy with HTTPS
- Custom domain configuration
- GitHub Actions CI/CD 

</td>
</tr>
</table>

---

## 🛠 Tech Stack

| Layer | Technologies |
|:---|:---|
| **Backend** | Java 21 · Spring Boot 3 · Spring Security · Spring WebSocket · Spring Data JPA · Hibernate · PostgreSQL · JWT · OAuth2 Client |
| **Frontend** | React · React Router · SockJS · STOMP.js · date-fns |
| **DevOps / Cloud** | Docker · Docker Hub · AWS EC2 · AWS RDS · Nginx · GitHub Actions · Vercel |

---

## 🏗 Architecture

```
┌─────────────────────────────────┐
│     React Frontend  (Vercel)    │
└────────────────┬────────────────┘
                 │  HTTPS
                 ▼
┌─────────────────────────────────┐
│       Nginx Reverse Proxy       │
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│  Spring Boot Backend            │
│  (Docker Container on EC2)      │
│                                 │
│  ┌──────────┐  ┌─────────────┐  │
│  │ REST API │  │  WebSocket  │  │
│  └──────────┘  └─────────────┘  │
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│    PostgreSQL Database          │
│         (AWS RDS)               │
└─────────────────────────────────┘
```
##🔐 Authentication Flow
```
User Login
 __________________________________
|    │                             |
|    ├── Email + Password          |
|    │        │                    |
|    │        ▼                    |
|    │   Spring Security           |
|    │        │                    |
|    │        ▼                    |
|    │       JWT                   |
|    |                             | 
|    └── Google OAuth2             |
|             │                    |
|             ▼                    |
|       Google Consent             |
|             │                    |
|             ▼                    |
|      OAuth2 Callback             | 
|             │                    |
|             ▼                    |
|       JWT Generation             |
|__________________________________|
```

---

## 🚀 Running Locally

### Prerequisites
- Java 21+
- Node.js 18+
- PostgreSQL

### 1 · Clone the Repository

```bash
git clone https://github.com/Karthik0806/functional-chat-application.git
cd functional-chat-application
```

### 2 · Configure Environment Variables

Create a `.env` file in the project root:

```env
SPRING_PROFILES_ACTIVE=prod
CLIENT_ID=your_google_client_id
CLIENT_SECRET=your_google_client_secret

DB_URL=your_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret
```

### 3 · Start the Backend

```bash
./mvnw spring-boot:run
```

### 4 · Start the Frontend

```bash
npm install
npm start
```

> The app will be available at `http://localhost:3000`

---

## 🐳 Docker Deployment

### Build & Push Image

```bash
docker buildx build \
  --platform linux/amd64 \
  -t karthi2005/connectchat-backend:latest \
  --push .
```

### Deploy to EC2

```bash
./deploy.sh
```

---

## ⚙️ CI/CD Pipeline

The GitHub Actions workflow runs automatically on every push:

```
Push to main
     │
     ▼
Build Docker Image
     │
     ▼
Push to Docker Hub
     │
     ▼
SSH into EC2
     │
     ▼
Pull & Restart Container
```

---

## 🗺 Roadmap

| Status | Feature |
|:---:|:---|
| ✅ | Private real-time messaging |
| ✅ | JWT authentication + refresh tokens |
| ✅ | Read receipts & online presence |
| ✅ | Docker + AWS EC2 deployment |
| ✅ | GitHub Actions CI/CD |
| ✅ | Google oauth2 |
| 🔲 | Contacts
| 🔲 | Typing indicators |
| 🔲 | Redis for scalable presence tracking |
| 🔲 | Message pagination |
| 🔲 | Group chat support |
| 🔲 | File & image sharing |
| 🔲 | Push notifications |
| 🔲 | Voice / video calls |
| 🔲 | Kubernetes deployment |
| 🔲 | Monitoring with Prometheus & Grafana |

---

## 📚 What I Learned

Building ConnectChat gave me hands-on experience across the full stack:

- **Real-time systems** — Designing bidirectional communication with WebSockets and STOMP
- **Security** — Implementing a robust JWT access + refresh token flow with Spring Security
- **OAuth2 & Identity** — Integrating Google OAuth2 login with JWT-based authentication flows
- **Containerization** — Dockerizing a Spring Boot app for consistent, portable deployments
- **Reverse proxying** — Configuring Nginx for SSL termination and API routing
- **Cloud deployment** — Provisioning, deploying, and debugging on AWS EC2 + RDS
- **CI/CD** — Automating the build and deploy pipeline with GitHub Actions
- Deployment & debugging — Troubleshooting issues across Docker, Nginx, and AWS infrastructure

---

## 👤 Author

<div align="center">

**Karthik Narravula**

[![GitHub](https://img.shields.io/badge/GitHub-Karthik0806-181717?style=for-the-badge&logo=github)](https://github.com/Karthik0806)
[![Portfolio](https://img.shields.io/badge/Portfolio-karthiknarravula.dev-4F8EF7?style=for-the-badge&logo=google-chrome&logoColor=white)](https://karthiknarravula.dev)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Karthik%20Narravula-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/karthik-narravula)

</div>

---

## 📄 License

This project is built for educational and portfolio purposes. Feel free to explore the code and architecture — and don't forget to star ⭐ the repo if you found it useful!

---

<div align="center">

*Made with ☕ and a lot of debugging by [Karthik Narravula](https://karthiknarravula.dev)*

</div>
