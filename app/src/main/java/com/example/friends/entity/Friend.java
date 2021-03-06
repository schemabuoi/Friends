package com.example.friends.entity;


import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.io.Serializable;
import java.util.Date;


@Entity
public class Friend implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String phone;
    private Date birthday;
    private String email;
    private String address;
    private String website;
    private boolean favorite;
    private String picturePath;

    public Friend(String name, String phone, Date birthday, String email, String address, String website, boolean favorite, String picturePath) {
        this.name = name;
        this.phone = phone;
        this.birthday = birthday;
        this.email = email;
        this.address = address;
        this.website = website;
        this.favorite = favorite;
        this.picturePath = picturePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone()
    {
        return phone;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getPicturePath() {
        return picturePath;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Friend friend1 = (Friend) obj;
        Friend friend2 = this;

        if(friend1 == null)
        {
            return false;
        }
        else
        {
            return (friend1.name == null ? friend2.name == null : friend1.name.equals(friend2.name)) &&
                    (friend1.phone == null ? friend2.phone == null : friend1.phone.equals(friend2.phone)) &&
                    (friend1.birthday == null ? friend2.birthday == null : friend1.birthday.equals(friend2.birthday)) &&
                    (friend1.email == null ? friend2.email == null : friend1.email.equals(friend2.email)) &&
                    (friend1.address == null ? friend2.address == null : friend1.address.equals(friend2.address)) &&
                    (friend1.website == null ? friend2.website == null : friend1.website.equals(friend2.website)) &&
                    friend1.favorite == friend2.favorite &&
                    (friend1.picturePath == null ? friend2.picturePath == null : friend1.picturePath.equals(friend2.picturePath));
        }

    }

}
