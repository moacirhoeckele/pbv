package com.volvo.wis.pbv.services;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.provider.BaseColumns;

import com.opencsv.CSVReader;
import com.volvo.wis.pbv.R;
import com.volvo.wis.pbv.contracts.PickingViewContract;
import com.volvo.wis.pbv.helpers.Log4jHelper;
import com.volvo.wis.pbv.helpers.PickingDbHelper;
import com.volvo.wis.pbv.viewmodels.KitViewModel;
import com.volvo.wis.pbv.viewmodels.OperationResult;
import com.volvo.wis.pbv.viewmodels.OperationResultSingle;
import com.volvo.wis.pbv.viewmodels.PickingStatusEnum;
import com.volvo.wis.pbv.viewmodels.PickingViewModel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PickingService extends ContextWrapper implements IPickingService {

    public PickingService(Context base) {
        super(base);
    }

    @Override
    public OperationResult LoadData() {
        PickingDbHelper dbHelper = new PickingDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {

            // Get Download folder path
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            List<PickingViewModel> pickingList = new ArrayList<>();

            // Read the CSV file entries
            File csvFile = new File(externalStoragePublicDirectory + "/KITA4.csv");

            if (csvFile.exists()) {

                Log4jHelper.getLogger(PickingService.class.getName()).info("Arquivo CSV existe. " + csvFile.getPath());

                CSVReader reader = new CSVReader(new FileReader(csvFile.getAbsolutePath()));
                List<String[]> pickingEntries = reader.readAll();

                Log4jHelper.getLogger(PickingService.class.getName()).info("Registros encontrados no arquivo: " + pickingEntries.size());

                // Delete the file
                csvFile.delete();
                Log4jHelper.getLogger(PickingService.class.getName()).info("Arquivo CSV excluído.");

                //Read each entry and create the picking object
                for (int i = 0; i < pickingEntries.size(); i++) {
                    String[] values = pickingEntries.get(i);
                    pickingList.add(new PickingViewModel(values));
                }

                if (!pickingList.isEmpty()) {

                    // Delete old rows
                    Log4jHelper.getLogger(PickingService.class.getName()).info("Limpando a tabela de picking.");
                    int deletedRows = db.delete(PickingViewContract.PickingViewEntry.TABLE_NAME, null, null);
                    Log4jHelper.getLogger(PickingService.class.getName()).info(deletedRows + " registros excluídos.");

                    for (int i = 0; i < pickingList.size(); i++) {

                        // Create a new map of values, where column names are the keys
                        ContentValues values = new ContentValues();
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_ESTACAO, pickingList.get(i).getEstacao());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_MODULO, pickingList.get(i).getModulo());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_BOX, pickingList.get(i).getBox());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_PRODUTO, pickingList.get(i).getProduto());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_DATAPRODUCAO, pickingList.get(i).getDataProducao());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_CHASSI01, pickingList.get(i).getChassi01());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_QUANTIDADE01, pickingList.get(i).getQuantidade01());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE01, pickingList.get(i).getSequence01());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_CHASSI02, pickingList.get(i).getChassi02());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_QUANTIDADE02, pickingList.get(i).getQuantidade02());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE02, pickingList.get(i).getSequence02());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_CHASSI03, pickingList.get(i).getChassi03());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_QUANTIDADE03, pickingList.get(i).getQuantidade03());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE03, pickingList.get(i).getSequence03());
                        values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_STATUS, String.valueOf(pickingList.get(i).getStatus()));

                        // Insert the new row, returning the primary key value of the new row
                        long newRowId = db.insert(PickingViewContract.PickingViewEntry.TABLE_NAME, null, values);

                        Log4jHelper.getLogger(PickingService.class.getName()).info(String.format("Inserindo registro: %d", newRowId));
                    }

                } else {
                    Log4jHelper.getLogger(PickingService.class.getName()).info("Nenhum registro encontrado no arquivo CSV.");
                }

            } else {

                Log4jHelper.getLogger(PickingService.class.getName()).info("Arquivo CSV não encontrado.");

                String selection = PickingViewContract.PickingViewEntry.COLUMN_NAME_STATUS + " = ?";
                String[] selectionArgs = {String.valueOf(PickingStatusEnum.Pendente)};
                Long count = DatabaseUtils.queryNumEntries(db, PickingViewContract.PickingViewEntry.TABLE_NAME, selection, selectionArgs);

                if (count > 0) {
                    Log4jHelper.getLogger(PickingService.class.getName()).info(count + " registros de picking pendentes encontrados na base de dados.");
                } else {
                    Log4jHelper.getLogger(PickingService.class.getName()).info("Não há registros para picking na base de dados, solicite uma nova carga de dados.");
                }
            }

            return new OperationResult(true, getString(R.string.data_load_success));

        } catch (Exception e) {
            Log4jHelper.getLogger(PickingService.class.getName()).error(e);
            return new OperationResult(false, getString(R.string.data_load_error));
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public OperationResultSingle<KitViewModel> ValidateKit(String json) {

        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject)parser.parse(json);

            //{"module":"312","station":"7"}
            if (!obj.isEmpty() && obj.containsKey("module") && obj.containsKey("station")) {

                KitViewModel kitViewModel = new KitViewModel();
                kitViewModel.setModule(Integer.valueOf(obj.get("module").toString()));
                kitViewModel.setStation(Integer.valueOf(obj.get("station").toString()));

                PickingDbHelper dbHelper = new PickingDbHelper(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                try {
                    Log4jHelper.getLogger(PickingService.class.getName()).info("Validando o kit lido.");

                    String[] projection = {
                            PickingViewContract.PickingViewEntry.COLUMN_NAME_ESTACAO,
                            PickingViewContract.PickingViewEntry.COLUMN_NAME_MODULO
                    };

                    String selection = PickingViewContract.PickingViewEntry.COLUMN_NAME_STATUS + " = ? and " +
                            PickingViewContract.PickingViewEntry.COLUMN_NAME_ESTACAO + " = ? and " +
                            PickingViewContract.PickingViewEntry.COLUMN_NAME_MODULO + " = ?";

                    String[] selectionArgs = {
                            String.valueOf(PickingStatusEnum.Pendente),
                            String.valueOf(kitViewModel.getStation()),
                            String.valueOf(kitViewModel.getModule())
                    };

                    String sortOrder = PickingViewContract.PickingViewEntry.COLUMN_NAME_ESTACAO + ", " + PickingViewContract.PickingViewEntry.COLUMN_NAME_MODULO;
                    String groupBy = PickingViewContract.PickingViewEntry.COLUMN_NAME_ESTACAO + ", " + PickingViewContract.PickingViewEntry.COLUMN_NAME_MODULO;
                    Cursor cursor = db.query(PickingViewContract.PickingViewEntry.TABLE_NAME, projection, selection, selectionArgs, groupBy, null, sortOrder);

                    Integer rows = cursor.getCount();

                    cursor.close();

                    return rows == 0 ?
                            new OperationResultSingle<KitViewModel>(false, kitViewModel, "Kit " + String.format("%03d-%01d", kitViewModel.getStation(), kitViewModel.getModule()) + " não disponível!") :
                            new OperationResultSingle<KitViewModel>(true, kitViewModel);

                } catch (Exception e) {
                    Log4jHelper.getLogger(PickingService.class.getName()).error(e);
                    return new OperationResultSingle<KitViewModel>(false, kitViewModel, "Não foi possível validar o Kit " + String.format("%03d-%01d", kitViewModel.getStation(), kitViewModel.getModule()) + "!");
                } finally {
                    if (db != null && db.isOpen()) {
                        db.close();
                    }
                }

            } else {
                return new OperationResultSingle<KitViewModel>(false, null, getString(R.string.invalid_qr_code));
            }

        } catch (Exception e){
            return new OperationResultSingle<KitViewModel>(false, null, getString(R.string.invalid_qr_code));
        }
    }

    @Override
    public OperationResultSingle<PickingViewModel> LoadNextPicking(Integer station, Integer module) {
        PickingDbHelper dbHelper = new PickingDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {

            PickingViewModel next = null;

            String[] projection = {
                    BaseColumns._ID,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_ESTACAO,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_MODULO,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_BOX,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_PRODUTO,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_DATAPRODUCAO,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_CHASSI01,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_QUANTIDADE01,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE01,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_CHASSI02,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_QUANTIDADE02,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE02,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_CHASSI03,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_QUANTIDADE03,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE03,
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_STATUS
            };

            String selection = PickingViewContract.PickingViewEntry.COLUMN_NAME_STATUS + " = ? and " +
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_ESTACAO + " = ? and " +
                    PickingViewContract.PickingViewEntry.COLUMN_NAME_MODULO + " = ?";

            String[] selectionArgs = {String.valueOf(PickingStatusEnum.Pendente), String.valueOf(station), String.valueOf(module)};

            String orderBy = PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE01 + ", "
                    + PickingViewContract.PickingViewEntry.COLUMN_NAME_BOX + ", "
                    + PickingViewContract.PickingViewEntry.COLUMN_NAME_PRODUTO;

            Cursor cursor = db.query(
                    PickingViewContract.PickingViewEntry.TABLE_NAME, // The table to query
                    projection,              // The array of columns to return (pass null to get all)
                    selection,               // The columns for the WHERE clause
                    selectionArgs,           // The values for the WHERE clause
                    null,           // don't group the rows
                    null,            // don't filter by row groups
                    orderBy               // The sort order
            );

            if (cursor.moveToFirst()) {
                next = new PickingViewModel(
                        cursor.getLong(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry._ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_ESTACAO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_MODULO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_BOX)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_PRODUTO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_DATAPRODUCAO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_CHASSI01)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_QUANTIDADE01)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE01)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_CHASSI02)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_QUANTIDADE02)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE02)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_CHASSI03)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_QUANTIDADE03)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_SEQUENCE03)),
                        PickingStatusEnum.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PickingViewContract.PickingViewEntry.COLUMN_NAME_STATUS))));
            }

            cursor.close();

            return new OperationResultSingle<PickingViewModel>(true, next);

        } catch (Exception e) {
            Log4jHelper.getLogger(PickingService.class.getName()).error("Erro ao carregar o próximo picking.", e);
            return new OperationResultSingle<PickingViewModel>(false, null, "Ocorreu um erro não esperado, contacte o suporte.");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public OperationResult SetAsPending(long id) {
        PickingDbHelper dbHelper = new PickingDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {

            ContentValues values = new ContentValues();
            values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_STATUS, String.valueOf(PickingStatusEnum.Pendente));
            String selection = PickingViewContract.PickingViewEntry._ID + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            db.update(PickingViewContract.PickingViewEntry.TABLE_NAME, values, selection, selectionArgs);

            return new OperationResult(true);

        } catch (Exception e) {
            Log4jHelper.getLogger(PickingService.class.getName()).error("Erro ao alterar status do picking.", e);
            return new OperationResult(false, "Ocorreu um erro não esperado, contacte o suporte.");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public OperationResult FinishPicking(long id) {
        PickingDbHelper dbHelper = new PickingDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {

            ContentValues values = new ContentValues();
            values.put(PickingViewContract.PickingViewEntry.COLUMN_NAME_STATUS, String.valueOf(PickingStatusEnum.Finalizado));
            String selection = PickingViewContract.PickingViewEntry._ID + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            int update = db.update(PickingViewContract.PickingViewEntry.TABLE_NAME, values, selection, selectionArgs);

            if (update != 1) {
                Log4jHelper.getLogger(PickingService.class.getName()).error("Nenhum registro foi encontrado na base de dados. ID tela: " + id);
                return new OperationResult(false, "Erro ao finalizar, nenhum registro encontrado.");
            }

            Log4jHelper.getLogger(PickingService.class.getName()).info(String.format("Finalizado ID: %s", String.valueOf(id)));

            return new OperationResult(true);
        } catch (Exception e) {
            Log4jHelper.getLogger(PickingService.class.getName()).error("Erro ao finalizar o picking.", e);
            return new OperationResult(false, "Ocorreu um erro não esperado, contacte o suporte.");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}
