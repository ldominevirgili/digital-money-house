# Test exploratorio - Sprint 3

## Objetivo del sprint
Ingresar dinero a las cuentas y obtener información sobre las transacciones.

## Forma de organización

- **Alcance:** Endpoints de actividad (GET activity, GET activity/transaccion), ingreso desde tarjeta (POST transferences).
- **Duración sugerida:** 1–2 sesiones.
- **Herramientas:** Postman/Insomnia o curl, usuario con cuenta y tarjeta asociada.

## Notas y checklist

### 1. GET /accounts/{id}/activity
- [ ] Con token válido y cuenta propia: 200, lista ordenada de más reciente a más antigua.
- [ ] Sin token: 401.
- [ ] Token de otro usuario, id de cuenta ajena: 403.
- [ ] Id de cuenta inexistente: 404 (o 403 según implementación).
- [ ] Cuenta sin movimientos: 200, array vacío.

### 2. GET /accounts/{id}/activity/{transactionId}
- [ ] Con token, cuenta y transacción propios: 200, detalle (id, amount, type, description, createdAt).
- [ ] Sin token: 401.
- [ ] Id transacción de otra cuenta: 404.
- [ ] Id transacción inexistente: 404.

### 3. POST /accounts/{id}/transferences (ingreso desde tarjeta)
- [ ] Body: `{ "cardId": 1, "amount": 100.50, "description": "opcional" }`. Token y cuenta propios, tarjeta asociada a la cuenta: 201, body con la transacción creada.
- [ ] Sin token: 401.
- [ ] Cuenta de otro usuario: 403.
- [ ] cardId de tarjeta no asociada a esa cuenta: 404.
- [ ] amount &lt; 0 o 0: 400.
- [ ] amount válido, sin description: 201 igual.
- [ ] Verificar que el saldo GET /accounts/{id} aumentó después del ingreso.
- [ ] Verificar que la nueva transacción aparece en GET /accounts/{id}/activity y en GET /accounts/{id}/transactions.

### Datos de prueba
- Usuario registrado + login para obtener token.
- Id de cuenta: GET /accounts/by-user/{userId}.
- Id de tarjeta: POST /cards luego POST /accounts/{id}/cards para asociar, o GET /cards/accounts/{accountId}/cards.

## Defectos / observaciones
_(Anotar aquí cualquier fallo o comportamiento inesperado.)_

## Resultado
- Casos ejecutados:
- Casos pasados:
- Defectos abiertos:
