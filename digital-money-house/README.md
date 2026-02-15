# Digital Money House

Backend billetera digital - microservicios. Sprint 1.

**Stack:** Java 17, Spring Boot 3, MySQL, Eureka, Gateway, Feign, JWT.

Servicios y puertos:
- eureka-server: 8761
- api-gateway: 8080 (entrada)
- users-service: 8081
- account-service: 8082
- auth-service: 8083

**BD:** MySQL, base `digital_money_house`. En users y account está el datasource (user root/root por defecto, se puede cambiar en el yml).

**Orden para levantar:** primero Eureka, después Gateway, después account, users y auth (así se registran en Eureka).

Endpoints por el gateway (puerto 8080):
- POST /users/register — body: firstName, lastName, email, password. Devuelve usuario + cvu + alias.
- POST /auth/login — email, password. Devuelve token.
- POST /user/logout — 200 ok.

Swagger en cada servicio: 8081, 8082, 8083 /swagger-ui.html

Ramas: main, dev, test (git checkout -b dev, etc).
