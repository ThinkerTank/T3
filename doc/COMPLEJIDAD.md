# Análisis de complejidad y elección de estructuras

## Operaciones

| Estructura / operación | Mejor | Promedio | Peor | Espacio adicional | Justificación |
|---|---:|---:|---:|---:|---|
| LinkedList.addFirst/addLast | O(1) | O(1) | O(1) | O(1) | cambia cabeza/cola y crea un nodo |
| LinkedList.removeFirst | O(1) | O(1) | O(1) | O(1) | avanza puntero de cabeza |
| LinkedList.get(i) | O(1) | O(n) | O(n) | O(1) | recorre i enlaces |
| Stack.push/pop/peek | O(1) | O(1) | O(1) | O(1) | delega en cabeza de lista |
| Queue.enqueue/dequeue | O(1) | O(1) | O(1) | O(1) | referencias a cabeza y cola |
| HashTable.put/get/remove | O(1) | O(1) esperado | O(n) | O(1) normal | encadenamiento, con rehash O(n) ocasional |
| Heap.insert/extractMax | O(1) | O(log n) | O(log n) | O(1) amortizado | ascenso/descenso de heap; expandir arreglo O(n) |
| Heap.peekMax | O(1) | O(1) | O(1) | O(1) | lee celda raíz |
| BST.insert/remove/max | O(1) | O(log n) | O(n) | O(h) | árbol sin balanceo, h es altura |
| UnionFind.find/union | O(1) | O(α(n)) | O(α(n)) amortizado | O(1) | compresión y rango |

## Algoritmos de grafos

| Algoritmo | Cota teórica con índice O(1) | Memoria auxiliar | Uso |
|---|---|---|---|
| BFS/DFS | O(V+E) | O(V) | cobertura, componentes, ciclos |
| Dijkstra con minheap | O((V+E) log(V+E)) sin decrease-key | O(V+E) | despacho |
| Floyd–Warshall | O(V³) | O(V²) | todos los pares |
| Prim con heap | O(E log E) sin decrease-key | O(E+V) | corredor MST |
| Kruskal con heap | O(E log E) | O(E+V) | MST con UnionFind |

**Diferencia importante:** la clase genérica usa `LinkedList<T>` propia y `index(v)` hace recorrido lineal de vértices. Además `adj.get(i)` y `vertex(i)` son lineales. Por tanto el costo real de la implementación presente tiene factores lineales adicionales respecto a estas cotas teóricas. La elección privilegia el requisito de estructuras propias; **no se debe afirmar** que esta implementación concreta cumple estrictamente los óptimos teóricos de grafos. Para conseguirlos, agregar un índice propio `HashTable<T,Integer>` y acceso O(1) a listas de adyacencia mediante arreglo de referencias, sin remplazar las listas enlazadas de aristas.

**Representación dual:** lista de adyacencia propia ocupa O(V+E) mientras matriz ocupa O(V²); la matriz permite Floyd y el acceso directo por par, a costa de memoria adicional.

**BST vs Heap:** BST ofrece recorrido ordenado (O(n)) y máximo en O(h), útil para tabla de incidentes; heap ofrece máximo O(1) y extracción O(log n), sin dar recorrido ordenado. Se mantienen ambos con comparador único y sincronización.

**Dijkstra vs Floyd:** el primero se ejecuta bajo demanda desde un vehículo; Floyd precalcula O(V³) pero permite luego consulta por índices O(1). En mapa 8×8 se pueden usar ambos. **Prim vs Kruskal:** Prim crece desde un origen y Kruskal considera aristas en costo creciente; ambos se comparan por el costo de MST.

## Método aplicado a selección de vehículos

Problema: elegir unidad compatible minimizando desplazamiento. Alternativas: primera disponible, Manhattan, Dijkstra con congestión, Floyd precalculado. Criterio: menor costo de tránsito respetando vías cerradas. Diseño: filtrar vehículos de tabla hash, calcular Dijkstra y poner candidatos en heap. Implementación: `Simulation.recommend`. Verificación: candidato compatible y camino finito, prueba funcional; pendientes pruebas extensas de rendimiento.
