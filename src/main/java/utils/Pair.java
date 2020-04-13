package utils;

import java.io.Serializable;

public class Pair<L,R> implements Serializable {

    public L first;
    public R second;

    public Pair(L left, R right) {
        assert left != null;
        assert right != null;

        this.first = left;
        this.second = right;
    }


    @Override
    public int hashCode() { return first.hashCode() ^ second.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.first.equals(pairo.first) &&
                this.second.equals(pairo.second);
    }

}