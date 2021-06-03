package org.starcoin.scan.service;

public class ServiceUtils {

    public static String getIndex(String network, String indexConstant) {
        StringBuilder sb = new StringBuilder();
        sb.append(network).append(".").append(indexConstant);
        return sb.toString();
    }
}
