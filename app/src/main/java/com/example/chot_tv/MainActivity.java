package com.example.chot_tv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.chot_tv.tedbottompicker.TedBottomPicker;
import com.example.chot_tv.tedbottompicker.TedRxBottomPicker;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.rx2.TedPermission;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private ImageView iv_image;
    private List<Uri> selectedUriList;
    private Uri selectedUri;
    private Disposable singleImageDisposable;
    private Disposable multiImageDisposable;
    private ViewGroup mSelectedImagesContainer;
    private RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_laibry);

        iv_image = findViewById(R.id.iv_image);
        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);
        requestManager = Glide.with(this);
        setSingleShowButton();
        setMultiShowButton();
        setRxSingleShowButton();
        setRxMultiShowButton();

    }

    private void setSingleShowButton() {
        Button btnSingleShow = findViewById(R.id.btn_single_show);
        btnSingleShow.setOnClickListener(view -> {
            if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openTedBottomPicker(); // Đã cấp quyền, mở trực tiếp TedBottomPicker
            } else {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        openTedBottomPicker(); // Quyền được cấp, mở TedBottomPicker
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions, Toast.LENGTH_SHORT).show();
                    }
                };
                checkPermission(permissionlistener);
            }
        });
    }

    private void checkPermission(PermissionListener permissionlistener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("Please enable permissions in Settings.")
                    .setPermissions(Manifest.permission.READ_MEDIA_IMAGES)
                    .request()
                    .subscribe();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("Please enable permissions in Settings.")
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .request()
                    .subscribe();
        } else {
            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("Please enable permissions in Settings.")
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request()
                    .subscribe();
        }
    }

    private boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void openTedBottomPicker() {
        TedBottomPicker.with(MainActivity.this)
                .setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2)
                .setSelectedUri(selectedUri)
                .show(uri -> {
                    Log.d("ted", "uri: " + uri);
                    Log.d("ted", "uri.getPath(): " + uri.getPath());
                    selectedUri = uri;

                    iv_image.setVisibility(View.VISIBLE);
                    mSelectedImagesContainer.setVisibility(View.GONE);

                    requestManager
                            .load(uri)
                            .into(iv_image);
                });
    }

    private void setMultiShowButton() {

        Button btnMultiShow = findViewById(R.id.btn_multi_show);
        btnMultiShow.setOnClickListener(view -> {

                    TedBottomPicker.with(MainActivity.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setCompleteButtonText("Done")
                            .setEmptySelectionText("No Select")
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage(uriList -> {
                                selectedUriList = uriList;
                                showUriList(uriList);
                            });

//                }

//                @Override
//                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                    Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
//                }
//
//
//            };
//
//            checkPermission(permissionlistener);

        });

    }


    private void setRxSingleShowButton() {

        Button btnSingleShow = findViewById(R.id.btn_rx_single_show);
        btnSingleShow.setOnClickListener(view -> {
//            PermissionListener permissionlistener = new PermissionListener() {
//                @Override
//                public void onPermissionGranted() {

                    singleImageDisposable = TedRxBottomPicker.with(MainActivity.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setSelectedUri(selectedUri)
                            //.showVideoMedia()
                            .setPeekHeight(1200)
                            .show()
                            .subscribe(uri -> {
                                selectedUri = uri;

                                iv_image.setVisibility(View.VISIBLE);
                                mSelectedImagesContainer.setVisibility(View.GONE);

                                requestManager
                                        .load(uri)
                                        .into(iv_image);
                            }, Throwable::printStackTrace);

//
//                }
//
//                @Override
//                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                    Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
//                }
//

//            };
//
//            checkPermission(permissionlistener);
        });
    }


    private void setRxMultiShowButton() {

        Button btnRxMultiShow = findViewById(R.id.btn_rx_multi_show);
        btnRxMultiShow.setOnClickListener(view -> {
//            PermissionListener permissionlistener = new PermissionListener() {
//                @Override
//                public void onPermissionGranted() {

                    multiImageDisposable = TedRxBottomPicker.with(MainActivity.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setCompleteButtonText("Done")
                            .setEmptySelectionText("No Select")
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage()
                            .subscribe(uris -> {
                                selectedUriList = uris;
                                showUriList(uris);
                            }, Throwable::printStackTrace);


//                }
//
//                @Override
//                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                    Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
//                }
//
//
//            };

//            checkPermission(permissionlistener);
        });

    }



    private void showUriList(List<Uri> uriList) {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();

        iv_image.setVisibility(View.GONE);
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int widthPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int heightPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());


        for (Uri uri : uriList) {

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = imageHolder.findViewById(R.id.media_image);

            requestManager
                    .load(uri.toString())
                    .apply(new RequestOptions().fitCenter())
                    .into(thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(widthPixel, heightPixel));

        }

    }

    @Override
    protected void onDestroy() {
        if (singleImageDisposable != null && !singleImageDisposable.isDisposed()) {
            singleImageDisposable.dispose();
        }
        if (multiImageDisposable != null && !multiImageDisposable.isDisposed()) {
            multiImageDisposable.dispose();
        }
        super.onDestroy();
    }
}
