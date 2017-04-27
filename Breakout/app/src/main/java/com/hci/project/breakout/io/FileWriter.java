package com.hci.project.breakout.io;

/**
 * Created by rauna on 4/25/2017.
 */
import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileWriter {

    private String fileName;
    private String filePath;
    private FileOutputStream outputStream;
    private File file;
    private Context mContext;

    /* Constructor for file writer
       Author: Parag Dakle
     */
    public FileWriter(String filePath, String fileName, Context context) {
        this.filePath = filePath;
        this.fileName = fileName;
        mContext = context;
    }

    /* Open a file for writing
       Author: Parag Dakle
     */
    public boolean openFile() {
        if(outputStream != null) return false;
        file = new File(filePath);
        try {
            outputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* Write a line to a file
       Author: Raunak Sabhani
     */
    public boolean writeLine(String line) {
        if(outputStream == null) return false;

        try {
            outputStream.write(line.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* Close file
       Author: Parag Dakle
     */
    public boolean closeFile() {
        if(outputStream == null) return false;
        try {
            outputStream.close();
            outputStream = null;
            file = null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
