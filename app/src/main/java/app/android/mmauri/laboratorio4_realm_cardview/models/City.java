package app.android.mmauri.laboratorio4_realm_cardview.models;

import app.android.mmauri.laboratorio4_realm_cardview.applications.RealmApplication;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by marc on 10/23/17.
 */

public class City extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String name;
    @Required
    private String description;
    @Required
    private String img_url;
    private float stars;

    // Empty constructor needed for Realm
    public City() {

    }

    public City(String name, String description, String img_url, float stars) {
        this.id = RealmApplication.CityID.incrementAndGet();
        this.name = name;
        this.description = description;
        this.img_url = img_url;
        this.stars = stars;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }
}
