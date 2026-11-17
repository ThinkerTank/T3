package model.structures;
import java.io.Serializable;
import java.util.Objects;
/** Separate chaining, own linked list; doubles at load factor 0.75. */
public class HashTable<K,V> implements Serializable {
    private static class Entry<K,V> implements Serializable {final K key;V value;Entry(K k,V v){key=k;value=v;}}
    private LinkedList<Entry<K,V>>[] buckets;
    private int size;
    public HashTable(){this(16);}
    @SuppressWarnings("unchecked") public HashTable(int capacity){if(capacity<1)throw new IllegalArgumentException("capacity");buckets=(LinkedList<Entry<K,V>>[])new LinkedList[capacity];}
    private int index(K key){return Math.floorMod(Objects.requireNonNull(key).hashCode(),buckets.length);}
    /** Expected O(1) time, worst O(n); O(1) extra except O(n) resize. */
    public V put(K key,V value){int i=index(key);if(buckets[i]==null)buckets[i]=new LinkedList<>();for(Entry<K,V> e:buckets[i])if(e.key.equals(key)){V old=e.value;e.value=value;return old;}buckets[i].addLast(new Entry<>(key,value));size++;if(size>0.75*buckets.length)resize();return null;}
    @SuppressWarnings("unchecked") private void resize(){LinkedList<Entry<K,V>>[] old=buckets;buckets=(LinkedList<Entry<K,V>>[])new LinkedList[old.length*2];size=0;for(LinkedList<Entry<K,V>> bucket:old)if(bucket!=null)for(Entry<K,V> e:bucket)put(e.key,e.value);}
    /** Expected O(1) time, worst O(n); O(1) space. */
    public V get(K key){LinkedList<Entry<K,V>> bucket=buckets[index(key)];if(bucket!=null)for(Entry<K,V> e:bucket)if(e.key.equals(key))return e.value;return null;}
    public boolean containsKey(K key){LinkedList<Entry<K,V>> bucket=buckets[index(key)];if(bucket!=null)for(Entry<K,V> e:bucket)if(e.key.equals(key))return true;return false;}
    public V remove(K key){LinkedList<Entry<K,V>> bucket=buckets[index(key)];if(bucket!=null)for(Entry<K,V> e:bucket)if(e.key.equals(key)){V value=e.value;bucket.removeIf(x->x.key.equals(key));size--;return value;}return null;}
    public int size(){return size;}
    public Iterable<V> values(){LinkedList<V> values=new LinkedList<>();for(LinkedList<Entry<K,V>> bucket:buckets)if(bucket!=null)for(Entry<K,V> e:bucket)values.addLast(e.value);return values;}
}
