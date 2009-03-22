/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.myorg.dgc.api;

import java.util.Date;

/**
 *
 * @author akira
 */
public class Acs extends Equipment {

   private final Date date = new Date();
   private static int count = 0;
   private final int index;
   private String name;
   private String path;


   public Acs() {
       this("camera");
   }

   public Acs(String name) {
       index = count++;
       this.name = name;
   }

   public Date getDate() {
      return date;
   }

   public int getIndex() {
      return index;
   }

   public String getName() {
       return name;
   }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    @Override
   public String toString() {
       return "Camera:" + index + " ("+name+") - " + date;
   }

}
