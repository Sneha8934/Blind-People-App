package com.sneha.blindpeople.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapOverlay;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;
import com.sneha.blindpeople.GeocodingLocation;
import com.sneha.blindpeople.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GeolocationActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private LinearLayout bottom_sheet;
    TextToSpeech t1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private AndroidXMapFragment mapFragment = null;
    private PositioningManager positioningManager = null;
    private EditText editText = null;
    private EditText fromText = null;
    private EditText toText = null;
    private ArrayList<MapObject> markers = new ArrayList<MapObject>();
    private PositioningManager.OnPositionChangedListener positionListener;
    private GeoCoordinate currentPosition;
    private GeoCoordinate coordinate;
    private String[] PlaceData;

    private Button places;
    private Button RouteButton;
    private MapRoute mRoute = null;
    private MapMarker FromMarker;
    public static List<DiscoveryResult> s_ResultList;

    Random random = new Random();

    //  private TextView txtView_title;
    private TextView txtview_distance;
    private TextView txtview_time;

    private TextView txtview_coupons;
    private LinearLayout input_of_destination;
    private final int REQ_CODE = 100;
    String mostRecentUtteranceID;
    private TextView destinationLocation;
    private  Double destinationLocationLat;
    private  Double destinationLocationLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocation);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        //  txtView_title = findViewById(R.id.txtView_title);
        txtview_time = findViewById(R.id.txtview_time);
        txtview_distance = findViewById(R.id.txtview_distance);
        txtview_coupons = findViewById(R.id.txtview_coupons);
        bottom_sheet = findViewById(R.id.bottom_sheet);

        input_of_destination = (LinearLayout) findViewById(R.id.input_of_destination);
        destinationLocation = (TextView) findViewById(R.id.txtName);

        input_of_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setCenter(coordinate, Map.Animation.NONE);
                map.setZoomLevel(16);
            }
        });

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (t1.getEngines().size() == 0) {
                    Toast.makeText(GeolocationActivity.this, "No Engines Installed", Toast.LENGTH_LONG).show();
                } else {
                    if (status == TextToSpeech.SUCCESS) {
                        ttsInitialized();
                    }
                }
            }
        });

        input_of_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        bottom_sheet.setVisibility(View.GONE);
        //createRoute();


        // Search for the map fragment to finish setup by calling init().
        final AndroidXMapFragment mapFragment = (AndroidXMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        // Set up disk cache path for the map service for this application
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "mapService");


        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG);
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {


                    if (error == OnEngineInitListener.Error.NONE) {
                        // retrieve a reference of the map from the map fragment
                        map = mapFragment.getMap();
                        // Set the map center to the Vancouver region (no animation)
                        //map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0),Map.Animation.NONE);
                        // Set the zoom level to the average between min and max
                        map.setMapScheme(Map.Scheme.NORMAL_DAY);
                        map.getPedestrianFeaturesVisible();
                        map.setLandmarksVisible(true);

                        //marker.setCoordinate(coordinate);
                        //marker.setTitle(placeLink.getTitle());
                        map.setExtrudedBuildingsVisible(true);
                        //initCreateRouteButton();
                        /////The code of getting the lat long of user starts here///////////////
                        positioningManager = PositioningManager.getInstance();
                        mapFragment.getMapGesture()
                                .addOnGestureListener(new MapGesture.OnGestureListener() {
                                    @Override
                                    public void onPanStart() {
                                        //showMsg("onPanStart");
                                    }

                                    @Override
                                    public void onPanEnd() {
                                        /* show toast message for onPanEnd gesture callback */
                                        //showMsg("onPanEnd");
                                    }

                                    @Override
                                    public void onMultiFingerManipulationStart() {

                                    }

                                    @Override
                                    public void onMultiFingerManipulationEnd() {
                                    }

                                    @Override
                                    public boolean onMapObjectsSelected(List<ViewObject> list) {
                                        bottom_sheet.setVisibility(View.GONE);
                                        for (ViewObject viewObject : list) {
                                            if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                                                final MapObject mapObject = (MapObject) viewObject;
                                                if (mapObject.getType() == MapObject.Type.MARKER) {
                                                    final MapMarker window_marker = ((MapMarker) mapObject);

                                                    coordinate = window_marker.getCoordinate();
                                                    bottom_sheet.setVisibility(View.VISIBLE);

                                                    //  txtView_title.setText(window_marker.getTitle());
                                                    txtview_coupons.setText(((MapMarker) mapObject).getDescription());
                                                    System.out.println(((MapMarker) mapObject).getDescription());

                                                    txtview_time.setVisibility(View.GONE);
                                                    txtview_distance.setVisibility(View.GONE);
                                                    txtview_coupons.setVisibility(View.GONE);
                                                    Log.d("danish", "danish");
                                                    return false;
                                                }
                                            }
                                        }
                                        return false;
                                    }

                                    @Override
                                    public boolean onTapEvent(PointF pointF) {
                                        /* show toast message for onPanEnd gesture callback */
                                        // showMsg("onTapEvent");

                                        return false;
                                    }

                                    @Override
                                    public boolean onDoubleTapEvent(PointF pointF) {
                                        return false;
                                    }

                                    @Override
                                    public void onPinchLocked() {

                                    }

                                    @Override
                                    public boolean onPinchZoomEvent(float v, PointF pointF) {
                                        return false;
                                    }

                                    @Override
                                    public void onRotateLocked() {

                                    }

                                    @Override
                                    public boolean onRotateEvent(float v) {
                                        /* show toast message for onRotateEvent gesture callback */
                                        //showMsg("onRotateEvent");
                                        return false;
                                    }

                                    @Override
                                    public boolean onTiltEvent(float v) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onLongPressEvent(PointF pointF) {
                                        return false;
                                    }

                                    @Override
                                    public void onLongPressRelease() {

                                    }

                                    @Override
                                    public boolean onTwoFingerTapEvent(PointF pointF) {
                                        return false;
                                    }
                                }, 0, false);


                        positionListener = new PositioningManager.OnPositionChangedListener() {
                            @Override
                            public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position, boolean isMapMatched) {
                                currentPosition = position.getCoordinate();
                                //map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);

                            }

                            @Override
                            public void onPositionFixChanged(PositioningManager.LocationMethod method, PositioningManager.LocationStatus status) {
                            }
                        };
                        try {
                            positioningManager.addListener(new WeakReference<>(positionListener));
                            if (!positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK)) {
                                Log.e("HERE", "PositioningManager.start: Failed to start...");
                            }

                        } catch (Exception e) {
                            Log.e("HERE", "Caught: " + e.getMessage());
                        }
                        mapFragment.getPositionIndicator().setVisible(true);
                        map.setCenter(positioningManager.getLastKnownPosition().getCoordinate(), Map.Animation.NONE);
                        map.setZoomLevel(18);
                        currentPosition = positioningManager.getLastKnownPosition().getCoordinate();

                    } else {
                        Log.e("Error", "ERROR: Cannot initialize Map Fragment " + error);
                    }
                }


            });
        }
        checkPermissions();
    }

    private void ttsInitialized() {

        // *** set UtteranceProgressListener AFTER tts is initialized ***
        t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                Log.i("XXX", "Done with the string");
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        // set Language
        t1.setLanguage(Locale.US);

        // set unique utterance ID for each utterance
        mostRecentUtteranceID = (new Random().nextInt() % 9999999) + ""; // "" is String force

        // set params
        // *** this method will work for more devices: API 19+ ***
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);

        t1.speak("Tell us where you want to go by tapping on the Screen", TextToSpeech.QUEUE_FLUSH, params);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    destinationLocation.setText(result.get(0).toString());
                    GeocodingLocation locationAddress = new GeocodingLocation();
                    locationAddress.getAddressFromLocation(destinationLocation.getText().toString().trim(),
                            getApplicationContext(), new GeocoderHandler());
                }
                break;
            }
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            destinationLocation.setText(locationAddress);

            String[] separated = destinationLocation.getText().toString().trim().split(":");
            //String destinationLatStr = separated[0];
            if(destinationLocation.getText().toString().trim().contains("Yes")){
                String destinationLongStr = separated[1];
                String[] separatedLatLong = destinationLongStr.split(" ");
                destinationLocationLat = Double.valueOf(separatedLatLong[0]);
                destinationLocationLong = Double.valueOf(separatedLatLong[1]);
                createRoute();
            }
            else{
                destinationLocation.setText("Please Try Again");
            }
        }
    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    protected void checkPermissions()
    {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS)
        {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty())
        {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else

        {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                break;
        }
    }



    private ResultListener<DiscoveryResultPage> discoveryResultPageListener = new ResultListener<DiscoveryResultPage>() {
        @Override
        public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode) {
            if (errorCode == ErrorCode.NONE) {
                s_ResultList = discoveryResultPage.getItems();

                for (DiscoveryResult item : s_ResultList) {

                    if (item.getResultType() == DiscoveryResult.ResultType.PLACE) {

                        Image img = new Image();

                        PlaceLink placeLink = (PlaceLink) item;
                        MapMarker marker = new MapMarker();

                        marker.setCoordinate(placeLink.getPosition());
                        //marker.setTitle(placeLink.getTitle());
                        placeLink.getPosition().getLatitude();

                        map.addMapObjects(markers);

                    }
                }
            } else {
                Toast.makeText(GeolocationActivity.this,
                        "ERROR:Discovery search request returned return error code+ " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private ResultListener<DiscoveryResultPage> discoveryResultPageListenerMalls = new ResultListener<DiscoveryResultPage>() {
        @Override
        public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode) {
            if (errorCode == ErrorCode.NONE) {

                s_ResultList = discoveryResultPage.getItems();

                for (DiscoveryResult item : s_ResultList) {
                    if (item.getResultType() == DiscoveryResult.ResultType.PLACE) {
                        Image img = new Image();
                        PlaceLink placeLink = (PlaceLink) item;
                        MapMarker marker = new MapMarker();
                        marker.setCoordinate(placeLink.getPosition());
                        //marker.setTitle(placeLink.getTitle());

                        //textView.setText(placeLink.getTitle());



                        int randomNum = 10;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            randomNum = ThreadLocalRandom.current().nextInt(10, 49 + 1);
                        }


                        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                        linearLayout.setBackgroundResource(R.color.blue_btn_bg_color);
                        linearLayout.setPadding(10, 10, 10, 10);


                        GeoCoordinate geoCoordinates = new GeoCoordinate(placeLink.getPosition().getLatitude(), placeLink.getPosition().getLongitude());
                        MapOverlay mapOverlay = new MapOverlay(linearLayout, geoCoordinates);

                        map.addMapOverlay(mapOverlay);
                        marker.setIcon(img);
                        marker.setDescription("Offer - " + randomNum + " %");

                        markers.add(marker);
                        map.addMapObjects(markers);
                    }
                }
            } else {
                Toast.makeText(GeolocationActivity.this,
                        "ERROR:Discovery search request returned return error code+ " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void createRoute() {

        /* Initialize a CoreRouter */
        CoreRouter coreRouter = new CoreRouter();

        /* Initialize a RoutePlan */
        final RoutePlan routePlan = new RoutePlan();

        /*
         * Initialize a RouteOption.HERE SDK allow users to define their own parameters for the
         * route calculation,including transport modes,route types and route restrictions etc.Please
         * refer to API doc for full list of APIs
         */
        RouteOptions routeOptions = new RouteOptions();
        /* Other transport modes are also available e.g Pedestrian */
        routeOptions.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
        /* Disable highway in this route. */
        routeOptions.setHighwaysAllowed(false);
        /* Calculate the shortest route available. */
        routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
        /* Calculate 1 route. */
        //routeOptions.setRouteCount(1);
        /* Finally set the route option */
        routePlan.setRouteOptions(routeOptions);

        /* Define waypoints for the route */
        /* START: 4350 Still Creek Dr */
        RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(currentPosition.getLatitude(), currentPosition.getLongitude()));
        RouteWaypoint Dest = new RouteWaypoint(new GeoCoordinate(destinationLocationLat, destinationLocationLong));
        /* END: Langley BC */
        //RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(19.19142, 72.97364));

        /* Add both waypoints to the route plan */
        routePlan.addWaypoint(startPoint);
        routePlan.addWaypoint(Dest);

        /* Trigger the route calculation,results will be called back via the listener */
        coreRouter.calculateRoute(routePlan,
                new Router.Listener<List<RouteResult>, RoutingError>() {
                    @Override
                    public void onProgress(int i) {
                        /* The calculation progress can be retrieved in this callback. */
                    }

                    @Override
                    public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                         RoutingError routingError) {
                        /* Calculation is done. Let's handle the result */
                        if (routingError == RoutingError.NONE) {
                            if (routeResults.get(0).getRoute() != null) {
                                /* Create a MapRoute so that it can be placed on the map */
                                mRoute = new MapRoute(routeResults.get(0).getRoute());
                                input_of_destination.setVisibility(View.GONE);
                                int length = routeResults.get(0).getRoute().getLength();
                                txtview_distance.setText("Distance : " + String.valueOf(length) + " " + "Meters");
                                System.out.println("----------------------------------- " + txtview_coupons.getText());
                                txtview_distance.setVisibility(View.VISIBLE);
                                txtview_coupons.setVisibility(View.VISIBLE);
                                /* Show the maneuver number on top of the route */
                                mRoute.setManeuverNumberVisible(true);


                                /* Add the MapRoute to the map */


                                map.addMapObject(mRoute);
                                mRoute.setRenderType(MapRoute.RenderType.PRIMARY);

                                /*
                                 * We may also want to make sure the map view is orientated properly
                                 * so the entire route can be easily seen.
                                 */
                                GeoBoundingBox gbb = routeResults.get(0).getRoute()
                                        .getBoundingBox();
                                map.zoomTo(gbb, Map.Animation.NONE,
                                        Map.MOVE_PRESERVE_ORIENTATION);
                            } else {
                                Toast.makeText( GeolocationActivity.this,
                                        "Error:route results returned is not valid",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(GeolocationActivity.this,
                                    "Error:route calculation returned error code: " + routingError,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}



