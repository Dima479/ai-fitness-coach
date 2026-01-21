# AI Fitness Coach

AI Fitness Coach este o aplicatie desktop Java care ajuta utilizatorii sa isi gestioneze profilul, sa genereze planuri de antrenament si nutritie, sa urmareasca progresul si sa interactioneze cu un asistent AI.

## Functionalitati

- Autentificare si creare cont.
- Profil utilizator cu date personale si obiective.
- Planuri de antrenament si nutritie generate automat.
- Monitorizare progres (greutate, calorii, antrenament, note).
- Chat AI pentru recomandari si intrebari.

## Tehnologii

- Java 17+
- Java Swing
- SQLite
- Maven
- Jackson, SLF4J, SQLite JDBC

## Rulare din terminal (PowerShell)

Seteaza cheia OpenRouter:

```
$env:OPENROUTER_API_KEY="sk-or-v1-..."
```

Ruleaza aplicatia:

```
mvn -q -DskipTests package dependency:copy-dependencies
java -cp "target/classes;target/dependency/*" aicoach.App
```

Alternativ, poti rula clasa `aicoach.App` direct din IDE.

## Baza de date

Aplicatia foloseste SQLite. La prima rulare sunt create tabelele necesare si este inserat un utilizator de test daca baza de date nu exista.

Date de acces pentru utilizatorul de test:
- Email: test@example.com
- Parola: test123

## Structura proiectului

- `ui` - interfata grafica
- `model` - modelele de date
- `dao` - acces la baza de date
- `service` - logica de business
- `util` - utilitare
