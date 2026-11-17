package model.domain;
import java.io.Serializable;
import java.time.Instant;
public record OperatorAction(String description,Instant at) implements Serializable {}
