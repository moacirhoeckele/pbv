package com.volvo.wis.pbv.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;

public class BaseVoiceCommandReceiver extends BroadcastReceiver {

    protected String LOG_TAG;

    /**
     * All custom phrases registered with insertPhrase() are handled here.
     * <p>
     * Custom intents may also be directed here, but this example does not demonstrate this.
     * <p>
     * Keycodes are never handled via this interface
     *
     * @param context Context in which the phrase is handled
     * @param intent  Intent associated with the recognized phrase
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(LOG_TAG, " action " + intent.getAction());

        // All phrases registered with insertPhrase() match ACTION_VOICE_COMMAND as do
        // recognizer status updates
        if (intent.getAction().equals(VuzixSpeechClient.ACTION_VOICE_COMMAND)) {

            Bundle extras = intent.getExtras();

            if (extras != null) {

                // We will determine what type of message this is based upon the extras provided
                if (extras.containsKey(VuzixSpeechClient.PHRASE_STRING_EXTRA)) {

                    // If we get a phrase string extra, this was a recognized spoken phrase.
                    // The extra will contain the text that was recognized, unless a substitution
                    // was provided.  All phrases in this example have substitutions as it is
                    // considered best practice
                    String phrase = intent.getStringExtra(VuzixSpeechClient.PHRASE_STRING_EXTRA);

                    // Determine the specific phrase that was recognized and act accordingly
                    if (phrase.equals("popup")) {
                        Log.e(LOG_TAG, "Você falou exibir mensagem");
                    } /*else if (phrase.equals(MATCH_RESTORE)) {
                        mMainActivity.OnRestoreClick();
                    } else if (phrase.equals(MATCH_CLEAR)) {
                        mMainActivity.OnClearClick();
                    } else if (phrase.equals(MATCH_EDIT_TEXT)) {
                        mMainActivity.SelectTextBox();
                    }*/ else {
                        Log.i(LOG_TAG, "Phrase not handled");
                    }

                } else if (extras.containsKey(VuzixSpeechClient.RECOGNIZER_ACTIVE_BOOL_EXTRA)) {
                    // if we get a recognizer active bool extra, it means the recognizer was
                    // activated or stopped
                    String isRecognizerActive = extras.getBoolean(VuzixSpeechClient.RECOGNIZER_ACTIVE_BOOL_EXTRA, false) ? "True" : "False";
                    //mMainActivity.RecognizerChangeCallback(isRecognizerActive);
                    Log.i(LOG_TAG, "Recognizer Active: " + isRecognizerActive);
                } else {
                    Log.i(LOG_TAG, "Voice Intent not handled");
                }
            }
        } else {
            Log.i(LOG_TAG, "Other Intent not handled " + intent.getAction());
        }
    }
}
