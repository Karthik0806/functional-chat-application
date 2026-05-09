ConnectChat

A real-time full-stack chat application built with Spring Boot, WebSockets, JWT Authentication, PostgreSQL, Docker, AWS EC2, Nginx, and React.

Live Demo

* Frontend: https://chat.karthiknarravula.dev
* Backend API: https://api.karthiknarravula.dev
* Swagger Documentation: https://api.karthiknarravula.dev/swagger-ui/index.html

⸻

Features

Authentication & Security

* JWT-based authentication
* Access + Refresh token flow
* Secure protected APIs with Spring Security
* Password validation
* Automatic token refresh handling

Real-Time Messaging

* Private real-time messaging
* WebSocket + STOMP communication
* Online/offline user tracking
* Read receipts
* Typing indicators (planned for V2)
* Optimistic UI updates

Frontend

* Responsive React UI
* Modern chat interface
* Mobile-friendly sidebar
* Conversation previews
* Real-time updates without refresh

Backend

* REST APIs with Swagger/OpenAPI
* Layered Spring Boot architecture
* PostgreSQL database integration
* JPA + Hibernate
* DTO-based responses
* Global exception handling

Deployment & DevOps

* Dockerized backend
* AWS EC2 deployment
* AWS RDS PostgreSQL database
* Nginx reverse proxy
* HTTPS support
* Custom domain configuration
* GitHub Actions CI/CD (in progress)

⸻

Tech Stack

Backend

* Java 25
* Spring Boot 3
* Spring Security
* Spring WebSocket
* Spring Data JPA
* Hibernate
* PostgreSQL
* JWT Authentication

Frontend

* React
* React Router
* SockJS
* STOMP.js
* date-fns

DevOps & Cloud

* Docker
* Docker Hub
* AWS EC2
* AWS RDS
* Nginx
* GitHub Actions
* Vercel

⸻

Architecture

React Frontend (Vercel)
↓
Nginx Reverse Proxy
↓
Spring Boot Backend (Docker on EC2)
↓
PostgreSQL (AWS RDS)

⸻

Screenshots

Login Page

(Add screenshot here)

Chat Interface

(Add screenshot here)

Swagger Documentation

(Add screenshot here)

⸻

API Documentation

Swagger UI:

https://api.karthiknarravula.dev/swagger-ui/index.html

OpenAPI Docs:

https://api.karthiknarravula.dev/v3/api-docs

⸻

Running Locally

Clone Repository

git clone https://github.com/Karthik0806/functional-chat-application.git
cd functional-chat-application

Configure Environment Variables

Create a .env file:

SPRING_PROFILES_ACTIVE=prod
DB_URL=your_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_jwt_secret

Run Backend

./mvnw spring-boot:run

Run Frontend

npm install
npm start

⸻

Docker Deployment

Build Docker Image

docker buildx build \
--platform linux/amd64 \
-t karthi2005/connectchat-backend:latest \
--push .

Deploy on EC2

./deploy.sh

⸻

CI/CD Pipeline

GitHub Actions automatically:

* Builds Docker image
* Pushes image to Docker Hub
* Connects to EC2 through SSH
* Deploys latest backend version

⸻

Future Improvements

* Redis for scalable online-user tracking
* Message pagination
* Group chat support
* Push notifications
* File/image sharing
* Voice/video calls
* Kubernetes deployment
* Monitoring with Prometheus & Grafana

⸻

What I Learned

This project helped me gain hands-on experience with:

* Real-time systems using WebSockets
* JWT authentication flows
* Docker containerization
* Reverse proxy configuration with Nginx
* Cloud deployment on AWS
* CI/CD pipelines
* Full-stack architecture design
* Production debugging and deployment troubleshooting

⸻

Author

Karthik Narravula

* GitHub: https://github.com/Karthik0806
* Portfolio: https://karthiknarravula.dev
* LinkedIn: (Add your LinkedIn here)

⸻

License

This project is for educational and portfolio purposes.