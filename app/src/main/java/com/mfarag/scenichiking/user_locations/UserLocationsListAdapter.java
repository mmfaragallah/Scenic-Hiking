package com.mfarag.scenichiking.user_locations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mfarag.scenichiking.R;
import com.mfarag.scenichiking.model.UserLocation;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserLocationsListAdapter extends RecyclerView.Adapter<UserLocationsListAdapter.ViewHolder> implements View.OnClickListener {

    //region objects
    private UserLocationsListContract.View userLocationsView;
    private List<UserLocation> userLocations;
    //endregion

    UserLocationsListAdapter(UserLocationsListContract.View userLocationsView, List<UserLocation> userLocations) {
        this.userLocationsView = userLocationsView;
        this.userLocations = userLocations;
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public UserLocationsListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_location_item, parent, false);

        view.setOnClickListener(this);

        return new ViewHolder(view);
    }

    /**
     * @param viewHolder
     * @param position   the position of the item to be drawn
     */
    @Override
    public void onBindViewHolder(UserLocationsListAdapter.ViewHolder viewHolder, int position) {

        UserLocation userLocation = userLocations.get(position);

        viewHolder.locationName.setText(userLocation.getName());

        String latitudeLongitudeText = viewHolder.locationLatLng.getContext().getString(R.string.latitude_longitude_text);
        latitudeLongitudeText = MessageFormat.format(latitudeLongitudeText, userLocation.getLatitude(), userLocation.getLongitude());
        viewHolder.locationLatLng.setText(latitudeLongitudeText);

        viewHolder.itemView.setTag(userLocation.getId());
    }

    /**
     * @param view
     */
    @Override
    public void onClick(View view) {

        String id = (String) view.getTag();
//        userLocationsView.onLocationClick(id);
    }

    /**
     * ViewHolder class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemView;

        @BindView(R.id.location_name)
        TextView locationName;

        @BindView(R.id.location_latlng)
        TextView locationLatLng;

        /**
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * @param userLocations
     */
    public void updateUserLocationsList(List<UserLocation> userLocations) {
        this.userLocations = userLocations;
        notifyDataSetChanged();
    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        return userLocations.size();
    }
}