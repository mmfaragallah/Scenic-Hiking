package com.mfarag.scenichiking.scenic_hiking_map;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.JsonElement;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mfarag.scenichiking.R;
import com.mfarag.scenichiking.base.BaseActivity;
import com.mfarag.scenichiking.repositories.UserLocationsRepository;
import com.mfarag.scenichiking.user_locations.UserLocationsListScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineDasharray;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineTranslate;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

/**
 * Mahmoud 9/2/2019
 */
public class ScenicHikingMapScreen extends BaseActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapLongClickListener {

    //region objects
    private Style style;
    private MapboxMap mapboxMap;
    private GeoJsonSource geoJsonSource;

    private List<Feature> featuresList;
    private List<LatLng> userLocations;

    private PermissionsManager permissionsManager;

    private Point originPoint;
    private Point destinationPoint;
    private List<Feature> directionsRouteFeatureList;
    private FeatureCollection dashedLineDirectionsFeatureCollection;

    private ScenicHikingMapViewModel viewModel;
    //endregion

    //region screen views
    @BindView(R.id.map_view)
    MapView mapView;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapView.onCreate(savedInstanceState);
    }

    //region BaseActivity methods
    @Override
    protected int getLayout() {
        return R.layout.scenic_hiking_map_screen;
    }

    @Override
    protected void initializeObjects() {

        featuresList = new ArrayList<>();
        userLocations = new ArrayList<>();

        directionsRouteFeatureList = new ArrayList<>();

        ViewModelProvider.Factory factory = new ScenicHikingMapViewModelProviderFactory(UserLocationsRepository.getInstance());
        viewModel = ViewModelProviders.of(this, factory).get(ScenicHikingMapViewModel.class);
    }

    @Override
    protected void initializeViews() {

        mapView.getMapAsync(this);
    }
    //endregion

    //region Activity callbacks
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    //endregion

    //region OnMapReadyCallback method
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                ScenicHikingMapScreen.this.style = style;

                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                enableLocationComponent(style);

                mapboxMap.addOnMapLongClickListener(ScenicHikingMapScreen.this);

                // Add the marker image to map
                style.addImage("marker-icon-id",
                        BitmapFactory.decodeResource(
                                ScenicHikingMapScreen.this.getResources(), R.drawable.mapbox_marker_icon_default));

                // Add the layer for the dashed directions route line
                initDottedLineSourceAndLayer(style);
            }
        });
    }
    //endregion

    //region private methods
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private String getLocationName(LatLng point) {

        String name = "No Defined Name";

        // Convert LatLng coordinates to screen pixel and only query the rendered features.
        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel);

        // Loop to all the features list
        for (Feature feature : features) {

            // Ensure the feature has properties defined
            if (feature.properties() != null) {
                for (Map.Entry<String, JsonElement> entry : feature.properties().entrySet()) {

                    // Log all the properties
                    Log.d("TAG", String.format("%s = %s", entry.getKey(), entry.getValue()));

                    // search for the "name" property
                    if (entry.getKey().equalsIgnoreCase("name")) {
                        name = entry.getValue().toString();
                        break;
                    }
                }
            }
        }

        return name;
    }
    //endregion

    //region callbacks
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //endregion

    //region MapboxMap callbacks
    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {

        String locationName = getLocationName(point);
        Toast.makeText(this, locationName, Toast.LENGTH_SHORT).show();

        viewModel.addLocation(point.getLatitude(), point.getLongitude(), locationName);

        userLocations.add(point);

        Feature feature = Feature.fromGeometry(
                Point.fromLngLat(point.getLongitude(), point.getLatitude())
        );
        featuresList.add(feature);


        if (geoJsonSource == null) {

            originPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());

            geoJsonSource = new GeoJsonSource("source-id", feature);

            style.addSource(geoJsonSource);

            SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
            symbolLayer.withProperties(
                    PropertyFactory.iconImage("marker-icon-id")
            );

            style.addLayer(symbolLayer);

        } else {

            FeatureCollection featureCollection = FeatureCollection.fromFeatures(featuresList);
            geoJsonSource.setGeoJson(featureCollection);

            destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());

            getRoute(originPoint, destinationPoint);

            originPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        }


        return false;
    }
    //endregion

    //region view clicks method
    public void centersUserRoutes(View v) {

        if (userLocations.size() > 1) {

            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .includes(userLocations)
                    .build();

            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

        } else if (userLocations.size() == 1) {
            Toast.makeText(this, "Only one user location selected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No user locations selected", Toast.LENGTH_LONG).show();
        }
    }

    public void showUserLocations(View view) {

        if (userLocations.size() > 0) {

            Intent intent = new Intent(this, UserLocationsListScreen.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "No selected user locations", Toast.LENGTH_LONG).show();
        }
    }
    // endregion

    //region routes methods

    /**
     * Make a call to the Mapbox Directions API to get the route from the device location to the
     * place picker location
     */
    @SuppressWarnings({"MissingPermission"})
    private void getRoute(Point originPoint, Point destinationPoint) {

        MapboxDirections client = MapboxDirections.builder()
                .origin(originPoint)
                .destination(destinationPoint)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Log.d("TAG", "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.d("TAG", "No routes found");
                    return;
                }
                drawNavigationPolylineRoute(response.body().routes().get(0));
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {

                Log.d("Error: %s", throwable.getMessage());

                if (!throwable.getMessage().equals("Coordinate is invalid: 0,0")) {
                    Toast.makeText(ScenicHikingMapScreen.this,
                            "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Update the GeoJson data that's part of the LineLayer.
     *
     * @param route The route to be drawn in the map's LineLayer that was set up above.
     */
    private void drawNavigationPolylineRoute(final DirectionsRoute route) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {

                    LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
                    List<Point> coordinates = lineString.coordinates();
                    for (int i = 0; i < coordinates.size(); i++) {
                        directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(coordinates)));
                    }

                    dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(directionsRouteFeatureList);

                    GeoJsonSource source = style.getSourceAs("SOURCE_ID");
                    if (source != null) {
                        source.setGeoJson(dashedLineDirectionsFeatureCollection);
                    }
                }
            });
        }
    }


    /**
     * Set up a GeoJsonSource and LineLayer in order to show the directions route from the device location
     * to the place picker location
     */
    private void initDottedLineSourceAndLayer(@NonNull Style loadedMapStyle) {

        dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(new Feature[]{});

        loadedMapStyle.addSource(new GeoJsonSource("SOURCE_ID", dashedLineDirectionsFeatureCollection));
        loadedMapStyle.addLayerBelow(
                new LineLayer(
                        "DIRECTIONS_LAYER_ID", "SOURCE_ID").withProperties(
                        lineWidth(4.5f),
                        lineColor(Color.GREEN),
                        lineTranslate(new Float[]{0f, 4f}),
                        lineDasharray(new Float[]{1.2f, 1.2f})
                ), "road-label-small");
    }
    //endregion
}