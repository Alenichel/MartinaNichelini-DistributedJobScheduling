package utils;

import Enumeration.LoggerPriority;

public final class Logger {

    private Logger(){
        throw new IllegalStateException("Utility class");
    }

    private static final String HOME_PATH = System.getProperty("user.home");
    private static final String CLIENT_LOG = "DJS_client.log";
    private static final String SERVER_LOG = "/DSJ_scheduling.log";

    private static boolean debugMode;

    public static void log (LoggerPriority priority, String toLog){
        if (priority == LoggerPriority.NORMAL)
            System.out.println(toLog);

        else if (priority == LoggerPriority.NOTIFICATION ){
            toLog = "[*] NOTIFICATION: " + toLog;
            System.out.println(toLog);
        }
        else if (priority == LoggerPriority.WARNING){
            toLog = "[*] WARNING: " + toLog;
            System.out.println((char) 27 + "[33m" + toLog + (char) 27 + "[30m");
        }
        else if (priority == LoggerPriority.ERROR){
            toLog = "[*] ERROR: " + toLog;
            System.out.println((char) 27 + "[31m"+ toLog + (char) 27 + "[30m");
        }
        else if(priority == LoggerPriority.DEBUG && debugMode){
            toLog = "[*] DEBUG: " + toLog;
            System.out.println(toLog);
    }
    }

}