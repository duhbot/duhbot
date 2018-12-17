package org.duh102.duhbot.data;

public class Pair<U, V> {
    final U first;
    final V second;
    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }
    public U getFirst() {
        return first;
    }
    public V getSecond() {
        return second;
    }
}
