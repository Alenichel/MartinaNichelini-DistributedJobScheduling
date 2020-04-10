package utils;

import Entities.Job;
import Enumeration.JobStatus;
import Enumeration.LoggerPriority;

import java.io.*;
import java.util.HashMap;

public class LazyHashMap<K, V> extends HashMap<K, V> {

    private String path;

    public LazyHashMap(String pathToArchiveDir){
        super();
        this.path = System.getProperty("user.home") + pathToArchiveDir;
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdirs();
        }
        this.loadKeySet();
    }

    @Override
    public V get(Object key){
        if (!super.containsKey(key)){               // if the Hashmap does not have that key element, returns null
            return null;
        }

        if (super.get((V)key) != null ){            // if the Hashmap has the actual value, returns that value
            return super.get(key);
        }
        else {                                      // if the Hashmap has just a placeholder, gets the actual value from file
            try {
                V obj = loadFromFile((K)key);
                return obj;
            } catch (Exception e)  {                // in case of error, returns null
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

    private void loadKeySet(){                       // it iterates over all filenames and it puts a couple (key, value)
        File dir = new File(path);                  //  in it's own data structure
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (!child.getName().contains("PENDING")) {
                    super.put((K) child.getName(), null);
                }
            }
        }
    }

    private void saveToFile(K key, V value){
        try {
            String filename = path;
            if ( ((Job)value).getStatus() == JobStatus.PENDING ){               // if i'm saving a pending job
                filename += ("PENDING_" + key);                                 // add a PENDING tag
            } else {                                                            // if i'm saving a completed job
                filename += key.toString();
                String pendingFilename = path + "PENDING_" + key.toString();    // before saving the new file
                File pendingFile = new File(pendingFilename);                   //   delete the old PENDING ONE
                Boolean ack = pendingFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(value);
            oos.close();
            fos.close();
            Logger.log(LoggerPriority.DEBUG, "Job with id: " + key.toString() + " successfully saved to file");
        } catch (IOException e) {
            Logger.log(LoggerPriority.ERROR, "Error during serialization");
            Logger.log(LoggerPriority.ERROR, "Path was = " + path);
            e.printStackTrace();
        }
    }

    private V loadFromFile(K key) throws IOException, ClassNotFoundException {
        String filename =  path + key.toString();
        File file = new File(filename);
        file.mkdirs();
        file.createNewFile();
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        V value = (V)ois.readObject();
        ois.close();
        fis.close();
        Logger.log(LoggerPriority.DEBUG, "Successfully load from file Job with id: " + key.toString());
        return value;
    }

    public void updateOnFile(K key){
        saveToFile(key, super.get(key));
    }

}
