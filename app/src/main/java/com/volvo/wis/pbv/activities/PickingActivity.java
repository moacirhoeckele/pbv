package com.volvo.wis.pbv.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.volvo.wis.pbv.R;
import com.volvo.wis.pbv.app.MyApplication;
import com.volvo.wis.pbv.contracts.PickingViewContract.PickingViewEntry;
import com.volvo.wis.pbv.helpers.Log4jHelper;
import com.volvo.wis.pbv.helpers.MessageHelper;
import com.volvo.wis.pbv.services.IAuthenticationService;
import com.volvo.wis.pbv.services.IPickingService;
import com.volvo.wis.pbv.utils.PickingVoiceCommandReceiver;
import com.volvo.wis.pbv.viewmodels.OperationResult;
import com.volvo.wis.pbv.viewmodels.OperationResultSingle;
import com.volvo.wis.pbv.viewmodels.PickingViewModel;
import com.volvo.wis.pbv.viewmodels.UserViewModel;

import java.util.Objects;

import javax.inject.Inject;

public class PickingActivity extends AppCompatActivity {

    Button btnOk;
    public final String LOG_TAG = "PickingActivity";
    private static int seq01;
    private static int seq02;
    private static int seq03;
    private Integer station;
    private Integer module;
    public final String CUSTOM_PICKING_INTENT = "com.volvo.wis.pbv.activities.PickingActivity.PickingIntent";
    Button btnVoltar;
    PickingVoiceCommandReceiver pickingVoiceCommandReceiver;
    MessageHelper messageHelper = MessageHelper.getHelper(this);
    UserViewModel user;

    @Inject
    IAuthenticationService authenticationService;

    @Inject
    IPickingService pickingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getApplicationComponent().inject(this);

        messageHelper = MessageHelper.getHelper(this);

