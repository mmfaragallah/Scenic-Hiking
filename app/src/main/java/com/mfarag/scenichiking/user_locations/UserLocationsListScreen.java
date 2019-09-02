package com.mfarag.scenichiking.user_locations;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mfarag.scenichiking.R;
import com.mfarag.scenichiking.base.BaseActivity;
import com.mfarag.scenichiking.repositories.UserLocationsRepository;

import butterknife.BindView;

/**
 * Mahmoud 9/2/2019
 */
public class UserLocationsListScreen extends BaseActivity implements UserLocationsListContract.View {

    //region screen views
    @BindView(R.id.user_locations_list)
    RecyclerView userLocationsListView;
    //endregion

    //region objects
    private UserLocationsListAdapter listAdapter;
    private UserLocationsListViewModel viewModel;
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //region BaseActivity methods
    @Override
    protected int getLayout() {
        return R.layout.user_locations_list_screen;
    }

    @Override
    protected void initializeObjects() {

        ViewModelProvider.Factory factory = new UserLocationsListViewModelProviderFactory(UserLocationsRepository.getInstance());
        viewModel = ViewModelProviders.of(this, factory).get(UserLocationsListViewModel.class);

        listAdapter = new UserLocationsListAdapter(this, viewModel.getUserLocations());
    }

    @Override
    protected void initializeViews() {

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        userLocationsListView.setHasFixedSize(true);
        userLocationsListView.setLayoutManager(new LinearLayoutManager(this));
        userLocationsListView.setAdapter(listAdapter);
    }
    //endregion
}