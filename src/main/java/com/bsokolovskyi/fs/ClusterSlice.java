package com.bsokolovskyi.fs;

public class ClusterSlice {
    private final int startId;
    private final int endId;

    public ClusterSlice(int startId, int endId) {
        if(endId < startId) {
            throw new IllegalArgumentException("start > end id");
        }

        if(endId <= -1 || startId <= -1) {
            throw new IllegalArgumentException("limit <= -1");
        }

        this.startId = startId;
        this.endId = endId;
    }

    public int getEndId() {
        return endId;
    }

    public int getStartId() {
        return startId;
    }

    @Override
    public String toString() {
        return String.format("cluster slice: [0x%s; 0x%s]",
                Integer.toHexString(startId).toUpperCase(),
                Integer.toHexString(endId).toUpperCase());
    }
}
