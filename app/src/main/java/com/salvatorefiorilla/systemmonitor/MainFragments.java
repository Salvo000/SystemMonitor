package com.salvatorefiorilla.systemmonitor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
//import android.support.v4.app.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.usage.UsageStatsManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.salvatorefiorilla.systemmonitor.room.AppList;
import com.salvatorefiorilla.systemmonitor.room.ApplicationStats;
import com.salvatorefiorilla.systemmonitor.room.DaoInterface;
import com.salvatorefiorilla.systemmonitor.room.SystemMonitorDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import static android.content.Context.ACTIVITY_SERVICE;

public class MainFragments extends Fragment {

    private static final int GET_APP_RUNNING_IN_BACKGROUND = 1;
    private static final int GET_APP_USE = 0;
    private static final int GET_APP_USING_MEMORY = 3;
    private static final int GET_STATS= 2;
    public MainFragments(){ }

    private RecyclerView.Adapter cardAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Card> cardList;
    SystemMonitorDatabase db;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("ON CREATE FRAGMENTS");
        super.onCreate(savedInstanceState);
    }


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

        System.out.println("ON CREATE VIEW FRAGMENTS");
        View v =  inflater.inflate(R.layout.content_main, container, false);

        if (savedInstanceState==null){
            Log.d("TAG", "ON CREATE VIEW FRAGMENTS savedistancestate is null: ");
        }
        if(cardList == null){
            cardList = new ArrayList<Card>();
        }
        mRecyclerView = (RecyclerView) v.findViewById(R.id.cardRV);
        //mRecyclerView.setHasFixedSize(true);
        cardAdapter = new AdapterForCard(v.getContext(), cardList);;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(cardAdapter);
        db = Room.databaseBuilder(getActivity().getApplicationContext(),SystemMonitorDatabase.class, "systemmonitor").allowMainThreadQueries().build();
        if(savedInstanceState!= null){
            int pos = savedInstanceState.getInt("posofSpinner");
            Log.d("TAG", "ON CREATE VIEW FRAGMENTS savedistancestate is not null, pos: "+pos);
        }
        return v ;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        System.out.println("ON VIEW CREATED FRAGMENTS");
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    public void getAppUse(final RadioButton checked) {

            new AsyncTask<Void, Void, Void>() {

                private Activity myself;
                @Override
                synchronized protected  Void doInBackground(Void... voids) {

                        if(cardList.size()>0){
                            cardList.clear();
                            publishProgress();
                        }
                        if(myself == null ){
                            myself = getActivity();
                        }

                        if(myself!= null) {


                            System.out.println("GET APP USED monitor task runs! size = " + cardList.size());
                            PackageManager pm = myself.getPackageManager();
                            final DaoInterface agent = db.statsDao();
                            ArrayList<Card> cards = new ArrayList<Card>();
                            List<ApplicationStats> myApp = new ArrayList<ApplicationStats>();
                            Log.d("UFO", "selezionato: " + checked.getText());
                            long timeToQuery;//= System.currentTimeMillis() - (long) 86400000;

                            switch ((String) checked.getText()) {
                                case "Last month":
                                    timeToQuery = System.currentTimeMillis() - (long) 86400000 * 30;

                                    break;
                                case "Last year":
                                    timeToQuery = System.currentTimeMillis() - (long) 86400000 * 365;
                                    break;
                                case "Last 24h":
                                    timeToQuery = System.currentTimeMillis() - (long) 86400000;
                                    break;
                                default:
                                    timeToQuery = System.currentTimeMillis() - (long) 86400000;
                                    Log.d("UFO", "No match button");
                                    break;
                            }
                            long ttu = agent.totalHourUseNumber(timeToQuery);
                            //int tan = agent.totalAppNumber(timeToQuery);

                            String textToAdd ="";
                            textToAdd += "\n\t\tAPPS DETECTED:\t\t" + agent.totalAppNumber(timeToQuery);
                            textToAdd += "\n\n\t\tTOTAL DETECTED TIME :\t" +
                                    String.format("%s", getTimeString(ttu));
                            textToAdd += "\n\n\t\tTOTAL BACKGROUND TIME :\t" +
                                    String.format("%s",getTimeString(agent.totalBackgroundTime(timeToQuery)));
                            textToAdd += "\n\n\t\tTOTAL FOREGROUND TIME :\t" +
                                    String.format("%s",getTimeString(agent.totalForegroundTime(timeToQuery)))+"\n";
                            //"\t\t\t\t\t\t\t\t\t\t\t\t" + getTimeString(agent.totalForegroundTime(timeToQuery))+"\n";


                            myApp = agent.queryLastTime(timeToQuery);

                            if (myApp.size() == 0) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final AlertDialog.Builder d = new AlertDialog.Builder(getActivity())
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle("Alert")
                                                .setMessage("Nothing was detect")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        System.out.println("clicked ok from dialog");
                                                        dialog.dismiss();
                                                    }
                                                });

                                        final AlertDialog aa = d.create();
                                        aa.show();
                                        // Hide after some seconds
                                        final Handler h = new Handler();
                                        final Runnable r = new Runnable() {
                                            @Override
                                            public void run() {
                                                if (aa.isShowing()) {
                                                    aa.dismiss();
                                                }
                                            }
                                        };

                                        aa.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                h.removeCallbacks(r);
                                            }
                                        });
                                        h.postDelayed(r, 1000);
                                    }
                                });

                            }

                            System.out.println("NUM OF APP READ FROM DB MYAPP.size = " + myApp.size());
                            //Log.d("TAG", "Build.VERSION.SDK_INT (" + Build.VERSION.SDK_INT + ")>= Build.VERSION_CODES.N (" + Build.VERSION_CODES.N + ") == " + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N));
                            int h = 0;
                            for (ApplicationStats as : myApp) {
                                try {
                                    String packageName = as.getNameApp();
                                    String nameOfApplication = "" + pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0));

                                        String title = "\t" + (h + 1) + ".  " + nameOfApplication;
                                        long time = (as.getBackgroundTime() + as.getForegroundTime());
                                        long millis = time % 1000;
                                        long second = (time / 1000) % 60;
                                        long minute = (time / (1000 * 60)) % 60;
                                        long hour = (time / (1000 * 60 * 60)) % 24;
                                        String desc = "Total use : ";
                                        if (hour > 0)
                                            desc += String.format("%02d h ", hour);
                                        if (minute > 0)
                                            desc += String.format("%02d min ", minute);

                                        desc += String.format("%02d sec %d ms", second, millis);

                                        if(getIfIsUnchecked(nameOfApplication,db)){
                                           desc +="\n\nDatas aren't updated, this app monitoring is turn off\n";
                                        }
                                        Drawable icon = pm.getApplicationIcon(packageName);
                                        //System.out.println("("+ttu+") / "+time+" == "+(ttu/time));
                                        //System.out.println("time * 100 / ttu == "+"( "+time+" ) * 100 / "+ttu +"= "+(time * 100));

                                        int rate = ((int) (time * 100)) / ((int) ttu);
                                        //System.out.println("("+ttu+") / "+time+" == "+(ttu/time)+" * 100 = "+rate);
                                        Card c = new Card(packageName, icon, title, desc, (rate + 8));
                                        //cardList.add(c);


                                    cards.add(c);
                                    System.out.println("giro num " + (h) + " app: " + c.toString() + " as Date: " + as.getDateTimeStamp());
                                    h++;

                                } catch (PackageManager.NameNotFoundException e) {
                                    System.out.println("catturata eccezzione nome not found giro " + (h) + " " + e.getMessage());
                                    //e.printStackTrace();
                                    h++;
                                }

                            }
                            final String finalTextToAdd = textToAdd;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView tw_stats = getActivity().findViewById(R.id.tw_stats);
                                    if (tw_stats != null ) {
                                        tw_stats.setTextColor(Color.BLACK);
                                        //tw_stats.setHeight(5);
                                        tw_stats.setTypeface(tw_stats.getTypeface(), Typeface.BOLD);
                                        tw_stats.setText(finalTextToAdd);
                                        tw_stats.setTextColor(getActivity().getResources().getColor(R.color.colorItem));
                                        tw_stats.setMovementMethod(new ScrollingMovementMethod());

                                        int orientation = getResources().getConfiguration().orientation;
                                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                            // In landscape
                                            tw_stats.setVisibility(View.GONE);
                                        } else {
                                            tw_stats.setVisibility(View.VISIBLE);
                                            // In portrait
                                        }
                                    }
                                }
                            });

                            cardList.addAll(cards);
                            publishProgress();

                        }

                    return null;
                }

                private boolean getIfIsUnchecked(final String name, final SystemMonitorDatabase db) {
                    return db.preferencesDao().getIfIsChecked(name);
                }



                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                    cardAdapter.notifyDataSetChanged();
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    System.out.println("Sono su on post execute");
                    try {
                        //Looper.getMainLooper();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //devo prendere pure la posizione dello spinner..
                                //myself = getActivity();
                                if(myself!=null){
                                    //Spinner mSpinner = (Spinner) myself.findViewById(R.id.spinnerFilter);
                                    //int pos = mSpinner.getSelectedItemPosition();
                                    //System.out.println("pos == "+pos);
                                    //if(pos == 0){
                                        RadioGroup mRadioGroup = (RadioGroup) myself.findViewById(R.id.radioGroupTimeFilter);
                                        RadioButton radioChecked = (RadioButton) myself.findViewById( mRadioGroup.getCheckedRadioButtonId());
                                        if(((String)radioChecked.getText()).equals((String)checked.getText()))
                                            getAppUse(checked);
                                    //}
                                }

                                //System.out.println("Sto per lanciare da post delay => \ngetAppUsed con checked: "+(String) checked.getText());
                                //System.out.println("attualmente ho : "+(String)radioChecked.getText());
                                //System.out.println("che confronto con quello sopra ho : "+(String)radioChecked.getText());
                                //System.out.println("sono uguali ?? == "+(((String)radioChecked.getText()).equals((String)checked.getText())));

                            }
                        }, TIME_TO_SLEEP);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                private static final int TIME_TO_SLEEP = 30000;

            }.execute();
    }

    public void setCardList() {

        Log.d("UFOO","1_ cardlist Size  == "+cardList.size());
        RadioGroup mRadioGroup = (RadioGroup) getActivity().findViewById(R.id.radioGroupTimeFilter);
        RadioButton radioChecked = (RadioButton) getActivity().findViewById( mRadioGroup.getCheckedRadioButtonId());
        //TextView tw_stats = (TextView) getActivity().findViewById(R.id.tw_stats);
        cardList.clear();
        cardAdapter.notifyDataSetChanged();
        getAppUse(radioChecked);
        //tw_stats.setVisibility(View.VISIBLE);
         /*
        switch (position){
            case GET_APP_USE:
                Log.d("TAG","GET_APP_USE Checked radio = "+radioChecked.getText());

                break;
            case GET_APP_RUNNING_IN_BACKGROUND:
                getAppRunningInBackground();
                tw_stats.setVisibility(View.GONE);
                break;
          case GET_STATS:
                Log.d("TAG","GET_STATS Checked radio = "+radioChecked.getText());
                tw_stats.setVisibility(View.VISIBLE);
                getStats(radioChecked);
            break;
            case GET_APP_USING_MEMORY:
                getAppUsingMemory();
                break;
            default:
                Log.d("TAG","DEFAULT GET_APP_USE Checked radio = "+radioChecked.getText());
                getAppUse(radioChecked);
                tw_stats.setVisibility(View.VISIBLE);
                break;
        }*/

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
}


