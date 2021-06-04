package org.starcoin.scan.service;

public class ServiceUtils {

    public static String getIndex(String network, String indexConstant) {
        return network + "." + indexConstant;
    }

    public static final int ELASTICSEARCH_MAX_HITS = 10000;

}
