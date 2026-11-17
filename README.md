# SGMMS — Sistema de Gestión y Monitoreo de Movilidad y Seguridad

Prototipo académico de monitoreo de incidentes y movilidad de **Palmira, Valle del Cauca**, desarrollado en Java 17, interfaz Swing y patrón MVC. Proyecto para APO II, Computación y Estructuras Discretas I e Ingeniería de Software II (Universidad Icesi, periodo 2026-2).

**Autoría:** estructura inicial de implementación generada con ayuda de ChatGPT para el repositorio T3. **Integrantes del equipo:** completar con nombres y códigos reales antes de entregar. Revisar y comprender el código para la sustentación.

## Ejecución

Requisitos: JDK 17 o superior; para Swing hace falta escritorio gráfico. No requiere base de datos, conexión a internet ni APIs externas.

```bash
bash scripts/test.sh
bash scripts/run.sh
```

En Windows con Maven:

```powershell
mvn test
mvn exec:java
```

También se puede abrir `pom.xml` en IntelliJ IDEA y ejecutar `ui.Main`. El directorio de trabajo debe ser la raíz del proyecto para encontrar los tres archivos JSON.

## Funcionalidades

- Centro de monitoreo: puntuación, incidentes por tipo y gravedad, vehículos disponibles, cobertura de BFS y componentes conexas.
- Mapa 2D: zonas comerciales, residenciales y vía principal, vehículos, incidentes, rutas de despacho, corredores MST y movimiento W/A/S/D o flechas; U deshace el último movimiento.
- Panel: registrar incidentes, orden de prioridad, orden FIFO, asignación compatible, recomendación por menor costo, resolución, historial administrativo.
- Simulación periódica concurrente: un hilo programado avanza vehículos y genera eventos e incidentes. Monitor `synchronized` protege el estado compartido; Swing se actualiza únicamente en el EDT.
- Accidente por tres causas comprobables: (1) congestión crítica de al menos 3 vehículos en una celda cada 3 ciclos, (2) dos unidades en ruta coinciden en una celda cada 5 ciclos, (3) presión cerca de una vía bloqueada cada 7 ciclos. Robos e incendios se generan aleatoriamente cada 4 ciclos.
- Cola FIFO de despacho; pila de movimientos y acciones; tablas hash de vehículos e incidentes; heap y BST de prioridad con desempate por antigüedad; grafo dual con listas de adyacencia propias y matriz.
- BFS, DFS, Dijkstra, Floyd-Warshall, Prim, Kruskal y Union-Find propio.
- Configuración desde JSON y estado guardado por serialización Java en `state/simulation.ser`.

## Reglas de decisión

Severidad ALTA: 100 puntos, MEDIA: 70, BAJA: 40. Se añaden 20 puntos por resolución antes de 60 / 120 / 180 segundos respectivamente; y 10 puntos si se asignó el vehículo de menor distancia Dijkstra, o se descuentan 10 en caso contrario. Los tiempos de atención son **valores de simulación**, no objetivos reales de emergencia. Los costos de ruta se calculan como distancia base × (1 + 0.25 × vehículos en los extremos del tramo); se actualizan por ciclo. No se otorgan puntos al rechazar una asignación.

Compatibilidad: patrulla → robo/accidente, ambulancia → accidente y bomberos → incendio. Los vehículos no disponibles y los incidentes asignados/resueltos no admiten nuevas asignaciones. Una unidad EN_ROUTE avanza un tramo por ciclo y al llegar pasa a ATTENDING.

## Estructura

```text
src/main/java/model/structures/  LinkedList, Stack, Queue, HashTable, PriorityQueue, BinarySearchTree, UnionFind
src/main/java/model/domain/      Incident, Vehicle, Movement, OperatorAction, SimulationEvent, Types, exceptions
src/main/java/model/graph/       IGraph, Graph (6 algoritmos de recorrido/rutas/MST)
src/main/java/model/service/     Simulation (dominio sincronizado)
src/main/java/model/io/          ConfigurationLoader, StateStore
src/main/java/ui/                Main (vista/controlador Swing)
src/main/resources/              map.json, routes.json, vehicles.json
src/test/java/                   IntegrationSelfTest + pruebas JUnit 5
scripts/                         run.sh, test.sh
doc/                             especificación, TAD, riesgos, complejidades y UML
```

## Validación en este entorno

`bash scripts/test.sh` compila con `javac --release 17` y ejecuta **51 aserciones verificadas** sin dependencias externas. También se suministran pruebas JUnit 5 para ejecutar con `mvn test` cuando Maven y JUnit estén disponibles. **No se ejecutó Maven/JUnit en este entorno**, porque Maven no está instalado y el acceso de red está deshabilitado. No se debe afirmar cobertura total ni que hayan pasado JUnit sin ejecutar esa herramienta.

## Indicadores por iteración y Gitflow

El enunciado exige **15 commits reales** distribuidos en el tiempo, con métricas y hashes. No se han fabricado commits anteriores ni métricas artificiales: al iniciar el Gitflow real deben registrar en cada versión (a) fallos / pruebas, (b) 1 − densidad de fallos, y (c) casos de prueba / funcionalidades implementadas. Usar `feature/...` → `dev` → PR hacia `main`; documentar los 15 hashes en esta sección cuando existan. La configuración de permisos de GitHub debe permitir escritura antes de realizarlo.

## Límites pendientes de validación

- La ciudad es un mapa didáctico 8×8, no una reproducción cartográfica de Palmira.
- Los accidentes se basan en tres reglas de simulación, pero su frecuencia está simplificada y no está calibrada con datos reales.
- El algoritmo genérico `Graph<T>` ofrece dos representaciones; el costo efectivo de acceso a vértices en la implementación didáctica crece linealmente debido al uso de `LinkedList<T>` propia. Ver `doc/COMPLEJIDAD.md` para esta distinción respecto de las cotas teóricas.
- La visualización de UI y los tests JUnit deben verificarse en el equipo de entrega. Falta el archivo fuente propietario de Visual Paradigm `.vpp` que no puede generarse fielmente sin ese programa. La fuente del diagrama se incluye en Mermaid.
- Deben completarse los nombres del equipo, validación con cliente real, artefactos firmados y métricas de evolución histórica, que no se pueden inventar.
