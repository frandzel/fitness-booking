# Aplikacja zapisów na zajęcia fitness

Prosty system zapisu na treningi fitness składający się z backendu w Spring Boot (H2, REST API) oraz frontendu w Angularze.

## Wymagania

- Java 21 i Maven Wrapper (`backend/mvnw`)
- Node.js 20+ oraz npm

## Backend

```bash
cd backend
./mvnw spring-boot:run
```

API udostępnia zasoby pod `http://localhost:8080/api/sessions`, a baza H2 działa w pamięci (`/h2-console`). Przy starcie ładowane są przykładowe zajęcia.

## Frontend

```bash
cd frontend
npm install
npm start
```

Angular uruchamia się pod `http://localhost:4200` i komunikuje z backendem (CORS skonfigurowany).

## Funkcjonalności

- lista zajęć z nazwą, opisem, datą i liczbą zapisanych
- dodawanie/edycja/usuwanie zajęć oraz zmiana daty
- zapisywanie uczestników i rezygnacje (blokada na 24h przed startem)
- walidacje po stronie backendu (unikalność nazwy+daty, limit miejsc, data w przyszłości)
- responsywny interfejs z formularzem i listą uczestników

## Testy

```bash
cd backend && ./mvnw test
cd frontend && npm run build
```

