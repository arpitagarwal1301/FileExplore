package com.example.fileexplore;

public class FileItem {

    private String file;
    private boolean isDirectory;

    public FileItem(String file,boolean isDirectory) {
        this.file = file;
        this.isDirectory = isDirectory;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }
}
