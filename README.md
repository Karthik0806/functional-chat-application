<div align="center">

# ConnectChat

### Real-Time Full Stack Chat Application

A production-style real-time chat platform built with Spring Boot, WebSockets, JWT authentication, Docker, AWS EC2, PostgreSQL, and React.

<br/>

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-RDS-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

[![Live Demo](https://img.shields.io/badge/🚀_Live_Demo-chat.karthiknarravula.dev-0A66C2?style=for-the-badge)](https://chat.karthiknarravula.dev)
[![API Docs](https://img.shields.io/badge/📖_API_Docs-Swagger_UI-85EA2D?style=for-the-badge)](https://api.karthiknarravula.dev/swagger-ui/index.html)

</div>

---

## 📸 Screenshots

<p align="center">
  <img src="./screenshots/login_page.png" width="45%" />
  <img src="./screenshots/register_page.png" width="45%" />
</p>

<p align="center">
  <img src="./screenshots/chat.png" width="90%" />
</p>

<p align="center">
  <img src="./screenshots/search.png" width="45%" />
  <img src="./screenshots/search_user.png" width="45%" />
</p>

---

## 🔗 Live Links

| Resource | URL |
|:---|:---|
| Frontend | https://chat.karthiknarravula.dev |
| Backend API | https://api.karthiknarravula.dev |
| Swagger UI | https://api.karthiknarravula.dev/swagger-ui/index.html |
| OpenAPI JSON | https://api.karthiknarravula.dev/v3/api-docs |

---

## ✨ Features

<details open>
<summary><b>🔐 Authentication & Security</b></summary>
<br/>

- JWT-based authentication with access + refresh token flow
- Protected APIs via Spring Security
- Password validation and automatic token refresh handling

</details>

<details open>
<summary><b>⚡ Real-Time Messaging</b></summary>
<br/>

- Private real-time messaging via WebSocket + STOMP
- Online/offline user presence tracking
- Read receipts and optimistic UI updates
- Typing indicators *(planned for V2)*

</details>

<details open>
<summary><b>🎨 Frontend</b></summary>
<br/>

- Responsive React UI with a modern chat experience
- Mobile-friendly sidebar with conversation previews
- Real-time updates without page refresh

</details>

<details open>
<summary><b>🛠 Backend</b></summary>
<br/>

- REST APIs documented with Swagger / OpenAPI
- Layered Spring Boot architecture with DTO-based responses
- Global exception handling
- PostgreSQL integration via JPA + Hibernate

</details>

<details open>
<summary><b>☁️ DevOps & Cloud</b></summary>
<br/>

- Dockerized backend deployed on AWS EC2
- Managed PostgreSQL via AWS RDS
- Nginx reverse proxy with HTTPS and custom domain
- GitHub Actions CI/CD *(in progress)*

</details>

---

## 🛠 Tech Stack

| Layer | Technologies |
|:---|:---|
| **Backend** | Java 25, Spring Boot 3, Spring Security, Spring WebSocket, Spring Data JPA, Hibernate, PostgreSQL, JWT |
| **Frontend** | React, React Router, SockJS, STOMP.js, date-fns |
| **DevOps / Cloud** | Docker, Docker Hub, AWS EC2, AWS RDS, Nginx, GitHub Actions, Vercel |

---

## 🏗 Architecture

```
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

## 🚀 Running Locally

### Prerequisites

- Java 25+
- Node.js 18+
- PostgreSQL (local or remote)
- Maven

### 1. Clone the Repository

```bash
git clone https://github.com/Karthik0806/functional-chat-application.git
cd functional-chat-application
```

### 2. Configure Environment Variables

Create a `.env` file in the project root:

```env
SPRING_PROFILES_ACTIVE=prod

DB_URL=your_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret
```

### 3. Run the Backend

```bash
./mvnw spring-boot:run
```

### 4. Run the Frontend

```bash
npm install
npm start
```

The app will be available at `http://localhost:3000`.

---

## 🐳 Docker Deployment

### Build the Image

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

The GitHub Actions workflow automatically:

1. Builds the Docker image
2. Pushes it to Docker Hub
3. Connects to EC2 via SSH
4. Deploys the latest backend version

---

## 🗺 Roadmap

| Feature | Status |
|:---|:---|
| Redis for scalable online-user tracking | 🔲 Planned |
| Message pagination | 🔲 Planned |
| Group chat support | 🔲 Planned |
| Push notifications | 🔲 Planned |
| File and image sharing | 🔲 Planned |
| Voice / video calls | 🔲 Planned |
| Kubernetes deployment | 🔲 Planned |
| Monitoring with Prometheus & Grafana | 🔲 Planned |
| Typing indicators | 🔲 V2 |

---

## 📚 What I Learned

Building ConnectChat provided hands-on experience with:

- **Real-time systems** — designing WebSocket + STOMP message flows
- **Security** — implementing JWT access/refresh token authentication
- **Containerization** — building and shipping Docker images
- **Infrastructure** — configuring Nginx as a reverse proxy with SSL
- **Cloud deployment** — running and debugging on AWS EC2 + RDS
- **CI/CD** — automating deployments with GitHub Actions
- **Full-stack design** — making frontend, backend, and infrastructure work cohesively in production

---

## 👤 Author

<div align="center">

**Karthik Narravula**

[![GitHub](https://img.shields.io/badge/GitHub-Karthik0806-181717?style=for-the-badge&logo=github)](https://github.com/Karthik0806)
[![Portfolio](https://img.shields.io/badge/Portfolio-karthiknarravula.dev-0A66C2?style=for-the-badge&logo=google-chrome&logoColor=white)](https://karthiknarravula.dev)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=for-the-badge&logo=linkedin)](https://linkedin.com/in/your-handle)

</div>

---

## 📄 License

This project is for educational and portfolio purposes. Feel free to explore the code and architecture for learning.