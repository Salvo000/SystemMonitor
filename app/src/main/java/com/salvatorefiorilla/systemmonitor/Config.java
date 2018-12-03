package com.salvatorefiorilla.systemmonitor;

class Config {

    private static  final String EMAIL = "sysmonitorlogreport@gmail.com";
    private static  final String PASSWORD ="sysm0nit0r";

    synchronized  public static String getEMAIL() {
        return EMAIL;
    }

    synchronized  public static String getPASSWORD(){
        return PASSWORD;
    }

}
