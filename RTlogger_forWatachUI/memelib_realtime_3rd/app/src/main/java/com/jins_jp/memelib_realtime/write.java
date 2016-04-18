package com.jins_jp.memelib_realtime;

//import android.widget.Toast;
//import com.jins_jp.meme.MemeRealtimeData;
import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class write {
    static Boolean write(File file, String args) {
        try {
            if (checkBeforeWritefile(file)){
                FileWriter fw = new FileWriter(file,true);
                PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
                try{
                    pw.print(args);
                    pw.println();
                    pw.close();
                    return true;
                }catch(Exception e) {
                    System.out.println(e);
                    return false;
                }
            }else return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkBeforeWritefile(File file) throws IOException {
        if (file.exists()){
            if (file.isFile() && file.canWrite()){
                return true;
            }
        }else {
            file.createNewFile();
            return false;
        }
        return false;
    }
}
