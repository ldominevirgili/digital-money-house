# Planilla de casos de prueba - Sprint 3

| ID | Funcionalidad | Caso de prueba | Precondiciones | Pasos | Resultado esperado | Automatizado |
|----|---------------|----------------|----------------|-------|-------------------|--------------|
| S3-01 | Actividad | Historial con token | Cuenta existente | GET /accounts/{id}/activity con token | 200, lista ordenada más reciente primero | Sí |
| S3-02 | Actividad | Historial sin token | - | GET /accounts/{id}/activity sin token | 401 | Sí |
| S3-03 | Actividad | Historial cuenta ajena | Token usuario A | GET /accounts/{idB}/activity con token A | 403 | Sí |
| S3-04 | Detalle transacción | Detalle con token | Transacción existente en la cuenta | GET /accounts/{id}/activity/{transactionId} con token | 200, detalle transacción | Sí |
| S3-05 | Detalle transacción | Detalle sin token | - | GET /accounts/1/activity/1 sin token | 401 | Sí |
| S3-06 | Detalle transacción | Transacción inexistente | - | GET /accounts/{id}/activity/999999 con token | 404 | Sí |
| S3-07 | Ingreso desde tarjeta | Ingreso válido | Cuenta con tarjeta asociada | POST /accounts/{id}/transferences body: cardId, amount (y opcional description) | 201, transacción creada | Sí |
| S3-08 | Ingreso desde tarjeta | Ingreso sin token | - | POST /accounts/1/transferences sin token | 401 | Sí |
| S3-09 | Ingreso desde tarjeta | Tarjeta no pertenece a la cuenta | Tarjeta de otra cuenta | POST con cardId ajeno a la cuenta | 404 | Sí |
| S3-10 | Ingreso desde tarjeta | Monto inválido | - | POST con amount 0 o negativo | 400 | Sí |

**Ejecución**
- Servicios levantados. Suite: `mvn -pl smoke-tests test` (incluye SmokeSprint3Test).
