package com.salvatorefiorilla.systemmonitor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.salvatorefiorilla.systemmonitor.room.ApplicationStats;
import com.salvatorefiorilla.systemmonitor.room.SystemMonitorDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReportService extends Service {

    private static final long TIME_TO_SLEEP_REPORT = 300000*8;
    public static final int SERVICE_ID = 932; //uno a caso
    //private Context context;
    private long TIME_TO_SLEEP= 30000*2;
    private String email;

    private void sendMessage() {
        try {
        sendEMAIL();
        } catch (Exception e) {
            Log.e("mylog", "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendEMAIL() {
        String email = this.email;
        String subject = "[report status]";//editTextSubject.getText().toString().trim();
        String message = "Report of today "+
                new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                        .format(new Date( System.currentTimeMillis() ))+"\n\n";

        String filename = createReport();
        if(filename == null){
            return;
        }
        String path = getApplicationContext().getFilesDir().toString()+"/"+filename;
        //System.out.println("Il percorso è : "+ path);
        //System.out.println("Il percorso dovrebbe essere :> /home/manisha/file.txt ");
        SendMail sm = new SendMail(getApplicationContext(), email, subject, message,path);
        sm.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private String createReport(){

            try {
                return
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... voids) {
                                SystemMonitorDatabase db = Room.databaseBuilder(getApplicationContext(), SystemMonitorDatabase.class, "systemmonitor").allowMainThreadQueries().build();
                                String text = "Status Report of "+
                                        new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss").format(new Date( System.currentTimeMillis() ))
                                        +":";
                                long[] timePeriod = new long[3];
                                timePeriod[0] = System.currentTimeMillis()- (long) 86400000;
                                timePeriod[1] = System.currentTimeMillis()- (long) 86400000 * 30;
                                timePeriod[2] = System.currentTimeMillis()- (long) 86400000 * 365;

                                String[] tit = new String[timePeriod.length];
                                tit[0] =  "\nLatest 24h:\n\t\t";;
                                tit[1] =  "\nLatest month:\n\t\t";;
                                tit[2] =  "\nLatest year:\n\t\t";;

                                for(int i = 0; i<timePeriod.length; i++) {
                                    text += tit[i];
                                    text +=" Info: "+ getInfoApp(timePeriod[i],db);

                                    List<ApplicationStats> myApp = getApps(timePeriod[i],db);
                                    for (ApplicationStats as : myApp){
                                        text += "\n "+as.toString()+" \n";
                                    }
                                    text+="\n\n";
                                }

                                return generateFile(getApplicationContext(), "report.txt", text);
                            }

                            private List<ApplicationStats> getApps(long time, SystemMonitorDatabase db) {
                                return db.statsDao().queryLastTime(time);
                            }

                            private String getInfoApp(long timePeriod,SystemMonitorDatabase db){
                                if(db!=null){

                                    String textToAdd = "\n\tTOTAL DETECTED TIME :\t"+getTimeString(db.statsDao().totalHourUseNumber(timePeriod));
                                    textToAdd += "\n\n\tTOTAL BACKGROUND TIME :\t"+getTimeString(db.statsDao().totalBackgroundTime(timePeriod));
                                    textToAdd += "\n\n\tTOTAL FOREGROUND TIME :\t"+getTimeString(db.statsDao().totalForegroundTime(timePeriod));
                                    textToAdd += "\n\n\tTOTAL APP DETECTED :\t"+db.statsDao().totalAppNumber(timePeriod)+"\n\n";
                                    return textToAdd;
                                }
                                return null;
                            }

                            public String getTimeString(long time ){

                                long millis = time % 1000;
                                long second = (time / 1000) % 60;
                                long minute = (time / (1000 * 60)) % 60;
                                long hour = (time / (1000 * 60 * 60)) % 24;
                                //System.out.println("hour : "+hour);
                                String desc = " ";

                                if(hour>0)
                                    desc += String.format("%02d h ",hour);
                                if(minute>0)
                                    desc += String.format("%02d min ",minute);

                                desc += String.format("%02d sec %d ms",second, millis);

                                return desc;
                            }

                        }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }


    public String generateFile(final Context context, String sFileName, String sBody) {

            File f = new File (context.getFilesDir(), sFileName );
            FileProvider file = new FileProvider();
            String fileContents = "Hello world!";
            FileOutputStream outputStream;
            try {
                outputStream = context.openFileOutput(sFileName, Context.MODE_PRIVATE);
                outputStream.write(sBody.getBytes());
                outputStream.close();
                System.out.println("IL FILE é STATO CREATO IN :"+f.toURI());
                return sFileName;

            } catch (IOException e) {

                System.out.println("FILE NON è STATO SCRITTO ATTENZIONE "+e.getMessage());
                e.printStackTrace();
                return null;

            }

        }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, " ReportService Created ", Toast.LENGTH_LONG).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null){
            System.out.println("ricevuto alert con intent "+intent.toString());
            email = intent.getStringExtra("emailkey");
            System.out.println("XXXX Email a cui inviare è "+email);
            if(email==null){
                return super.onStartCommand(intent, flags, startId);
            }

            sendMessage();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "ReportService Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

}