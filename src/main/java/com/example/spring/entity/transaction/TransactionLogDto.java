package com.example.spring.entity.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class TransactionLogDto {
    @Expose
    @SerializedName("mode")
    private String mode;
    @Expose
    @SerializedName("amount")
    private Integer amount;
    @Expose
    @SerializedName("chargetime")
    private String chargeTime;
    @Expose
    @SerializedName("channel")
    private String channel;
    @Expose
    @SerializedName("serviceid")
    private String serviceId;
    @Expose
    @SerializedName("msisdn")
    private String msisdn;
    @Expose
    @SerializedName("params")
    private String params;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("command")
    private String command;
    @Expose
    @SerializedName("transactionid")
    private String transactionId;
}
