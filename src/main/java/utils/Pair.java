package utils;

public class Pair<L,R> {

    public final L first;
    public final R second;

    public Pair(L left, R right) {
        assert left != null;
        assert right != null;

        this.first = left;
        this.second = right;
    }

    public L getLeft() { return first; }
    public R getRight() { return second; }

    @Override
    public int hashCode() { return first.hashCode() ^ second.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.first.equals(pairo.getLeft()) &&
                this.second.equals(pairo.getRight());
    }

}