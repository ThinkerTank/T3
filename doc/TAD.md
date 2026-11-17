# Especificaciones de Tipos Abstractos de Datos

## LinkedList<T>
Valor: secuencia finita ordenada de elementos T. `addFirst(x)` / `addLast(x)`: pre x válido, post x queda en extremo; `removeFirst`: pre tamaño > 0, post retorna primer elemento y tamaño −1; `get(i)`: pre 0≤i<size, post retorna valor en i; `removeIf(p)`: elimina primera coincidencia.

## Stack<T>
Valor: secuencia LIFO. `push(x)`: post tope=x, tamaño +1. `pop()`: pre no vacía, post retorna antiguo tope, tamaño −1. `peek()`: pre no vacía, post retorna tope sin cambiar estructura. `isEmpty()`, `size()`: no mutan.

## Queue<T>
Valor: secuencia FIFO. `enqueue(x)`: post x al final. `dequeue()`: pre no vacía; retorna el más antiguo y reduce tamaño. `front()`: pre no vacía; retorna antiguo primero sin modificar. `isEmpty()` y `size()` no mutan.

## HashTable<K,V>
Valor: función parcial de claves únicas no nulas K hacia V. `put(k,v)`: inserta o sustituye. `get(k)`: retorna valor o null si ausente. `remove(k)`: borra y retorna antiguo valor. `containsKey(k)`: comprueba existencia. `size()`: número de claves. Encadenamiento con LinkedList propia; índice `floorMod(hashCode, capacidad)` y crecimiento a factor de carga > 0.75.

## PriorityQueue<T>
Valor: multiconjunto ordenado bajo comparator. `insert(x)` agrega. `peekMax()`: pre no vacío, retorna máximo sin cambiar heap. `extractMax()`: pre no vacío; retorna/elimina máximo. `isEmpty()`/`size()` observadores. Heap binario de arreglo extensible.

## BinarySearchTree<T>
Valor: conjunto ordenado por comparador; clave de ordenación de incidentes `(gravedad, -instante, -ID)`. `insert`, `remove`, `maximum`, `descending`, `size`. Un incidente se elimina de árbol y hash dentro del mismo método sincronizado del modelo.

## UnionFind
Valor: partición de enteros de 0 a n−1. `find(x)` devuelve representante, pre x en rango. `union(a,b)` fusiona conjuntos y retorna true si eran distintos. Compresión de caminos + unión por rango.

## IGraph<T> / Graph<T>
Valor: grafo simple no dirigido, ponderado en positivos; cada vértice es una celda transitable. `addVertex`, `addEdge(a,b,w)` (pre vértices existentes y w positivo), `removeEdge`, `getAdjacent`, `getWeight`, `bfs`, `dfs`, `dijkstra`, `floydWarshall`, `primMST`, `kruskalMST`. Los caminos deben ser simétricos. BFS/DFS retornan recorrido; Dijkstra retorna distancias y predecesores, Floyd retorna matriz, Prim/Kruskal retornan bosque de expansión y costo.

## Invariantes del modelo

1. `Vehicle.status != AVAILABLE` si está asignado a incidente activo.
2. Incidentes activos se reflejan en el BST y en el índice hash, y se retiran al resolver.
3. Cola de despacho contiene los no asignados en orden de llegada; heap de prioridad da acceso al más grave y más antiguo.
4. Se rechaza cualquier ruta sin camino finito o combinación incompatible.
5. `Simulation` usa métodos `synchronized` para evitar escrituras concurrentes inconsistentes.