/*

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
        /*
        System.out.println("Data di oggi = "+df.format(dateOfEvent));
        System.out.println("ore = "+hour);
        System.out.println("min = "+min);
        System.out.println("sec = "+sec);

long t = dateTimeStamp - time;
        System.out.println("Data di oggi = "+df.format(dateOfEvent));
                System.out.println("prova che t è la data di oggi alle 00 ==> "+(df.format(new Date(t))) );
                return t;
                }

*/
/*

*/
    /**
     *
     * make cast from ApplicationStats to Card, turn null if Name not found exception occurs
     *
    private Card getCard(ApplicationStats as,PackageManager pm){
        Card c =  null;
        try {
            String packageName = as.getNameApp();
            String title = ""+pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0));

            long time = (as.getBackgroundTime() + as.getForegroundTime());
            long millis = time % 1000;
            long second = (time / 1000) % 60;
            long minute = (time / (1000 * 60)) % 60;
            long hour = (time / (1000 * 60 * 60)) % 24;

            String desc = "Total use : ";
            if(hour>0)
                desc += String.format("%02d h ",hour);
            if(minute>0)
                desc += String.format("%02d min ",minute);

            desc += String.format("%02d sec %d ms",second, millis);

            Drawable icon = pm.getApplicationIcon(packageName);
            c = new Card(packageName, icon, title, desc);


        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("catturata eccezzione nome not found " + e.getMessage());
            //e.printStackTrace();

        }finally {
            return c;
        }

    }
     */

 /*
  @SuppressLint("StaticFieldLeak")
    public void getStats(final RadioButton checked) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                System.out.println("GET STATS monitor task runs! size = " + cardList.size());
                PackageManager pm = getActivity().getPackageManager();
                DaoInterface agent = db.statsDao();
                List<ApplicationStats> myApp = new ArrayList<ApplicationStats>();
                Log.d("UFO", "selezionato: " + checked.getText());

                long timePeriod =(long) 86400000;

                switch ((String) checked.getText()) {
                    case "Last month":
                        timePeriod = (long)86400000 * 30;
                        break;
                    case "Last year":
                        timePeriod = (long)86400000 * 365;
                        break;
                    case "Last 24h":
                        break;
                    default:
                        Log.d("UFO", "No match button");
                        break;
                }

                int[] colors = new int[4];
                colors[0] = Color.rgb(255,215,0);
                colors[1] = Color.rgb(176,196,222);
                colors[2] = Color.rgb(205,88,50);
                colors[3] = Color.rgb(20,80,50);

                myApp = agent.bestThreeApp(timePeriod);
                 ArrayList<Card> cards = new ArrayList<Card>();
                int h = 0;
                for (ApplicationStats as : myApp) {
                    try {
                        String packageName = as.getNameApp();
                        String title = "" + (h + 1) + ".  " + pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0));
                        long time = (as.getBackgroundTime() + as.getForegroundTime());
                        long millis = time % 1000;
                        long second = (time / 1000) % 60;
                        long minute = (time / (1000 * 60)) % 60;
                        long hour = (time / (1000 * 60 * 60)) % 24;
                        String desc = ""+(h+1)+" Most used App!\nTotal use : ";

                        if(hour>0)
                            desc += String.format("%02d h ",hour);
                        if(minute>0)
                            desc += String.format("%02d min ",minute);

                        desc += String.format("%02d sec %d ms",second, millis);

                        Drawable icon = pm.getApplicationIcon(packageName);
                        Card c;
                        /*
                        if(h>=0 && h<4){
                            c = new Card(packageName, icon, title, desc,colors[h]);
                        }else {
                            c = new Card(packageName, icon, title, desc);
                                    /*}
                                    //cardList.add(c);
                                    cards.add(c);
                                    //System.out.println("giro num " + (h) + " app: " + c.toString() + "  as Date: " + as.getDateTimeStamp());
                                    h++;
                                    } catch (PackageManager.NameNotFoundException e) {
                                    System.out.println("catturata eccezzione nome not found giro " + (h) + " " + e.getMessage());
                                    //e.printStackTrace();
                                    h++;
                                    }
                                    }
                                    cardList.addAll(cards);
                                    publishProgress();

                                    myApp = (ArrayList<ApplicationStats>) agent.lessUsed(timePeriod);
        Card c = getCard(myApp.get(0),pm);
        String desc1= "Less used app!! \n\t"+c.getInfo();
        Card cc = new Card(c.getPackageName(),c.getIcon(),c.getTitle(),desc1);
        cardList.add(cc);
        publishProgress();

        String textToAdd = "";
        textToAdd += "\n\tAPPS DETECTED :\t"+agent.totalAppNumber(timePeriod);
        textToAdd += "\n\n\tTOTAL DETECTED TIME :\n\t\t"+getTimeString(agent.totalHourUseNumber(timePeriod));
        textToAdd += "\n\n\tTOTAL BACKGROUND TIME :\n\t\\t"+getTimeString(agent.totalBackgroundTime(timePeriod));
        textToAdd += "\n\n\tTOTAL FOREGROUND TIME :\n\t\\t"+getTimeString(agent.totalForegroundTime(timePeriod))+"\n";

final String finalTextToAdd = textToAdd;
        getActivity().runOnUiThread(new Runnable() {
@Override
public void run() {
        TextView tw_stats = getActivity().findViewById(R.id.tw_stats);
        if (tw_stats != null) {
        tw_stats.setTextColor(Color.BLACK);
        tw_stats.setTypeface(tw_stats.getTypeface(), Typeface.BOLD);
        tw_stats.setText(finalTextToAdd);
        tw_stats.setVisibility(View.VISIBLE);
        }
        }
        });


        //cardList.add(new Card("","TOTAL DETECTED TIME ",desc));
        publishProgress();

        return null;
        }

@Override
protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        cardAdapter.notifyDataSetChanged();
        }

@Override
protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        System.out.println("Sono su on post execute");
        try {
        //Looper.getMainLooper();
        new Handler().postDelayed(new Runnable() {
@Override
public void run() {
        //devo prendere pure la posizione dello spinner..
        Activity myself = getActivity();
        Spinner mSpinner = (Spinner) myself.findViewById(R.id.spinnerFilter);
        int pos = mSpinner.getSelectedItemPosition();
        System.out.println("pos == "+pos);
        if(pos == 2){
        RadioGroup mRadioGroup = (RadioGroup) myself.findViewById(R.id.radioGroupTimeFilter);
        RadioButton radioChecked = (RadioButton) myself.findViewById( mRadioGroup.getCheckedRadioButtonId());
        if(((String)radioChecked.getText()).equals((String)checked.getText()))
        getStats(checked);
        }
        //System.out.println("Sto per lanciare da post delay => \ngetAppUsed con checked: "+(String) checked.getText());
        //System.out.println("attualmente ho : "+(String)radioChecked.getText());
        //System.out.println("che confronto con quello sopra ho : "+(String)radioChecked.getText());
        //System.out.println("sono uguali ?? == "+(((String)radioChecked.getText()).equals((String)checked.getText())));

        }
        }, TIME_TO_SLEEP);
        }catch (Exception e) {
        e.printStackTrace();
        }
        }

private static final int TIME_TO_SLEEP = 30000;
        }.execute();
        }




    @SuppressLint("StaticFieldLeak")
    public void getAppUsingMemory() {

        if(isAdded()){
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {

                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
                    activityManager.getMemoryInfo(mi);
                    double availableMegs = mi.availMem / 0x100000L;
                    double totalMegs = mi.totalMem/ 0x100000L;
                    double usedMemory = totalMegs-availableMegs;
                    Card c = new Card(null,"Memory",
                            "total memory: "+totalMegs+"\tavailableMegs: "+availableMegs+"\ttotalMegs: "+usedMemory);

                    cardList.clear();
                    cardList.add(c);
                    cardAdapter.notifyItemInserted(cardList.size()-1);
                    return null;
                }
            }.execute();
        }

    }*/
