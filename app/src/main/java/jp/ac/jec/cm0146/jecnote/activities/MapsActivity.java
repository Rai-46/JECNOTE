package jp.ac.jec.cm0146.jecnote.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.databinding.ActivityMapsBinding;
import jp.ac.jec.cm0146.jecnote.models.SchoolInfo;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final int MY_PERMISSIONS_REQUEST1 = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setListener();
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mapInit();
    }


    private void mapInit() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        LatLng location = new LatLng(35.698213009546514, 139.69811069028256);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(18.5f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        UiSettings ui = mMap.getUiSettings();
        ui.setZoomControlsEnabled(false);
        ui.setMapToolbarEnabled(true);
        ui.setMyLocationButtonEnabled(true);
        ui.setMyLocationButtonEnabled(true);


        // 各号館のピンを指す
        markerSet();
        mMap.setInfoWindowAdapter(new CustomInfoAdapter());

        // InfoWindowのリスナー
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(@NonNull Marker marker) {
//                showToast(marker.getSnippet());
//            }
//        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 位置情報が取れていない
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST1);
            return;
        }
        mMap.setMyLocationEnabled(true);




    }

    private void markerSet() {
        MarkerOptions markerOptions = new MarkerOptions();
        ArrayList<SchoolInfo> infos = new ArrayList<>(Arrays.asList(
                new SchoolInfo().setName("本館").setInformation("職員室、保健室、キャリアセンター").setPosition(new LatLng(35.698213009546514, 139.69811069028256)),
                new SchoolInfo().setName("2号館").setInformation("教室").setPosition(new LatLng(35.69878781363483, 139.69790234102527)),
                new SchoolInfo().setName("3号館").setInformation("図書室、ライセンスセンター").setPosition(new LatLng(35.69849617976141, 139.6983408176549)),
                new SchoolInfo().setName("4号館").setInformation("教室").setPosition(new LatLng(35.698764324125385, 139.69818017140486)),
                new SchoolInfo().setName("5号館").setInformation("教室").setPosition(new LatLng(35.69893930119337, 139.69746241549768)),
                new SchoolInfo().setName("6号館").setInformation("教室").setPosition(new LatLng(35.698239957945354, 139.69791419341254)),
                new SchoolInfo().setName("7号館").setInformation("学生窓口").setPosition(new LatLng(35.69888807953913, 139.6965862172064)),
                new SchoolInfo().setName("8号館").setInformation("教室").setPosition(new LatLng(35.69745216382213, 139.69785876181632)),
                new SchoolInfo().setName("9号館").setInformation("メディアホール").setPosition(new LatLng(35.69912662904634, 139.69795039818942)),
                new SchoolInfo().setName("10号館").setInformation("心理相談室").setPosition(new LatLng(35.699337792284915, 139.69793800020543)),
                new SchoolInfo().setName("11号館").setInformation("教室").setPosition(new LatLng(35.70067737298001, 139.6978924042737)),
                new SchoolInfo().setName("12号館").setInformation("教室").setPosition(new LatLng(35.69529864379966, 139.69890897187213)),
                new SchoolInfo().setName("13号館").setInformation("教室").setPosition(new LatLng(35.70012442644344, 139.69825347762546))
        ));
        for(SchoolInfo info: infos) {
            markerOptions.title(info.getName());
            markerOptions.position(info.getPosition());
            markerOptions.snippet(info.getInformation());
            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            markerOptions.icon(icon);
            mMap.addMarker(markerOptions);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // 権限取得
                    showToast("権限を取得しました");
                    mMap.setMyLocationEnabled(true);
                } else {
                    // 未取得
                    showToast("権限を取得できませんでした");
                    // アラートダイヤログにして設定のアプリ詳細画面（権限）に飛ばす。
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("確認")
                            .setMessage("JECNOTEに位置情報を許可してください。")
                                    .setPositiveButton("設定へ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Uri uri = Uri.parse("package:jp.ac.jec.cm0146.jecnote");
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            // TODO ここで戻ってきた時に、権限があればsetMyLocationEnabledをtrueにしたい
                                        }
                                    })
                            .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    builder.show();
                }
                return;
            }
        }
    }

    // InfoWindowの制御
    private class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mWindow;

        public CustomInfoAdapter() {
            // InfoWindowのデザインを作成したXMLファイルを指定
            mWindow = getLayoutInflater().inflate(
                    R.layout.my_info_window, null
            );
        }

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        private void render(Marker marker, View view) {
            TextView textName = (TextView) view.findViewById(R.id.buildingName);
            textName.setText(marker.getTitle());
            TextView textInfo = (TextView) view.findViewById(R.id.infomation);
            textInfo.setText(marker.getSnippet());
            //TODO ここで各号館の写真に切り変える
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            if("本館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.honnkann);
            } else if("2号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan2);
            } else if("3号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan3);
            } else if("4号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan4);
            } else if("5号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan5);
            } else if("6号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan6);
            } else if("7号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan7);
            } else if("8号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan8);
            } else if("9号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan9);
            } else if("10号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan10);
            } else if("11号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.gouukan11);
            } else if("12号館".equals(marker.getTitle())) {
                imageView.setImageResource(R.drawable.goukan12);
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }


        }
    }
}