package model.structures;
import java.io.Serializable;
import java.util.Comparator;
/** Binary max heap using a dynamically resized array; comparator is serializable for snapshots. */
public class PriorityQueue<T> implements Serializable {
    private Object[] heap=new Object[16];private int size;
    private final Comparator<? super T> comparator;
    public PriorityQueue(Comparator<? super T> comparator){this.comparator=comparator;}
    @SuppressWarnings("unchecked") private T at(int i){return (T)heap[i];}
    /** Best O(1), average/worst O(log n) time; amortized O(1) extra. */
    public void insert(T item){if(size==heap.length){Object[] bigger=new Object[heap.length*2];System.arraycopy(heap,0,bigger,0,size);heap=bigger;}int i=size++;while(i>0){int p=(i-1)/2;if(comparator.compare(item,at(p))<=0)break;heap[i]=heap[p];i=p;}heap[i]=item;}
    /** Best O(1), average/worst O(log n) time; O(1) auxiliary space. */
    public T extractMax(){if(size==0)throw new EmptyStructureException("Empty heap");T answer=at(0);T last=at(--size);heap[size]=null;if(size>0){int i=0;while(2*i+1<size){int j=2*i+1;if(j+1<size&&comparator.compare(at(j+1),at(j))>0)j++;if(comparator.compare(last,at(j))>=0)break;heap[i]=heap[j];i=j;}heap[i]=last;}return answer;}
    public T peekMax(){if(size==0)throw new EmptyStructureException("Empty heap");return at(0);}
    public boolean isEmpty(){return size==0;}
    public int size(){return size;}
    public boolean remove(T value){boolean found=false;PriorityQueue<T> next=new PriorityQueue<>(comparator);while(!isEmpty()){T x=extractMax();if(!found&&x.equals(value))found=true;else next.insert(x);}this.heap=next.heap;this.size=next.size;return found;}
}
