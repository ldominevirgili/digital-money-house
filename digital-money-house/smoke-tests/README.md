# Smoke tests - Sprint 1 y 2

Suite de pruebas automatizadas con **JUnit 5** y **RestAssured**. Ejecuta contra el Gateway (puerto 8080).

## Requisitos

- Los servicios deben estar levantados: Eureka, Gateway, Auth, Users, Account (en ese orden).

## Ejecución

Desde la raíz del proyecto:

```bash
mvn -pl smoke-tests test
```

Para usar otra URL (ej. entorno remoto):

```bash
mvn -pl smoke-tests test -Dtest.base.uri=http://host:8080
```

## Contenido

- **SmokeSprint1Test**: registro, login, logout.
- **SmokeSprint2Test**: saldo, transacciones, perfil, tarjetas (crear, asociar, listar, detalle, eliminar), resumen cuenta, validación.

Los casos coinciden con la planilla en `docs/planilla-casos-prueba-sprint2.md` y `docs/planilla-casos-prueba-sprint2.csv`.
