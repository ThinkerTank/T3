# GitFlow y métricas reales

La rama de desarrollo debe crearse desde `main`, luego una rama `feature/sgmms-core` desde `dev`, y la integración realizarse mediante pull requests. No se puede afirmar haber hecho 15 commits equitemporales ni haber colaborado con otros estudiantes sin evidencia histórica.

Tabla editable para acumular al menos 15 versiones **reales**:

| Versión | Commit SHA | # pruebas | # fallos | Densidad | Confiabilidad | # casos/# funcionalidades |
|---:|---|---:|---:|---:|---:|---:|
| 1–15 | Pendiente de historial auténtico | — | — | — | — | — |

Fórmulas del PDF: densidad = fallos/pruebas; confiabilidad = 1 − densidad; completitud = casos de prueba / funcionalidades. En una versión con cero pruebas, la densidad queda indefinida; no dividir entre cero.
