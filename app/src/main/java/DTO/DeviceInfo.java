package DTO;

import java.util.Objects;

public class DeviceInfo {
    public String modelName;
    public String imei;
    public String seriNum;

    public String label;



    public DeviceInfo(String modelName, String imei, String seriNum, String label) {
        this.modelName = modelName;
        this.imei = imei;
        this.seriNum = seriNum;
        this.label = label;

    }

    public boolean isWifiModel(){
        return Objects.equals(imei, seriNum);
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSeriNum() {
        return seriNum;
    }

    public void setSeriNum(String seriNum) {
        this.seriNum = seriNum;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
