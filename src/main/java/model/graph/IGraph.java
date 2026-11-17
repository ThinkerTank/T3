package model.graph;
import model.structures.LinkedList;
public interface IGraph<T> {
    void addVertex(T v);
    void addEdge(T a,T b,double weight);
    void removeEdge(T a,T b);
    LinkedList<T> getAdjacent(T v);
    double getWeight(T a,T b);
    LinkedList<T> bfs(T start);
    LinkedList<T> dfs();
    Graph.ShortestPaths<T> dijkstra(T source);
    double[][] floydWarshall();
    Graph.SpanningTree<T> primMST(T start);
    Graph.SpanningTree<T> kruskalMST();
}
