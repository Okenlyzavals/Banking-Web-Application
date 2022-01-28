package by.epam.baranovsky.banking.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OperationTransferObject{
    private Date date;
    private String accountNumber;
    private String targetAccountNumber;
    private String cardNumber;
    private String targetCardNumber;
    private Double value;
    private Integer bill;
    private Integer penalty;
    private Integer typeId;
    private Boolean isAccrual;

    public OperationTransferObject(){}
}