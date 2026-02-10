# WebRTC Signaling Server

Real-time video translation platform - Phase 1: Core signaling + one-to-one WebRTC

## Features

- WebSocket-based signaling for WebRTC
- Room management with token-based authentication
- One-to-one video calling
- STUN/TURN server integration
- Automatic room and participant cleanup
- Health monitoring and metrics
- Production-ready error handling

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker (optional)

## Quick Start

### Using Maven

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Using Docker

```bash
docker-compose up --build
```

The server will start on `http://localhost:8080`

## API Endpoints

### REST API

#### Create Room
```http
POST /api/rooms
Content-Type: application/json

{
  "name": "My Video Call"
}
```

Response:
```json
{
  "roomId": "abc123",
  "roomName": "My Video Call",
  "token": "creator-token",
  "createdAt": "2024-01-01T10:00:00",
  "expiresAt": "2024-01-01T11:00:00",
  "iceServers": [...]
}
```

#### Join Room
```http
POST /api/rooms/join
Content-Type: application/json

{
  "roomId": "abc123"
}
```

#### Get Room Info
```http
GET /api/rooms/{roomId}
```

#### Health Check
```http
GET /api/health
GET /api/health/stats
```

### WebSocket API

Connect to: `ws://localhost:8080/ws/signaling`

#### Message Types

**Join Room:**
```json
{
  "type": "join",
  "roomId": "abc123",
  "token": "your-token"
}
```

**Send Offer:**
```json
{
  "type": "offer",
  "targetParticipantId": "participant-id",
  "payload": {
    "sdp": "...",
    "type": "offer"
  }
}
```

**Send Answer:**
```json
{
  "type": "answer",
  "targetParticipantId": "participant-id",
  "payload": {
    "sdp": "...",
    "type": "answer"
  }
}
```

**Send ICE Candidate:**
```json
{
  "type": "ice-candidate",
  "targetParticipantId": "participant-id",
  "payload": {
    "candidate": "...",
    "sdpMid": "...",
    "sdpMLineIndex": 0
  }
}
```

**Leave Room:**
```json
{
  "type": "leave"
}
```

## Configuration

Edit `src/main/resources/application.yml`:

```yaml
webrtc:
  stun:
    servers:
      - urls: "stun:stun.l.google.com:19302"
  room:
    max-participants: 2
    token-expiry-minutes: 60
```

## Testing

Run unit tests:
```bash
mvn test
```

## Production Deployment

1. Build production JAR:
   ```bash
   mvn clean package -Pprod
   ```

2. Run with production profile:
   ```bash
   java -jar target/signaling-server-1.0.0.jar --spring.profiles.active=prod
   ```

3. Or use Docker:
   ```bash
   docker build -t signaling-server:1.0.0 .
   docker run -p 8080:8080 signaling-server:1.0.0
   ```

## License