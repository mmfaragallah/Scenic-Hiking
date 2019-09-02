package com.mfarag.scenichiking.scenic_hiking_map;

import androidx.lifecycle.ViewModel;

import com.mfarag.scenichiking.model.UserLocation;
import com.mfarag.scenichiking.repositories.UserLocationsRepository;

public class ScenicHikingMapViewModel extends ViewModel {

    private UserLocationsRepository repo;

    public ScenicHikingMapViewModel(UserLocationsRepository repo) {
        this.repo = repo;
    }

    void addLocation(double latitude, double longitude, String name) {

        UserLocation userLocation = new UserLocation();
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);
        userLocation.setName(name);

        repo.addUserLocation(userLocation);
    }


}
