package app.android.mmauri.laboratorio4_realm_cardview.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.android.mmauri.laboratorio4_realm_cardview.R;
import app.android.mmauri.laboratorio4_realm_cardview.models.City;

/**
 * Created by marc on 10/23/17.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<City> cities;
    private int layout;
    private OnItemClickListener itemClickListener;
    private Context context;

    public CityAdapter(List<City> cities, int layout, OnItemClickListener itemClickListener) {
        this.cities = cities;
        this.layout = layout;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(this.layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CityAdapter.ViewHolder holder, int position) {
        holder.bind(this.cities.get(position), this.itemClickListener, this.context);
    }

    @Override
    public int getItemCount() {
        return this.cities.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewCity;
        private TextView textViewCityName;
        private TextView textViewCityDescription;
        private TextView textViewStars;
        private Button buttonDeleteCity;


        public ViewHolder(View itemView) {
            super(itemView);
            this.imageViewCity = (ImageView) itemView.findViewById(R.id.imageViewCity);
            this.textViewCityName = (TextView) itemView.findViewById(R.id.textViewCityName);
            this.textViewCityDescription = (TextView) itemView.findViewById(R.id.textViewCityDescription);
            this.textViewStars = (TextView) itemView.findViewById(R.id.textViewStars);
            this.buttonDeleteCity = (Button) itemView.findViewById(R.id.buttonDeleteCity);
        }

        public void bind (final City city, final OnItemClickListener listener, Context context) {
            Picasso.with(context).load(Uri.parse(city.getImg_url())).fit().into(imageViewCity);;
            textViewCityName.setText(city.getName());
            textViewCityDescription.setText(city.getDescription());
            String stars = String.valueOf(city.getStars());
            textViewStars.setText(stars);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(city, getAdapterPosition());
                }
            });

            buttonDeleteCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemDelete(city, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(City city, int position);
        void onItemDelete(City city, int position);
    }

}
