package com.mfarag.scenichiking.user_locations;

import androidx.lifecycle.ViewModel;

import com.mfarag.scenichiking.model.UserLocation;
import com.mfarag.scenichiking.repositories.UserLocationsRepository;

import java.util.List;

public class UserLocationsListViewModel extends ViewModel {

    private UserLocationsRepository repo;

    public UserLocationsListViewModel(UserLocationsRepository repo) {
        this.repo = repo;
    }

    public List<UserLocation> getUserLocations() {
        return repo.getUserLocationsList();
    }

}
