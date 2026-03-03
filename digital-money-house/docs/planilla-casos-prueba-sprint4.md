# Planilla de casos de prueba - Sprint 4

| ID | Funcionalidad | Caso de prueba | Precondiciones | Pasos | Resultado esperado | Automatizado |
|----|---------------|----------------|----------------|-------|-------------------|--------------|
| TF-01 | Transferencias | Transferencia exitosa | Cuenta con saldo suficiente | POST /accounts/{id}/transferences/transfer body: {target, amount, description} | 200 OK, transacción saliente creada, saldos actualizados | Sí (SmokeSprint4Test)
| TF-02 | Transferencias | Fondos insuficientes | Cuenta con saldo insuficiente | POST /accounts/{id}/transferences/transfer body: {target, amount} | 410 Fondos insuficientes, sin cambios en saldos | Sí (SmokeSprint4Test)
| TF-03 | Transferencias | Destinatario inexistente | - | POST /accounts/{id}/transferences/transfer with invalid target | 404 Not Found | No
| TF-04 | Transferencias | Validación de request | amount < 0.01 o target vacío | POST invalid payload | 400 Bad Request | No
| TF-05 | Transferencias | Transferir a la misma cuenta | target = misma cuenta | POST ... | 400 Bad Request | No
| TF-06 | Transferencias | Listado últimos destinatarios | Cuenta con transferencias previas | GET /accounts/{id}/transferences | 200 OK, lista de destinatarios (últimos primero) | No

**Ejecución**
- Servicios levantados. Suite de smoke-tests: `mvn -pl smoke-tests test` (incluye `SmokeSprint4Test`).
- Para ejecutar sólo sprint 4: `mvn -pl smoke-tests -Dtest=com.digitalmoneyhouse.smoke.SmokeSprint4Test test`.

