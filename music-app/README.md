# Music Ranking System

A multi-module Spring Boot application that provides music ranking functionality with Spotify API integration. The system collects music data from Spotify playlists and maintains a real-time ranking system using Redis caching.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Development Guide](#development-guide)
- [Deployment Guide](#deployment-guide)
- [Contributing](#contributing)

## Overview

The Music Ranking System is designed to:
- Collect music data from Spotify playlists via batch processing
- Provide REST API endpoints for music CRUD operations
- Maintain real-time music rankings with scoring functionality
- Cache ranking data using Redis for optimal performance
- Support daily ranking queries and historical tracking

### Tech Stack

- **Framework**: Spring Boot 3.2.5
- **Language**: Kotlin 1.9.25
- **Database**: H2 (development), ready for production databases
- **Caching**: Redis 6+
- **External API**: Spotify Web API
- **Build Tool**: Gradle with Kotlin DSL
- **Additional**: Spring Batch, Spring Data JPA, Resilience4j

## Architecture

### Module Structure

```
music-app/
├── music-core/          # Shared domain models, repositories, and clients
├── music-api/           # REST API server
├── music-batch/         # Spotify data collection batch jobs
└── build.gradle.kts     # Root project configuration
```

#### music-core
**Purpose**: Shared components across all modules
- **Entities**: `MusicEntity`, `ScoreboardEntity` with audit fields
- **Repositories**: JPA repositories with custom query methods
- **DTOs**: Response objects and event classes
- **External Clients**: Spotify API integration using Feign

#### music-api
**Purpose**: REST API server for music operations
- **Controllers**: RESTful endpoints for music and ranking operations
- **Services**: Business logic implementation
- **Configuration**: Redis caching, Swagger documentation, circuit breaker
- **Port**: 8080 (configurable)

#### music-batch
**Purpose**: Spotify playlist data collection
- **Batch Jobs**: Spring Batch configuration for data processing
- **Processors**: Transform Spotify track data to internal format
- **Schedulers**: Automated data collection (configurable)

### Database Schema

```sql
-- Music table
CREATE TABLE MUSIC (
    music_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Scoreboard for current rankings
CREATE TABLE SCOREBOARD (
    scoreboard_id INT AUTO_INCREMENT PRIMARY KEY,
    music_id INT NOT NULL,
    score INT DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (music_id) REFERENCES MUSIC(music_id)
);

-- Historical ranking data
CREATE TABLE SCOREBOARD_HISTORY (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    music_id INT NOT NULL,
    score INT,
    snapshot_date DATE,
    created_at TIMESTAMP
);
```

### Key Design Patterns

- **Multi-module Architecture**: Separation of concerns with shared core
- **Repository Pattern**: Data access abstraction
- **Event-Driven Architecture**: Asynchronous score updates
- **Circuit Breaker Pattern**: Resilience for external API calls
- **Caching Strategy**: Redis for performance optimization

## Getting Started

### Prerequisites

- **Java 17** or higher
- **Redis 6.0+** running on localhost:6379
- **Spotify Developer Account** with API credentials
- **Git** for version control

### Environment Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd music-app
   ```

2. **Start Redis server**
   ```bash
   # Using Docker
   docker run --name redis -p 6379:6379 -d redis:6-alpine

   # Or using local installation
   redis-server
   ```

3. **Configure Spotify API credentials**

   Create or update `music-batch/src/main/resources/application.yml`:
   ```yaml
   spotify:
     account:
       grant-type: client_credentials
       client-id: your-spotify-client-id
       client-secret: your-spotify-client-secret
   ```

4. **Build the project**
   ```bash
   ./gradlew build
   ```

### Running the Applications

#### Start the API Server
```bash
# Run API server (port 8080)
./gradlew :music-api:bootRun

# Or using the JAR
java -jar music-api/build/libs/music-api-0.0.1-SNAPSHOT.jar
```

#### Run Batch Jobs
```bash
# Run batch data collection
./gradlew :music-batch:bootRun

# Or using the JAR
java -jar music-batch/build/libs/music-batch-0.0.1-SNAPSHOT.jar
```

### Verification

1. **Check API health**
   ```bash
   curl http://localhost:8080/v1/music/scoreboard/daily/top/10
   ```

2. **Access H2 Console** (development only)
   - URL: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: (empty)

3. **Verify Redis connection**
   ```bash
   redis-cli ping  # Should return PONG
   ```

## API Documentation

### Base URL
```
http://localhost:8080/v1/music
```

### Endpoints

#### Music Operations

**Get Music by ID**
```http
GET /v1/music/id/{id}
```
- **Parameters**: `id` (integer) - Music ID
- **Response**: Music details with metadata
- **Example**: `GET /v1/music/id/1`

**Get Music by Title**
```http
GET /v1/music/title/{title}
```
- **Parameters**: `title` (string) - Exact or partial title match
- **Response**: List of matching music entries
- **Example**: `GET /v1/music/title/Bohemian%20Rhapsody`

**Get Music by Artist**
```http
GET /v1/music/artist/{artist}
```
- **Parameters**: `artist` (string) - Artist name
- **Response**: All songs by the specified artist
- **Example**: `GET /v1/music/artist/Queen`

#### Ranking Operations

**Get Daily Top Rankings**
```http
GET /v1/music/scoreboard/daily/top/{count}
```
- **Parameters**: `count` (integer) - Number of top songs to return (1-100)
- **Response**: Top ranked songs with scores
- **Caching**: Results cached in Redis for performance
- **Example**: `GET /v1/music/scoreboard/daily/top/10`

**Increase Music Score**
```http
POST /v1/music/id/{id}/increase/{score}
```
- **Parameters**:
  - `id` (integer) - Music ID
  - `score` (integer) - Points to add
- **Response**: Updated music with new score
- **Side Effects**: Updates ranking cache, triggers ranking events
- **Example**: `POST /v1/music/id/1/increase/5`

### Response Examples

**Music Response**
```json
{
  "musicId": 1,
  "title": "Bohemian Rhapsody",
  "artist": "Queen",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Ranking Response**
```json
[
  {
    "rank": 1,
    "musicId": 1,
    "title": "Bohemian Rhapsody",
    "artist": "Queen",
    "score": 1250,
    "scoreboardId": 1
  },
  {
    "rank": 2,
    "musicId": 2,
    "title": "Hotel California",
    "artist": "Eagles",
    "score": 1180,
    "scoreboardId": 2
  }
]
```

### Error Responses

**Standard Error Format**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Music not found with id: 999",
  "path": "/v1/music/id/999"
}
```

## Development Guide

### Project Setup for Development

1. **IDE Configuration**
   - **IntelliJ IDEA**: Import as Gradle project, enable Kotlin support
   - **VS Code**: Install Kotlin and Spring Boot extensions
   - **Code Style**: Follow Kotlin coding conventions

2. **Database Development**
   ```bash
   # Access H2 console for data inspection
   open http://localhost:8080/h2-console

   # Reset database (development only)
   curl -X DELETE http://localhost:8080/h2-console/reset
   ```

3. **Redis Development**
   ```bash
   # Monitor Redis operations
   redis-cli monitor

   # Inspect cached data
   redis-cli keys "*ranking*"
   redis-cli get "daily_top_rankings"
   ```

### Adding New Features

#### Adding New API Endpoints

1. **Define the endpoint** in appropriate controller:
   ```kotlin
   @GetMapping("/genre/{genre}")
   fun getMusicByGenre(@PathVariable genre: String) =
       ResponseEntity.ok(musicService.getMusicByGenre(genre))
   ```

2. **Implement service method**:
   ```kotlin
   interface MusicService {
       fun getMusicByGenre(genre: String): List<MusicResponseDto>
   }
   ```

3. **Add repository method** if needed:
   ```kotlin
   interface MusicRepository : JpaRepository<MusicEntity, Int> {
       fun findByGenreIgnoreCase(genre: String): List<MusicEntity>
   }
   ```

4. **Update database schema** with migration:
   ```sql
   -- db/migration/V2__add_genre_column.sql
   ALTER TABLE MUSIC ADD COLUMN genre VARCHAR(100);
   ```

#### Adding Batch Job Components

1. **Create new batch configuration**:
   ```kotlin
   @Bean
   fun newDataBatchJob(): Job {
       return JobBuilder("newDataBatchJob", jobRepository)
           .incrementer(RunIdIncrementer())
           .start(newDataBatchStep())
           .build()
   }
   ```

2. **Implement processors and writers** following existing patterns
3. **Add configuration properties** for external API integration

#### Extending Domain Models

1. **Update entity classes** with new fields
2. **Create database migration scripts**
3. **Update DTOs and response objects**
4. **Modify repository methods** for new query requirements

### Testing Strategy

#### Unit Testing
```kotlin
@ExtendWith(MockitoExtension::class)
class MusicServiceTest {
    @Mock
    private lateinit var musicRepository: MusicRepository

    @InjectMocks
    private lateinit var musicService: MusicServiceImpl

    @Test
    fun `should return music by id`() {
        // Test implementation
    }
}
```

#### Integration Testing
```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = ["classpath:application-test.yml"])
class MusicControllerIntegrationTest {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `should get daily top rankings`() {
        // Integration test implementation
    }
}
```

#### Testing Redis Integration
```kotlin
@DataRedisTest
class RedisCacheTest {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Test
    fun `should cache ranking data`() {
        // Redis integration test
    }
}
```

### Code Quality Standards

- **Kotlin Coding Conventions**: Follow official Kotlin style guide
- **Null Safety**: Leverage Kotlin's null safety features
- **Documentation**: Use KDoc for public APIs
- **Error Handling**: Implement proper exception handling with custom exceptions
- **Logging**: Use structured logging with appropriate levels

### Debugging Tips

1. **Enable SQL logging** in `application.yml`:
   ```yaml
   logging:
     level:
       org.hibernate.SQL: DEBUG
   ```

2. **Monitor Redis operations**:
   ```bash
   redis-cli monitor
   ```

3. **Use Spring Boot Actuator** for health monitoring:
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: health,info,metrics
   ```

## Deployment Guide

### Production Configuration

#### Database Configuration
Replace H2 with production database in `application-prod.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/music_ranking
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate  # Use migrations only
    show-sql: false      # Disable in production
```

#### Redis Configuration
```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms
      jedis:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0
```

#### Application Security
```yaml
server:
  port: ${PORT:8080}

management:
  endpoints:
    web:
      exposure:
        include: health,metrics
  endpoint:
    health:
      show-details: when_authorized

logging:
  level:
    root: INFO
    kr.co.music: INFO
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

### Docker Deployment

#### Dockerfile for API Server
```dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app
COPY music-api/build/libs/music-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Docker Compose
```yaml
version: '3.8'
services:
  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"

  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: music_ranking
      POSTGRES_USER: music_user
      POSTGRES_PASSWORD: music_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  music-api:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - redis
      - postgres
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_USERNAME=music_user
      - DB_PASSWORD=music_password
      - REDIS_HOST=redis

volumes:
  postgres_data:
```

### Monitoring and Observability

#### Health Checks
The application exposes health endpoints:
- `/actuator/health` - Application health status
- `/actuator/metrics` - Application metrics
- Custom health indicators for Redis and database connectivity

#### Logging Configuration
```yaml
logging:
  level:
    kr.co.music: INFO
    org.springframework.retry: DEBUG
  pattern:
    file: "%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/music-app/application.log
```

#### Performance Monitoring
- Monitor Redis cache hit rates
- Track API response times
- Monitor batch job execution times
- Database connection pool metrics

### Scaling Considerations

1. **Horizontal Scaling**
   - API servers are stateless and can be load balanced
   - Redis can be clustered for high availability
   - Database read replicas for read-heavy workloads

2. **Caching Strategy**
   - Ranking data cached with TTL
   - Consider cache warming strategies
   - Monitor cache hit rates

3. **Batch Processing**
   - Scale batch jobs based on data volume
   - Consider partitioning large datasets
   - Monitor job execution times and failure rates

## Contributing

### Development Workflow

1. **Fork and Clone**
   ```bash
   git fork <repository-url>
   git clone <your-fork-url>
   cd music-app
   ```

2. **Create Feature Branch**
   ```bash
   git checkout -b feature/new-feature-name
   ```

3. **Development Process**
   - Write tests first (TDD approach)
   - Implement feature following existing patterns
   - Ensure all tests pass
   - Update documentation if needed

4. **Code Quality Checks**
   ```bash
   # Run tests
   ./gradlew test

   # Build project
   ./gradlew build

   # Check code style (if configured)
   ./gradlew ktlintCheck
   ```

5. **Commit and Push**
   ```bash
   git add .
   git commit -m "feat: add new ranking algorithm"
   git push origin feature/new-feature-name
   ```

6. **Create Pull Request**
   - Provide clear description of changes
   - Link to related issues
   - Ensure CI checks pass

### Code Standards

#### Kotlin Style Guide
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Keep functions focused and small
- Leverage Kotlin's null safety

#### Commit Message Format
```
type(scope): description

feat(api): add genre-based music filtering
fix(batch): resolve Spotify token refresh issue
docs(readme): update deployment instructions
test(service): add integration tests for ranking
```

#### Testing Requirements
- Unit tests for service layer logic
- Integration tests for API endpoints
- Repository tests for custom queries
- Minimum 80% code coverage

#### Documentation Standards
- KDoc for public APIs
- README updates for new features
- API documentation for new endpoints
- Architecture decision records (ADRs) for significant changes

### Issue Reporting

When reporting issues, include:
- Environment details (OS, Java version, etc.)
- Steps to reproduce
- Expected vs actual behavior
- Relevant logs or error messages
- Minimal code example if applicable

### Feature Requests

For new features:
- Describe the use case and business value
- Provide API design proposals
- Consider backward compatibility
- Include testing strategy

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For questions and support:
- Create an issue in the repository
- Check existing documentation
- Review closed issues for similar problems

## Changelog

### Version 0.0.1-SNAPSHOT
- Initial release with core functionality
- Spotify API integration
- Redis caching implementation
- Basic REST API endpoints
- Spring Batch data collection