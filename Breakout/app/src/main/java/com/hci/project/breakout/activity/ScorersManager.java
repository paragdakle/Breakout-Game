package com.hci.project.breakout.activity;

/**
 * Created by rauna on 4/25/2017.
 */
import android.content.Context;

//import com.example.hci.hciassignment.entity.Scorer;

import com.hci.project.breakout.io.FileReader;
import com.hci.project.breakout.io.FileWriter;
import com.hci.project.breakout.model.Scorer;

import java.util.ArrayList;
import java.util.List;

public class ScorersManager {

    final String fileName = "HCIScorers.txt";
    String filePath;
    Context mContext;
    FileReader reader;
    FileWriter writer;

    /*Constructor for class
      Author: Parag Dakle
     */
    public ScorersManager(Context context) {
        this.filePath = context.getFilesDir().getAbsolutePath()+ "/" + fileName;
        mContext = context;
        reader = new FileReader(filePath, fileName, context);
        writer = new FileWriter(filePath, fileName, context);
    }

    /* Add scorer to file
       Author: Raunak Sabhani
     */
    public boolean addScorer(Scorer scorer) {
        if(writer.openFile()) {
            boolean success = writer.writeLine(scorer.toString());
            writer.closeFile();
            return success;
        }
        return false;
    }

    /* Add multiple scorers to file
       Author: Parag Dakle
     */
    public Boolean addScorers(List<Scorer> scorerList) {
        if(writer.openFile()) {
            for(Scorer scorer : scorerList) {
                writer.writeLine(scorer.toString());
            }
            writer.closeFile();
            return true;
        }
        return false;
    }

    /* Get scorers from file
       Author: Raunak Sabhani
     */
    public List<Scorer> getScorers() {
        List<Scorer> scorerList = new ArrayList<>();
        if(reader.openFile()) {
            String line;
            Scorer scorer;
            while ((line = reader.readLine()) != null) {
                scorer = Scorer.stringToScorer(line);
                scorerList.add(scorer);
            }
            reader.closeFile();
        }
        return scorerList;
    }
}

