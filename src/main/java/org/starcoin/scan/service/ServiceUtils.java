package org.starcoin.scan.service;

public class ServiceUtils {

    public static String getIndex(String network, String indexConstant) {
        return network + "." + indexConstant;
    }
}
