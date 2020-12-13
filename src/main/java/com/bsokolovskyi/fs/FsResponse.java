package com.bsokolovskyi.fs;

import java.util.ArrayList;
import java.util.List;

public class FsResponse {
    private final ClusterSlice slice;
    private final List<ReturnedStatus> statusList;

    public FsResponse(ClusterSlice slice) {
        this.slice = slice;
        this.statusList = new ArrayList<>();
    }

    public void addStatus(ReturnedStatus status) {
        statusList.add(status);
    }

    public ReturnedStatus getLastStatus() {
        return statusList.get(statusList.size() - 1);
    }

    public ClusterSlice getSlice() {
        return slice;
    }

    public List<ReturnedStatus> getStatusList() {
        return statusList;
    }
}
