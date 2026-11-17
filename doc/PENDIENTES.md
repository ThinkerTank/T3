# Estado de la entrega y puntos por cerrar

Se entrega código fuente Java 17, GUI Swing, tres archivos JSON, serialización, estructuras propias, algoritmos y documentación. Compilación `javac --release 17` y 51 aserciones del smoke test ejecutadas satisfactoriamente. Inicio de UI verificado en servidor de pantalla virtual, sin inspección humana completa de cada flujo.

**Pendientes externos a la implementación automatizada:**

- Autorizar escritura para la aplicación GitHub sobre `ThinkerTank/T3`, o subir localmente la carpeta. Intento de creación de `.gitignore` retornó HTTP 403 Resource not accessible by integration.
- Ejecutar pruebas JUnit 5 con Maven e inspeccionar la GUI con el equipo final; Maven no existe en el entorno de construcción actual.
- Archivo original `.vpp` y exportación PDF específica de Visual Paradigm; se entrega la fuente Mermaid, no se finge compatibilidad con el formato propietario.
- Evidencia de revisión con cliente real, nombres y códigos de integrantes, colaboración real y 15 commits de evolución con métricas genuinas.
- Mejorar cota asintótica real de `Graph<T>` mediante tabla hash propia de índices de vértice y arreglo de accesos a listas (ver COMPLEJIDAD.md).
- Reemplazar parser JSON didáctico por una librería JSON validada si el curso permite dependencias externas, y añadir análisis de rutas/cobertura en distintos escenarios de bloqueos.
- Diseñar pruebas de estrés concurrente y pruebas UI automatizadas para una certificación más amplia.

No presentar este proyecto como equivalente a un sistema municipal desplegable, ni atribuirle validaciones con clientes reales que aún no ocurrieron.
