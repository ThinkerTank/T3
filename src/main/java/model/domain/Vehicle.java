package model.domain;
import java.io.Serializable;
import model.domain.Types.*;
public class Vehicle implements Serializable {
    public final String id;public final VehicleType type;public int location;public VehicleStatus status=VehicleStatus.AVAILABLE;public String incidentId;
    public Vehicle(String id,VehicleType type,int location){this.id=id;this.type=type;this.location=location;}
    /** Best/average/worst O(1) time and space. */
    public boolean compatible(IncidentType incident){return switch(incident){case ACCIDENT -> type==VehicleType.AMBULANCE||type==VehicleType.PATROL;case ROBBERY -> type==VehicleType.PATROL;case FIRE -> type==VehicleType.FIRE_TRUCK;};}
    /** Best/average/worst O(1) time and space. */
    public boolean available(){return status==VehicleStatus.AVAILABLE;}
    @Override public String toString(){return id+" "+type+" at "+location+" ("+status+")";}
}
