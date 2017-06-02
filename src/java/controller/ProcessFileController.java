package controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ProcessFileController is used to process files, and to send the necessary file to the pertinent controllers to be processed.
 * @author jia cheng / jing xiang
 */
public class ProcessFileController {

    /**
     * BootstrapController is used to bootstrap the files after we have un-zipped it
     */
    BootstrapController bootstrapController;

    /**
     * Constructor of Process File Controller
     * Instantiate the bootstrap controller to be used later on in processing
     */
    public ProcessFileController() {
        bootstrapController = new BootstrapController();
    }

    /**
     * Process the file and gets the validation results after it has been
     * uploaded to the server
     *
     * @param file the file that has been uploaded
     * @return LinkedHashMap the validation results for the file that has been
     * uploaded
     */
    public LinkedHashMap processFile(File file) {
        
        //Declare and instantiate a LinkedHashMap which contains the results
        LinkedHashMap<String, Object> uploadResult = new LinkedHashMap<String, Object>();
        
        //Get the file name of File object
        String fileName = file.getName();
        
        // Only process the file is in .zip format
        if (fileName.endsWith("zip")) {
            try {
                
                //Instantiate various InputStreams so that the Zip can be read and converted
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ZipInputStream zis = new ZipInputStream(bis);
                
                // Declare a ZipEntry, which is a file inside the zip file
                ZipEntry entry;
                // Looping through all the ZipEntry inside the Zip
                while ((entry = zis.getNextEntry()) != null) {
                    String outputPath = file.getParent() + File.separator + entry.getName();
                    //Any reference to MACOS, lets skip. no point dealing with MAC OS folders in zip
                    if (outputPath.contains("MACOSX")) {
                        continue;
                    }
                    // if the entry is not a folder but a file
                    if (!entry.isDirectory()) {
                        FileOutputStream output = new FileOutputStream(outputPath);
                        int len = 0;
                        byte[] buffer = new byte[2048];
                        while ((len = zis.read(buffer)) > 0) {
                            // since stream does not have any stream.hasNext() or hasRead(), this is the shortcut to it
                            output.write(buffer, 0, len);
                        }
                        output.close();
                    } else {
                        // at this point the file is just a reference
                        File dir = new File(outputPath);
                        // make the directory
                        dir.mkdir();
                    }
                }

                //Delete the zip when done
                file.delete();
                
                // Close all InputStreams 
                fis.close();
                bis.close();
                zis.close();
                
                // Let's do some bootstrap processing now
                uploadResult = bootstrapController.processBootstrap(file.getParent()); // dependent on which repository u r using
            } catch (IOException ioe) {
                System.out.println("Unable to unzip file:" + ioe);
            }
        } 
        
        //Return uploadResults
        return uploadResult;
    }
}
