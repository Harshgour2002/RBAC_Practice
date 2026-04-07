# RBAC Authentication & Authorization Microservice

Production-grade Java 17 + Spring Boot microservice for high-throughput (10k+ RPS) stateless authentication and authorization.

## Architecture

```text
Client -> API Layer -> Authentication Module -> Authorization Engine
                                              |-> Redis Cache
                                              |-> MySQL
                                              |-> Kafka Event Stream
```

## Enterprise Modules
- `controller`: API endpoints
- `service`: business logic
- `repository`: persistence layer
- `security`: JWT + filters + bcrypt
- `config`: security, kafka, redis, OpenAPI
- `caching`: redis blacklist + permission cache
- `events`: Kafka producer/consumer for invalidation
- `metrics`: micrometer metrics
- `audit`: audit trail persistence
- `exception`: centralized exception mapping

## Setup
1. Start infra (host ports are remapped to avoid common local conflicts):
   ```bash
   docker-compose up -d
   ```
2. Run service:
   ```bash
   SPRING_DATASOURCE_URL=jdbc:mysql://localhost:13306/rbac \
   SPRING_DATA_REDIS_HOST=localhost SPRING_DATA_REDIS_PORT=16379 \
   SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:19092 \
   ./mvnw spring-boot:run
   ```
3. OpenAPI docs: `http://localhost:8080/swagger-ui/index.html`
4. Prometheus metrics: `http://localhost:8080/actuator/prometheus`
5. Observability UIs from compose: Prometheus `http://localhost:19090`, Grafana `http://localhost:13000`

## API Endpoints
### Authentication
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`

### Authorization
- `POST /authorize`
- `GET /roles`
- `POST /roles`
- `POST /permissions`
- `POST /assign-role`

## Scalability Strategy
- Stateless JWT access tokens
- Refresh token rotation + blacklist revocation in Redis
- User permissions cached in Redis and in-memory fallback
- Kafka-driven cache invalidation events
- HikariCP tuning for high concurrency
- Resilience4j (circuit breaker, retry, timeout)
- Kubernetes HPA for horizontal scaling

## Load Testing (k6)
```bash
k6 run k6/load-test.js -e ACCESS_TOKEN=<token>
```

## Observability
- Prometheus + Grafana dashboard in `grafana/dashboard.json`
- Security metrics: auth latency, login failures, error counters

