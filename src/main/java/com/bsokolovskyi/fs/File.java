package com.bsokolovskyi.fs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class File {
    private int size;
    private String name;
    private String lastUpdate;
    private final String created;
    private int startId;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public File(String name, int size) {
        this.name = name;
        this.size = size;
        this.created = DTF.format(LocalDateTime.now());
        this.lastUpdate = this.created;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public String getName() {
        return name;
    }

    public ClusterSlice getSlice() {
        return new ClusterSlice(startId, startId + size - 1);
    }

    public int getSize() {
        return size;
    }

    public int getStartId() {
        return startId;
    }

    public void setName(String name) {
        this.lastUpdate = DTF.format(LocalDateTime.now());
        this.name = name;
    }

    public void resize(int size) {
        this.lastUpdate = DTF.format(LocalDateTime.now());
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("name: %s, size: %d (clusters), last update: %s, created: %s, start id: %s, %s",
                name,
                size,
                lastUpdate,
                created,
                Integer.toHexString(startId).toUpperCase(),
                getSlice());
    }
}
