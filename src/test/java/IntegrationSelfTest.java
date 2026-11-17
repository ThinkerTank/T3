import java.nio.file.*;
import java.io.*;
import java.time.*;
import java.util.Comparator;
import model.structures.*;
import model.graph.*;
import model.io.*;
import model.domain.*;
import model.domain.Types.*;
import model.service.*;
/** Dependency-free smoke/regression suite. Run via scripts/test.sh (also complements JUnit). */
public class IntegrationSelfTest {
    private static int assertions;
    private static void check(boolean condition,String msg){assertions++;if(!condition)throw new AssertionError(msg);}
    private static void expect(Class<? extends Throwable> error,Runnable action){boolean threw=false;try{action.run();}catch(Throwable e){if(!error.isInstance(e))throw new AssertionError("Unexpected exception: "+e);threw=true;}check(threw,"Expected "+error.getSimpleName());}
    public static void main(String[] args)throws Exception {
        Stack<Integer> stack=new Stack<>();stack.push(1);stack.push(2);check(stack.pop()==2,"stack LIFO");check(stack.pop()==1,"stack end");expect(EmptyStructureException.class,stack::pop);
        model.structures.Queue<Integer> queue=new model.structures.Queue<>();queue.enqueue(1);queue.enqueue(2);check(queue.front()==1,"queue front");check(queue.dequeue()==1,"queue FIFO");check(queue.dequeue()==2,"queue FIFO 2");expect(EmptyStructureException.class,queue::dequeue);
        HashTable<String,Integer> hash=new HashTable<>(2);hash.put("Aa",1);hash.put("BB",2);check(hash.get("Aa")==1&&hash.get("BB")==2,"hash collision");hash.put("Aa",5);check(hash.get("Aa")==5,"hash replacement");check(hash.remove("BB")==2,"hash removal");check(!hash.containsKey("BB"),"hash absence");
        model.structures.PriorityQueue<Integer> heap=new model.structures.PriorityQueue<>(Comparator.naturalOrder());heap.insert(4);heap.insert(9);heap.insert(2);check(heap.peekMax()==9,"priority peak");check(heap.extractMax()==9,"priority extract");check(heap.extractMax()==4,"priority second");
        BinarySearchTree<Integer> bst=new BinarySearchTree<>(Comparator.naturalOrder());bst.insert(4);bst.insert(2);bst.insert(7);bst.insert(6);check(bst.maximum()==7,"bst max");check(bst.remove(4)&&bst.size()==3,"bst removal with successor");check(bst.descending().first()==7,"bst sorted");
        UnionFind uf=new UnionFind(4);check(uf.union(0,1),"uf merge");check(!uf.union(1,0),"uf no duplicate");
        Graph<Integer> g=new Graph<>();for(int i=0;i<5;i++)g.addVertex(i);g.addEdge(0,1,1);g.addEdge(1,2,2);g.addEdge(0,2,7);g.addEdge(2,3,3);
        check(g.components()==2,"DFS components");check(g.hasCycle(),"DFS cycle");check(g.bfs(0).size()==4,"BFS reachable");check(g.dfs().size()==5,"DFS all vertices");check(g.dijkstra(0).distanceTo(3)==6,"Dijkstra");check(Double.isInfinite(g.dijkstra(0).distanceTo(4)),"unreachable Dijkstra");check(g.floydWarshall()[0][3]==6,"Floyd");check(g.primMST(0).cost()==6,"Prim cost");check(g.kruskalMST().cost()==6,"Kruskal cost");g.removeEdge(0,2);check(!g.hasCycle(),"cycle removed");
        Simulation sim=ConfigurationLoader.load(Path.of("src/main/resources"));check(sim.graph().size()==64,"JSON map");check(sim.availableCount()==6,"JSON vehicles");expect(DomainException.class,()->sim.vehicle("NOT_FOUND"));int initial=sim.position();sim.move(1,0);check(sim.position()==initial+1,"movement");sim.undo();check(sim.position()==initial,"undo");expect(EmptyStructureException.class,sim::undo);
        Incident a=sim.register(IncidentType.FIRE,Severity.HIGH,63,"fire");Incident b=sim.register(IncidentType.ROBBERY,Severity.HIGH,0,"robbery");check(sim.highest().id.equals(a.id),"equal priority older first");check(sim.nextArrival().id.equals(a.id),"FIFO oldest");expect(DomainException.class,()->sim.assign(a.id,"PAT-1"));check(sim.score()==0,"invalid assignment awards no points");Vehicle suggestion=sim.recommend(a.id);check(suggestion!=null&&suggestion.type==VehicleType.FIRE_TRUCK,"recommend compatible");sim.assign(a.id,suggestion.id);check(sim.nextArrival().id.equals(b.id),"dispatch dequeues assigned");expect(DomainException.class,()->sim.assign(a.id,suggestion.id));int points=sim.resolve(a.id);check(points>=100,"scoring");check(sim.activeCount()==1&&sim.highest().id.equals(b.id),"hash/BST/heap consistency");check(sim.vehicle(suggestion.id).available(),"vehicle released");
        Path tmp=Files.createTempFile("sgmms-test-",".ser");StateStore.save(sim,tmp);Simulation restored=StateStore.load(tmp);check(restored.activeCount()==1,"serialization incidents");check(restored.availableCount()==6,"serialization vehicles");check(restored.score()==sim.score(),"serialization score");Files.deleteIfExists(tmp);
        sim.blockRoad(0,1);check(Double.isInfinite(sim.graph().getWeight(0,1)),"blocked road");sim.unblockRoad(0,1);check(sim.graph().getWeight(0,1)==1,"road restored");sim.tick();check(sim.components()==1,"component stable");
        System.out.println("PASS: "+assertions+" assertions across ADTs, graph algorithms, service, JSON, persistence.");
    }
}
