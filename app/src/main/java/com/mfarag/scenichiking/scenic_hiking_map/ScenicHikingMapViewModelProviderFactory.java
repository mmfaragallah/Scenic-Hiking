package com.mfarag.scenichiking.scenic_hiking_map;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.mfarag.scenichiking.repositories.UserLocationsRepository;

class ScenicHikingMapViewModelProviderFactory implements ViewModelProvider.Factory {

    private UserLocationsRepository repo;

    public ScenicHikingMapViewModelProviderFactory(UserLocationsRepository repo) {
        this.repo = repo;
    }

    @NonNull
    @Override
    public ScenicHikingMapViewModel create(@NonNull Class modelClass) {
        return new ScenicHikingMapViewModel(repo);
    }
}
