package com.volvo.wis.pbv.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.volvo.wis.pbv.R;
import com.volvo.wis.pbv.activities.ScanResultImageView;
import com.vuzix.sdk.barcode.ScanResult2;

/**
 * A fragment to show the result of the barcode scan
 */
public class ScanResultFragment extends Fragment {

    public static final String ARG_BITMAP = "bitmap";
    public static final String ARG_SCAN_RESULT = "scan_result";

    /**
     * Inflate the correct layout upon creation
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState  If non-null, this fragment is being re-constructed from a previous saved state as given here.

     * @return - Returns the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    /**
     * Once our view is created, we will show the image with the scan result
     *
     * @param view - The new view
     * @param savedInstanceState - required argument that we ignore
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ScanResultImageView bitmap = (ScanResultImageView)view.findViewById(R.id.bitmap);
        TextView text = (TextView)view.findViewById(R.id.text);
        // The arguments Bundle gives us the bitmap that was taken upon recognition of a barcode, and
        // the text extracted from the barcode within the image
        Bundle args = getArguments();
        if (args != null) {
            ScanResult2 scanResult = args.getParcelable(ARG_SCAN_RESULT);
            bitmap.setImageBitmap((Bitmap)args.getParcelable(ARG_BITMAP));
            bitmap.setLocation(scanResult.getResultPoints());
            text.setText(scanResult.getText());
        }
    }
}

