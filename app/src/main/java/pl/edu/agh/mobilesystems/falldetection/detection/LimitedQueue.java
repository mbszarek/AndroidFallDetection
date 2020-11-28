package pl.edu.agh.mobilesystems.falldetection.detection;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> {
    private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        super.add(o);
        while (size() > limit) {
            super.remove();
        }
        return true;
    }

    @Override
    public void addFirst(E o) {
        super.addFirst(o);
        while (size() > limit) {
            super.removeLast();
        }
    }
}