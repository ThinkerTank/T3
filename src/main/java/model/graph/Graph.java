package model.graph;

import java.util.Arrays;
import model.structures.*;
import java.util.Comparator;

/** Undirected graph, custom linked-list adjacency and dense matrix for Floyd-Warshall. */
public class Graph<T> implements IGraph<T>, java.io.Serializable {
    public record Edge<T>(T from,T to,double weight) implements java.io.Serializable {}
    public record ShortestPaths<T>(Object[] vertices,double[] distances,int[] parents) {
        public double distanceTo(T v){for(int i=0;i<vertices.length;i++)if(vertices[i].equals(v))return distances[i];throw new IllegalArgumentException("Unknown vertex");}
        @SuppressWarnings("unchecked") public LinkedList<T> routeTo(T v){int target=-1;for(int i=0;i<vertices.length;i++)if(vertices[i].equals(v)){target=i;break;}LinkedList<T> path=new LinkedList<>();if(target<0||Double.isInfinite(distances[target]))return path;Stack<T> reverse=new Stack<>();for(int i=target;i>=0;i=parents[i])reverse.push((T)vertices[i]);while(!reverse.isEmpty())path.addLast(reverse.pop());return path;}
    }
    public record SpanningTree<T>(LinkedList<Edge<T>> edges,double cost) {}
    private final LinkedList<T> vertices=new LinkedList<>();
    private final LinkedList<LinkedList<Edge<T>>> adj=new LinkedList<>();
    private double[][] matrix=new double[0][0];
    public int size(){return vertices.size();}
    public T vertex(int i){return vertices.get(i);}
    private int index(T v){int i=0;for(T x:vertices){if(x.equals(v))return i;i++;}throw new IllegalArgumentException("Unknown vertex: "+v);}
    /** O(V^2) time/space due to dense-matrix expansion; O(1) average additional list work. */
    public void addVertex(T v){for(T x:vertices)if(x.equals(v))return;int n=size();vertices.addLast(v);adj.addLast(new LinkedList<>());double[][] next=new double[n+1][n+1];for(int i=0;i<=n;i++)for(int j=0;j<=n;j++)next[i][j]=i==j?0:Double.POSITIVE_INFINITY;for(int i=0;i<n;i++)System.arraycopy(matrix[i],0,next[i],0,n);matrix=next;}
    /** O(V+degree(a)+degree(b)) time, O(1) additional space. */
    public void addEdge(T a,T b,double weight){if(!Double.isFinite(weight)||weight<=0)throw new IllegalArgumentException("Positive finite weight required");int i=index(a),j=index(b);if(i==j)throw new IllegalArgumentException("No loops");removeEdge(a,b);adj.get(i).addLast(new Edge<>(a,b,weight));adj.get(j).addLast(new Edge<>(b,a,weight));matrix[i][j]=matrix[j][i]=weight;}
    public void removeEdge(T a,T b){int i=index(a),j=index(b);adj.get(i).removeIf(e->e.to().equals(b));adj.get(j).removeIf(e->e.to().equals(a));matrix[i][j]=matrix[j][i]=Double.POSITIVE_INFINITY;}
    public double getWeight(T a,T b){return matrix[index(a)][index(b)];}
    public LinkedList<T> getAdjacent(T v){LinkedList<T> out=new LinkedList<>();for(Edge<T> e:adj.get(index(v)))out.addLast(e.to());return out;}
    /** O(V+E) traversal, plus O(V^2+VE) vertex lookup for generic T; O(V) space. */
    public LinkedList<T> bfs(T start){boolean[] seen=new boolean[size()];Queue<Integer> queue=new Queue<>();LinkedList<T> order=new LinkedList<>();int at=index(start);seen[at]=true;queue.enqueue(at);while(!queue.isEmpty()){int u=queue.dequeue();order.addLast(vertex(u));for(Edge<T> e:adj.get(u)){int v=index(e.to());if(!seen[v]){seen[v]=true;queue.enqueue(v);}}}return order;}
    public LinkedList<T> dfs(){LinkedList<T> result=new LinkedList<>();boolean[] visited=new boolean[size()];for(int i=0;i<size();i++)if(!visited[i])dfsVisit(i,visited,result);return result;}
    private void dfsVisit(int u,boolean[] visited,LinkedList<T> out){visited[u]=true;out.addLast(vertex(u));for(Edge<T> e:adj.get(u)){int v=index(e.to());if(!visited[v])dfsVisit(v,visited,out);}}
    public int components(){boolean[] visited=new boolean[size()];int count=0;for(int i=0;i<size();i++)if(!visited[i]){count++;dfsVisit(i,visited,new LinkedList<>());}return count;}
    public boolean hasCycle(){boolean[] seen=new boolean[size()];for(int i=0;i<size();i++)if(!seen[i]&&cycle(i,-1,seen))return true;return false;}
    private boolean cycle(int u,int parent,boolean[] seen){seen[u]=true;for(Edge<T> e:adj.get(u)){int v=index(e.to());if(!seen[v]){if(cycle(v,u,seen))return true;}else if(v!=parent)return true;}return false;}
    private record Candidate(int index,double distance) {}
    /** Binary min-heap via inverted comparator. O((V+E)log(V+E)) heap operations, plus generic vertex lookup. */
    public ShortestPaths<T> dijkstra(T source){int n=size(),origin=index(source);double[] dist=new double[n];int[] parent=new int[n];Arrays.fill(dist,Double.POSITIVE_INFINITY);Arrays.fill(parent,-1);dist[origin]=0;
        PriorityQueue<Candidate> pq=new PriorityQueue<>(Comparator.comparingDouble((Candidate c)->c.distance).reversed());pq.insert(new Candidate(origin,0));
        while(!pq.isEmpty()){Candidate c=pq.extractMax();if(c.distance>dist[c.index])continue;for(Edge<T> e:adj.get(c.index)){int v=index(e.to());double nd=dist[c.index]+e.weight();if(nd<dist[v]){dist[v]=nd;parent[v]=c.index;pq.insert(new Candidate(v,nd));}}}
        Object[] vv=new Object[n];for(int i=0;i<n;i++)vv[i]=vertex(i);return new ShortestPaths<>(vv,dist,parent);}
    /** O(V^3) time, O(V^2) auxiliary memory. */
    public double[][] floydWarshall(){int n=size();double[][] result=new double[n][n];for(int i=0;i<n;i++)result[i]=matrix[i].clone();for(int k=0;k<n;k++)for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(result[i][k]+result[k][j]<result[i][j])result[i][j]=result[i][k]+result[k][j];return result;}
    /** O(E log V) heap operations excluding generic indexing; O(V+E) memory. */
    public SpanningTree<T> primMST(T start){int n=size();boolean[] seen=new boolean[n];LinkedList<Edge<T>> result=new LinkedList<>();PriorityQueue<Edge<T>> pq=new PriorityQueue<>(Comparator.comparingDouble((Edge<T> e)->e.weight()).reversed());int root=index(start);seen[root]=true;for(Edge<T> e:adj.get(root))pq.insert(e);double sum=0;while(!pq.isEmpty()){Edge<T> e=pq.extractMax();int v=index(e.to());if(seen[v])continue;seen[v]=true;result.addLast(e);sum+=e.weight();for(Edge<T> other:adj.get(v))if(!seen[index(other.to())])pq.insert(other);}return new SpanningTree<>(result,sum);}
    /** O(E log E) sorting/heap operations excluding generic indexing; O(V+E) memory. */
    public SpanningTree<T> kruskalMST(){PriorityQueue<Edge<T>> pq=new PriorityQueue<>(Comparator.comparingDouble((Edge<T> e)->e.weight()).reversed());for(int i=0;i<size();i++)for(Edge<T> e:adj.get(i))if(i<index(e.to()))pq.insert(e);UnionFind union=new UnionFind(size());LinkedList<Edge<T>> result=new LinkedList<>();double sum=0;while(!pq.isEmpty()){Edge<T> e=pq.extractMax();if(union.union(index(e.from()),index(e.to()))){result.addLast(e);sum+=e.weight();}}return new SpanningTree<>(result,sum);}
    public LinkedList<Edge<T>> edges(){LinkedList<Edge<T>> result=new LinkedList<>();for(int i=0;i<size();i++)for(Edge<T> e:adj.get(i))if(i<index(e.to()))result.addLast(e);return result;}
}
