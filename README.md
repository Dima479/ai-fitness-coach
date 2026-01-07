# AI Fitness Coach (Desktop Swing + SQLite)

**Same concept as your Oracle schema** (users/profile/plans/progress/chat) but:
- **Java Swing (javax.swing)** UI (no HTML)
- **SQLite** local DB file: `src/main/resources/baza de date.db`

## Run
1) Open as Maven project.
2) `mvn -q clean package`
3) `java -jar target/ai-fitness-coach-swing-1.0.0.jar`

First start auto-creates tables and inserts a test user:
- email: `test@example.com`
- password: `test123`

## DB path
Preferred path (as requested): `src/main/resources/baza de date.db`  
Fallback if not writable: `./baza de date.db`
