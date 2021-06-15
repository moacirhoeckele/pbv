package com.volvo.wis.pbv.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.volvo.wis.pbv.R;
import com.volvo.wis.pbv.contracts.PickingViewContract.PickingViewEntry;
import com.volvo.wis.pbv.helpers.Log4jHelper;
import com.volvo.wis.pbv.helpers.PickingDbHelper;
import com.volvo.wis.pbv.viewmodels.PickingStatusEnum;

public class MainActivity extends ListActivity {

    public final String LOG_TAG = "MainActivity";
    public final String CUSTOM_LIST_INTENT = "com.volvo.wis.pbv.MainActivity.KitListIntent";
    //LoginVoiceCommandReceiver mainVoiceCommandReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the voice command receiver class
        //mainVoiceCommandReceiver = new LoginVoiceCommandReceiver(this);
        //registerReceiver(mainVoiceCommandReceiver, new IntentFilter(CUSTOM_LIST_INTENT));

        SetList();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        StartPicking(item);
    }

    private void SetList() {

        PickingDbHelper dbHelper = new PickingDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            Log4jHelper.getLogger(MainActivity.class.getName()).info("Preparando o menu de kits.");

            String[] projection = {
                    PickingViewEntry.COLUMN_NAME_ESTACAO,
                    PickingViewEntry.COLUMN_NAME_MODULO
            };

            String selection = PickingViewEntry.COLUMN_NAME_STATUS + " = ? ";
            String[] selectionArgs = {String.valueOf(PickingStatusEnum.Pendente)};
            String sortOrder = PickingViewEntry.COLUMN_NAME_ESTACAO + ", " + PickingViewEntry.COLUMN_NAME_MODULO;
            String groupBy = PickingViewEntry.COLUMN_NAME_ESTACAO + ", " + PickingViewEntry.COLUMN_NAME_MODULO;
            Cursor cursor = db.query(PickingViewEntry.TABLE_NAME, projection, selection, selectionArgs, groupBy, null, sortOrder);

            String[] values;

            if (cursor.getCount() == 0){
                values = new String[] { "Nenhum kit disponível" };
            } else {
                values = new String[cursor.getCount()];
                while (cursor.moveToNext()) {
                    values[cursor.getPosition()] = String.format("%03d-%01d",
                            cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewEntry.COLUMN_NAME_ESTACAO)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewEntry.COLUMN_NAME_MODULO)));
                }
            }

            cursor.close();

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_layout, R.id.label, values);
            setListAdapter(adapter);

        } catch (Exception e) {
            Log4jHelper.getLogger(MainActivity.class.getName()).error("Erro ao montar o menu de kits. Erro: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private void StartPicking(String item) {
        if (item != "Nenhum kit disponível") {
            try{
                // Start PickingActivity.class
                Intent myIntent = new Intent(getBaseContext(), PickingActivity.class);

                // Prepare parameters
                myIntent.putExtra(PickingViewEntry.COLUMN_NAME_ESTACAO, Integer.parseInt(item.substring(0, item.indexOf('-'))));
                myIntent.putExtra(PickingViewEntry.COLUMN_NAME_MODULO, Integer.parseInt(item.substring(item.indexOf('-') + 1)));

                // Call activity
                startActivity(myIntent);
            } catch (Exception ex) {
                Log4jHelper.getLogger(MainActivity.class.getName()).error("Erro ao ler a estação e módulo selecionados. Erro: " + ex.getMessage());
            }
        }
    }

    /* Quando retorna do picking, atualiza a lista. */
    @Override
    public void onRestart()
    {
        super.onRestart();
        SetList();
    }

    /**
     * Unregister from the speech SDK
     */
    @Override
    protected void onDestroy() {
        //mainVoiceCommandReceiver.unregister();
        super.onDestroy();
    }
}
