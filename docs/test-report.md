# Backend Test Report

**Command:** `mvn test` (run from `fashionmate-backend/`)
**Date:** 2026-08-26
**Result:** BUILD FAILURE (1 test errored)

## Summary

| Total | Passed | Failed | Errored |
|-------|--------|--------|---------|
| 23    | 22     | 0      | 1       |

## Per-class results

| Test class | Tests run | Failures | Errors |
|---|---|---|---|
| `fashionmate_backend.controllers.GlamUpControllerTest` | 2 | 0 | 0 |
| `fashionmate_backend.controllers.OutfitTipControllerTest` | 1 | 0 | 0 |
| `fashionmate_backend.controllers.ReviewControllerTest` | 6 | 0 | 0 |
| `fashionmate_backend.controllers.StyleLensControllerTest` | 1 | 0 | 0 |
| `fashionmate_backend.controllers.UserControllerTest` | 7 | 0 | 0 |
| `fashionmate_backend.FashionmateBackendApplicationTests` | 1 | 0 | **1** |
| `fashionmate_backend.models.GlamUpTest` | 1 | 0 | 0 |
| `fashionmate_backend.models.ImageRequestTest` | 1 | 0 | 0 |
| `fashionmate_backend.models.ReviewTest` | 1 | 0 | 0 |
| `fashionmate_backend.models.StyleLensTest` | 1 | 0 | 0 |
| `fashionmate_backend.models.UserTest` | 1 | 0 | 0 |

## Errored test

### `fashionmate_backend.FashionmateBackendApplicationTests.contextLoads`

**Error:** `java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@... testClass = fashionmate_backend.FashionmateBackendApplicationTests, ... classes = [fashionmate_backend.FashionmateBackendApplication] ...]`

**Root cause (nested exception chain):**

```
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.exception.JDBCConnectionException: Unable to open JDBC Connection for DDL execution [Communications link failure

The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.] [n/a]

Caused by: org.hibernate.exception.JDBCConnectionException: Unable to open JDBC Connection for DDL execution [Communications link failure

The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.] [n/a]

Caused by: com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure

The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.

Caused by: com.mysql.cj.exceptions.CJCommunicationsException: Communications link failure

The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.

Caused by: java.net.ConnectException: Connection refused
	at java.base/sun.nio.ch.Net.pollConnect(Native Method)
	at java.base/sun.nio.ch.Net.pollConnectNow(Net.java:694)
	...
	at com.mysql.cj.protocol.StandardSocketFactory.connect(StandardSocketFactory.java:144)
```

**Analysis:** `contextLoads` boots the full Spring application context, which requires a live MySQL database connection to build the Hibernate `EntityManagerFactory`. No MySQL server was reachable in this environment, so the connection was refused and the context failed to load. This is an environment/infrastructure issue (no database available), not a code defect surfaced by the test itself.

## Notes

- No source or test files were modified as part of this run.
- Full raw Maven output is available at `/tmp/mvn_test_output.log` (not committed) and per-test XML/TXT reports under `fashionmate-backend/target/surefire-reports/`.
