import Database.DataConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ExecutionSetManager {
    private static final Logger logger = LogManager.getLogger();
    private DataConnection db;
    private  Helper helpObj;
    private Map<String, String> SampleXml;


    public ExecutionSetManager(DataConnection db, Helper helpObj, Map<String, String> SampleXml){
        this.db = db;
        this.helpObj = helpObj;
        this.SampleXml = SampleXml;
    }

    public void run(){
        callerMethod();
    }

    private void callerMethod() {

    }

}
