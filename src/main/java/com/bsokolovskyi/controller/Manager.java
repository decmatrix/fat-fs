package com.bsokolovskyi.controller;

import com.bsokolovskyi.fs.Cluster;
import com.bsokolovskyi.fs.FAT16;
import com.bsokolovskyi.fs.File;
import com.bsokolovskyi.fs.ReturnedStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

public class Manager {
    private static Manager manager;

    private final FAT16 fs;
    private BufferedReader reader;

    private Manager(FAT16 fs) {
        this.fs = fs;
    }

    public void run() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        int res;

        while(true) {
            res = runMenu();

            if(res == 0) {
                break;
            } else if(res == -1) {
                System.out.println("Incorrect input");
            } else if(res == 2) {
                System.out.println("Unknown point of menu");
            }
        }
    }

    private int runMenu() {
        try {
            System.out.println("\n" +
                    "1. show cluster table\n" +
                    "2. create file\n" +
                    "3. enlarge file\n" +
                    "4. rename file\n" +
                    "5. remove file\n" +
                    "6. show files\n" +
                    "7. exit\n");

            System.out.print("Enter: ");

            switch(Integer.parseInt(reader.readLine())) {
                case 1:
                    showClusterTable();
                    break;
                case 2:
                    createFile();
                    break;
                case 3:
                    enlargeFile();
                    break;
                case 4:
                    renameFile();
                    break;
                case 5:
                    removeFile();
                    break;
                case 6:
                    showFiles();
                    break;
                case 7:
                    return 0;
                default:
                    return 2;
            }
        } catch (IOException | NumberFormatException e) {
            return -1;
        }

        return 3;
    }

    public void showClusterTable() {
        System.out.println("Cluster table:");
        Cluster[] clusters = fs.getClusterList().toArray(new Cluster[0]);

        for(int i = 0; i < clusters.length; i++) {
            if(i % 10 == 0 && i != 0) {
                System.out.println("    ⃕");
            }

            System.out.printf("%15s", clusters[i]);
        }
        System.out.println();
    }

    public void createFile() throws IOException, NumberFormatException {
        System.out.print("Enter name of file: ");
        String fileName = reader.readLine();

        if(fs.fileExist(fileName)) {
            System.out.println(" file already exist");
            return;
        }

        System.out.print("Enter size of file: ");
        int size = Integer.parseInt(reader.readLine());

        outStatusList(fs.addFile(new File(fileName, size)));
    }

    public void renameFile() throws IOException {
        System.out.print("Enter name of file: ");
        String fileName = reader.readLine();

        if(!fs.fileExist(fileName)) {
            System.out.println(" file not exist");
        } else {
            System.out.print("Enter new file name: ");
            fs.renameFile(fileName, reader.readLine());

            System.out.println(" file was renamed");
        }
    }

    public void removeFile() throws IOException {
        System.out.print("Enter name of file: ");
        outStatusList(fs.removeFile(reader.readLine()));
    }

    public void enlargeFile() throws IOException, NumberFormatException {
        System.out.print("Enter name of file: ");
        String fileName = reader.readLine();
        System.out.print("Enter delta of enlarging: ");
        int delta = Integer.parseInt(reader.readLine());

        outStatusList(fs.enlargeFile(fileName, delta));
    }

    public void showFiles() {
        System.out.println("Files in file system:");

        Collection<File> files = fs.getFiles();

        if(!files.isEmpty()) {
            for (File file : fs.getFiles()) {
                System.out.println(file);
            }
        } else {
            System.out.println(" no files in file system");
        }
    }

    public static Manager createManager(FAT16 fs) {
        if(manager == null) {
            manager = new Manager(fs);
        }

        return manager;
    }

    private void outStatusList(List<ReturnedStatus> statusList) {
        for(ReturnedStatus status : statusList) {
            System.out.println(" " + status);
        }
    }

}
