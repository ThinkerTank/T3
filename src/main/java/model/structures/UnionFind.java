package model.structures;
/** Disjoint set with path compression and union by rank. */
public class UnionFind {
    private final int[] parent,rank;
    public UnionFind(int n){parent=new int[n];rank=new int[n];for(int i=0;i<n;i++)parent[i]=i;}
    /** Amortized O(alpha(n)) time; O(1) auxiliary space. */
    public int find(int x){if(parent[x]!=x)parent[x]=find(parent[x]);return parent[x];}
    /** Amortized O(alpha(n)) time; O(1) auxiliary space. */
    public boolean union(int a,int b){int x=find(a),y=find(b);if(x==y)return false;if(rank[x]<rank[y])parent[x]=y;else if(rank[x]>rank[y])parent[y]=x;else{parent[y]=x;rank[x]++;}return true;}
}
