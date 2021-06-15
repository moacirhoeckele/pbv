package com.volvo.wis.pbv.helpers;

import android.os.Environment;

import org.apache.log4j.Logger;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class Log4jHelper {
    private final static LogConfigurator mLogConfigurator = new LogConfigurator();

    private static void configureLog4j() {
        String fileName = Environment.getExternalStorageDirectory() + "/Logs/PbV.log";
        String filePattern = "%d - [%c] - %p : %m%n";
        int maxBackupSize = 10;
        long maxFileSize = 1024 * 1024;
        configure(fileName, filePattern, maxBackupSize, maxFileSize);
    }

    private static void configure( String fileName, String filePattern, int maxBackupSize, long maxFileSize ) {
        mLogConfigurator.setFileName( fileName );
        mLogConfigurator.setMaxFileSize( maxFileSize );
        mLogConfigurator.setFilePattern(filePattern);
        mLogConfigurator.setMaxBackupSize(maxBackupSize);
        mLogConfigurator.setUseLogCatAppender(true);
        mLogConfigurator.configure();
    }

    public static Logger getLogger(String name) {
        String fileName = mLogConfigurator.getFileName();
        if (fileName.isEmpty() || !fileName.equals(Environment.getExternalStorageDirectory() + "/Logs/PbV.log")) {
            configureLog4j();
        }
        return Logger.getLogger(name);
    }
}