        // Remove the action bar on this window
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_picking);

        // Set button Ok behavior
        btnOk = findViewById(R.id.btnOk);
        btnOk.setBackgroundResource(R.color.black);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinishPicking();
            }
        });

        // Set button Voltar behavior
        btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setBackgroundResource(R.color.black);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Back();
            }
        });

        seq01 = 0;
        seq02 = 0;
        seq03 = 0;

        Intent intent = getIntent();
        station = intent.getIntExtra(PickingViewEntry.COLUMN_NAME_ESTACAO, 0);
        module = intent.getIntExtra(PickingViewEntry.COLUMN_NAME_MODULO, 0);

        user = authenticationService.GetLoggedUser().getData();

        TextView txtUser = findViewById(R.id.txtUser);
        txtUser.setText(String.valueOf(user.getId()));

        // Create the voice command receiver class
        pickingVoiceCommandReceiver = new PickingVoiceCommandReceiver(this);
        registerReceiver(pickingVoiceCommandReceiver, new IntentFilter(CUSTOM_PICKING_INTENT));

        LoadNextPicking(station, module);
    }

    /* Encontra o picking da vez a carrega na tela. */
    private void LoadNextPicking(Integer station, Integer module) {

        OperationResultSingle<PickingViewModel> resNext = pickingService.LoadNextPicking(station, module);

        if (resNext.isSuccess()) {
            if (resNext.getData() != null) {
                OperationResult resUpd = pickingService.SetAsPending(resNext.getData().getId());

                if (resUpd.isSuccess()) {

                    final PickingViewModel finalNext = resNext.getData();

                    if (seq01 == 0 && seq02 == 0 && seq03 == 0) {
                        seq01 = resNext.getData().getSequence01();
                        seq02 = resNext.getData().getSequence02();
                        seq03 = resNext.getData().getSequence03();
                    }

                    if (resNext.getData().getSequence01() == seq01 && resNext.getData().getSequence02() == seq02 && resNext.getData().getSequence03() == seq03) {
                        messageHelper.ShowSyncToast("Carregando próxima peça.");
                    } else {
                        messageHelper.ShowOkToast("Sequência finalizada! Carregando próximos chassis.");
                        seq01 = resNext.getData().getSequence01();
                        seq02 = resNext.getData().getSequence02();
                        seq03 = resNext.getData().getSequence03();
                    }

                    // Aguarda a mensagem desaparecer para carregar o picking seguinte
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FillPickingFields(finalNext);
                        }
                    }, 1500);

                } else {
                    messageHelper.ShowErrorToast(resUpd.getMessage());
                }
            } else {
                messageHelper.ShowCustomToast("Picking finalizado! Solicite uma nova carga de dados.");
            }
        } else {
            messageHelper.ShowErrorToast(resNext.getMessage());
        }
    }

    /* Preenche os campos na tela. */
    private void FillPickingFields(PickingViewModel model) {
        try {
            TextView txtId = findViewById(R.id.txtId);
            txtId.setText(String.valueOf(model.getId()));

            TextView txtStation = findViewById(R.id.txtStation);
            txtStation.setText(String.format("%03d-%01d", model.getEstacao(), model.getModulo()));

            TextView txtBox = findViewById(R.id.txtBox);
            txtBox.setText(String.format("%02d", model.getBox()));

            TextView txtPartNumber = findViewById(R.id.txtPartNumber);
            txtPartNumber.setText(String.format("%08d", model.getProduto()));

            TextView txtSequence01 = findViewById(R.id.txtSequence01);
            txtSequence01.setText(model.getSequence01() == 0 ? "-" : String.valueOf(model.getSequence01()));

            TextView txtSequence02 = findViewById(R.id.txtSequence02);
            txtSequence02.setText(model.getSequence02() == 0 ? "-" : String.valueOf(model.getSequence02()));

            TextView txtSequence03 = findViewById(R.id.txtSequence03);
            txtSequence03.setText(model.getSequence03() == 0 ? "-" : String.valueOf(model.getSequence03()));

            TextView txtChassi01 = findViewById(R.id.txtChassi01);
            txtChassi01.setText(model.getChassi01() == model.getSequence01() ? "000000" : String.valueOf(model.getChassi01()));

            TextView txtChassi02 = findViewById(R.id.txtChassi02);
            txtChassi02.setText(model.getChassi02() == model.getSequence02() ? "000000" : String.valueOf(model.getChassi02()));

            TextView txtChassi03 = findViewById(R.id.txtChassi03);
            txtChassi03.setText(model.getChassi03() == model.getSequence03() ? "000000" : String.valueOf(model.getChassi03()));

            TextView txtQuantity01 = findViewById(R.id.txtQuantity01);
            txtQuantity01.setText(model.getQuantidade01() == 0 ? "-" : String.valueOf(model.getQuantidade01()));

            TextView txtQuantity02 = findViewById(R.id.txtQuantity02);
            txtQuantity02.setText(model.getQuantidade02() == 0 ? "-" : String.valueOf(model.getQuantidade02()));

            TextView txtQuantity03 = findViewById(R.id.txtQuantity03);
            txtQuantity03.setText(model.getQuantidade03() == 0 ? "-" : String.valueOf(model.getQuantidade03()));

            Log4jHelper.getLogger(PickingActivity.class.getName()).info(String.format("Carregado ID: %s, Estacao: %s, Modulo: %s, Box: %s, Produto: %s, Chassi_1: %s, Qtde_1: %s, Chassi_2: %s, Qtde_2: %s, Chassi_3: %s, Qtde_3: %s",
                    model.getId(), model.getEstacao(), model.getModulo(), model.getBox(), model.getProduto(), model.getChassi01(), model.getQuantidade01(), model.getChassi02(), model.getQuantidade02(), model.getChassi03(), model.getQuantidade03()));

        } catch (Exception e) {
            Log4jHelper.getLogger(PickingActivity.class.getName()).error("Erro ao exibir dados na tela.", e);

            messageHelper.ShowErrorToast("Ocorreu um erro não esperado, contacte o suporte.");
        }
    }

    /* Finaliza o picking atual e carrega o próximo na tela. */
    private void FinishPicking() {
        TextView txtId = findViewById(R.id.txtId);
        OperationResult resFin = pickingService.FinishPicking(Long.parseLong(String.valueOf(txtId.getText())));

        if (!resFin.isSuccess()) {
            messageHelper.ShowErrorToast(resFin.getMessage());
        } else {
            LoadNextPicking(station, module);
        }
    }

    private void Back() {
        pickingVoiceCommandReceiver.unregister();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        Back();
    }
}
