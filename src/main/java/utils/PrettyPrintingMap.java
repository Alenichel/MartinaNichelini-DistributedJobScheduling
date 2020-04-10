package utils;

import Entities.Executor;
import Enumeration.BroadcastingType;
import Main.ExecutorMain;

import java.net.InetAddress;
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
            String key = entry.getKey().toString().substring(1);
            key = (key.equals(NetworkUtilis.getLocalAddress())) ? "localhost" : key;
            key += (ExecutorMain.bt == BroadcastingType.GLOBAL_TCP) ? ExecutorMain.externalIP : "";
            String value = entry.getValue().toString();
            sb.append(key);
            sb.append("\t\t====> ");
            sb.append(value);
            if (iter.hasNext()) {
                sb.append('\n');
            }
        }
        return sb.toString();

    }
}
