package com.bsokolovskyi.fs;

import java.util.*;

public class FAT16 {
    private final static Random RANDOM = new Random();
    private static final int MAX_SIZE = 65536;

    private final int size;
    private final int cofOfCorrupted;
    private final List<Cluster> clusterList;
    private final Map<String, File> fileRegistry;

    public FAT16(int size, int cofOfCorrupted) {

        if(size % 10 != 0) {
            throw new IllegalArgumentException("Size % 10 != 0");
        }

        if(size > MAX_SIZE) {
            throw new IllegalArgumentException("FAT16 max size is " + MAX_SIZE);
        }

        this.size = size;
        this.clusterList = new LinkedList<>();
        this.fileRegistry = new HashMap<>();
        this.cofOfCorrupted = cofOfCorrupted;

        init();
    }

    private void init() {
        for(int i = 0; i < size; i++) {
            Cluster cluster = new Cluster(i);

            if(RANDOM.nextInt(100) % cofOfCorrupted == 0) {
                cluster.setState(Cluster.State.CORRUPTED);
            }

            clusterList.add(cluster);
        }
    }

    public List<ReturnedStatus> addFile(File file) {
        List<ReturnedStatus> statusList = new ArrayList<>();

        if(fileRegistry.containsKey(file.getName())) {
            statusList.add(ReturnedStatus.FILE_ALREADY_EXIST);
            return statusList;
        }

        FsResponse response = allocateClusters(file.getSize());

        if(response.getLastStatus().equals(ReturnedStatus.NO_FREE_CLUSTERS)) {
            return response.getStatusList();
        }

        updateClusterStates(response.getSlice(), Cluster.State.USING);
        file.setStartId(response.getSlice().getStartId());
        fileRegistry.put(file.getName(), file);

        statusList.add(ReturnedStatus.FILE_ADDED_TO_FS);
        return statusList;
    }

    public List<ReturnedStatus> removeFile(String fileName) {
        List<ReturnedStatus> statusList = new ArrayList<>();

        if(!fileExist(fileName)) {
            statusList.add(ReturnedStatus.FILE_NOT_EXIST);
            return statusList;
        }

        File file = fileRegistry.get(fileName);
        updateClusterStates(file.getSlice(), Cluster.State.EMPTY);
        fileRegistry.remove(fileName);

        statusList.add(ReturnedStatus.FILE_WAS_REMOVED);
        return statusList;
    }

    public List<ReturnedStatus> enlargeFile(String fileName, int delta) {
        List<ReturnedStatus> statusList = new ArrayList<>();

        if(!fileExist(fileName)) {
            statusList.add(ReturnedStatus.FILE_NOT_EXIST);
            return statusList;
        }

        File file = fileRegistry.get(fileName);
        updateClusterStates(file.getSlice(), Cluster.State.EMPTY);
        FsResponse response = allocateClusters(file.getSize() + delta);

        if(response.getLastStatus().equals(ReturnedStatus.NO_FREE_CLUSTERS)) {
            updateClusterStates(file.getSlice(), Cluster.State.USING);
            return response.getStatusList();
        }

        updateClusterStates(response.getSlice(), Cluster.State.USING);
        file.resize(file.getSize() + delta);
        file.setStartId(response.getSlice().getStartId());

        statusList.add(ReturnedStatus.FILE_ENLARGED);
        return statusList;
    }

    public boolean fileExist(String fileName) {
        return fileRegistry.containsKey(fileName) && fileInClusterTable(fileRegistry.get(fileName).getStartId());
    }

    public void renameFile(String fileName, String newFileName) {
        File file = fileRegistry.get(fileName);
        file.setName(newFileName);
        fileRegistry.remove(fileName);
        fileRegistry.put(newFileName, file);
    }

    public Collection<File> getFiles() {
        return fileRegistry.values();
    }

    public List<Cluster> getClusterList() {
        return clusterList;
    }

    private boolean fileInClusterTable(int startId) {
        for(Cluster cluster : clusterList) {
            if(cluster.getId() == startId && cluster.getState().equals(Cluster.State.USING)) {
                return true;
            }
        }

        return false;
    }

    private void updateClusterStates(ClusterSlice slice, Cluster.State state) {
        int needId = slice.getStartId();

        for(Cluster cluster : clusterList) {
            if(cluster.getId() == needId) {
                cluster.setState(state);
                needId++;
            }

            if(needId > slice.getEndId()) {
                return;
            }
        }
    }

    private FsResponse allocateClusters(int size) {
        int startClusterId = -1;
        int endClusterId = -1;

        for(Cluster cluster : clusterList) {
            if(cluster.getState().equals(Cluster.State.EMPTY) && startClusterId == -1) {
                startClusterId = cluster.getId();
            }

            if(cluster.getState().equals(Cluster.State.EMPTY) && startClusterId != -1) {
                endClusterId = cluster.getId();
            }

            if(startClusterId != -1 && endClusterId != -1 && (endClusterId - startClusterId) + 1 == size) {
                FsResponse response = new FsResponse(new ClusterSlice(startClusterId, endClusterId));
                response.addStatus(ReturnedStatus.CLUSTERS_ALLOCATED);
                return response;
            }

            if(cluster.getState().equals(Cluster.State.USING) || cluster.getState().equals(Cluster.State.CORRUPTED)) {
                startClusterId = -1;
                endClusterId = -1;
            }

        }

        FsResponse response = new FsResponse(null);
        response.addStatus(ReturnedStatus.NO_FREE_CLUSTERS);

        return response;
    }
}
