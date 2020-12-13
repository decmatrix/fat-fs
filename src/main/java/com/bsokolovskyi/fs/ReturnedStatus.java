package com.bsokolovskyi.fs;

public enum ReturnedStatus {
    FILE_ENLARGED("file was enlarged"),
    FILE_ADDED_TO_FS("file added to file system"),
    FILE_NOT_EXIST("file not exist"),
    FILE_WAS_REMOVED("file was removed"),
    FILE_ALREADY_EXIST("file already exist"),
    NO_FREE_CLUSTERS("no free clusters"),
    CLUSTERS_ALLOCATED("clusters allocated");

    private String info;

    ReturnedStatus(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return info;
    }
}
