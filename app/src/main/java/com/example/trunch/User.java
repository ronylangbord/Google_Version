package com.example.trunch;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "android_id",
        "firstName",
        "headline",
        "lastName",
        "pictureUrl"
})
/**
 * Created by or on 4/15/2015.
 */
public class User implements Parcelable {

    @JsonProperty("android_id")
    private String androidId;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("headline")
    private String headline;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("pictureUrl")
    private String pictureUrl;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The androidId
     */
    @JsonProperty("android_id")
    public String getAndroidId() {
        return androidId;
    }

    /**
     *
     * @param androidId
     * The android_id
     */
    @JsonProperty("android_id")
    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }


    /**
     *
     * @return
     * The firstName
     */
    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The firstName
     */
    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The headline
     */
    @JsonProperty("headline")
    public String getHeadline() {
        return headline;
    }

    /**
     *
     * @param headline
     * The headline
     */
    @JsonProperty("headline")
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     *
     * @return
     * The lastName
     */
    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The lastName
     */
    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The pictureUrl
     */
    @JsonProperty("pictureUrl")
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     *
     * @param pictureUrl
     * The pictureUrl
     */
    @JsonProperty("pictureUrl")
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(androidId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(headline);
        dest.writeString(pictureUrl);
    }

    public User(Parcel in) {
        this.androidId = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.headline = in.readString();
        this.pictureUrl = in.readString();
    }


    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(){
    }
}
