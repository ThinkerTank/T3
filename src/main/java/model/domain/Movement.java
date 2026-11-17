package model.domain;
import java.io.Serializable;
public record Movement(int from,int to) implements Serializable {}
