package com.example.pmu_test;

import static androidx.core.content.ContextCompat.startActivity;

import com.example.pmu_test.model.CityCard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CityCardsAdapter extends ListAdapter<CityCard, CityCardsAdapter.ViewHolder> {

    private Context context;
    private OnItemClickListener listener;
    public CityCardsAdapter(@NonNull DiffUtil.ItemCallback<CityCard> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
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
        CityCard model = getItem(position);
        holder.cityName.setText(model.getCity());
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + model.getCity() + "&units=metric";
                Intent intent = new Intent(context, WeatherData.class);
                intent.putExtra("city", model.getCity());
                intent.putExtra("url", url);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });*/
    }

    /*public void deleteItem(int position) {
        //mRecentlyDeletedItem = cities.get(position);
        //mRecentlyDeletedItemPosition = position;
        (position);
        notifyItemRemoved(position);
        //showUndoSnackbar();
    }*/

    public Context getContext() {
        return context;
    }

    static class CityDiff extends DiffUtil.ItemCallback<CityCard> {

        @Override
        public boolean areItemsTheSame(@NonNull CityCard oldItem, @NonNull CityCard newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CityCard oldItem, @NonNull CityCard newItem) {
            return oldItem.getCity().equals(newItem.getCity());
        }
    }

    public CityCard getCityAt(int position) {
        return getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.city_name);

            // adding on click listener for each item of recycler view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // inside on click listener we are passing
                    // position to our item of recycler view.
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
        public void bind(String text) {
            cityName.setText(text);
        }
        ViewHolder create(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_layout, parent, false);
            return new ViewHolder(view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CityCard model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
