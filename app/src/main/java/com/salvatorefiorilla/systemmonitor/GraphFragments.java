package com.salvatorefiorilla.systemmonitor;

import android.app.Fragment;
import android.arch.persistence.room.Room;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.salvatorefiorilla.systemmonitor.room.ApplicationStats;
import com.salvatorefiorilla.systemmonitor.room.DaoInterface;
import com.salvatorefiorilla.systemmonitor.room.SystemMonitorDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphFragments extends Fragment {

    private BarChart mChart;
    private ArrayList<Bitmap> imageList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Graph Fragments created! ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("ON CREATE VIEW FRAGMENTS");
        View v =  inflater.inflate(R.layout.content_graph_layout, container, false);
        mChart = (BarChart) v.findViewById(R.id.chart);
        mChart.setDrawGridBackground(false);
        setData();
        return v;
    }

    private void setData() {

        SystemMonitorDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(),SystemMonitorDatabase.class, "systemmonitor").allowMainThreadQueries().build();
        final DaoInterface agent = db.statsDao();
        ArrayList<ApplicationStats> myApp = (ArrayList<ApplicationStats>)
                agent.queryLastTime(System.currentTimeMillis()- (long)86400000 );
        System.out.println("-------prese dal db elementi size myApp "+myApp.size());

        List<BarEntry> entries = new ArrayList<BarEntry>();
        BarData barData = new BarData();

        int i = 0;
        for (ApplicationStats data : myApp) {
            Long ttu = data.getBackgroundTime()+data.getForegroundTime();
            //entries.add(new BarEntry(i++,ttu));
            List<BarEntry> ll = new ArrayList<BarEntry>();
            ll.add(new BarEntry(i++,ttu));
            PackageManager pm = getActivity().getPackageManager();
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
        mChart.setData(barData);
        mChart.invalidate();

    }

    public int randomColor() {
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return Color.rgb(r, g, b);
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