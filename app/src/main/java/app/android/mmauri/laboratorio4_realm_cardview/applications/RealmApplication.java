package app.android.mmauri.laboratorio4_realm_cardview.applications;

import android.app.Application;

import java.util.concurrent.atomic.AtomicInteger;

import app.android.mmauri.laboratorio4_realm_cardview.models.City;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by marc on 10/24/17.
 */

public class RealmApplication extends Application {

    public static AtomicInteger CityID = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();

        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();
        try {
            CityID = getIdByTable(realm, City.class);
        } finally {
            realm.close();
        }
    }

    /* Configuracion basica de Realm, se guarda en FILES_DIR/city_realm.realm */
    private void setUpRealmConfig() {
        Realm.init(this);
        RealmConfiguration cityConfig = new RealmConfiguration.Builder()
                .name("city_realm.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(cityConfig);
    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass) {
        /* Recuperamos la tabla del objeto "anyClass" */
        RealmResults<T> results = realm.where(anyClass).findAll();
        /* Devolvemos el "id" maximo de la tabla, o bien 0 si no hay valores */
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }

}
