# Gym CRM System Monitoring and Metrics Visualization

## Monitoring Architecture

The monitoring system consists of three components:

1. **Gym CRM App** - Application with Actuator and Prometheus metrics
2. **Prometheus** - Metrics collection server
3. **Grafana** - Metrics visualization

## Quick Start

### 1. Start the Complete Stack

```bash
docker-compose up -d
```

This command will start all three services:
- Gym CRM App: http://localhost:8080
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

### 2. Access Services

**Application:**
- URL: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Actuator Health: http://localhost:8080/actuator/health
- Actuator Metrics: http://localhost:8080/actuator/metrics
- Prometheus Metrics: http://localhost:8080/actuator/prometheus

**Prometheus:**
- URL: http://localhost:9090
- View Metrics: http://localhost:9090/graph
- Targets: http://localhost:9090/targets

**Grafana:**
- URL: http://localhost:3000
- Login: `admin`
- Password: `admin`

### 3. View Dashboard

After logging into Grafana:
1. Navigate to "Dashboards"
2. Select "Gym CRM System Metrics"
3. The dashboard will load automatically with preconfigured panels

## Available Metrics

### Custom Metrics

**Training Metrics:**
- `gym_trainings_created_total` - Total number of trainings created
- `gym_trainings_active` - Number of active trainings

**User Metrics:**
- `gym_users_registered_total` - Total number of registered users
- `gym_users_logins_total` - Total number of logins
- `gym_users_active` - Number of active users

**Request Metrics:**
- `gym_requests_total` - Total number of HTTP requests
- `gym_requests_errors_total` - Number of errors
- `gym_requests_duration_seconds` - Request duration

### Health Indicators

**Database Health:**
- PostgreSQL connection status
- Number of training types in database

**Disk Space Health:**
- Free disk space
- Usage percentage
- Threshold: 10 GB

**External Service Health:**
- External service status (Notification Service)

## Grafana Dashboard

The preconfigured dashboard includes:

1. **Training Metrics** - Chart of created and active trainings
2. **User Metrics** - Registrations, logins, and active users
3. **HTTP Request Rate** - Requests per second and errors
4. **Request Duration** - Response time percentiles (p50, p95, p99)
5. **Health Status** - Application status (UP/DOWN)
6. **Total Trainings** - Total number of trainings
7. **Total Users** - Total number of users

## Prometheus Queries

Examples of useful queries:

```promql
# Training creation rate over the last minute
rate(gym_trainings_created_total[1m])

# Average request duration over 5 minutes
rate(gym_requests_duration_seconds_sum[5m]) / rate(gym_requests_duration_seconds_count[5m])

# Error rate percentage
rate(gym_requests_errors_total[1m]) / rate(gym_requests_total[1m]) * 100

# Active users
gym_users_active
```

## Stop Services

```bash
docker-compose down
```

To remove data (volumes):
```bash
docker-compose down -v
```

## Troubleshooting

### Prometheus Cannot See Targets

1. Check if the application is running:
```bash
docker-compose ps
```

2. Check logs:
```bash
docker-compose logs gym-app
docker-compose logs prometheus
```

3. Check metrics endpoint manually:
```bash
curl http://localhost:8080/actuator/prometheus
```

### Grafana Shows No Data

1. Ensure Prometheus is connected as a data source
2. Check that Prometheus has data: http://localhost:9090/targets
3. Reload the dashboard

### Configuration Changes

After modifying configuration:
```bash
docker-compose down
docker-compose up -d --build
```