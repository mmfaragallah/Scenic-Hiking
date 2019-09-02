package com.mfarag.scenichiking.user_locations;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.mfarag.scenichiking.repositories.UserLocationsRepository;

class UserLocationsListViewModelProviderFactory implements ViewModelProvider.Factory {

    private UserLocationsRepository repo;

    public UserLocationsListViewModelProviderFactory(UserLocationsRepository repo) {
        this.repo = repo;
    }

    @NonNull
    @Override
    public UserLocationsListViewModel create(@NonNull Class modelClass) {
        return new UserLocationsListViewModel(repo);
    }
}
