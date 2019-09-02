package com.mfarag.scenichiking.repositories;

import com.mfarag.scenichiking.model.UserLocation;

import java.util.ArrayList;
import java.util.List;

public class UserLocationsRepository {

    private static UserLocationsRepository instance;

    public static UserLocationsRepository getInstance() {
        if (instance == null) {
            instance = new UserLocationsRepository();
        }
        return instance;
    }


    private List<UserLocation> userLocationsList;


    public List<UserLocation> getUserLocationsList() {
        return userLocationsList;
    }

    public void addUserLocation(UserLocation userLocation) {
        if (userLocationsList == null) {
            userLocationsList = new ArrayList<>();
        }
        userLocationsList.add(userLocation);
    }

}
