package com.salvatorefiorilla.systemmonitor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.IntentService;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.salvatorefiorilla.systemmonitor.room.ApplicationStats;
import com.salvatorefiorilla.systemmonitor.room.DaoInterface;
import com.salvatorefiorilla.systemmonitor.room.SystemMonitorDatabase;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.os.Process.myUid;

public class ConfigService extends Service {

    SystemMonitorDatabase db;

    public ConfigService(){
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, " ConfigService Created ", Toast.LENGTH_LONG).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        detectService();
        Toast.makeText(this, " ConfigService Started", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Toast.makeText(this, "ConfigService Stopped", Toast.LENGTH_SHORT).show();
    }

    synchronized private void detectService(){

        final Context context = getApplicationContext();
            System.out.println("Config service runs!! ");

            db = Room.databaseBuilder(getApplicationContext(),
                    SystemMonitorDatabase.class, "systemmonitor").allowMainThreadQueries().build();


            //ri controllo perchè può essere che nel frattempo il permesso è stato tolto
            if (checkForPermission(context)) {
                Log.d("Start","Start config service method");
                long start = getLastTimestampFromDB();
                long end = System.currentTimeMillis();
                System.out.println("Start è "+start+" ");
                if (start == -1){
                    System.out.println("quindi lo metto 24 ore prima ");
                    start = end - (long) 86400000;
                }else{
                    start ++;
                }//per il db vuoto la prima volta

                StatisticsCalculator sc = new StatisticsCalculator(context);
                TreeMap<String, TreeMap<String, Stats>> treemap = sc.getAppTimeExecution(start, end);

                PackageManager pm = getPackageManager();
                for (TreeMap.Entry<String, TreeMap<String, Stats>> entry : treemap.entrySet()) {
                    String dateString = entry.getKey();
                    int m = 0;

                    for (TreeMap.Entry<String, Stats> statsEntry : entry.getValue().entrySet()){
                        String nameApp = statsEntry.getKey();
                        Stats stats = statsEntry.getValue();
                        long dtimestamp = Math.max(stats.getLastBackgroundTimeStamp(), stats.getLastForegroundTimeStamp());

                        String nameOfApplication= "";
                        try {
                            nameOfApplication = String.valueOf(getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(nameApp, 0)));
                        } catch (PackageManager.NameNotFoundException e) {
                            nameOfApplication = nameApp;
                            e.printStackTrace();
                            nameOfApplication = nameApp;
                        }finally {
                            if(getIfIsUnchecked(nameOfApplication,db)){
                                loadOnDb(stats, nameApp, dtimestamp,dateString);
                            }
                        }

                    }
                }

            } else {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        String message = "You need of permit check in settings > security! ";
                        Toast toast = Toast.makeText(context, message,Toast.LENGTH_LONG);
                        stopSelf();
                    }
                });

            }


        return;
    }

    private void loadOnDb(Stats stats, String nameApp, long dtimestamp,String dateStringDetect) {

        long bgtime = stats.getTimeBackgroung();
        long fgtime = stats.getTimeForeground();
        long lbgtime = stats.getLastBackgroundTimeStamp();
        long lfgtime = stats.getLastForegroundTimeStamp();

        //se le date sono uguali a l'ultima data allora faccio update, altrimenti faccio insert

        ApplicationStats as = new ApplicationStats();
        as.setDateTimeStamp(dtimestamp);
        as.setNameApp(nameApp);
        as.setBackgroundTime(bgtime);
        as.setForegroundTime(fgtime);
        as.setLastBackgroundTimestamp(lbgtime);
        as.setLastForegroundTimestamp(lfgtime);


        System.out.println("dentro load arriva prima di getLastTimeSTAMPFROMDB");
        long lastDetection = getLastTimestampFromDB();
        System.out.println("dentro load arriva dopo getLastTimeSTAMPFROMDB");

        if(lastDetection == -1){//se non c'è la metto due giorni prima così direttamente inserisco.. se c'è di 4 giorni fa e le date sono diverse inserisco
            lastDetection = ( System.currentTimeMillis()-((long) 86400000 * (long) 2) );
        }

        Date lastDateDetection = new Date(lastDetection);
        DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String dateLastDetection = df.format(lastDateDetection);

        //long maxTS = Math.max(as.getLastBackgroundTimestamp(),as.getLastForegroundTimestamp());
        //boolean datesAreEquals = dateStringDetect.equalsIgnoreCase(df.format(new Date(maxTS)));

        boolean datesAreEquals = dateLastDetection.equalsIgnoreCase(dateStringDetect);
        //Log.d("UFO","dateLastDetection: "+dateLastDetection+", df.format()"+df.format(new Date(maxTS))+" ==? "+datesAreEquals);
        Log.d("UFO","dateLastDetection: "+dateLastDetection+", dateStringDetect"+dateStringDetect+" ==? "+datesAreEquals);
        Log.d("UFO","as = "+as.toString());



            if(datesAreEquals)
            {
                System.out.println("Aggiorno_ update !! ");
                updateItem(as,dateStringDetect);
            }else{
                System.out.println("inserisco item !! ");
                insertItem(as);
            }

    }

    private long getLastTimestampFromDB() {

        final DaoInterface agent = (DaoInterface) db.statsDao();
        long maxForeground = -1 , maxBackground = -1 ;

        //try {

            /*return
                    new AsyncTask<Void, Void, Long>() {
                @Override
                protected Long doInBackground(Void... voids) {
                    return getMaxTimestam();
                }

                private Long getMaxTimestam() {
                    */ /*/
                    long[] getAllForegroundTimestamp = agent.getAllForegroundTimestamp();
                    long[] getAllBackgroundTimestamp = agent.getAllBackgroundTimestamp();
                    for(int i =0; i<getAllBackgroundTimestamp.length ; i++){
                        System.out.println("getAllBackgroundTimestamp["+i+"]" + getAllBackgroundTimestamp[i]);
                    }
                    for(int i =0; i<getAllForegroundTimestamp.length ; i++){
                        System.out.println("getAllForegroundTimestamp["+i+"]" + getAllForegroundTimestamp[i]);
                    }
                    /*/

                    ApplicationStats appStatsLastForegroundTimestamp = agent.getLastForegroundTimestamp();
                    ApplicationStats appStatsLastBackgroundTimestamp = agent.getLastBackgroundTimestamp();
                    if(appStatsLastForegroundTimestamp != null ){
                        maxForeground = appStatsLastForegroundTimestamp.getLastForegroundTimestamp();
                    }
                    if(appStatsLastBackgroundTimestamp != null){
                        maxBackground = appStatsLastBackgroundTimestamp.getLastBackgroundTimestamp();
                    }
                    System.out.println(" last foreground ts: " + maxForeground );
                    System.out.println(" last background ts: " + maxBackground );
                    //return Math.max(appStatsLastBackgroundTimestamp.getLastBackgroundTimestamp(),appStatsLastForegroundTimestamp.getLastForegroundTimestamp());
                    return Math.max(maxBackground,maxForeground);
                /*//}
            //}.execute().get();
                } catch (InterruptedException e) {
            return -1;
            //e.printStackTrace();

        } catch (ExecutionException e) {
            return -1;
            //e.printStackTrace();

        }*/
    }

    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }



    @SuppressLint("StaticFieldLeak")
    private void insertItem(final ApplicationStats as) {

        if(as!=null){
            final DaoInterface agent = db.statsDao();
            //Log.d("Insert ","Sto inserendo in db :"+as.getNameApp());
            /*new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {*/
                    agent.insertAll(as);
                /*    return null;
                }
            }.execute();*/
            }
        return;

        }

    @SuppressLint("StaticFieldLeak")
    private void updateItem(final ApplicationStats as, String dateToQuery) {
        //qui puà accadere che è una nuova app che ho utilizzato oggi oppure no se non lo è vuol dire che aa non c'è

        if(as!=null){
            final DaoInterface agent = db.statsDao();
            //Log.d("Insert ","Sto inserendo in db :"+as.getNameApp());
            /*new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
             */
                    Log.d("NewApp info to add db",
                            "Nome app : "+as.getNameApp()
                                +"\n DateTimeStamp: "+as.getDateTimeStamp()
                                +"\n BackgroundTime: "+as.getBackgroundTime()
                                +"\n ForegroundTime: "+as.getForegroundTime()
                                +"\n LastBackgroundTimestamp: "+as.getLastBackgroundTimestamp()
                                +"\n LastForegroundTimestamp: "+as.getLastForegroundTimestamp());

                    //ci vuole la data del giorno del timestamp
                    System.out.println("leggo se ho una app sul db di nome  "+as.getNameApp()+" e data "+dateToQuery+" ==  [Query] "+getTimeStampForQuery(as.getDateTimeStamp()) );


//                    getTimeStampForQuery(as.getDateTimeStamp());




                    ApplicationStats aa = agent.getApplicationStats(as.getNameApp(),getTimeStampForQuery(as.getDateTimeStamp()));
                    System.out.println("ho trovat l'app che é già sul db, non è null ? "+(aa!=null) +" (se è true entra, se è false vuol dire : che \n1 le date fra ultima rilevazione e quella del calcolo sono uguali,\n2 ma nel db NON ci sono app tonate dalla query su)");

                    if(aa!=null){
                        agent.deleteApplicationStats(aa);
                        //se è andato a buon fine allora
                        //bb è null
                        ApplicationStats bb = agent.getApplicationStats(as.getNameApp(), getTimeStampForQuery(as.getDateTimeStamp()));
                        if(bb==null){
                            System.out.println("cancellazione è andato a buon fine ");
                        }else
                        {
                            System.out.println("cancellazione NON è andato a buon fine");
                            /*Log.d("caricata dal db" ,
                                    "App BB : "+bb.getNameApp()
                                            +"\n DateTimeStamp: "+bb.getDateTimeStamp()
                                            +"\n BackgroundTime: "+bb.getBackgroundTime()
                                            +"\n ForegroundTime: "+bb.getForegroundTime()
                                            +"\n LastBackgroundTimestamp: "+bb.getLastBackgroundTimestamp()
                                            +"\n LastForegroundTimestamp: "+bb.getLastForegroundTimestamp());*/
                        }

                        Log.d("load from db" ,
                                    "App ancora che devo aggiornare con i nuovi valori AA : "+aa.getNameApp()
                                            +"\n DateTimeStamp: "+aa.getDateTimeStamp()
                                            +"\n BackgroundTime: "+aa.getBackgroundTime()
                                            +"\n ForegroundTime: "+aa.getForegroundTime()
                                            +"\n LastBackgroundTimestamp: "+aa.getLastBackgroundTimestamp()
                                            +"\n LastForegroundTimestamp: "+aa.getLastForegroundTimestamp());


                        aa.setBackgroundTime((aa.getBackgroundTime()+as.getBackgroundTime()));
                        aa.setForegroundTime((aa.getForegroundTime()+as.getForegroundTime()));
                        aa.setLastBackgroundTimestamp(as.getLastBackgroundTimestamp());
                        aa.setLastForegroundTimestamp(as.getLastForegroundTimestamp());
                        aa.setDateTimeStamp(as.getDateTimeStamp());

                        //agent.deleteApplicationStats(aa);
                        //sto caricando sul db
                        Log.d("caricand sul db" ,
                                "App aggiornata  che sto per fare insert su db: "+aa.getNameApp()
                                        +"\n DateTimeStamp: "+aa.getDateTimeStamp()
                                        +"\n BackgroundTime: "+aa.getBackgroundTime()
                                        +"\n ForegroundTime: "+aa.getForegroundTime()
                                        +"\n LastBackgroundTimestamp: "+aa.getLastBackgroundTimestamp()
                                        +"\n LastForegroundTimestamp: "+aa.getLastForegroundTimestamp());

                        insertItem(aa);

                    }else{
                        System.out.println("inserisco l'app nel db perchè oggi non è stata ancora inserita");
                        insertItem(as);
                    }
                    /*return null;
                }
            }.execute();*/

            //db.close();
        }

        return;

    }

    private long getTimeStampForQuery(long dateTimeStamp) {
        long time = 0;
        //al timestamp vedo la data e moltiplico per il num di millisecondi e poi lo sottraggo
        Date dateOfEvent = new Date( dateTimeStamp );
        java.text.DateFormat df = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String[] d = df.format(dateOfEvent).split(" ");
        String day = d[0];
        String[] dd = d[3].split(":");
        String hour = dd[0];
        String min = dd[1];
        String sec = dd[2];

        time += (Long.parseLong(hour)* (long) (3600000));
        time += (Long.parseLong(min) * (long) (60000));
        time += (Long.parseLong(sec) * (long) 1000);
        /*System.out.println("Data di oggi = "+df.format(dateOfEvent));
        System.out.println("ore = "+hour);
        System.out.println("min = "+min);
        System.out.println("sec = "+sec);*/
        long t = dateTimeStamp - time;
        System.out.println("Data di oggi = "+df.format(dateOfEvent));
        System.out.println("prova che t è la data di oggi alle 00 ==> "+(df.format(new Date(t))) );
        return t;
    }


    private boolean getIfIsUnchecked(final String name, final SystemMonitorDatabase db) {
        return ! db.preferencesDao().getIfIsChecked(name);
    }


    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
 /*    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {

        new MyServiceThread().start();
        return ;
    }

   class MyServiceThread extends Thread{

        public MyServiceThread(){
            super();
        }

        @Override
        public void run() {
            super.run();
            detectService();
            try {

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new MyServiceThread().start();
                    }
                }, TIME_TO_SLEEP);
            }catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("FinishService", "Config services execution terminated !");
        }

    }
*/
}
