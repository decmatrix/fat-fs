package com.bsokolovskyi.fs;

import java.util.Objects;

public class Cluster {
    public static final int STD_ID_OFFSET = 2;
    private State state;
    private final int id;

    Cluster(int id) {
        this.id = id + STD_ID_OFFSET;
        this.state = State.EMPTY;
    }

    void setState(State state) {
        this.state = state;
    }

    State getState() {
        return state;
    }

    int getId() {
        return id;
    }

    enum State {
        CORRUPTED("cluster is corrupted"),
        EMPTY("empty"),
        USING("using");

        private String info;

        State(String info) {
            this.info = info;
        }

        @Override
        public String toString() {
            return info;
        }
    }

    @Override
    public String toString() {
        char ch;

        switch(this.state) {
            case CORRUPTED:
                ch = '!';
                break;
            case EMPTY:
                ch = 'O';
                break;
            case USING:
                ch = 'X';
                break;
            default:
                throw new IllegalArgumentException("Unknown state -> " + this.state);
        }


        return String.format("0x%s(%c)",
                Integer.toHexString(id).toUpperCase(),
                ch);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cluster)) return false;

        Cluster cluster = (Cluster) o;
        return this.id == cluster.id;
    }
}
