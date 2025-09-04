# Time-Travel Analytics on Operational Data (MariaDB System-Versioned)

Bring **“undo the past”** to business data. This project uses **MariaDB system-versioned tables** to query and visualize data “as of” any point in time for **audits, rollbacks, and KPI drift analysis**. It includes a **Spring Boot backend**, **Next.js frontend**, and **Docker Compose deployment**.

---

## 🚀 Features

* System-versioned (temporal) tables in MariaDB.
* REST endpoints for:

  * `AS OF` snapshots of tables.
  * Diffs between two points in time.
  * Row-level audit logs (with triggers).
* Row-level masking for historical PII.
* Next.js web UI to explore snapshots & diffs.
* Integration tests for backend.
* Docker Compose for local stack.
* CI/CD workflow for GitHub Actions.

---

## 📂 Project Structure

```
time-travel-analytics/
├── backend/          # Spring Boot app
├── frontend/         # Next.js app
├── db/               # SQL schema, seed, triggers
├── .github/workflows # CI/CD
├── docker-compose.yml
└── README.md
```

See [Project Layout](#project-layout) for details.

---

## ⚙️ Prerequisites

* **Docker & Docker Compose** (recommended way to run)
* Alternatively:

  * JDK 17+
  * Maven 3.9+
  * Node.js 18+
  * MariaDB 10.11+

---

## 🏃 Quick Start (with Docker Compose)

```bash
# Clone repo
 git clone https://github.com/your-org/time-travel-analytics.git
 cd time-travel-analytics

# Start all services
 docker compose up -d --build

# Access services
Frontend: http://localhost:3000
Backend:  http://localhost:8080/api/asof?table=inventory&timestamp=2025-09-01T12:00:00
MariaDB:  localhost:3306 (user: root, password: example)
```

### Verify database

```bash
docker exec -it time-travel-analytics-mariadb-1 mariadb -uroot -pexample time_travel
SELECT * FROM inventory FOR SYSTEM_TIME AS OF TIMESTAMP '2025-09-01 12:00:00';
```

---

## 🔧 Running Without Docker

### 1. Database

```bash
# Start MariaDB locally
brew install mariadb   # macOS
sudo apt install mariadb-server   # Linux

# Initialize schema & seed data
mysql -uroot -p < db/schema.sql
mysql -uroot -p < db/seed.sql
mysql -uroot -p < db/triggers.sql
```

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend will start on `http://localhost:3000`.

---

## 🧪 Testing

Run backend integration tests:

```bash
cd backend
mvn test
```

Key tests:

* As-of snapshots return rows.
* Diff endpoint returns before/after.
* Row-level masking hides old PII.
* Audit log contains price change history.

---

## 📜 Endpoints

| Endpoint                                                                    | Description                              |
| --------------------------------------------------------------------------- | ---------------------------------------- |
| `/api/asof?table=inventory&timestamp=2025-09-01T12:00:00`                   | Snapshot of table at timestamp           |
| `/api/diff?table=inventory&from=2025-09-01T12:00:00&to=2025-09-02T12:00:00` | Compare two snapshots                    |
| `/api/audit/inventory_audit/{id}`                                           | Audit log for given row (inventory item) |

---

## 🖥️ Frontend Usage

1. Open `http://localhost:3000`
2. Enter table (`inventory`, `orders`, `users`) and timestamp.
3. Click **Load Snapshot**.
4. For diffs: enter two timestamps → view changes.
5. For audit logs: query backend API directly.

---

## 🔒 Security & Masking

* Historical **PII (email, address)** is masked if timestamp < current.
* `inventory_audit` table records **who changed price, when, and old/new values**.
* Extend triggers to capture changes on other tables.

---

## 🔄 Deployment Options

### 1. Docker Compose (default)

```bash
docker compose up -d --build
```

### 2. Kubernetes

* Convert services into Deployments (backend, frontend, mariadb).
* Use PersistentVolume for DB storage.
* Expose frontend with Ingress.

### 3. Cloud Run / ECS

* Push backend & frontend images to registry.
* Use managed DB (e.g., Cloud SQL for MariaDB).
* Configure `application.properties` to point to cloud DB.

---

## 🛠️ Troubleshooting

| Issue                          | Cause                               | Fix                                                                 |
| ------------------------------ | ----------------------------------- | ------------------------------------------------------------------- |
| `backend cannot connect to DB` | DB not ready yet                    | Run `docker compose logs mariadb` → wait until ready                |
| `Table not found`              | Schema not loaded                   | Run `docker exec -it ... mariadb -uroot -pexample < /db/schema.sql` |
| `Frontend 500 error`           | API URL mismatch                    | Check `frontend/next.config.js` proxy points to backend URL         |
| Port conflicts                 | Other services using 3000/8080/3306 | Change ports in `docker-compose.yml`                                |

---

## 📂 Project Layout

```
time-travel-analytics/
├── backend/
│   ├── src/main/java/com/example/timetravel/{...}
│   ├── src/test/java/com/example/timetravel/AsOfIntegrationTest.java
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/
│   ├── pages/index.js
│   ├── package.json
│   └── Dockerfile
│
├── db/
│   ├── schema.sql
│   ├── seed.sql
│   └── triggers.sql
│
├── .github/workflows/ci.yml
├── docker-compose.yml
└── README.md
```

---

## ✅ Next Steps

* Extend diff endpoint to return structured added/removed/changed rows.
* Add RBAC (mask for normal users, reveal for auditors).
* Add Prometheus/Grafana monitoring for query performance.
* Deploy to Kubernetes or Cloud Run for production demo.

---

👩‍💻 With this setup, you can **time-travel your data** safely, audit who changed what, and explore drift in KPIs over time.
