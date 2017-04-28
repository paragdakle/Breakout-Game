/*=============================================================================
 |   Assignment:  CS6326 Project
 |       Author:  Parag Dakle, Raunak Sabhani
 |     Language:  Android
 |    File Name:  Scorer.java
 |
 +-----------------------------------------------------------------------------
 |
 |  Description:  A breakout game
 |
 |  File Purpose: Class which holds details of each scorer
 *===========================================================================*/
package com.hci.project.breakout.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Scorer implements Comparable<Scorer> {
    private static final String separator = "\t";
    private final String terminator = "\n";

    private String name;
    private int score;

    /*Constructor with two arguments
      Author: Raunak Sabhani
     */
    public Scorer(String name, int score)
    {
        this.name = name;
        this.score = score;
    }

    /*Get name of scorer
      Author: Parag Dakle
     */
    public String getName()
    {
        return this.name;
    }

    /*Get score of scorer
      Author: Parag Dakle
     */
    public int getScore()
    {
        return this.score;
    }

    /*Convert to string object
      Author: Parag Dakle
     */
    @Override
    public String toString()
    {
        return this.name + separator + this.score + this.terminator;
    }

    /*Convert to string object to be stored in shared preferences
      Author: Parag Dakle
     */
    public String toSharedPreferenceString()
    {
        return this.name + separator + this.score;
    }

    /*Compare two scorer objects. Used by Collections.sort
      Author: Raunak Sabhani
     */
    @Override
    public int compareTo(Scorer s1)
    {
        if (score < s1.score)
        {
            return 1;
        } else if (score > s1.score)
        {
            return -1;
        }
        return 0;
    }

    /*Convert string object to Scorer object
      Author: Raunak Sabhani
     */
    public static Scorer stringToScorer(String strScorer) {
        String[] attrs = strScorer.split(separator);
        if(attrs.length == 2) {
            return new Scorer(attrs[0], Integer.parseInt(attrs[1]));
        }
        return null;
    }
}
