package com.volvo.wis.pbv.viewmodels;

public class PickingViewModel {

    private long id;
    private int estacao;
    private int modulo;
    private int box;
    private int produto;
    private String dataProducao;
    private int chassi01;
    private int quantidade01;
    private int chassi02;
    private int quantidade02;
    private int chassi03;
    private int quantidade03;
    private PickingStatusEnum status;
    private int sequence01;
    private int sequence02;
    private int sequence03;

    public PickingViewModel() {
    }

    public PickingViewModel(long id,
                            int estacao,
                            int modulo,
                            int box,
                            int produto,
                            String dataProducao,
                            int chassi01,
                            int quantidade01,
                            int sequence01,
                            int chassi02,
                            int quantidade02,
                            int sequence02,
                            int chassi03,
                            int quantidade03,
                            int sequence03,
                            PickingStatusEnum status) {
        this.setId(id);
        this.estacao = estacao;
        this.modulo = modulo;
        this.box = box;
        this.produto = produto;
        this.dataProducao = dataProducao;
        this.chassi01 = chassi01;
        this.quantidade01 = quantidade01;
        this.chassi02 = chassi02;
        this.quantidade02 = quantidade02;
        this.chassi03 = chassi03;
        this.quantidade03 = quantidade03;
        this.status = status;
        this.sequence01 = sequence01;
        this.sequence02 = sequence02;
        this.sequence03 = sequence03;
    }

    public PickingViewModel(String[] strings) {
        this.id = 0;
        this.estacao = Integer.parseInt(strings[0]);
        this.modulo = Integer.parseInt(strings[1]);
        this.box = Integer.parseInt(strings[2]);
        this.produto = Integer.parseInt(strings[3]);
        this.dataProducao = strings[4];
        this.chassi01 = Integer.parseInt(strings[5]);
        this.quantidade01 = Integer.parseInt(strings[6]);
        this.chassi02 = Integer.parseInt(strings[7]);
        this.quantidade02 = Integer.parseInt(strings[8]);
        this.chassi03 = Integer.parseInt(strings[9]);
        this.quantidade03 = Integer.parseInt(strings[10]);
        this.status = PickingStatusEnum.valueOf(strings[11]);
        this.sequence01 = Integer.parseInt(strings[12]);
        this.sequence02 = Integer.parseInt(strings[13]);
        this.sequence03 = Integer.parseInt(strings[14]);
    }

    public int getEstacao() {
        return estacao;
    }

    public void setEstacao(int estacao) {
        this.estacao = estacao;
    }

    public int getModulo() {
        return modulo;
    }

    public void setModulo(int modulo) {
        this.modulo = modulo;
    }

    public int getBox() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public int getProduto() {
        return produto;
    }

    public void setProduto(int produto) {
        this.produto = produto;
    }

    public int getChassi01() {
        return chassi01;
    }

    public void setChassi01(int chassi01) {
        this.chassi01 = chassi01;
    }

    public int getQuantidade01() {
        return quantidade01;
    }

    public void setQuantidade01(int quantidade01) {
        this.quantidade01 = quantidade01;
    }

    public int getChassi02() {
        return chassi02;
    }

    public void setChassi02(int chassi02) {
        this.chassi02 = chassi02;
    }

    public int getQuantidade02() {
        return quantidade02;
    }

    public void setQuantidade02(int quantidade02) {
        this.quantidade02 = quantidade02;
    }

    public int getChassi03() {
        return chassi03;
    }

    public void setChassi03(int chassi03) {
        this.chassi03 = chassi03;
    }

    public int getQuantidade03() {
        return quantidade03;
    }

    public void setQuantidade03(int quantidade03) {
        this.quantidade03 = quantidade03;
    }

    public String getDataProducao() {
        return dataProducao;
    }

    public void setDataProducao(String dataProducao) {
        this.dataProducao = dataProducao;
    }

    public PickingStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PickingStatusEnum status) {
        this.status = status;
    }

    public int getSequence01() {
        return sequence01;
    }

    public void setSequence01(int sequence01) {
        this.sequence01 = sequence01;
    }

    public int getSequence02() {
        return sequence02;
    }

    public void setSequence02(int sequence02) {
        this.sequence02 = sequence02;
    }

    public int getSequence03() {
        return sequence03;
    }

    public void setSequence03(int sequence03) {
        this.sequence03 = sequence03;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
