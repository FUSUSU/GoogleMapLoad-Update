package com.example.teo.googlemapload;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnPoiClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener

{
    //Our Map
    //Bản đồ của chúng tôi
    private GoogleMap mMap;

    //To store longitude and latitude from map.
    //Để lưu kinh độ và vĩ độ từ bản đồ.
    private double longitude;
    private double latitude;

    //Buttons
    private ImageButton buttonSave; //Lưu
    private ImageButton buttonCurrent; // tạo độ hiện tại
    private ImageButton buttonView; //Xem tạo độ

    //Google ApiClient
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        //Khởi tạo khách hàng googleapi
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this) //Bắt sự kiện
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Initializing views and adding onclick listeners.
        //Khởi tạo views và thêm bộ lắng nghe click
        buttonSave = (ImageButton) findViewById(R.id.buttonSave);
        buttonCurrent = (ImageButton) findViewById(R.id.buttonCurrent);
        buttonView = (ImageButton) findViewById(R.id.buttonView);
        buttonSave.setOnClickListener(this);
        buttonCurrent.setOnClickListener(this);
        buttonView.setOnClickListener(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnPoiClickListener(this);

        mMap = googleMap;

        // Add a marker in CauRong and move the camera
        // Thêm một đánh dấu tại Cầu Rồng và thêm di chuyển camera
        LatLng caurong = new LatLng(16.061145, 108.227482);
        mMap.addMarker(new MarkerOptions().position(caurong).title("Cầu Rống"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(caurong));

        //Setting onMarkerDragListener to track the marker drag
        //Cài đặt onMarkerDragListener
        mMap.setOnMarkerDragListener(this);
        //Adding a long click listener to the map
        //Thêm một độ dài click lắng nghe từ map
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getApplicationContext(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Call the getCurrentLocation()
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        //Lấy tọa độ
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map.
        //Di chuyển bản đồ.
        moveMap();
    }

    //click lâu để thay đổi vị trí đánh dấu.
    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers
        //Xóa tất cả các đánh dấu.
        mMap.clear();

        //Adding a new marker to the current pressed position we are also making the draggable true
        //Thêm một đánh dấu mới cho vị trí ép lại hiện tại chúng ta cũng làm kéo đúng
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
    }

    //Click buttonCurrent
    @Override
    public void onClick(View v) {
        if (v == buttonCurrent){
            getCurrentLocation();
            moveMap();
        }
    }

    //Connect to our Google Api Client.
    //Kết nối GoogleApiClient.
    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    //Disconnect to our Google Api Client.
    //Ngắt kết nối GoogleApiClient.
    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    //Getting current location
    //Bắt vị trí hiện tại
    private void getCurrentLocation() {
        //Creating a location object
        // Tạo mới một đối tượng vị trí
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null){
            //Getting longitude and latitude
            //Lấy kinh độ và vĩ độ
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            //di chuyển bản đồ đến vị trí
        }
    }

    //Function to move the map
    //Chức năng di chuyển bản đồ
    private void moveMap(){
        //String to display current latitude and
        //String hiển thị vĩ độ và kinh độ hiện tại
        String msg = latitude+" , "+longitude;

        //Creating a LatLng Object to store Coordinates
        //Tạo một đối tượng LatLng để lưu tạo độ
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        //Thêm đánh dấu bản đồ
        mMap.addMarker(new MarkerOptions()
            .position(latLng) //Setting position //vị trí đặt
            .draggable(true) //Making the marker draggable //Làm đánh dấu kéo
            .title("Current Location")); //Adding a title //Thêm tiêu đề.

        //Moving the camera
        //Di chuyển camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        //Hoạt ảnh máy ảnh
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
