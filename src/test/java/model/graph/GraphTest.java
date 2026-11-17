package model.graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class GraphTest {
    private Graph<Integer> graph(){Graph<Integer> g=new Graph<>();for(int i=0;i<5;i++)g.addVertex(i);g.addEdge(0,1,1);g.addEdge(1,2,2);g.addEdge(0,2,9);g.addEdge(2,3,4);return g;}
    @Test void bfsDisconnected(){assertEquals(4,graph().bfs(0).size());}
    @Test void dfsComponents(){assertEquals(2,graph().components());}
    @Test void cycleRemoval(){Graph<Integer> g=graph();assertTrue(g.hasCycle());g.removeEdge(0,2);assertFalse(g.hasCycle());}
    @Test void dijkstraKnownRoute(){Graph<Integer> g=graph();assertEquals(7,g.dijkstra(0).distanceTo(3));}
    @Test void dijkstraUnreachable(){assertEquals(Double.POSITIVE_INFINITY,graph().dijkstra(0).distanceTo(4));}
    @Test void floydMatchesDijkstra(){Graph<Integer> g=graph();for(int i=0;i<5;i++)for(int j=0;j<5;j++)assertEquals(g.dijkstra(i).distanceTo(j),g.floydWarshall()[i][j]);}
    @Test void primAndKruskal(){Graph<Integer> g=graph();assertEquals(7,g.primMST(0).cost());assertEquals(g.primMST(0).cost(),g.kruskalMST().cost());}
    @Test void invalidWeight(){assertThrows(IllegalArgumentException.class,()->graph().addEdge(0,4,-2));}
}
