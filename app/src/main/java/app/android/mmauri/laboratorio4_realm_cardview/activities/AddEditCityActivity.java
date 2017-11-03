package app.android.mmauri.laboratorio4_realm_cardview.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import app.android.mmauri.laboratorio4_realm_cardview.R;
import app.android.mmauri.laboratorio4_realm_cardview.models.City;
import io.realm.Realm;

public class AddEditCityActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewCityPreview;
    private EditText editTextCityName;
    private EditText editTextCityImgLink;
    private ImageButton imageButtonCityPreview;
    private EditText editTextCityDescription;
    private RatingBar ratingBarCity;
    private FloatingActionButton fabSaveCity;

    private int cityId;
    private City city;

    private Realm realm;

    private final int PICASSO_GET_URL = 10;
    private boolean NEW_CITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_city);

        this.realm = Realm.getDefaultInstance();
        initializeVariables();

        /* Comprobar si ciudad nueva o existente */
        Bundle b = getIntent().getExtras();
        if (b != null) {
            this.cityId = b.getInt("id");
            Toast.makeText(AddEditCityActivity.this, "Editing the existing city", Toast.LENGTH_SHORT).show();
            NEW_CITY = false;
            city = realm.where(City.class).equalTo("id", cityId).findFirst();
            paintCityAttributes();
        } else NEW_CITY = true;

        setActivityTitle();

        this.imageButtonCityPreview.setOnClickListener(this);

        this.fabSaveCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrEditCity();
            }
        });

    }

    private void addOrEditCity() {
        String cityName = editTextCityName.getText().toString();
        String cityDescription = editTextCityDescription.getText().toString();
        String cityImgLink = editTextCityImgLink.getText().toString();
        float cityRating = ratingBarCity.getRating();

        if (!cityName.isEmpty() && !cityDescription.isEmpty() && !cityImgLink.isEmpty()) {
            if (NEW_CITY){
                Toast.makeText(AddEditCityActivity.this, "Saving the new " + cityName + " city", Toast.LENGTH_SHORT).show();
                createNewCity(cityName, cityDescription, cityImgLink, cityRating);
            }
            else{
                Toast.makeText(AddEditCityActivity.this, "Update the city " + cityName, Toast.LENGTH_SHORT).show();
                editCity(cityName, cityDescription, cityImgLink, cityRating);
            }

            Intent i = new Intent(AddEditCityActivity.this, CityActivity.class);
            startActivity(i);
        } else
            Toast.makeText(AddEditCityActivity.this, "The data is not valid, please check the fields again", Toast.LENGTH_SHORT).show();
    }

    private void setActivityTitle() {
        String title = "Edit City";
        if (NEW_CITY) title = "Create New City";
        setTitle(title);
    }

    private void paintCityAttributes() {
        editTextCityName.setText(city.getName());
        editTextCityDescription.setText(city.getDescription());
        editTextCityImgLink.setText(city.getImg_url());
        ratingBarCity.setRating(city.getStars());
    }

    private void initializeVariables() {
        imageViewCityPreview = (ImageView) findViewById(R.id.imageViewCityPreview);
        editTextCityName = (EditText) findViewById(R.id.editTextCityName);
        editTextCityImgLink = (EditText) findViewById(R.id.editTextCityImgLink);
        imageButtonCityPreview = (ImageButton) findViewById(R.id.imageButtonCityPreview);
        editTextCityDescription = (EditText) findViewById(R.id.editTextCityDescription);
        ratingBarCity = (RatingBar) findViewById(R.id.ratingBarCity);
        fabSaveCity = (FloatingActionButton) findViewById(R.id.fabSaveCity);
    }

    private void runPicassoWithUri() {
        Uri imgLink = Uri.parse(editTextCityImgLink.getText().toString().trim());
        Picasso.with(AddEditCityActivity.this).load(imgLink).fit().into(imageViewCityPreview);
    }


    /* CRUD actions */

    private void createNewCity (String name, String description, String imgUrl, float stars) {
        realm.beginTransaction();
        City _city = new City(name, description, imgUrl, stars);
        realm.copyToRealm(_city);
        realm.commitTransaction();
    }

    private void editCity(String name, String description, String imgUrl, float stars) {
        realm.beginTransaction();
        city.setName(name);
        city.setDescription(description);
        city.setImg_url(imgUrl);
        city.setStars(stars);
        realm.copyToRealmOrUpdate(city);
        realm.commitTransaction();
    }


    /* Events */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PICASSO_GET_URL:
                String permission = permissions[0];
                int result = grantResults[0];
                /* Se comprueba el permiso */
                if (permission.equals(Manifest.permission.INTERNET)) {
                    /* Se comprueba que este aceptado */
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        /* Se ejecuta la accion */
                        runPicassoWithUri();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    // El usuario ha aceptado el permiso? En las versiones antiguas se acepta al instalar la app
    private boolean checkPermission(String permission) {
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        // Comprorbar version de Android que estamos corriendo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Telefonos modernos:
                    /* Comprobar si ha aceptado, no ha aceptado, o nunca se le ha preguntado por este permiso */
            if (checkPermission(Manifest.permission.INTERNET)) {
                        /* Ha aceptado, procedemos a la ejecucion del Picasso "online" */
                runPicassoWithUri();
            } else {
                        /* Ha denegado o es la primera vez que se le pregunta por el permiso */
                if (!shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {
                    // No se le ha preguntado todavia, por lo tanto hacemos la peticion del permiso
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PICASSO_GET_URL);
                } else {
                    // Ha denegado el permiso, proporcionamos facilidad para aceptarlo
                    Toast.makeText(AddEditCityActivity.this, "Please, enable the request permission", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                }
            }
        } else {
            // Telefonos viejos:
            if (checkPermission(Manifest.permission.INTERNET)) {
                runPicassoWithUri();
            } else {
                Toast.makeText(AddEditCityActivity.this, "Version antigua, no hay permisos suficientes :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

}
