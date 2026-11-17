# Ingeniería de Software II — Requerimientos, historias y casos de prueba

## Actores y alcance

**Actor principal:** operador municipal. **Fuentes externas:** configuración inicial JSON, reloj de simulación. El prototipo no usa datos personales de residentes y no toma decisiones reales de emergencia.

## Historias de usuario

| Historia | Como operador quiero... | Criterio de aceptación |
|---|---|---|
| HU-01 | visualizar resumen y mapa | centro, mapa y panel muestran datos actuales |
| HU-02 | desplazarme y deshacer | no se atraviesan límites/obstáculos; U restaura la posición previa |
| HU-03 | registrar incidentes | cada uno recibe ID, gravedad, tipo, ubicación y fecha |
| HU-04 | priorizar casos | heap/BST usan gravedad y antigüedad; FIFO conserva llegada |
| HU-05 | asignar una unidad | se rechazan vehículos incompatibles/no disponibles y se sugiere el más cercano |
| HU-06 | cerrar atención | cambia estado, libera vehículo y acumula puntuación correcta |
| HU-07 | consultar vías | Dijkstra encuentra ruta mínima; BFS/DFS informan cobertura y conectividad |
| HU-08 | visualizar corredores | se dibuja un MST del mapa con Prim/Kruskal como alternativas |
| HU-09 | guardar y volver | serialización restaura puntaje, incidentes, vehículos y mapa |
| HU-10 | conocer errores | validaciones expresan causa sin cerrar la aplicación |

## Matriz de trazabilidad del enunciado (RF 1 a 35)

| RF | Implementación principal | Verificación |
|---|---|---|
| 1–2 | ui.Main vistas Swing y dibujo en cuadrícula | inspección manual de interfaz pendiente |
| 3–4 | Simulation.register y tick | IntegrationSelfTest: alta; reglas en README |
| 5 | BinarySearchTree + PriorityQueue + comparator | IntegrationSelfTest prioridad/empate |
| 6–8 | Vehicle + Simulation.assign/resolve | tests de asignación y liberación |
| 9–12 | Graph + rutas y MST opcional | 8 pruebas JUnit de grafo y 51 aserciones integradas |
| 13–14 | refresh, score | assert de puntuación |
| 15–16 | ConfigurationLoader, StateStore | carga, guardado y recuperación |
| 17–18 | Stack OperatorAction, Queue SimulationEvent | última acción y event log en UI |
| 19–20 | DomainException y sincronización | verificación parcial (no prueba de estrés) |
| 21–22 | Stack Movement / OperatorAction | prueba de undo, última acción en UI |
| 23–24 | Queue dispatch/event | orden FIFO + refresco UI |
| 25–26 | HashTable de Vehicle e Incident | colisiones y consistencia al resolver |
| 27–28 | PriorityQueue de incidentes/vehículos candidatos | empate, sugerencia por distancia |
| 29–30 | Graph dual y BFS en ConfigurationLoader | rutas, validación de mapa |
| 31 | Graph DFS, componentes y ciclos | test de grafo desconectado y ciclos |
| 32–33 | Dijkstra y Floyd-Warshall | distancias conocidas y regresión |
| 34 | Prim y Kruskal, capa opcional del mapa | comparación de costo en pruebas |
| 35 | Simulation.assign + resolve | bonificación/penalización por distancia |

## Casos de caja negra

| ID | Entrada | Salida esperada |
|---|---|---|
| CP01 | registrar incendio alta | incidente activo, ID nuevo, heap y BST actualizados |
| CP02 | asignar patrulla al incendio | excepción clara, sin puntuación |
| CP03 | asignar bomberos disponible | incidente en proceso, recurso en ruta |
| CP04 | asignar mismo vehículo a otro incidente | rechazo por recurso no disponible |
| CP05 | resolver incidente | puntaje y unidad libre, eliminado de hash/BST/heap |
| CP06 | deshacer sin movimientos | EmptyStructureException; UI sigue abierta |
| CP07 | cargar mapa con vértices aislados | InvalidMapException |
| CP08 | cortar vía y consultar | peso infinito y rutas recálculadas |
| CP09 | persistir y recuperar | misma cantidad de incidentes, unidades y puntuación |
| CP10 | severidad igual, fechas distintas | primero el más antiguo |
| CP11 | buscar ID desconocido | DomainException con información del ID |
| CP12 | comparar Prim/Kruskal | mismo costo total para mapa conectado |

## PESTLE (análisis del prototipo)

| Dimensión | Riesgo y mitigación |
|---|---|
| Política | sesgo hacia zonas de alta visibilidad; auditar cobertura por barrios |
| Económica | recursos municipales limitados; transparentar criterios de despacho |
| Social | acceso desigual de la población; evaluar representación de zonas y prioridades |
| Tecnológica | errores por datos ficticios o estado corrupto; validaciones, pruebas y registro de eventos |
| Legal | la operación real exigiría control de acceso, protección de datos y protocolos municipales |
| Ambiental | ruta óptima reduce recorrido simulado; un incendio requiere protocolos no modelados |

**Validación con cliente real:** pendiente; no se inventan entrevistas, aprobaciones ni evidencias.
