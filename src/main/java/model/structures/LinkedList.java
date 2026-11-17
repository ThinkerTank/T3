package model.structures;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/** Custom singly-linked generic list; no Java collection backing. */
public class LinkedList<T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private static class Node<T> implements Serializable { T value; Node<T> next; Node(T v) { value=v; } }
    private Node<T> head, tail;
    private int size;
    /** Best/average/worst O(1) time and O(1) auxiliary space. */
    public void addLast(T value) { Node<T> n=new Node<>(value); if(tail==null) head=n; else tail.next=n; tail=n; size++; }
    /** Best/average/worst O(1) time and O(1) auxiliary space. */
    public void addFirst(T value) { Node<T> n=new Node<>(value); n.next=head; head=n; if(tail==null)tail=n; size++; }
    public T removeFirst() { if(head==null)throw new EmptyStructureException("Empty list"); T v=head.value; head=head.next; if(head==null)tail=null;size--;return v; }
    public T first() { if(head==null)throw new EmptyStructureException("Empty list");return head.value; }
    public int size(){return size;}
    public boolean isEmpty(){return size==0;}
    public T get(int index){if(index<0||index>=size)throw new IndexOutOfBoundsException(); Node<T> n=head;for(int i=0;i<index;i++)n=n.next;return n.value;}
    public boolean removeIf(Predicate<T> condition){Node<T> prev=null,n=head;while(n!=null){if(condition.test(n.value)){if(prev==null)head=n.next;else prev.next=n.next;if(n==tail)tail=prev;size--;return true;}prev=n;n=n.next;}return false;}
    public void clear(){head=tail=null;size=0;}
    @Override public Iterator<T> iterator(){return new Iterator<>(){Node<T> n=head;public boolean hasNext(){return n!=null;}public T next(){if(n==null)throw new NoSuchElementException();T v=n.value;n=n.next;return v;}};}
}
