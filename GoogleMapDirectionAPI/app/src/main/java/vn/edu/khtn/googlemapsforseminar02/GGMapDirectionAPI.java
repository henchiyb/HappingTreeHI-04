package vn.edu.khtn.googlemapsforseminar02;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;

import java.util.ArrayList;

import cn.fanrunqi.waveprogress.WaveProgressView;

public class GGMapDirectionAPI extends AppCompatActivity implements OnMapReadyCallback, LocationListener{

    private static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    private static final String MYTAG = "MYTAG";
    private ArrayList<LatLng> listStep;
    private PolylineOptions polyline;
    private Polyline line;
    private GoogleMap map;
    private AsyncTask<Position, Void, Void> task;
    private WaveProgressView waveProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ggmap_direction_api);
        waveProgressView = findViewById(R.id.waveProgressbar);
        waveProgressView.setCurrent(55,"");
        waveProgressView.setMaxProgress(100);
        waveProgressView.setText("#FFFF00", 40);
        waveProgressView.setWaveColor("#5b9ef4"); //"#5b9ef4"
        waveProgressView.setWave(5, 20);
        waveProgressView.setmWaveSpeed(10);//The larger the value, the slower the vibration

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDirection));
        mapFragment.getMapAsync(this);
    }


    public String makeURL (String sourcelat, String sourcelng, String destlat, String destlng ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(sourcelat);
        urlString.append(",");
        urlString.append(sourcelng);
        urlString.append("&destination=");// to
        urlString.append(destlat);
        urlString.append(",");
        urlString.append(destlng);
        urlString.append("&mode=walking");
        urlString.append("&key="+getResources().getString(R.string.google_api_key));
        return urlString.toString();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Tiêu chí để tìm một nhà cung cấp vị trí.
        Criteria criteria = new Criteria();

        // Tìm một nhà cung vị trí hiện thời tốt nhất theo tiêu chí trên.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

    private Location getMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = this.getEnabledLocationProvider();
        Log.d(MYTAG, "test");
        if (locationProvider == null) {
            return null;
        }
        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
        Location myLocation = null;
        try {

            // Đoạn code nay cần người dùng cho phép (Hỏi ở trên ***).
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

            // Lấy ra vị trí.
            myLocation = locationManager.getLastKnownLocation(locationProvider);
            return myLocation;
        }
        // Với Android API >= 23 phải catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Marker createTreeMarker(LatLng position, int iconID, float alpha){
        MarkerOptions option = new MarkerOptions();
        option.position(position);
        option.title("Cây thông").snippet("Tôi thiếu nước. Hãy tưới cho tôi");
        option.icon(bitmapDescriptorFromVector(this, iconID));
        option.alpha(alpha);
        option.rotation(0);
        return map.addMarker(option);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        LatLng haYen = new LatLng(21.0207512, 105.7938957);
        LatLng layNuoc = new LatLng(21.030754, 105.7938977);
        LatLng PhoDiBoNguyenHue = new LatLng(10.774467, 106.703274);

        Marker maker = createTreeMarker(haYen, R.drawable.ic_tree_with_three_circles_of_foliage, 1.0f);
        new Handler().postDelayed(new AnimateMarker(maker, 0.5F, 0.1F), 50);
        maker.showInfoWindow();

        final Marker makerWater = createTreeMarker(layNuoc, R.drawable.ic_tree_with_three_circles_of_foliage, 1.0f);
        new Handler().postDelayed(new AnimateMarker(makerWater, 0.5F, 0.1F), 50);
        makerWater.showInfoWindow();

        Marker maker2 = createTreeMarker(PhoDiBoNguyenHue, R.drawable.ic_big_pine_tree_shape, 1.0f);
        maker2.showInfoWindow();

        waveProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listStep = new ArrayList<LatLng>();
                polyline = new PolylineOptions();
                Position position = new Position();
                position.setDesLat(Double.toString(makerWater.getPosition().latitude));
                position.setDesIng(Double.toString(makerWater.getPosition().longitude));

                task = new AsyncTask<Position, Void, Void>() {

                    @Override
                    protected Void doInBackground(Position... params) {

                        // Ha Yen : 21.0207512, 105.7938957
                        Location location = getMyLocation();
                        String request = makeURL(location.getLatitude()+"", location.getLongitude()+"",
                                params[0].getDesLat(), params[0].getDesIng());
                        Log.d("Test URL: ", request);
                        GetDirectionsTask task = new GetDirectionsTask(request);
                        ArrayList<LatLng> list = task.testDirection();
                        for (LatLng latLng : list) {
                            listStep.add(latLng);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);
                        int arrowColor = Color.BLUE;
                        BitmapDescriptor endCapIcon = getEndCapIcon(GGMapDirectionAPI.this, arrowColor);
                        polyline.addAll(listStep);
                        polyline.jointType(JointType.ROUND);
                        polyline.geodesic(true);
                        polyline.startCap(new CustomCap(endCapIcon,8));
                        if (line != null)
                            line.remove();
                        line = map.addPolyline(polyline);
                        line.setColor(Color.BLUE);
                        line.setWidth(10);
                    }
                };
                task.execute(position);
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public boolean onMarkerClick(Marker marker) {
                listStep = new ArrayList<LatLng>();
                polyline = new PolylineOptions();
                Position position = new Position();
                position.setDesLat(Double.toString(marker.getPosition().latitude));
                position.setDesIng(Double.toString(marker.getPosition().longitude));

                 task = new AsyncTask<Position, Void, Void>() {

                    @Override
                    protected Void doInBackground(Position... params) {

                        // Ha Yen : 21.0207512, 105.7938957
                        Location location = getMyLocation();
                        String request = makeURL(location.getLatitude()+"", location.getLongitude()+"",
                                params[0].getDesLat(), params[0].getDesIng());
                        Log.d("Test URL: ", request);
                        GetDirectionsTask task = new GetDirectionsTask(request);
                        ArrayList<LatLng> list = task.testDirection();
                        for (LatLng latLng : list) {
                            listStep.add(latLng);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);
                        int arrowColor = Color.BLUE;
                        BitmapDescriptor endCapIcon = getEndCapIcon(GGMapDirectionAPI.this, arrowColor);
                        polyline.addAll(listStep);
                        polyline.jointType(JointType.ROUND);
                        polyline.geodesic(true);
                        polyline.startCap(new CustomCap(endCapIcon,8));
                        if (line != null)
                            line.remove();
                        line = map.addPolyline(polyline);
                        line.setColor(Color.BLUE);
                        line.setWidth(10);
                    }
                };
                task.execute(position);
                return false;
            }
        });
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                int accessCoarsePermission
                        = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                int accessFinePermission
                        = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                        || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                    // Các quyền cần người dùng cho phép.
                    String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION};

                    // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                    ActivityCompat.requestPermissions(this, permissions,
                            REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                    return;
                }
            }
        }
        map.setMyLocationEnabled(true);
        this.showMyLocation();
    }

    public BitmapDescriptor getEndCapIcon(Context context,  int color) {
        // mipmap icon - white arrow, pointing up, with point at center of image
        // you will want to create:  mdpi=24x24, hdpi=36x36, xhdpi=48x48, xxhdpi=72x72, xxxhdpi=96x96
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_send_black_24dp);
        // set the bounds to the whole image (may not be necessary ...)
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        // overlay (multiply) your color over the white icon
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        // create a bitmap from the drawable
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // render the bitmap on a blank canvas
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        // create a BitmapDescriptor from the new bitmap
        return BitmapDescriptorFactory.fromBitmap(rotateBitmap(bitmap, 90));
    }
    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {

                // Chú ý: Nếu yêu cầu bị bỏ qua, mảng kết quả là rỗng.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Hiển thị vị trí hiện thời trên bản đồ.
                    this.showMyLocation();
                }
                // Hủy bỏ hoặc từ chối.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void showMyLocation() {
        Location myLocation = getMyLocation();
        if (myLocation != null) {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location not found");
        }
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
