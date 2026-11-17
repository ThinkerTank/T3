package model.structures;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Comparator;
public class StructuresTest {
    @Test void stackLifo(){Stack<Integer> s=new Stack<>();s.push(1);s.push(2);assertEquals(2,s.pop());assertEquals(1,s.pop());}
    @Test void stackPeekDoesNotRemove(){Stack<Integer> s=new Stack<>();s.push(6);assertEquals(6,s.peek());assertEquals(1,s.size());}
    @Test void stackEmptyThrows(){assertThrows(EmptyStructureException.class,()->new Stack<>().pop());}
    @Test void stackSizeAndEmpty(){Stack<Integer> s=new Stack<>();assertTrue(s.isEmpty());s.push(2);assertFalse(s.isEmpty());}
    @Test void queueFifo(){Queue<Integer> q=new Queue<>();q.enqueue(1);q.enqueue(2);assertEquals(1,q.dequeue());assertEquals(2,q.dequeue());}
    @Test void queueFront(){Queue<Integer> q=new Queue<>();q.enqueue(1);assertEquals(1,q.front());assertEquals(1,q.size());}
    @Test void queueEmpty(){assertThrows(EmptyStructureException.class,()->new Queue<>().dequeue());}
    @Test void queueRemoveIf(){Queue<Integer> q=new Queue<>();q.enqueue(1);q.enqueue(2);assertTrue(q.removeIf(x->x==1));assertEquals(2,q.front());}
    @Test void hashCollision(){HashTable<String,Integer> h=new HashTable<>(16);h.put("Aa",1);h.put("BB",2);assertEquals(1,h.get("Aa"));assertEquals(2,h.get("BB"));}
    @Test void hashGrow(){HashTable<Integer,Integer> h=new HashTable<>(2);for(int i=0;i<40;i++)h.put(i,i*2);assertEquals(40,h.size());assertEquals(78,h.get(39));}
    @Test void hashRemove(){HashTable<Integer,Integer> h=new HashTable<>();h.put(1,2);assertEquals(2,h.remove(1));assertFalse(h.containsKey(1));}
    @Test void hashReplace(){HashTable<Integer,Integer> h=new HashTable<>();h.put(1,2);h.put(1,3);assertEquals(1,h.size());assertEquals(3,h.get(1));}
    @Test void heapOrder(){PriorityQueue<Integer> p=new PriorityQueue<>(Comparator.naturalOrder());p.insert(2);p.insert(9);p.insert(4);assertEquals(9,p.extractMax());assertEquals(4,p.extractMax());}
    @Test void heapPeek(){PriorityQueue<Integer> p=new PriorityQueue<>(Comparator.naturalOrder());p.insert(3);assertEquals(3,p.peekMax());assertEquals(1,p.size());}
    @Test void heapEmpty(){assertThrows(EmptyStructureException.class,()->new PriorityQueue<Integer>(Comparator.naturalOrder()).peekMax());}
    @Test void heapRemove(){PriorityQueue<Integer> p=new PriorityQueue<>(Comparator.naturalOrder());p.insert(3);p.insert(8);assertTrue(p.remove(8));assertEquals(3,p.peekMax());}
    @Test void linkedListIterations(){LinkedList<String> l=new LinkedList<>();l.addLast("a");l.addFirst("z");assertEquals("z",l.get(0));assertEquals("a",l.get(1));}
    @Test void linkedListRemoval(){LinkedList<Integer> l=new LinkedList<>();l.addLast(1);l.addLast(2);l.removeIf(x->x==1);assertEquals(2,l.first());}
    @Test void bstOrder(){BinarySearchTree<Integer> t=new BinarySearchTree<>(Comparator.naturalOrder());t.insert(2);t.insert(1);t.insert(3);assertEquals(3,t.maximum());}
    @Test void bstDeleteRoot(){BinarySearchTree<Integer> t=new BinarySearchTree<>(Comparator.naturalOrder());t.insert(2);t.insert(1);t.insert(3);assertTrue(t.remove(2));assertEquals(2,t.size());}
}
