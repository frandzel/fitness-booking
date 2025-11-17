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

## Hosting na Render.com (darmowy tier)

Aplikacja jest gotowa do wdrożenia na Render.com z darmowym planem.

### Wymagania
- Konto na [Render.com](https://render.com) (darmowe)
- Repozytorium na GitHubie (już masz: https://github.com/frandzel/fitness-booking)

### Kroki wdrożenia

1. **Zaloguj się do Render.com** i połącz swoje konto GitHub

2. **Utwórz nowy "Blueprint"** (lub użyj opcji "New +" → "Blueprint"):
   - Render automatycznie wykryje plik `render.yaml` z repozytorium
   - Wybierz repozytorium `frandzel/fitness-booking`
   - Render utworzy 3 serwisy:
     - **PostgreSQL Database** (darmowa baza danych)
     - **Backend API** (Spring Boot)
     - **Frontend** (statyczna strona Angular)

3. **Po wdrożeniu**:
   - Backend będzie dostępny pod adresem: `https://fitness-booking-api.onrender.com`
   - Frontend będzie dostępny pod adresem: `https://fitness-booking-frontend.onrender.com`
   - Baza danych będzie automatycznie skonfigurowana

4. **Aktualizuj CORS w backendzie** (jeśli potrzebne):
   - W Render Dashboard → Backend Service → Environment
   - Ustaw `CORS_ORIGINS` na URL frontendu

### Alternatywne opcje hostingu

- **Railway.app** - podobny do Render, darmowy tier z $5 kredytem miesięcznie
- **Vercel** (frontend) + **Render** (backend) - Vercel świetny dla Angular
- **Netlify** (frontend) + **Render** (backend) - Netlify też dobry dla statycznych stron

### Uwagi
- Na darmowym planie Render aplikacja "śpi" po 15 minutach bezczynności (pierwsze żądanie może być wolne)
- Baza PostgreSQL jest darmowa, ale ma limit 90MB
- Wszystkie dane są trwałe (nie znikają po restarcie jak H2 w pamięci)

