package model.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import model.io.*;
import model.domain.*;
import model.domain.Types.*;
import model.structures.EmptyStructureException;
public class SimulationTest {
    private Simulation simulation()throws Exception{return ConfigurationLoader.load(Path.of("src/main/resources"));}
    @Test void mapConnected()throws Exception{Simulation s=simulation();assertEquals(64,s.graph().bfs(s.position()).size());}
    @Test void moveUndo()throws Exception{Simulation s=simulation();int origin=s.position();s.move(0,1);s.undo();assertEquals(origin,s.position());assertThrows(EmptyStructureException.class,s::undo);}
    @Test void dispatchPriorityAndArrival()throws Exception{Simulation s=simulation();Incident a=s.register(IncidentType.FIRE,Severity.LOW,3,"first");Incident b=s.register(IncidentType.ROBBERY,Severity.HIGH,10,"second");assertEquals(a.id,s.nextArrival().id);assertEquals(b.id,s.highest().id);}
    @Test void invalidAssignment()throws Exception{Simulation s=simulation();Incident a=s.register(IncidentType.FIRE,Severity.HIGH,3,"fire");assertThrows(DomainException.class,()->s.assign(a.id,"PAT-1"));}
    @Test void candidateAndResolve()throws Exception{Simulation s=simulation();Incident a=s.register(IncidentType.FIRE,Severity.HIGH,3,"fire");Vehicle v=s.recommend(a.id);assertNotNull(v);s.assign(a.id,v.id);assertTrue(s.resolve(a.id)>=100);assertTrue(s.vehicle(v.id).available());assertEquals(0,s.activeCount());}
    @Test void saveRestore()throws Exception{Simulation s=simulation();s.register(IncidentType.FIRE,Severity.HIGH,3,"fire");Path f=Files.createTempFile("sgmms", ".ser");try{StateStore.save(s,f);assertEquals(1,StateStore.load(f).activeCount());}finally{Files.deleteIfExists(f);}}
    @Test void removeRoad()throws Exception{Simulation s=simulation();s.blockRoad(0,1);assertTrue(Double.isInfinite(s.graph().getWeight(0,1)));s.unblockRoad(0,1);assertEquals(1,s.graph().getWeight(0,1));}
}
