package com.volvo.wis.pbv.helpers;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.volvo.wis.pbv.R;

public class MessageHelper {

    private static Activity _activity;

    public MessageHelper(Activity activity) {
        _activity = activity;
    }

    public static void ShowErrorToast(String message) {
        LayoutInflater inflater = _activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.error_toast, (ViewGroup) _activity.findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.txtToastMessage);
        text.setText(message);

        Toast toast = new Toast(_activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void ShowCustomToast(String message) {
        LayoutInflater inflater = _activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) _activity.findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.txtToastMessage);
        text.setText(message);

        Toast toast = new Toast(_activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void ShowSyncToast(String message) {
        LayoutInflater inflater = _activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.sync_toast, (ViewGroup) _activity.findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.txtToastMessage);
        text.setText(message);

        final Toast toast = new Toast(_activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        //toast.show();

        int toastDurationInMilliSeconds = 1500;

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }
            public void onFinish() {
                toast.cancel();
            }
        };

        // Show the toast and starts the countdown
        toast.show();
        toastCountDown.start();
    }

    public static void ShowOkToast(String message) {
        LayoutInflater inflater = _activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.ok_toast, (ViewGroup) _activity.findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.txtToastMessage);
        text.setText(message);

        Toast toast = new Toast(_activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static MessageHelper getHelper(Activity activity) {
        return new MessageHelper(activity);
    }
}
