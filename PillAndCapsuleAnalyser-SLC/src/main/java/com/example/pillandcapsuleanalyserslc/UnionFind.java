package com.example.pillandcapsuleanalyserslc;

public class UnionFind {

    private int[] parent;
    private int[] size;

    public UnionFind(int size) {
        parent = new int[size];
        this.size = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            this.size[i] = 1;  // each node initially size 1
        }
    }

    // find parent of the set in which p belongs
    public int find(int p) {
        if (parent[p] != p) {
            parent[p] = find(parent[p]);
        }
        return parent[p];
    }


    // unite the sets
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP != rootQ) {
            if (size[rootP] < size[rootQ]) {
                parent[rootP] = rootQ;
                size[rootQ] += size[rootP];
            } else {
                parent[rootQ] = rootP;
                size[rootP] += size[rootQ];
            }
        }
    }

    public int getSize(int p) {
        int root = find(p);
        return size[root];
    }
}
