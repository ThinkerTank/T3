package model.structures;
import java.io.Serializable;
/** Linked-list-backed LIFO ADT. */
public class Stack<T> implements Serializable {
    private final LinkedList<T> values=new LinkedList<>();
    /** Best/average/worst O(1) time and space. */
    public void push(T item){values.addFirst(item);}
    /** Best/average/worst O(1) time; O(1) auxiliary space. */
    public T pop(){return values.removeFirst();}
    public T peek(){return values.first();}
    public boolean isEmpty(){return values.isEmpty();}
    public int size(){return values.size();}
}
