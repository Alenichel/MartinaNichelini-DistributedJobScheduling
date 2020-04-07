package utils;

import Enumeration.LoggerPriority;

import java.io.*;
import java.util.HashMap;

public class StendusHashMap<K, V> extends HashMap<K, V> {

    private static final String path = "src/main/java/ser/";

    public StendusHashMap(){
        super();
        this.loadKeySet();
    }

    @Override
    public V get(Object key){
        if (!super.containsKey(key)){
            return null;
        }

        if (super.get((V)key) != null ){
            return super.get(key);
        }
        else {
            try {
                V obj = loadFromFile((K)key);
                return obj;
            } catch (Exception e)  {
                Logger.log(LoggerPriority.ERROR, "Error while loading from file");
                return null;
            }
        }
    }

    @Override
    public V put(K key, V value ){
        super.put(key, value);
        saveToFile(key, value);
        return value;
    }

    private void loadKeySet(){
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                super.put( (K)child.getName(), null);
            }
        }
    }

    private void saveToFile(K key, V value ){
        try {
            String filename = path + key.toString();
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(value);
            oos.close();
            fos.close();
            Logger.log(LoggerPriority.DEBUG, "Successfully saved to file");
        } catch (IOException e) {
            Logger.log(LoggerPriority.ERROR, "Error during serialization");
            e.printStackTrace();
        }
    }

    private V loadFromFile(K key) throws IOException, ClassNotFoundException {
        String filename = path + key.toString();
        File file = new File(filename);
        file.mkdirs();
        file.createNewFile();
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        V value = (V) ois.readObject();
        ois.close();
        fis.close();
        return value;
    }

    public void updateOnFile(K key){
        saveToFile(key, super.get(key));
    }

}
