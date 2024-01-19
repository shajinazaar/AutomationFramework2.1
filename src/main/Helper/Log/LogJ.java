package Log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class LogJ{

    public static void initializeLogger(String target, String filename) {

        // Create file appender with dynamic filename
        FileAppender fileAppender = FileAppender.newBuilder()
                .withName("logfile")
                .withFileName(target + "/" + filename + "_" + getCurrentDate() + ".log")
                .withLayout(PatternLayout.newBuilder().withPattern("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n").build())
                .build();

        // Create console appender
        ConsoleAppender consoleAppender = ConsoleAppender.newBuilder()
                .withName("logconsole")
                .withLayout(PatternLayout.createDefaultLayout())
                .build();

        // Add appenders to configuration
        org.apache.logging.log4j.core.Logger rootLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        rootLogger.addAppender(fileAppender);
        rootLogger.addAppender(consoleAppender);

        // Set logging levels
        rootLogger.setLevel(Level.DEBUG);
        fileAppender.setHandler(Level.DEBUG);
        consoleAppender.setHandler(Level.INFO);
    }

    private static String getCurrentDate() {
        // Implement your preferred date formatting logic here
        return java.time.LocalDate.now().toString();
    }

}
