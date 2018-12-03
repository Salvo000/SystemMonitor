package com.salvatorefiorilla.systemmonitor;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import com.salvatorefiorilla.systemmonitor.room.SystemMonitorDatabase;

import java.util.ArrayList;
import java.util.List;

public class ToolsActivity extends AppCompatActivity implements ListAppAdapter.ItemRowAdapterListener {

    //Cose da fare:
    private RecyclerView mRecyclerView;
    private ListAppAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ItemRow> mDataSet ;
    private List<ItemRow> mDataSetCopy ;
    private boolean dataSetCopied;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.preferencesRV);

        if(savedInstanceState == null){
            mDataSet = new ArrayList<ItemRow>();
            mDataSetCopy = new ArrayList<ItemRow>();
            this.dataSetCopied = false;
        }

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ListAppAdapter(mDataSet,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        getOptions(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.toolbar_title);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void getOptions(final Context context) {
        System.out.println("LANCIO GET OPTIONS");
        new AsyncTask<Void, Void, Void>() {
            ArrayList<ItemRow> mSet = new ArrayList<ItemRow>();
            @Override
            protected Void doInBackground(Void... voids) {

                SystemMonitorDatabase db = (SystemMonitorDatabase) Room.databaseBuilder(getApplicationContext(),
                        SystemMonitorDatabase.class, "systemmonitor").allowMainThreadQueries().build();
                PackageManager pm = context.getPackageManager();
                List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                //List<ItemRow> listApps = new ArrayList<ItemRow>();
                //System.out.println("totale app installate: "+apps.size());

                /*
                *
                * Flag update system app = viene impostato se questa applicazione è stata installata come aggiornamento a un'applicazione di sistema integrata.
                * Flag System = se impostato, questa applicazione viene installata nell'immagine di sistema del dispositivo.
                * Flag_Installed = true if the application is currently installed for the calling user.
                *
                * */
                for(ApplicationInfo app : apps){
                    if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) > 0 || (app.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 || (app.flags & ApplicationInfo.FLAG_INSTALLED)> 0)
                    {

                        String app_package = app.packageName;
                        //System.out.println("app.processName_ "+app.processName);
                        String name = ""+pm.getApplicationLabel(app);
                        //System.out.println("packageName_ "+app_package);
                        //System.out.println("name "+name);
                        //listApps.add(new ItemRow(context,app_package,name));
                        mSet.add(new ItemRow(context,app_package,name,(!getIfIsChecked(name,db))));

                    }
                }

                publishProgress();
                return null;
            }

            private boolean getIfIsChecked(final String name, final SystemMonitorDatabase db) {
                    return db.preferencesDao().getIfIsChecked(name);
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                mDataSet.addAll(mSet);
                mAdapter.notifyDataSetChanged();
                mDataSetCopy.addAll(mDataSet);
            }

            //close method
        }.execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preferences, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        System.out.println(" id == "+id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }else{

            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onItemRowSelected(ItemRow itemRow, int position) {
        //System.out.println("chiamato itemRow ");
        //Toast.makeText(this, "Selected: " + itemRow.getName() + ", " + itemRow.getIsChecked()+" position == "+position, Toast.LENGTH_LONG).show();
        //itemRow.changeBool();
        //mDataSet.set(position,itemRow);
        //mAdapter.notifyDataSetChanged();
    }

}





