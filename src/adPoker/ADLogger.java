package adPoker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ADLogger {

    private File logger;
    private FileWriter loggerWriter;

    public ADLogger(String nom) {
        try {
            this.logger = new File("log/log_" + nom + ".log");
            this.logger.createNewFile();
            this.loggerWriter = new FileWriter(logger);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void write(String nomMessage, String from, String parametre) {
        try {
            loggerWriter.write(nomMessage + " : (" + from + " | " + parametre + ")\n");
            loggerWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(ADLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
