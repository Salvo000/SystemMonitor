package com.salvatorefiorilla.systemmonitor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
//import android.widget.ShareActionProvider;


//import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.salvatorefiorilla.systemmonitor.room.ApplicationStats;
import com.salvatorefiorilla.systemmonitor.room.DaoInterface;
import com.salvatorefiorilla.systemmonitor.room.SystemMonitorDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.os.Process.myUid;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener, NavigationView.OnNavigationItemSelectedListener {

    final private long TIME_TO_SLEEP = 1000 * 90;// 1.5 minutes in milliseconds;
    private MainFragments mainFragment;
    private RadioGroup mRadioGroup;
    private ShareActionProvider mShareActionProvider;
    private SharedPreferences sharedpreferences;
    final private String EmailKey ="emailkey";
    final private String SwitchKey ="switchkey";
    final private String ToggleKey = "togglekey";
    final private String RepeatingTimeKey = "repeatingtimeKey";
    private PendingIntent mIntent;
    public static final String mypreference = "mypref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println(">>>Lancio ON CREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroupTimeFilter);
        mRadioGroup.setOnCheckedChangeListener(this);
        FragmentManager fm = getFragmentManager();
        mainFragment = (MainFragments) fm.findFragmentById(R.id.main_fragment);

        if (savedInstanceState == null) {
            //Log.d("TAG", "ON CREATE savedistancestate is null: ");

            if (!checkForPermission(this)) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("The application needs of USAGE STATS permits")
                        .setMessage("To continue you need to grant permits to System Monitor.\nClicking on \"Redirect\" you will be redirect to setting.");
                dialog.setPositiveButton("Redirect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if(!checkForPermission(getApplicationContext())){
                            Intent i = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                        dialog.dismiss();
                        finish();
                        System.exit(0);
                    }
                });
                final AlertDialog alert = dialog.create();
                alert.show();
            }else{

                Toast.makeText(getApplicationContext(), "loading date", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), ConfigService.class);
                PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, i, 0);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, TIME_TO_SLEEP, pintent);
                startService(i); //cosi parte subito
            }

        }

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            TextView lab = (TextView) findViewById(R.id.tw_stats);
            lab.setVisibility(View.GONE);
        } else {
            // In portrait
            TextView lab = (TextView) findViewById(R.id.tw_stats);
            lab.setVisibility(View.VISIBLE);
        }

        mainFragment.setCardList();

    }

    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, ToolsActivity.class);
            this.startActivity(i);
            return true;
        } else if (id == R.id.action_deleteDb) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Are you sure, you want delete all records ? ")
                    .setMessage("all elements will be deleted from db ")
                    .setPositiveButton("delete db content", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AsyncTask<Void, Void, Boolean>() {
                                        @Override
                                        protected Boolean doInBackground(Void[] objects) {

                                            SystemMonitorDatabase db = Room.databaseBuilder(getApplicationContext(), SystemMonitorDatabase.class, "systemmonitor").allowMainThreadQueries().build();
                                            ArrayList<ApplicationStats> apps = (ArrayList<ApplicationStats>) db.statsDao().loadAllAppllicationStats();
                                            for (ApplicationStats a : apps) {
                                                db.statsDao().deleteApplicationStats(a);
                                            }
                                            //System.out.println("Cancellazione effettuata ");
                                            //List<ApplicationStats> list = db.statsDao().loadAllAppllicationStats();
                                            //System.out.println("Dopo lettura num record trovati is " + list.size());
                                            return true;
                                        }
                                    }.execute();
                                }
                            }
                    )
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

            return true;
        }else if( id == R.id.action_sendReport){
            showDialog();
            return true;
        }else if(id == R.id.menu_item_share){
            setShareButton();
            return true;
        }else if(id == R.id.menu_item_graph){
            RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radioGroupTimeFilter);
            RadioButton radioChecked = (RadioButton) findViewById( mRadioGroup.getCheckedRadioButtonId());
            showGraph(radioChecked);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("UFO", "Nothing Item Selected");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton checked = (RadioButton) findViewById(checkedId);
        mainFragment.setCardList();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_camera:
                break;
            case R.id.nav_gallery:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
            default:
                setButtons();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setButtons() {
        //here set allButtons of DrawingExamples
    }

    private void showGraph(final RadioButton checked){

        new AsyncTask<Void,Void,Void>(){
            AlertDialog dialog;
            View mView;
            AlertDialog.Builder mBuilder;
            BarChart mChart;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mBuilder = new AlertDialog.Builder(MainActivity.this);
                mView = getLayoutInflater().inflate(R.layout.content_graph_layout, null);
            }

            @Override
            protected Void doInBackground(Void... voids) {


                mChart = (BarChart) mView.findViewById(R.id.chart);
                mChart.setDrawGridBackground(false);
                SystemMonitorDatabase db = Room.databaseBuilder(getApplicationContext(),SystemMonitorDatabase.class, "systemmonitor").allowMainThreadQueries().build();
                final DaoInterface agent = db.statsDao();


                long timeToQuery;
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

                ArrayList<ApplicationStats> myApp = (ArrayList<ApplicationStats>)
                        agent.queryLastTime(timeToQuery);
                //System.out.println("-------prese dal db elementi size myApp "+myApp.size());

                List<BarEntry> entries = new ArrayList<BarEntry>();
                BarData barData = new BarData();

                int i = 0;
                for (ApplicationStats data : myApp) {
                    Long ttu = data.getBackgroundTime()+data.getForegroundTime();
                    //entries.add(new BarEntry(i++,ttu));
                    List<BarEntry> ll = new ArrayList<BarEntry>();
                    ll.add(new BarEntry(i++,ttu));
                    PackageManager pm = getPackageManager();
                    String name = null;

                    try {
                        name = (String) pm.getApplicationLabel(pm.getApplicationInfo(data.getNameApp(),0));
                    } catch (PackageManager.NameNotFoundException e) {
                        name = data.getNameApp();
                    }
                    BarDataSet bds = new BarDataSet(ll ,name);

                    bds.setColor(randomColor());
                    bds.setValueTextColor(Color.BLACK); // styling, ...
                    barData.addDataSet(bds);


                }

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mChart.setMinimumWidth(1450);
                    mChart.setMinimumHeight(950);
                }else {
                    mChart.setMinimumHeight(1450);
                    mChart.setMinimumWidth(950);
                }



                mChart.setData(barData);
                mChart.setAutoScaleMinMaxEnabled(false);
                mChart.invalidate();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);



                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        }.execute();

    }

    private int randomColor() {
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return Color.rgb(r, g, b);
    }

    private void showDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        final EditText mailTo = (EditText) mView.findViewById(R.id.etMailTo);
        final RadioGroup mRadioGroupRepeatingTime = (RadioGroup) mView.findViewById(R.id.radioGroupRepeatingTime);
        final LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.layoutToSendReportRecursive);
        final ToggleButton tbutton = (ToggleButton) mView.findViewById(R.id.activeRecursiveButton);
        final Switch switchButton = (Switch) mView.findViewById(R.id.switch_recursive);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        //System.out.println("sharedpreferences.contains(SwitchKey) == "+sharedpreferences.contains(SwitchKey));

        if (sharedpreferences.contains(SwitchKey)) {
            switchButton.setChecked(sharedpreferences.getBoolean(SwitchKey,false));

            if(switchButton.isChecked()){
                linearLayout.setVisibility(View.VISIBLE);
                if(sharedpreferences.contains(EmailKey)){
                    mailTo.setText( sharedpreferences.getString(EmailKey,"") );
                }
                if(sharedpreferences.contains(ToggleKey)){
                    tbutton.setChecked(sharedpreferences.getBoolean(ToggleKey,false));
                }

                if(sharedpreferences.contains(RepeatingTimeKey)){
                    int child = sharedpreferences.getInt(RepeatingTimeKey, R.id.radioDay);
                    mRadioGroupRepeatingTime.check(child);
                }

            }else{
                linearLayout.setVisibility(View.INVISIBLE);
            }

        }else{
            switchButton.setChecked(false);
            linearLayout.setVisibility(View.INVISIBLE);
        }


        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged (CompoundButton buttonView,boolean isChecked){
                if (isChecked) {
                    linearLayout.setVisibility(View.VISIBLE);
                    if (sharedpreferences.contains(EmailKey)) {
                        mailTo.setText(sharedpreferences.getString(EmailKey, ""));
                        if (sharedpreferences.contains(ToggleKey)) {
                            tbutton.setChecked(sharedpreferences.getBoolean(ToggleKey, false));
                        }else{
                            tbutton.setChecked(false);
                        }

                    }//allora sicuro sarà tbutton come false..

                    if(sharedpreferences.contains(RepeatingTimeKey)){
                        int child = sharedpreferences.getInt(RepeatingTimeKey, R.id.radioDay);
                        System.out.println("indexChild = "+child);
                        mRadioGroupRepeatingTime.check(child);
                    }
                }else{

                    linearLayout.setVisibility(View.INVISIBLE);
                    // switch viene impostato a false..
                    // e il servizio deve terminare,
                    // tutto deve essere salvato
                    tbutton.setChecked(false);
                    //stopService(new Intent(getApplicationContext(), ReportService.class));
                    //stopReportService(); forse non serve
                }

                saveSharedPreferences(isChecked,mRadioGroupRepeatingTime.getCheckedRadioButtonId(),mailTo.getText().toString(),tbutton.isChecked());


            }
        });


        mailTo.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {

            }
            public void afterTextChanged(Editable c) {
                // this one too
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove(EmailKey);
                editor.remove(ToggleKey);
                editor.putString(EmailKey,c.toString());
                editor.putBoolean(ToggleKey,false);
                editor.commit();
                tbutton.setChecked(false);
            }
        });

        mRadioGroupRepeatingTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove(RepeatingTimeKey);
                editor.remove(ToggleKey);
                editor.putInt(RepeatingTimeKey,checkedId);
                editor.putBoolean(ToggleKey,false);
                editor.commit();
                tbutton.setChecked(false);
            }
        });

        tbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Pattern pattern = Pattern.compile("^[A-Za-z0-9._]{1,50}+@{1}+[A-Za-z0-9._]{1,50}\\.[a-z]{1,3}$");
                String e = mailTo.getText().toString();
                final Matcher mail = pattern.matcher(e);

                if (mail.find()) {
                    if (isChecked) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Service Repeating report active!")
                                        .setMessage("you will receive a report ");

                                dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        dialog.dismiss();
                                    }
                                });

                                dialog.create();
                                dialog.show();

                            }
                        });
                        startReportService();

                    } else {
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Service report was stopped")
                                .setMessage("You will not receve a periodic report in your email");
                        dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                        final AlertDialog alert = dialog.create();
                        alert.show();

                        // Hide after some seconds
                        final Handler handler = new Handler();
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (alert.isShowing()) {
                                    alert.dismiss();
                                }
                            }
                        };

                        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                handler.removeCallbacks(runnable);
                            }
                        });

                        stopReportService();
                        handler.postDelayed(runnable, 1000);

                    }

                } else {

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Email not good")
                            .setMessage("Email format error or email too long");
                    dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog alert = dialog.create();
                    alert.show();

                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (alert.isShowing()) {
                                alert.dismiss();
                            }
                        }
                    };

                    alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (tbutton.isChecked()) {
                                tbutton.setChecked(false);
                            }
                            handler.removeCallbacks(runnable);
                        }
                    });

                    tbutton.setChecked(false);
                    handler.postDelayed(runnable, 1000);
                }
                saveSharedPreferences(switchButton.isChecked(),mRadioGroupRepeatingTime.getCheckedRadioButtonId(),mailTo.getText().toString(),isChecked);

            }
        });

        final ImageButton closeButton = (ImageButton) mView.findViewById(R.id.imageClose);
        closeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                dialog.dismiss();
            }
            });
        dialog.show();
        return;
    }

    private void saveSharedPreferences(boolean switchStatusButton, int childRadioGroupChecked, String mailTo, boolean toogleStatusButton) {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(SwitchKey);
        editor.remove(RepeatingTimeKey);
        editor.remove(EmailKey);
        editor.remove(ToggleKey);
        editor.putBoolean(ToggleKey,toogleStatusButton);
        editor.putString(EmailKey,mailTo);
        editor.putInt(RepeatingTimeKey,childRadioGroupChecked);
        editor.putBoolean(SwitchKey, switchStatusButton);
        editor.commit();
    }

    private void startReportService() {

        String email = sharedpreferences.getString(EmailKey,"").trim();
        int rButtonChecked = sharedpreferences.getInt(RepeatingTimeKey,R.id.radioDay);
        long TIME_TO_SLEEP_REPORT;
        switch (rButtonChecked){
            case R.id.radio6Hour:
                TIME_TO_SLEEP_REPORT = 1000 * 60 * 60 * 6 ; //every 6 hours
                break;
            case R.id.radioDay:
                TIME_TO_SLEEP_REPORT = 1000 * 60 * 60 * 24 ; //every day
                break;
            case R.id.radioWeek:
                TIME_TO_SLEEP_REPORT = 1000 * 60 * 60 * 24 * 7 ; //every week
                break;
            default://daily..
                TIME_TO_SLEEP_REPORT = 1000 * 60 * 60 * 24 ; //every 6 hours
                break;
        }

        Intent myIntent = new Intent(getApplicationContext(), ReportService.class);
        myIntent.putExtra(EmailKey, email);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(myIntent != null)
            alarmManager.cancel(mIntent);

        mIntent = PendingIntent.getService(this, ReportService.SERVICE_ID, myIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000,TIME_TO_SLEEP_REPORT,mIntent);
    }

    private void stopReportService(){
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        System.out.println("sto per lanciare cancell su ==> "+mIntent);
                if (mIntent!=null) {
                    System.out.println("lancio cancel "+mIntent.toString());
                    am.cancel(mIntent);

                }else{
                    System.out.println("non lancio cancel perchè è null");
                }
            stopService(new Intent(this,ReportService.class));
        mIntent = null;
    }

    private  void setShareButton() {

        Log.i("Send email", "");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setData(Uri.parse("content :"));
        shareIntent.setType("text/* application/*");

        String body="File report was created from SystemMonitor.";
        String filename = createReport();
        if(filename == null){
            return;
        }
        File filelocation = new File(getFilesDir(), filename);
        Uri path = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", filelocation);
        System.out.println("path readed is : "+path.toString());
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent , "Chose kind to send your report status"));

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

                            return generateFile(MainActivity.this, "report.txt", text);
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
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(sFileName, Context.MODE_PRIVATE);
                outputStream.write(sBody.getBytes());
                outputStream.close();
                System.out.println("IL FILE é STATO CREATO IN :"+f.toURI());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Success: File created", Toast.LENGTH_SHORT).show();
                    }
                });
                return sFileName;

            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Error: File NOT created", Toast.LENGTH_SHORT).show();
                    }
                });
                System.out.println("FILE NON è STATO CREATO ATTENZIONE "+e.getMessage());
                e.printStackTrace();
                return null;
            }
    }
}