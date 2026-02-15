# Digital Money House

Backend billetera digital - microservicios. Sprint 1 + 2.

**Stack:** Java 17, Spring Boot 3, MySQL, Eureka, Gateway, Feign, JWT.

Servicios y puertos:
- eureka-server: 8761
- api-gateway: 8080 (entrada)
- users-service: 8081
- account-service: 8082
- auth-service: 8083

**BD:** MySQL, base `digital_money_house`. En users y account está el datasource (user root/root por defecto, se puede cambiar en el yml).

**Orden para levantar:** primero Eureka, después Gateway, después account, users y auth (así se registran en Eureka).

Endpoints por el gateway (puerto 8080). Donde pide token: header `Authorization: Bearer <token>`.

Sprint 1:
- POST /users/register — body: firstName, lastName, email, password. Devuelve usuario + cvu + alias.
- POST /auth/login — email, password. Devuelve token.
- POST /user/logout — 200 ok.

Sprint 2 (con token):
- GET /accounts/{id} — saldo de la cuenta.
- GET /accounts/{id}/transactions — movimientos (más reciente primero).
- GET /accounts/by-user/{userId} — resumen cuenta (cvu, alias).
- GET /users/{id} — perfil (datos usuario + cvu + alias).
- POST /cards — body: number, type, holderName, expiry. Crear tarjeta.
- POST /accounts/{id}/cards — body: { "cardId": 1 }. Asociar tarjeta a cuenta (409 si ya está en otra).
- GET /cards/accounts/{accountId}/cards — listar tarjetas de la cuenta.
- GET /cards/accounts/{accountId}/cards/{cardId} — detalle tarjeta.
- DELETE /cards/accounts/{accountId}/cards/{cardId} — eliminar tarjeta.

Swagger en cada servicio: 8081, 8082, 8083 /swagger-ui.html

Ramas: main, dev, test (git checkout -b dev, etc).

**Testing Sprint 2**
- Planilla de casos: `docs/planilla-casos-prueba-sprint2.md` y `docs/planilla-casos-prueba-sprint2.csv`.
- Suite ejecutable: `docs/suite-pruebas-sprint2.md`.
- Smoke automatizado (RestAssured): módulo `smoke-tests`. Con todos los servicios arriba: `mvn -pl smoke-tests test`.
