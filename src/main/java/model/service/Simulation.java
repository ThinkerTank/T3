package model.service;

import java.io.*;
import java.time.*;
import java.util.Random;
import java.util.Comparator;
import model.structures.*;
import model.domain.*;
import model.domain.Types.*;
import model.graph.Graph;

/** Thread-safe simulation model. All mutations and snapshots share this monitor. */
public class Simulation implements Serializable {
    private static final long serialVersionUID=1L;
    private static final Comparator<Incident> INCIDENT_ORDER=(Comparator<Incident> & Serializable)(a,b)->{
        int bySeverity=Integer.compare(a.severity.ordinal(),b.severity.ordinal());if(bySeverity!=0)return bySeverity;
        int byAge=b.createdAt.compareTo(a.createdAt);return byAge!=0?byAge:b.id.compareTo(a.id);
    };
    private final HashTable<String,Vehicle> vehicles=new HashTable<>();
    private final HashTable<String,Incident> incidents=new HashTable<>();
    private final BinarySearchTree<Incident> severityTree=new BinarySearchTree<>(INCIDENT_ORDER);
    private final PriorityQueue<Incident> severityHeap=new PriorityQueue<>(INCIDENT_ORDER);
    private final Queue<Incident> dispatch=new Queue<>();
    private final Queue<SimulationEvent> events=new Queue<>();
    private final Stack<Movement> movements=new Stack<>();
    private final Stack<OperatorAction> actions=new Stack<>();
    private final Graph<Integer> roads=new Graph<>();
    private final LinkedList<Graph.Edge<Integer>> blocked=new LinkedList<>();
    private final LinkedList<Graph.Edge<Integer>> baseRoads=new LinkedList<>();
    private final int width,height;private final String[] cells;
    private int operatorPosition,score,sequence,ticks;
    private transient Random random=new Random();
    public Simulation(int width,int height,String[] cells,int startingPosition){
        if(width<=0||height<=0||cells.length!=width*height)throw new InvalidMapException("Invalid grid dimensions");
        this.width=width;this.height=height;this.cells=cells.clone();this.operatorPosition=startingPosition;
        for(int i=0;i<cells.length;i++)if(passable(i))roads.addVertex(i);
        if(!passable(startingPosition))throw new InvalidMapException("Monitoring center is blocked");
    }
    public synchronized Graph<Integer> graph(){return roads;}
    public synchronized void connect(int a,int b,double base){if(!passable(a)||!passable(b))throw new InvalidMapException("Road connects blocked cell");roads.addEdge(a,b,base);baseRoads.addLast(new Graph.Edge<>(a,b,base));}
    public synchronized void verifyMap(){LinkedList<Integer> reached=roads.bfs(operatorPosition);if(reached.size()!=roads.size())throw new InvalidMapException("Disconnected cells: "+(roads.size()-reached.size()));}
    public synchronized void addVehicle(Vehicle v){if(!passable(v.location))throw new DomainException("Vehicle outside road");if(vehicles.containsKey(v.id))throw new DomainException("Duplicate vehicle");vehicles.put(v.id,v);}
    public synchronized Vehicle vehicle(String id){Vehicle v=vehicles.get(id);if(v==null)throw new DomainException("VehicleNotFoundException: "+id);return v;}
    public synchronized Incident incident(String id){Incident x=incidents.get(id);if(x==null)throw new DomainException("IncidentNotFoundException: "+id);return x;}
    public synchronized Incident register(IncidentType type,Severity severity,int position,String description){if(!passable(position))throw new DomainException("Invalid incident location");
        Incident i=new Incident("INC-"+(++sequence),type,severity,position,description,Instant.now());incidents.put(i.id,i);severityTree.insert(i);severityHeap.insert(i);dispatch.enqueue(i);event("Incident created: "+i.id);return i;}
    /** O(1) heap maximum; best/average/worst O(1) time/space. */
    public synchronized Incident highest(){while(!severityHeap.isEmpty()&&!severityHeap.peekMax().isActive())severityHeap.extractMax();return severityHeap.isEmpty()?null:severityHeap.peekMax();}
    public synchronized Incident nextArrival(){return dispatch.isEmpty()?null:dispatch.front();}
    public synchronized LinkedList<Incident> activeIncidents(){return severityTree.descending();}
    public synchronized Iterable<Vehicle> allVehicles(){return vehicles.values();}
    public synchronized int activeCount(){return severityTree.size();}
    public synchronized int countType(IncidentType type){int c=0;for(Incident x:severityTree.descending())if(x.type==type)c++;return c;}
    public synchronized int availableCount(){int c=0;for(Vehicle v:vehicles.values())if(v.available())c++;return c;}
    public synchronized int score(){return score;}
    public synchronized int position(){return operatorPosition;}
    public synchronized int width(){return width;}
    public synchronized int height(){return height;}
    public synchronized String cell(int i){return cells[i];}
    public synchronized boolean passable(int i){return i>=0&&i<cells.length&&!cells[i].equals("#");}
    public synchronized void move(int dx,int dy){int x=operatorPosition%width+dx,y=operatorPosition/width+dy;if(x<0||x>=width||y<0||y>=height||!passable(y*width+x))throw new DomainException("Movement blocked by map boundary or obstacle");
        int next=y*width+x;movements.push(new Movement(operatorPosition,next));operatorPosition=next;event("Operator moved to "+next);}
    public synchronized void undo(){Movement m=movements.pop();operatorPosition=m.from();event("Operator undo to "+operatorPosition);}
    public synchronized OperatorAction lastAction(){return actions.isEmpty()?null:actions.peek();}
    public synchronized SimulationEvent pollEvent(){return events.isEmpty()?null:events.dequeue();}
    private void event(String s){events.enqueue(new SimulationEvent(s,Instant.now()));if(events.size()>200)events.dequeue();}
    public synchronized double[][] distances(){return roads.floydWarshall();}
    private synchronized double distance(int from,int to){return roads.dijkstra(from).distanceTo(to);}
    public synchronized Vehicle recommend(String incidentId){Incident x=incident(incidentId);Vehicle best=null;double bestDist=Double.POSITIVE_INFINITY;
        PriorityQueue<VehicleChoice> choices=new PriorityQueue<>(Comparator.comparingDouble((VehicleChoice c)->c.distance).reversed());
        for(Vehicle v:vehicles.values())if(v.available()&&v.compatible(x.type)){double d=distance(v.location,x.location);if(Double.isFinite(d))choices.insert(new VehicleChoice(v,d));}
        if(!choices.isEmpty()){VehicleChoice c=choices.extractMax();best=c.vehicle;bestDist=c.distance;}return best;
    }
    private record VehicleChoice(Vehicle vehicle,double distance) {}
    public synchronized void assign(String incidentId,String vehicleId){Incident x=incident(incidentId);Vehicle v=vehicle(vehicleId);
        if(x.status!=IncidentStatus.PENDING)throw new DomainException("Incident not pending");if(!v.available())throw new DomainException("Vehicle unavailable");if(!v.compatible(x.type))throw new DomainException("Incompatible vehicle");
        double cost=distance(v.location,x.location);if(!Double.isFinite(cost))throw new DomainException("No connected route");
        Vehicle candidate=recommend(x.id);x.optimalAssignment=candidate!=null&&Math.abs(cost-distance(candidate.location,x.location))<1e-9;
        x.estimatedCost=cost;x.vehicleId=v.id;x.status=IncidentStatus.IN_PROGRESS;v.status=VehicleStatus.EN_ROUTE;v.incidentId=x.id;
        dispatch.removeIf(i->i.id.equals(x.id));actions.push(new OperatorAction("Assigned "+v.id+" to "+x.id,Instant.now()));event("Vehicle dispatched: "+v.id);}
    public synchronized LinkedList<Integer> route(String incidentId){Incident x=incident(incidentId);if(x.vehicleId==null)return new LinkedList<>();Vehicle v=vehicle(x.vehicleId);return roads.dijkstra(v.location).routeTo(x.location);}
    public synchronized int resolve(String incidentId){Incident x=incident(incidentId);if(x.status!=IncidentStatus.IN_PROGRESS)throw new DomainException("Incident not in progress");Vehicle v=vehicle(x.vehicleId);
        x.resolvedAt=Instant.now();long seconds=Math.max(0,Duration.between(x.createdAt,x.resolvedAt).getSeconds());int points=x.severity.points+(seconds<=x.severity.deadlineSeconds?20:0)+(x.optimalAssignment?10:-10);
        score+=points;x.status=IncidentStatus.RESOLVED;v.location=x.location;v.status=VehicleStatus.AVAILABLE;v.incidentId=null;
        incidents.remove(x.id);severityTree.remove(x);severityHeap.remove(x);actions.push(new OperatorAction("Resolved "+x.id+" (+"+points+")",Instant.now()));event("Incident resolved: "+x.id);return points;}
    public synchronized void blockRoad(int a,int b){double w=roads.getWeight(a,b);if(!Double.isFinite(w))throw new DomainException("Road already blocked or missing");roads.removeEdge(a,b);blocked.addLast(new Graph.Edge<>(a,b,w));event("Road blocked: "+a+"-"+b);}
    public synchronized void unblockRoad(int a,int b){for(Graph.Edge<Integer> e:blocked)if((e.from()==a&&e.to()==b)||(e.from()==b&&e.to()==a)){roads.addEdge(a,b,e.weight());blocked.removeIf(x->x==e);event("Road opened");return;}throw new DomainException("Road not blocked");}
    public synchronized int components(){return roads.components();}
    public synchronized boolean hasCycle(){return roads.hasCycle();}
    public synchronized Graph.SpanningTree<Integer> corridors(){return roads.kruskalMST();}
    public synchronized void tick(){ticks++;if(random==null)random=new Random();
        advanceVehicles();updateCongestion();
        // Three justified accident triggers: congestion, vehicle collision, blocked-approach pressure.
        int[] traffic=new int[cells.length];for(Vehicle v:vehicles.values())traffic[v.location]++;
        for(int at=0;at<traffic.length;at++)if(passable(at)&&traffic[at]>=3&&ticks%3==0){register(IncidentType.ACCIDENT,Severity.HIGH,at,"Critical congestion: 3+ vehicles in one road cell");break;}
        if(ticks%5==0){outer:for(Vehicle a:vehicles.values())for(Vehicle b:vehicles.values())if(!a.id.equals(b.id)&&a.location==b.location&&a.status==VehicleStatus.EN_ROUTE&&b.status==VehicleStatus.EN_ROUTE){register(IncidentType.ACCIDENT,Severity.MEDIUM,a.location,"Two moving emergency vehicles converged");break outer;}}
        if(ticks%7==0&&!blocked.isEmpty()){Graph.Edge<Integer> e=blocked.first();register(IncidentType.ACCIDENT,Severity.LOW,e.from(),"Vehicle pressure near a blocked road");}
        if(ticks%4==0){int p=randomPassable();register(random.nextBoolean()?IncidentType.ROBBERY:IncidentType.FIRE,Severity.values()[random.nextInt(3)],p,"Simulated city incident");}
    }
    /** Move dispatched units one traversable edge each simulation cycle. */
    private void advanceVehicles(){
        for(Vehicle v:vehicles.values())if(v.status==VehicleStatus.EN_ROUTE&&v.incidentId!=null){
            Incident x=incidents.get(v.incidentId);if(x==null)continue;
            LinkedList<Integer> route=roads.dijkstra(v.location).routeTo(x.location);
            if(route.size()>1){int next=route.get(1);v.location=next;event("Vehicle moved: "+v.id+" -> "+next);}
            if(v.location==x.location){v.status=VehicleStatus.ATTENDING;event("Vehicle attending: "+v.id);}
        }
    }
    /** Refresh live congestion-weighted costs. Closed edges remain absent. */
    private void updateCongestion(){
        int[] traffic=new int[cells.length];for(Vehicle v:vehicles.values())traffic[v.location]++;
        for(Graph.Edge<Integer> e:baseRoads){boolean closed=false;for(Graph.Edge<Integer> b:blocked)if((b.from().equals(e.from())&&b.to().equals(e.to()))||(b.from().equals(e.to())&&b.to().equals(e.from()))){closed=true;break;}
            if(!closed){double factor=1.0+0.25*(traffic[e.from()]+traffic[e.to()]);roads.addEdge(e.from(),e.to(),e.weight()*factor);}
        }
    }
    private int randomPassable(){int p;do{p=random.nextInt(cells.length);}while(!passable(p));return p;}
    @Serial private void readObject(ObjectInputStream stream)throws IOException,ClassNotFoundException{stream.defaultReadObject();random=new Random();}
}
