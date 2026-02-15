# Planilla de casos de prueba - Sprint 2

| ID | Funcionalidad | Caso de prueba | Precondiciones | Pasos | Resultado esperado | Automatizado |
|----|---------------|----------------|----------------|-------|-------------------|--------------|
| S2-01 | Saldo cuenta | Consultar saldo con token válido | Usuario registrado, cuenta creada, token JWT | GET /accounts/{id} con header Authorization: Bearer {token}, id = cuenta del usuario | 200, body con balance | Sí |
| S2-02 | Saldo cuenta | Consultar saldo sin token | - | GET /accounts/1 sin header Authorization | 401 | Sí |
| S2-03 | Saldo cuenta | Consultar saldo de cuenta de otro usuario | Token de usuario A, id de cuenta de usuario B | GET /accounts/{idOtro} con token de A | 403 | Sí |
| S2-04 | Transacciones | Listar movimientos con token válido | Cuenta existente, usuario dueño | GET /accounts/{id}/transactions con token | 200, array (puede estar vacío) | Sí |
| S2-05 | Transacciones | Listar movimientos sin token | - | GET /accounts/1/transactions sin token | 401 | Sí |
| S2-06 | Perfil usuario | Ver perfil propio con token | Usuario registrado | GET /users/{id} con token del mismo id | 200, firstName, lastName, email, cvu, alias | Sí |
| S2-07 | Perfil usuario | Ver perfil de otro usuario | Token usuario A | GET /users/{idB} con token de A | 403 | Sí |
| S2-08 | Perfil usuario | Ver perfil sin token | - | GET /users/1 sin token | 401 | Sí |
| S2-09 | Tarjetas | Crear tarjeta con token | Usuario autenticado | POST /cards body: number, type, holderName, expiry | 201, body con id y datos | Sí |
| S2-10 | Tarjetas | Asociar tarjeta a cuenta | Tarjeta creada, cuenta del usuario | POST /accounts/{id}/cards body: { "cardId": 1 } con token | 201 | Sí |
| S2-11 | Tarjetas | Asociar tarjeta ya asociada a otra cuenta | Tarjeta con accountId distinto | POST /accounts/{id}/cards con cardId de tarjeta ya vinculada | 409 | Sí |
| S2-12 | Tarjetas | Listar tarjetas de la cuenta | Cuenta con al menos una tarjeta | GET /cards/accounts/{accountId}/cards con token | 200, lista de tarjetas | Sí |
| S2-13 | Tarjetas | Detalle de una tarjeta | Tarjeta existente en la cuenta | GET /cards/accounts/{accountId}/cards/{cardId} con token | 200, detalle tarjeta | Sí |
| S2-14 | Tarjetas | Eliminar tarjeta | Tarjeta asociada a la cuenta | DELETE /cards/accounts/{accountId}/cards/{cardId} con token | 200 | Sí |
| S2-15 | Tarjetas | Detalle tarjeta inexistente | - | GET /cards/accounts/1/cards/99999 con token | 404 | Sí |
| S2-16 | Resumen cuenta | Obtener CVU y alias por userId | Usuario con cuenta | GET /accounts/by-user/{userId} con token del mismo userId | 200, cvu, alias | Sí |
| S2-17 | Validación | Crear tarjeta sin campos obligatorios | - | POST /cards body vacío o sin number/type/holderName | 400 | Sí |

**Leyenda**
- Automatizado: Sí = incluido en suite smoke con RestAssured.

**Ejecución suite smoke**
- Servicios levantados (Eureka, Gateway, Auth, Users, Account).
- `mvn -pl smoke-tests test` desde la raíz del proyecto.
