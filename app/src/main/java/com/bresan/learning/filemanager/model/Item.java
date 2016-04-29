package com.bresan.learning.filemanager.model;

/**
 * Created by rodrigobresan on 4/27/16.
 */
public class Item implements Comparable<Item> {

    private String name;
    private String data;
    private String date;
    private String path;
    private String image;
    private boolean isDirectory;

    public Item() {
    }

    public Item(String name, String data, String date, String path, String image, boolean isDirectory) {
        this.name = name;
        this.data = data;
        this.date = date;
        this.path = path;
        this.image = image;
        this.isDirectory = isDirectory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public String getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    public String getImage() {
        return image;
    }

    public int compareTo(Item anotherItem) {
        if(this.name != null) {
            return this.name.toLowerCase().compareTo(anotherItem.getName().toLowerCase());
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}