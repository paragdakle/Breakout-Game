package com.hci.project.breakout.io;

/**
 * Created by rauna on 4/25/2017.
 */

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileReader {

    private String fileName;
    private String filePath;
    private FileInputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader reader;
    private Context mContext;

    /* Constructor for file reader
       Author: Raunak Sabhani
     */
    public FileReader(String filePath, String fileName, Context context) {
        this.filePath = filePath;
        this.fileName = fileName;
        mContext = context;
    }

    /* Open a file
       Author: Raunak Sabhani
    */
    public boolean openFile() {
        if(inputStream != null) return false;
        try {
            inputStream = mContext.openFileInput(fileName);
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* Read line from file
       Author: Parag Dakle
     */
    public String readLine() {
        if(inputStream == null) return null;

        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* close the file
       Author: Raunak Sabhani
     */
    public boolean closeFile() {
        if(inputStream == null) return false;
        try {
            reader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            inputStreamReader = null;
            reader = null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

