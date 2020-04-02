package utils;

import Entities.Executor;

import java.util.Iterator;
import java.util.Map;

public class PrettyPrintingMap<K, V> {
    private Map<K, V> map;

    public PrettyPrintingMap(Map<K, V> map) {
        this.map = map;
        System.out.println("SYSTEM STATE - " + Executor.getIstance().getNumberOfJobs());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> entry = iter.next();
            sb.append(entry.getKey().toString().substring(1));
            sb.append("\t====> ");
            sb.append(entry.getValue());
            if (iter.hasNext()) {
                sb.append('\n');
            }
        }
        return sb.toString();

    }
}
