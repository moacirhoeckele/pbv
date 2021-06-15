package com.volvo.wis.pbv.contracts;

import android.provider.BaseColumns;

public final class PickingViewContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PickingViewContract() { }

    /* Inner class that defines the table contents */
    public static class PickingViewEntry implements BaseColumns {
        public static final String TABLE_NAME = "PickingView";
        public static final String COLUMN_NAME_ESTACAO = "estacao";
        public static final String COLUMN_NAME_MODULO = "modulo";
        public static final String COLUMN_NAME_BOX = "box";
        public static final String COLUMN_NAME_PRODUTO = "produto";
        public static final String COLUMN_NAME_DATAPRODUCAO = "dataProducao";
        public static final String COLUMN_NAME_CHASSI01 = "chassi01";
        public static final String COLUMN_NAME_QUANTIDADE01 = "quatidade01";
        public static final String COLUMN_NAME_SEQUENCE01 = "sequencia01";
        public static final String COLUMN_NAME_CHASSI02 = "chassi02";
        public static final String COLUMN_NAME_QUANTIDADE02 = "quantidade02";
        public static final String COLUMN_NAME_SEQUENCE02 = "sequencia02";
        public static final String COLUMN_NAME_CHASSI03 = "chassi03";
        public static final String COLUMN_NAME_QUANTIDADE03 = "quantidade03";
        public static final String COLUMN_NAME_SEQUENCE03 = "sequencia03";
        public static final String COLUMN_NAME_STATUS = "status";
    }
}
