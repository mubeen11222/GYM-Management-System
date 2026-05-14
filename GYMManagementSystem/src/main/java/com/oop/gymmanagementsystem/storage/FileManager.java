package com.oop.gymmanagementsystem.storage;

import java.io.*;

public class FileManager {
    private static final String DATA_DIR = "gym_data";

    public FileManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void saveObject(Object obj, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_DIR + File.separator + filename))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.err.println("Error saving " + filename + ": " + e.getMessage());
        }
    }

    public Object loadObject(String filename) {
        File file = new File(DATA_DIR + File.separator + filename);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading " + filename + ": " + e.getMessage());
            return null;
        }
    }

    public boolean fileExists(String filename) {
        return new File(DATA_DIR + File.separator + filename).exists();
    }

    public void deleteFile(String filename) {
        File file = new File(DATA_DIR + File.separator + filename);
        if (file.exists()) {
            file.delete();
        }
    }
}
