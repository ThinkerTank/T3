package model.domain;
public final class Types {
    private Types(){}
    public enum IncidentType { ACCIDENT, ROBBERY, FIRE }
    public enum Severity { LOW(40,180), MEDIUM(70,120), HIGH(100,60); public final int points,deadlineSeconds;Severity(int p,int d){points=p;deadlineSeconds=d;} }
    public enum IncidentStatus { PENDING, IN_PROGRESS, RESOLVED }
    public enum VehicleType { PATROL, AMBULANCE, FIRE_TRUCK }
    public enum VehicleStatus { AVAILABLE, EN_ROUTE, ATTENDING, OUT_OF_SERVICE }
}