/*

    @SuppressLint("StaticFieldLeak")
    public void getAppRunningInBackground(){

           new AsyncTask<Void, Void, Void>() {
                @Override
                synchronized protected  Void doInBackground(Void... voids) {

                    System.out.println("GET_APP_ROCESSRUNNING monitor task runs! ");
                    PackageManager pm = getActivity().getPackageManager();
                    Context context = getActivity().getApplicationContext();
                    ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> processRunning = am.getRunningAppProcesses();

                    for(ActivityManager.RunningAppProcessInfo process : processRunning){

                        String packageName = process.processName;
                        String title = packageName;
                        String desc ="";
                        Drawable icon =(Drawable) getView().getResources().getDrawable(R.drawable.ic_launcher);

                        //fare distinzione fra Applicazione e processo
                        try{
                            ApplicationInfo ai = pm.getApplicationInfo(packageName,0);
                            title= ""+pm.getApplicationLabel(pm.getApplicationInfo(packageName,0));
                            desc = "- Application";
                        }catch (PackageManager.NameNotFoundException e ){
                            title = "Process name: ";
                            desc = packageName;
                            //Log.d("NameNotFoundException", "not found name for package: "+packageName+"\nmessage"+e.getMessage());
                        }
                        try{
                            icon = pm.getApplicationIcon(packageName);
                        }catch (PackageManager.NameNotFoundException ee){
                            Log.d("NameNotFoundException", "not found icon for package: "+packageName+"\nmessage"+ee.getMessage());
                        }

                        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

                        Card c = new Card(packageName,icon,title,desc);
                        cardList.add(c);
                    }
                    //Caused by: android.view.ViewRootImpl$CalledFromWrongThreadException:
                    // Only the original thread that created a view hierarchy can touch its views.
                    Runnable action = new Runnable() {
                        @Override
                        public void run() {
                            cardAdapter.notifyDataSetChanged();
                        }
                    };
                    getActivity().runOnUiThread(action);
                    return null;
                }
            }.execute();
        //cardAdapter.notifyDataSetChanged();
    }*/


