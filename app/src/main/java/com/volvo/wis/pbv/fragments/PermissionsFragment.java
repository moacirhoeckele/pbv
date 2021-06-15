package com.volvo.wis.pbv.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

/**
 * A fragment to encapsulate the run-time permissions
 */
@TargetApi(23)
public class PermissionsFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSIONS = 0;

    private Listener listener;

    /**
     * One-time initialization. Sets up the view
     * @param savedInstanceState - we have no saved state. Just pass through to superclass
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        requestPermissions();
    }

    /**
     * Make the permissions request to the system
     */
    private void requestPermissions() {
        if (getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted();
        } else {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
        }

        if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted();
        } else {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }
    }

    /**
     * Called upon the permissions being granted. Notifies the permission listener.
     */
    private synchronized void permissionsGranted() {
        if (listener != null) {
            listener.permissionsGranted();
        }
    }


    /**
     * Sets the listener on which we will call permissionsGranted()
     * @param listener pointer to the class implementing the PermissionsFragment.Listener
     */
    public synchronized void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Required interface for any activity that requests a run-time permission
     *
     * @see <a href="https://developer.android.com/training/permissions/requesting.html">https://developer.android.com/training/permissions/requesting.html</a>
     * @param requestCode int: The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions String: The requested permissions. Never null.
     * @param grantResults int: The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (permissions.length == 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionsGranted();  // Permission was just granted by the user.
                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        requestPermissions();  // Ask for permission again
                    } else {
                        // Permission was denied. Give the user a hint, and exit
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", getContext().getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                        Toast.makeText(getContext(), "To use the barcode scanner, please grant camera and storage permissions and try again.", Toast.LENGTH_LONG).show();
                    }
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Define the interface of a permission fragment listener
     */
    public interface Listener {
        void permissionsGranted();
    }
}