package com.salvatorefiorilla.systemmonitor;

import android.app.ActivityManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class AdapterForCard extends RecyclerView.Adapter<AdapterForCard.ViewHolder> {

    private List<Card> myCards;
    private int iconID;
    private Context mContext;

    public AdapterForCard(List<Card> cards){
        this.myCards = cards;
    }

    public AdapterForCard(Context context, List<Card> cardList) {
        this.mContext = context;
        this.myCards = cardList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView descView;
        public ImageView iv;
        public CardView cv;
        public ProgressBar rateBar;

        public ViewHolder(View v) {
            super(v);
            titleView = (TextView)v.findViewById(R.id.tvText);
            descView = (TextView)v.findViewById(R.id.tvDesc);
            iv = (ImageView)v.findViewById(R.id.iconView);
            cv = (CardView) v.findViewById(R.id.itemCard);
            rateBar = (ProgressBar) v.findViewById(R.id.progressRateBar);
            rateBar.setMax(108);
        }
    }

    @Override
    public AdapterForCard.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = (View) LayoutInflater.from(mContext ).inflate(R.layout.card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }



    @Override
    public void onBindViewHolder(@NonNull final AdapterForCard.ViewHolder holder, final int position) {

        Card c = myCards.get(position);
        holder.iv.setImageDrawable(c.getIcon());
        holder.titleView.setText(c.getTitle());
        holder.titleView.setTypeface(holder.titleView.getTypeface(), Typeface.BOLD);
        holder.descView.setText(c.getInfo());
        if (c.getRate() != -1){
            //System.out.println("rate è ===> "+c.getRate());
            holder.rateBar.setProgress(c.getRate());
        }else{
            holder.rateBar.setVisibility(View.GONE);
        }

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = holder.titleView.getText().toString();
                if(!title.equalsIgnoreCase("TOTAL DETECT TIME")){
                    Toast.makeText(holder.cv.getContext(), "Cliccked card: "+(holder.titleView.getText()), Toast.LENGTH_SHORT).show();
                    Context context = v.getContext();

                    Intent i = new Intent(context,DetailsAppActivity.class);
                    i.putExtra("title", ""+holder.titleView.getText());
                    i.putExtra("pkgn",myCards.get(position).getPackageName()) ;
                    context.startActivity(i);
                }
                else {
                    Toast.makeText(holder.cv.getContext(), "No action for this card: ", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return myCards.size();
    }
}
/*    Codice utile!
                Drawable d =myCards.get(position).getIcon() ;
                String id = v.getResources().getResourceName(v.getId());
                System.out.println("id__ "+id);
                Bitmap btm = BitmapFactory.decodeResource(v.getResources(),Integer.parseInt(id));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                System.out.println("bitmap is null ? "+(btm==null));
                btm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
*/