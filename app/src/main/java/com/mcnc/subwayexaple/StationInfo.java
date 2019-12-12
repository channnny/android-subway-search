package com.mcnc.subwayexaple;

public class StationInfo {
    private String name;
    private String lineNum;
    private String rideNum;
    private String alightNum;
    private String workDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLineNum(){
        return lineNum;
    }

    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    public String getRideNum() {
        return this.rideNum;
    }

    public void setRideNum(String rideNum) {
        this.rideNum = rideNum;
    }

    public String getAlightNum() {
        return alightNum;
    }

    public void setAlightNum(String alightNum) {
        this.alightNum = alightNum;
    }

    public String getWorkDate() {
        return this.workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public void setAllData(String name, String lineNum, String rideNum, String alightNum, String workDate) {
        this.name = name;
        this.lineNum = lineNum;
        this.rideNum = rideNum;
        this.alightNum = alightNum;
        this.workDate = workDate;
    }

}
