package vn.edu.khtn.googlemapsforseminar02;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import cn.fanrunqi.waveprogress.WaveProgressView;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener{

    private static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    private static final String MYTAG = "MYTAG";
    private ArrayList<LatLng> listStep;
    private PolylineOptions polyline;
    private Polyline line;
    private GoogleMap map;
    private AsyncTask<Position, Void, Void> task;
    private WaveProgressView waveProgressView;
    private FragmentActivity context;
    private ArrayList<Marker> listMarker;
    private ArrayList<Marker> listMarkerCannotWater;
    private ArrayList<Marker> listMarkerCanWater;
    private ArrayList<Marker> listMarkerWater;
    public static boolean isMultipleChoiceMarker = false;
    private ArrayList<Marker> listMarkerSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listMarker = new ArrayList<>();
        listMarkerCannotWater = new ArrayList<>();
        listMarkerCanWater = new ArrayList<>();
        listMarkerWater = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        waveProgressView = view.findViewById(R.id.waveProgressbar);
        waveProgressView.setCurrent(80,"");
        waveProgressView.setMaxProgress(100);
        waveProgressView.setText("#FFFF00", 40);
        waveProgressView.setWaveColor("#5b9ef4"); //"#5b9ef4"
        waveProgressView.setWave(5, 20);
        waveProgressView.setmWaveSpeed(10);//The larger the value, the slower the vibration
        context = getActivity();
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapDirection));
        mapFragment.getMapAsync(this);
        return view;
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
        urlString.append("&sensor=false");
        urlString.append("&key="+getResources().getString(R.string.google_api_key));
        return urlString.toString();
    }

    public String makeURLWithMultipleMarker (String sourcelat, String sourcelng, String destlat, String destlng, ArrayList<Marker> markers ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(sourcelat);
        urlString.append(",");
        urlString.append(sourcelng);
        if(markers.size() >= 2){
            for (int i = 0; i < markers.size() - 1; i ++){
                urlString.append("&waypoints=");// to
                urlString.append(markers.get(i).getPosition().latitude);
                urlString.append(",");
                urlString.append(markers.get(i).getPosition().longitude);
                urlString.append("|");
            }
            urlString.append("&waypoints=");// to
            urlString.append(markers.get(markers.size()-1).getPosition().latitude);
            urlString.append(",");
            urlString.append(markers.get(markers.size()-1).getPosition().longitude);
        } else {
            urlString.append("&waypoints=");// to
            urlString.append(markers.get(0).getPosition().latitude);
            urlString.append(",");
            urlString.append(markers.get(0).getPosition().longitude);
        }

        urlString.append("&destination=");// to
        urlString.append(destlat);
        urlString.append(",");
        urlString.append(destlng);
        urlString.append("&mode=walking");
        urlString.append("&sensor=false");
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
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        // Tiêu chí để tìm một nhà cung cấp vị trí.
        Criteria criteria = new Criteria();

        // Tìm một nhà cung vị trí hiện thời tốt nhất theo tiêu chí trên.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(context, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

    private Location getMyLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
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
            Toast.makeText(context, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Marker createTreeMarker(LatLng position, int iconID, float alpha){
        MarkerOptions option = new MarkerOptions();
        option.position(position);
        option.icon(bitmapDescriptorFromVector(context, iconID));
        option.alpha(alpha);
        option.rotation(0);
        Marker marker = map.addMarker(option);
        listMarker.add(marker);
        return marker;
    }
    private Marker makerWater;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        if (listMarker.size() == 0) {
            LatLng haYen = new LatLng(21.0207512, 105.7938957);
            LatLng layNuoc = new LatLng(21.030754, 105.7938977);
            LatLng haYen2 = new LatLng(21.0228512, 105.7938957);

            Marker makerWater = createTreeMarker(new LatLng(21.004450, 105.843359), R.drawable.ic_tap, 1.0f);
            listMarkerWater.add(makerWater);
            makerWater.setTitle("Chỗ lấy nước");
            new Handler().postDelayed(new AnimateMarker(makerWater, 1, 0.5F, 0.1F), 50);

            Marker makerWater1 = createTreeMarker(new LatLng(21.005666, 105.841769), R.drawable.ic_tap, 1.0f);
            listMarkerWater.add(makerWater1);
            makerWater.setTitle("Chỗ lấy nước");
            new Handler().postDelayed(new AnimateMarker(makerWater1, 1, 0.5F, 0.1F), 50);

            Marker makerWater2 = createTreeMarker(new LatLng(21.006926, 105.844432), R.drawable.ic_tap, 1.0f);
            listMarkerWater.add(makerWater2);
            makerWater.setTitle("Chỗ lấy nước");
            new Handler().postDelayed(new AnimateMarker(makerWater2, 1, 0.5F, 0.1F), 50);

            Marker makerWater3 = createTreeMarker(new LatLng(21.004620, 105.845105), R.drawable.ic_tap, 1.0f);
            listMarkerWater.add(makerWater3);
            makerWater.setTitle("Chỗ lấy nước");
            new Handler().postDelayed(new AnimateMarker(makerWater3, 1, 0.5F, 0.1F), 50);

            Marker makerWater4 = createTreeMarker(new LatLng(21.007145, 105.842410), R.drawable.ic_tap, 1.0f);
            listMarkerWater.add(makerWater4);
            makerWater.setTitle("Chỗ lấy nước");
            new Handler().postDelayed(new AnimateMarker(makerWater4, 1, 0.5F, 0.1F), 50);

            Marker maker = createTreeMarker(new LatLng(21.006543, 105.841761), R.drawable.ic_tree_with_three_circles_of_foliage, 1.0f);
            maker.setTitle("Cây bàng");
            maker.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(maker);
            new Handler().postDelayed(new AnimateMarker(maker, 0.1F, 0.1F), 50);
            maker.showInfoWindow();

            Marker maker2 = createTreeMarker(new LatLng(21.004140, 105.844579), R.drawable.ic_big_pine_tree_shape_green, 1.0f);
            maker2.setTitle("Cây thông");
            maker2.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(maker2);

            Marker marker3 = createTreeMarker(new LatLng(21.005459, 105.842923), R.drawable.autumn_tree_silhouette_green, 1.0f);
            marker3.setTitle("Anh đào");
            marker3.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker3);
            new Handler().postDelayed(new AnimateMarker(marker3, 0.1F, 0.1F), 50);
            marker3.showInfoWindow();

            Marker marker4 = createTreeMarker(new LatLng(21.005180, 105.843667), R.drawable.tree_of_hand_draw_foliage_circles, 1.0f);
            marker4.setTitle("Anh tuc");
            marker4.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker4);
            new Handler().postDelayed(new AnimateMarker(marker4, 0.1F, 0.1F), 50);
            marker4.showInfoWindow();

            Marker marker5 = createTreeMarker(new LatLng(21.005050, 105.843015), R.drawable.tree_of_irregular_shape_branches_on_winter_without_leaves_green, 1.0f);
            marker5.setTitle("Cam");
            marker5.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker5);

            Marker marker21 = createTreeMarker(new LatLng(21.007228, 105.842671), R.drawable.tree_of_irregular_shape_branches_on_winter_without_leaves_green, 1.0f);
            marker21.setTitle("Cam");
            marker21.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker21);

            Marker marker6 = createTreeMarker(new LatLng(21.004489, 105.843056), R.drawable.tree_black_silhouette_shape, 1.0f);
            marker6.setTitle("Banana");
            marker6.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker6);
            new Handler().postDelayed(new AnimateMarker(marker6, 0.1F, 0.1F), 50);
            marker6.showInfoWindow();

            Marker marker22 = createTreeMarker(new LatLng(21.006108, 105.843149), R.drawable.tree_black_silhouette_shape, 1.0f);
            marker22.setTitle("Banana");
            marker22.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker22);
            new Handler().postDelayed(new AnimateMarker(marker22, 0.1F, 0.1F), 50);
            marker22.showInfoWindow();

            Marker marker7 = createTreeMarker(new LatLng(21.004134, 105.843704), R.drawable.tall_tree_rounded_shape_of_three_areas_green, 1.0f);
            marker7.setTitle("Bưởi");
            marker7.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker7);

            Marker marker23 = createTreeMarker(new LatLng(21.006251, 105.842154), R.drawable.tall_tree_rounded_shape_of_three_areas_green, 1.0f);
            marker23.setTitle("Bưởi");
            marker23.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker23);

            Marker marker8 = createTreeMarker(new LatLng(21.004673, 105.844584), R.drawable.tree_symmetrical_shape_with_oval_foliage, 1.0f);
            marker8.setTitle("Xoài");
            marker8.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker8);
            new Handler().postDelayed(new AnimateMarker(marker8, 0.1F, 0.1F), 50);
            marker8.showInfoWindow();

            Marker marker24 = createTreeMarker(new LatLng(21.006786, 105.844589), R.drawable.tree_symmetrical_shape_with_oval_foliage, 1.0f);
            marker24.setTitle("Xoài");
            marker24.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker24);
            new Handler().postDelayed(new AnimateMarker(marker24, 0.1F, 0.1F), 50);
            marker24.showInfoWindow();

            Marker marker9 = createTreeMarker(new LatLng(21.004614, 105.843717), R.drawable.tree_trunk_and_leaves_green, 1.0f);
            marker9.setTitle("Dâu tây");
            marker9.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker9);

            Marker marker25 = createTreeMarker(new LatLng(21.004878, 105.844951), R.drawable.tree_trunk_and_leaves_green, 1.0f);
            marker25.setTitle("Dâu tây");
            marker25.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker25);

            Marker marker10 = createTreeMarker(new LatLng(21.004097, 105.844157), R.drawable.tree_black_silhouette_shape_green, 1.0f);
            marker10.setTitle("Duong xi");
            marker10.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker10);

            Marker marker11 = createTreeMarker(new LatLng(21.006104, 105.842925), R.drawable.tree_trunk_growing_from_soil, 1.0f);
            marker11.setTitle("Rose");
            marker11.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker11);
            new Handler().postDelayed(new AnimateMarker(marker11, 0.1F, 0.1F), 50);
            marker11.showInfoWindow();

            Marker marker12 = createTreeMarker(new LatLng(21.006386, 105.843302), R.drawable.palm_tree_outline, 1.0f);
            marker12.setTitle("Cây dừa");
            marker12.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker12);
            new Handler().postDelayed(new AnimateMarker(marker12, 0.1F, 0.1F), 50);
            marker12.showInfoWindow();

            Marker marker13 = createTreeMarker(new LatLng(21.005617, 105.842227), R.drawable.tree_irregular_silhouette, 1.0f);
            marker13.setTitle("Cây táo ta");
            marker13.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker13);
            new Handler().postDelayed(new AnimateMarker(marker13, 0.1F, 0.1F), 50);
            marker13.showInfoWindow();

            Marker marker14 = createTreeMarker(new LatLng(21.005934, 105.843491), R.drawable.tree_of_dots_foliage_green, 1.0f);
            marker14.setTitle("Đu đủ");
            marker14.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker14);

            Marker marker15 = createTreeMarker(new LatLng(21.005200, 105.845201), R.drawable.trees_two_tall_shapes_green, 1.0f);
            marker15.setTitle("Cây bạch đàn");
            marker15.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker15);

            Marker marker16 = createTreeMarker(new LatLng(21.005679, 105.844647), R.drawable.tree_gross_outline_of_triangular_shape, 1.0f);
            marker16.setTitle("Cây đào");
            marker16.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker16);
            new Handler().postDelayed(new AnimateMarker(marker16, 0.1F, 0.1F), 50);
            marker16.showInfoWindow();

            Marker marker17 = createTreeMarker(new LatLng(21.004966, 105.842018), R.drawable.tree, 1.0f);
            marker17.setTitle("Cây chanh");
            marker17.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker17);
            new Handler().postDelayed(new AnimateMarker(marker17, 0.1F, 0.1F), 50);
            marker17.showInfoWindow();

            Marker marker18 = createTreeMarker(new LatLng(21.006715, 105.842861), R.drawable.tree_of_spirals, 1.0f);
            marker18.setTitle("Cây phượng vĩ");
            marker18.setSnippet("Tôi thiếu nước. Hãy tưới cho tôi");
            listMarkerCanWater.add(marker18);
            new Handler().postDelayed(new AnimateMarker(marker18, 0.1F, 0.1F), 50);
            marker18.showInfoWindow();

            Marker marker19 = createTreeMarker(new LatLng(21.004530, 105.842663), R.drawable.trees_couple_with_circular_foliage_green, 1.0f);
            marker19.setTitle("Cây chò nâu");
            marker19.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker19);

            Marker marker20 = createTreeMarker(new LatLng(21.006489, 105.844245), R.drawable.tree_of_oval_horizontal_foliage_green, 1.0f);
            marker20.setTitle("Cây ổi");
            marker20.setSnippet("Tôi đã đủ nước rồi");
            listMarkerCannotWater.add(marker20);
        }
        waveProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMultipleChoiceMarker){
                    Location location = getMyLocation();
                    final String URL = makeURLWithMultipleMarker(location.getLatitude() + "", location.getLongitude() + "",
                            String.valueOf(listMarkerSelected.get(listMarkerSelected.size()-1).getPosition().latitude),
                            String.valueOf(listMarkerSelected.get(listMarkerSelected.size()-1).getPosition().longitude),
                            listMarkerSelected);
                    Log.d("TEST", URL);
                    listStep = new ArrayList<LatLng>();
                    polyline = new PolylineOptions();
                    Position position = new Position(String.valueOf(listMarkerSelected.get(listMarkerSelected.size()-1).getPosition().latitude),
                            String.valueOf(listMarkerSelected.get(listMarkerSelected.size()-1).getPosition().longitude));
                    task = new AsyncTask<Position, Void, Void>() {
                        @Override
                        protected Void doInBackground(Position... params) {
                            // Ha Yen : 21.0207512, 105.7938957
                            Location location = getMyLocation();
                            String request = URL;
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
                            BitmapDescriptor endCapIcon = getEndCapIcon(context, arrowColor);
                            polyline.addAll(listStep);
                            polyline.jointType(JointType.ROUND);
                            polyline.geodesic(true);
                            polyline.startCap(new CustomCap(endCapIcon, 8));
                            if (line != null)
                                line.remove();
                            line = map.addPolyline(polyline);
                            line.setColor(Color.BLUE);
                            line.setWidth(10);
                        }
                    };
                    task.execute(position);
                } else {
                    SoundEffects.getInstance(context).playSoundClick();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Lấy nước");
                    builder.setIcon(R.drawable.ic_add_water_black_24dp);
                    builder.setMessage("Bạn có muốn đi lấy thêm nước không ?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SoundEffects.getInstance(context).playSoundClick();
                            Toast.makeText(context, "Bắt đàu di chuyển để lấy nước", Toast.LENGTH_SHORT).show();
                            listStep = new ArrayList<LatLng>();
                            polyline = new PolylineOptions();
                            Position position = new Position();
                            position.setDesLat(Double.toString(listMarker.get(1).getPosition().latitude));
                            position.setDesIng(Double.toString(listMarker.get(1).getPosition().longitude));
                            task = new AsyncTask<Position, Void, Void>() {

                                @Override
                                protected Void doInBackground(Position... params) {

                                    // Ha Yen : 21.0207512, 105.7938957
                                    Location location = getMyLocation();
                                    String request = makeURL(location.getLatitude() + "", location.getLongitude() + "",
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
                                    BitmapDescriptor endCapIcon = getEndCapIcon(context, arrowColor);
                                    polyline.addAll(listStep);
                                    polyline.jointType(JointType.ROUND);
                                    polyline.geodesic(true);
                                    polyline.startCap(new CustomCap(endCapIcon, 8));
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
                    builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SoundEffects.getInstance(context).playSoundClick();
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if(!isMultipleChoiceMarker) {
                    if (!listMarkerWater.contains(marker)) {
                        if (listMarkerCanWater.contains(marker)) {
                            if (marker.getAlpha() == 1.0f && polyline != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Xác nhận tưới cây");
                                builder.setMessage("Bạn đã tưới cây này chưa ?");
                                builder.setIcon(R.drawable.ic_tree_with_three_circles_of_foliage_green);
                                builder.setCancelable(false);
                                builder.setPositiveButton("Đã tưới", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SoundEffects.getInstance(context).playSoundClick();
                                        waveProgressView.setCurrent(30, "");
                                        waveProgressView.setMaxProgress(100);
                                        waveProgressView.setWaveColor("#D80027");
                                        waveProgressView.setWave(5, 20);
                                        waveProgressView.setmWaveSpeed(10);
                                        marker.setIcon(bitmapDescriptorFromVector(context,
                                                R.drawable.ic_tree_with_three_circles_of_foliage_green));
                                        listMarkerCanWater.remove(marker);
                                        listMarkerCannotWater.add(marker);
                                    }
                                });
                                builder.setNegativeButton("Chưa tưới", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SoundEffects.getInstance(context).playSoundClick();
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else {
                                SoundEffects.getInstance(context).playSoundClick();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                LayoutInflater factory = LayoutInflater.from(context);
                                final View view = factory.inflate(R.layout.dialog_layout, null);
                                builder.setView(view);
                                builder.setTitle("Tưới cây");
                                builder.setIcon(R.drawable.ic_tree_with_three_circles_of_foliage);
                                builder.setCancelable(false);
                                builder.setPositiveButton("Tưới nước", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SoundEffects.getInstance(context).playSoundClick();
                                        Toast.makeText(context, "Bắt đầu di chuyển để tưới", Toast.LENGTH_SHORT).show();
                                        for (int j = 0; j < listMarker.size(); j++) {
                                            listMarker.get(j).setAlpha(0.5F);
                                        }
                                        marker.setAlpha(1.0f);
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
                                                String request = makeURL(location.getLatitude() + "", location.getLongitude() + "",
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
                                                BitmapDescriptor endCapIcon = getEndCapIcon(context, arrowColor);
                                                polyline.addAll(listStep);
                                                polyline.jointType(JointType.ROUND);
                                                polyline.geodesic(true);
                                                polyline.startCap(new CustomCap(endCapIcon, 8));
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
                                builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SoundEffects.getInstance(context).playSoundClick();
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Lỗi");
                            builder.setMessage("Xin hãy chọn cây có màu vàng hoặc đỏ để tưới");
                            builder.setIcon(R.drawable.ic_big_pine_tree_shape);
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SoundEffects.getInstance(context).playSoundClick();
                                    dialogInterface.dismiss();

                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } else {
                        if (polyline != null && marker.getAlpha() == 1.0f) {
                            SoundEffects.getInstance(context).playSoundClick();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Xác nhận lấy nước");
                            builder.setIcon(R.drawable.ic_add_water_black_24dp);
                            builder.setMessage("Bạn đã lấy nước xong ? ");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    waveProgressView.setWaveColor("#5b9ef4");
                                    waveProgressView.setCurrent(80, "");
                                    Polyline poly = map.addPolyline(polyline);
                                    poly.remove();
                                }
                            });
                            builder.setNegativeButton("Chưa lấy xong", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SoundEffects.getInstance(context).playSoundClick();
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            SoundEffects.getInstance(context).playSoundClick();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Lấy nước");
                            builder.setIcon(R.drawable.ic_add_water_black_24dp);
                            builder.setMessage("Bạn có muốn lấy nước không? ");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SoundEffects.getInstance(context).playSoundClick();
                                    Toast.makeText(context, "Bắt đầu di chuyển để lấy nước", Toast.LENGTH_SHORT).show();
                                    for (int j = 0; j < listMarker.size(); j++) {
                                        listMarker.get(j).setAlpha(0.5F);
                                    }
                                    marker.setAlpha(1.0f);
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
                                            String request = makeURL(location.getLatitude() + "", location.getLongitude() + "",
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
                                            BitmapDescriptor endCapIcon = getEndCapIcon(context, arrowColor);
                                            polyline.addAll(listStep);
                                            polyline.jointType(JointType.ROUND);
                                            polyline.geodesic(true);
                                            polyline.startCap(new CustomCap(endCapIcon, 8));
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
                            builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SoundEffects.getInstance(context).playSoundClick();
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                } else {
                    listMarkerSelected.add(marker);
                    marker.setAlpha(1.0f);
                }
                return false;
            }
        });
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                int accessCoarsePermission
                        = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                int accessFinePermission
                        = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                        || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                    // Các quyền cần người dùng cho phép.
                    String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION};

                    // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                    ActivityCompat.requestPermissions(context, permissions,
                            REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                    return;
                }
            }
        }
        map.setMyLocationEnabled(true);
        this.showMyLocation();
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                ((MainActivity)getActivity()).getSupportActionBar().setTitle("Chọn nhiều cây");
                listMarkerSelected = new ArrayList();
                isMultipleChoiceMarker = true;
                for (int i = 0; i < listMarker.size(); i ++){
                    listMarker.get(i).setAlpha(0.5F);
                }
            }
        });
    }

    private void startFindWayWithMultipleMarker(){

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

                    Toast.makeText(context, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Hiển thị vị trí hiện thời trên bản đồ.
                    this.showMyLocation();
                }
                // Hủy bỏ hoặc từ chối.
                else {
                    Toast.makeText(context, "Permission denied!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, "Location not found!", Toast.LENGTH_LONG).show();
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
