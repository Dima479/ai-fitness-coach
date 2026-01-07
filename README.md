# AI Fitness Coach

Acest proiect este o aplicatie desktop dezvoltata in Java, menita sa functioneze ca un asistent personal pentru fitness si nutritie. Aplicatia permite utilizatorilor sa isi gestioneze profilul, sa vizualizeze planuri de antrenament si dieta, sa isi monitorizeze progresul si sa comunice cu un asistent virtual.

## Functionalitati principale

- Autentificare si Inregistrare: Sistem securizat de login si creare cont.
- Profil Utilizator: Gestionarea datelor personale (varsta, greutate, inaltime, obiective).
- Planuri Personalizate: Vizualizarea planurilor de antrenament si nutritie.
- Monitorizare Progres: Urmarirea evolutiei in timp a greutatii si a altor parametri.
- Asistent AI: Un modul de chat unde utilizatorul poate cere sfaturi sau informatii suplimentare.

## Tehnologii utilizate

- Limbaj: Java (JDK 17+)
- Interfata Grafica: Java Swing
- Baza de date: SQLite (stocare locala)
- Build System: Apache Maven
- Biblioteci: Jackson (pentru JSON), SLF4J (logging), SQLite JDBC

## Instructiuni de instalare si rulare

Pentru a rula aplicatia pe propriul calculator, urmati pasii de mai jos:

1. Deschideti proiectul intr-un mediu de dezvoltare (IDE) care suporta Maven, cum ar fi IntelliJ IDEA sau Eclipse.
2. Asigurati-va ca aveti instalat Java Development Kit (JDK) versiunea 17 sau mai noua.
3. Compilati proiectul folosind Maven. Puteti face acest lucru din linia de comanda:
   mvn clean package
4. Rulati aplicatia. Dupa compilare, fisierul executabil (JAR) se va gasi in folderul `target`. Il puteti lansa cu comanda:
   java -jar target/ai-fitness-coach-swing-1.0.0.jar

Alternativ, puteti rula clasa principala `aicoach.App` direct din IDE.

## Baza de date

Aplicatia foloseste o baza de date SQLite. La prima rulare, aplicatia va crea automat tabelele necesare si va insera un utilizator de test daca baza de date nu exista.

Fisierul bazei de date se va numi `baza de date.db` si va fi creat in folderul `src/main/resources` sau in directorul curent, in functie de permisiunile de scriere.

Date de acces pentru utilizatorul de test (generat automat):
- Email: test@example.com
- Parola: test123

## Structura proiectului

Codul sursa este organizat in pachete pentru a separa logica aplicatiei:
- ui: Contine ferestrele si panourile interfetei grafice.
- model: Clasele care definesc structura datelor (Utilizator, Plan, etc.).
- dao: Obiecte de acces la date pentru comunicarea cu baza de date.
- service: Logica de business a aplicatiei.
- util: Clase utilitare pentru criptare, dialoguri si configurari.

---
Dezvoltat pentru a oferi o experienta simpla si eficienta in gestionarea stilului de viata sanatos.
