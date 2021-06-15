package com.volvo.wis.pbv.utils;

import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;

import com.volvo.wis.pbv.activities.PickingActivity;
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;

public class PickingVoiceCommandReceiver extends BaseVoiceCommandReceiver {

    private PickingActivity pickingActivity;

    public PickingVoiceCommandReceiver(PickingActivity activity) {
        LOG_TAG = activity.LOG_TAG;
        pickingActivity = activity;
        pickingActivity.registerReceiver(this, new IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND));

        try {
            // Create a VuzixSpeechClient from the SDK
            VuzixSpeechClient sc = new VuzixSpeechClient(activity);

            // Delete specific phrases. This is useful if there are some that sound similar to yours, but
            // you want to keep the majority of them intact
            //sc.deletePhrase("go home");
            //sc.deletePhrase("go back");

            // Delete every phrase in the dictionary! (Available in SDK version 1.3 and newer)
            //
            // Note! When developing applications on the Vuzix Blade and Vuzix M400, deleting all
            // phrases in the dictionary removes the wake-up word(s) and voice-off words. The M300
            // cannot change the wake-up word, so "hello vuzix" is unaffected by the deletePhrase call.
            //sc.deletePhrase("*");

            // Now add any new strings.  If you put a substitution in the second argument,
            // you will be passed that string instead of the full string
            sc.insertKeycodePhrase("ok", KeyEvent.KEYCODE_ENTER);
            sc.insertKeycodePhrase("confirma", KeyEvent.KEYCODE_ENTER);
            sc.insertKeycodePhrase("confirmar", KeyEvent.KEYCODE_ENTER);

            //sc.insertKeycodePhrase("sobe", KeyEvent.KEYCODE_DPAD_UP);
            //sc.insertKeycodePhrase("desce", KeyEvent.KEYCODE_DPAD_DOWN);

            //sc.insertKeycodePhrase("anterior", KeyEvent.KEYCODE_DPAD_DOWN);
            //sc.insertKeycodePhrase("próximo", KeyEvent.KEYCODE_DPAD_DOWN);

            sc.insertKeycodePhrase("volta", KeyEvent.KEYCODE_BACK);
            sc.insertKeycodePhrase("voltar", KeyEvent.KEYCODE_BACK);
            sc.insertKeycodePhrase("retorna", KeyEvent.KEYCODE_BACK);
            sc.insertKeycodePhrase("retornar", KeyEvent.KEYCODE_BACK);
            sc.insertKeycodePhrase("cancela", KeyEvent.KEYCODE_BACK);
            sc.insertKeycodePhrase("cancelar", KeyEvent.KEYCODE_BACK);

            sc.insertKeycodePhrase("sai", KeyEvent.KEYCODE_HOME);
            sc.insertKeycodePhrase("sair", KeyEvent.KEYCODE_HOME);
            sc.insertKeycodePhrase("inicio", KeyEvent.KEYCODE_HOME);

            // Insert phrases for our broadcast handler
            //
            // ** NOTE **
            // The "s:" is required in the SDK version 1.2, but is not required in the latest JAR distribution
            // or SDK version 1.3.  But it is harmless when not required. It indicates that the recognizer is making a
            // substitution.  When the multi-word string is matched (in any language) the associated MATCH string
            // will be sent to the BroadcastReceiver
            //sc.insertPhrase("exibir mensagem", "popup");
            //sc.insertPhrase(mMainActivity.getResources().getString(R.string.btn_text_restore), MATCH_RESTORE);
            //sc.insertPhrase(mMainActivity.getResources().getString(R.string.btn_text_clear),   MATCH_CLEAR);
            //sc.insertPhrase("Edit Text", MATCH_EDIT_TEXT);

            // See what we've done
            Log.i(pickingActivity.LOG_TAG, sc.dump());

            // The recognizer may not yet be enabled in Settings. We can enable this directly
            VuzixSpeechClient.EnableRecognizer(pickingActivity, true);
        } catch (NoClassDefFoundError e) {
            // We get this exception if the SDK stubs against which we compiled cannot be resolved
            // at runtime. This occurs if the code is not being run on a Vuzix device supporting the voice
            // SDK
            Log.e(pickingActivity.LOG_TAG, "Voice SDK only runs on Vuzix Smart Glasses w. Vuzix Speech SDK support");
            Log.e(pickingActivity.LOG_TAG, e.getMessage());
            e.printStackTrace();
            activity.finish();
        } catch (Exception e) {
            Log.e(pickingActivity.LOG_TAG, "Error setting custom vocabulary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called to unregister for voice commands. An important cleanup step.
     */
    public void unregister() {
        try {
            pickingActivity.unregisterReceiver(this);
            Log.i(pickingActivity.LOG_TAG, "Custom vocab removed");
            pickingActivity = null;
        } catch (Exception e) {
            Log.e(pickingActivity.LOG_TAG, "Custom vocab died " + e.getMessage());
        }
    }
}
