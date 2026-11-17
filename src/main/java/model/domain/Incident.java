package model.domain;
import java.io.Serializable;
import java.time.Instant;
import model.domain.Types.*;
public class Incident implements Serializable {
    public final String id,description;public final IncidentType type;public final Severity severity;public final int location;
    public final Instant createdAt;public IncidentStatus status=IncidentStatus.PENDING;
    public String vehicleId;public Instant resolvedAt;public double estimatedCost;public boolean optimalAssignment;
    public Incident(String id,IncidentType type,Severity severity,int location,String description,Instant createdAt){this.id=id;this.type=type;this.severity=severity;this.location=location;this.description=description;this.createdAt=createdAt;}
    /** Best/average/worst O(1) time and space. */
    public boolean isActive(){return status!=IncidentStatus.RESOLVED;}
    /** Best/average/worst O(1) time and space. */
    public String toString(){return id+" | "+type+" | "+severity+" | "+status+" | zone "+location;}
    @Override public boolean equals(Object o){return o instanceof Incident x&&id.equals(x.id);}
    @Override public int hashCode(){return id.hashCode();}
}
