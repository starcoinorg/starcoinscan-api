package org.starcoin.scan.config;

import com.alibaba.fastjson.annotation.JSONField;

public class Event {

    @JSONField(name = "event_seq_number")
    private String eventSeqNumber;

    @JSONField(name = "block_hash")
    private String blockHash;

    @JSONField(name = "block_number")
    private String blockNumber;

    @JSONField(name = "transaction_hash")
    private String transactionHash;

    @JSONField(name ="transaction_index")
    private String transactionIndex;

    private String data;

    @JSONField(name="type_tag")
    private String typeTag;

    @JSONField(name="event_key")
    private String eventKey;

    @JSONField(name = "event_address")
    private String eventAddress;

    @JSONField(name="tag_address")
    private String tagAddress;

    @JSONField(name = "tag_module")
    private String tagModule;

    @JSONField(name = "tag_name")
    private String tagName;

    private long timestamp;

    public String getEventSeqNumber() {
        return eventSeqNumber;
    }

    public void setEventSeqNumber(String eventSeqNumber) {
        this.eventSeqNumber = eventSeqNumber;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTypeTag() {
        return typeTag;
    }

    public void setTypeTag(String typeTag) {
        this.typeTag = typeTag;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getTagAddress() {
        return tagAddress;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress;
    }

    public String getTagModule() {
        return tagModule;
    }

    public void setTagModule(String tagModule) {
        this.tagModule = tagModule;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
