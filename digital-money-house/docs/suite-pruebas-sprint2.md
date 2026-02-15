# Suite de pruebas Sprint 2

Casos que se pueden ejecutar (manual o automatizado).

## Condiciones
- Base URL: `http://localhost:8080` (Gateway).
- Token: obtener con POST /auth/login (email, password). Incluir en requests: `Authorization: Bearer <token>`.

## Casos ejecutables

1. **Saldo**
   - GET /accounts/{id} con token → 200 y balance.
   - GET /accounts/{id} sin token → 401.
   - GET /accounts/{id} con token de otro usuario → 403.

2. **Transacciones**
   - GET /accounts/{id}/transactions con token → 200 y lista.

3. **Perfil**
   - GET /users/{id} con token del mismo usuario → 200, datos + cvu + alias.
   - GET /users/{id} con token de otro → 403.

4. **Tarjetas**
   - POST /cards (crear) → 201.
   - POST /accounts/{id}/cards con cardId → 201 (o 409 si ya asociada).
   - GET /cards/accounts/{accountId}/cards → 200.
   - GET /cards/accounts/{accountId}/cards/{cardId} → 200.
   - DELETE /cards/accounts/{accountId}/cards/{cardId} → 200.
   - GET tarjeta inexistente → 404.

5. **Resumen cuenta**
   - GET /accounts/by-user/{userId} con token → 200, cvu, alias.

6. **Validación**
   - POST /cards sin datos requeridos → 400.

La suite automatizada (smoke) está en el módulo `smoke-tests` y usa RestAssured para estos casos.
