package app.android.mmauri.laboratorio4_realm_cardview.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import app.android.mmauri.laboratorio4_realm_cardview.R;
import app.android.mmauri.laboratorio4_realm_cardview.adapters.CityAdapter;
import app.android.mmauri.laboratorio4_realm_cardview.models.City;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CityActivity extends AppCompatActivity implements View.OnClickListener, RealmChangeListener<RealmResults<City>> {

    private Realm realm;
    private FloatingActionButton fabAddCity;

    private RealmResults<City> cities;

    private RecyclerView mRecyclerView;
    private CityAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);


        /* BD Realm */
        realm = Realm.getDefaultInstance();
        cities = realm.where(City.class).findAll();
        /* Creamos el metodo que se activara siempre que haya cambios en "cities" y lo vinculamos */
        cities.addChangeListener(this);


        fabAddCity = (FloatingActionButton) findViewById(R.id.fabAddCity);
        fabAddCity.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(this.mLayoutManager);

        mAdapter = new CityAdapter(this.cities, R.layout.card_view_city, new CityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(City city, int position) {
                Toast.makeText(CityActivity.this, "City: " + city.getName() + " on position: " + position, Toast.LENGTH_SHORT).show();

                Intent intentToEditCity = new Intent(CityActivity.this, AddEditCityActivity.class);
                intentToEditCity.putExtra("id", city.getId());
                startActivity(intentToEditCity);
            }

            @Override
            public void onItemDelete(City city, int position) {
                showAlertToDeleteCity("Delete city", "Are you sure you want to delete " + city.getName() + "?", position);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        setHideShowFAB();
    }


    /* CRUD actions */

    private void deleteCity(City city) {
        try {
            realm.beginTransaction();
            city.deleteFromRealm();
            realm.commitTransaction();
            Toast.makeText(CityActivity.this, "It has been deleted successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(CityActivity.this, "An error has been occurred. \n" + e, Toast.LENGTH_SHORT).show();
        }
    }


    /* Dialogs */

    private void showAlertToDeleteCity(String title, String msg, final int city_position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (msg != null) builder.setMessage(msg);

        /* Insertar boton REMOVE */
        builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCity(cities.get(city_position));
            }
        });

        /* Insertar boton CANCEL */
        builder.setNegativeButton("CANCEL", null);

        /* Creamos y mostramos el alert dialog */
        builder.create().show();
    }


    /* Events */

    @Override
    public void onClick(View v) {
        /* Function when we push the FAB button */
        Toast.makeText(this, "Creating a new city...", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(CityActivity.this, AddEditCityActivity.class);
        startActivity(i);
    }

    @Override
    public void onChange(RealmResults<City> element) {
        mAdapter.notifyDataSetChanged();
    }

    private void setHideShowFAB() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    fabAddCity.hide();
                else
                    fabAddCity.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
