package model.structures;
import java.io.Serializable;
import java.util.Comparator;
/** Unbalanced BST, comparator orders from smallest to greatest. */
public class BinarySearchTree<T> implements Serializable {
    private static class Node<T> implements Serializable {T data;Node<T> left,right;Node(T d){data=d;}}
    private Node<T> root;private int size;private final Comparator<? super T> cmp;
    public BinarySearchTree(Comparator<? super T> comparator){cmp=comparator;}
    /** Best O(1), average O(log n), worst O(n) time; O(h) recursive stack. */
    public void insert(T item){root=insert(root,item);}
    private Node<T> insert(Node<T> n,T v){if(n==null){size++;return new Node<>(v);}int c=cmp.compare(v,n.data);if(c<0)n.left=insert(n.left,v);else if(c>0)n.right=insert(n.right,v);return n;}
    /** Best O(1), average O(log n), worst O(n) time; O(h) recursive stack. */
    public boolean remove(T item){int before=size;root=remove(root,item);return size<before;}
    private Node<T> remove(Node<T> n,T v){if(n==null)return null;int c=cmp.compare(v,n.data);if(c<0)n.left=remove(n.left,v);else if(c>0)n.right=remove(n.right,v);else{size--;if(n.left==null)return n.right;if(n.right==null)return n.left;Node<T> successor=n.right;while(successor.left!=null)successor=successor.left;n.data=successor.data;n.right=removeSuccessor(n.right);}return n;}
    private Node<T> removeSuccessor(Node<T> n){if(n.left==null)return n.right;n.left=removeSuccessor(n.left);return n;}
    public T maximum(){if(root==null)throw new EmptyStructureException("Empty BST");Node<T> n=root;while(n.right!=null)n=n.right;return n.data;}
    public int size(){return size;}
    public LinkedList<T> descending(){LinkedList<T> out=new LinkedList<>();visit(root,out);return out;}
    private void visit(Node<T> n,LinkedList<T> out){if(n==null)return;visit(n.right,out);out.addLast(n.data);visit(n.left,out);}
}
