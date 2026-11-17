package model.domain;
import java.io.Serializable;
import java.time.Instant;
public record SimulationEvent(String description,Instant at) implements Serializable {}
