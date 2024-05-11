package com.example.pmu_test;

import static androidx.core.content.ContextCompat.startActivity;

import com.example.pmu_test.model.CityCard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CityCardsAdapter extends  RecyclerView.Adapter<CityCardsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CityCard> cities;

    private CityCard mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    public CityCardsAdapter(Context context, ArrayList<CityCard> cities) {
        this.context = context;
        this.cities = cities;
    }

    @NonNull
    @Override
    public CityCardsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityCardsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // to set data to textview and imageview of each card layout
        CityCard model = cities.get(position);
        holder.cityName.setText(model.getCity());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + model.getCity() + "&units=metric";
                Intent intent = new Intent(context, WeatherData.class);
                intent.putExtra("city", model.getCity());
                intent.putExtra("url", url);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return cities.size();
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = cities.get(position);
        mRecentlyDeletedItemPosition = position;
        cities.remove(position);
        notifyItemRemoved(position);
        //showUndoSnackbar();
    }

    /*private void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.coordinator_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
        snackbar.show();
    }*/

    private void undoDelete() {
        cities.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }

    public Context getContext() {
        return context;
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.city_name);
        }
    }

}
