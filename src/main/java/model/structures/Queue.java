package model.structures;
import java.io.Serializable;
/** Linked-list-backed FIFO ADT. */
public class Queue<T> implements Serializable {
    private final LinkedList<T> values=new LinkedList<>();
    /** Best/average/worst O(1) time and space. */
    public void enqueue(T item){values.addLast(item);}
    /** Best/average/worst O(1) time and O(1) auxiliary space. */
    public T dequeue(){return values.removeFirst();}
    public T front(){return values.first();}
    public boolean isEmpty(){return values.isEmpty();}
    public int size(){return values.size();}
    public boolean removeIf(java.util.function.Predicate<T> condition){return values.removeIf(condition);}
    public Iterable<T> items(){return values;}
}
