package by.epam.baranovsky.banking.entity;

import lombok.Data;

import java.io.Serial;

@Data
public class Penalty extends Entity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double value;
    private Integer paymentAccountId;

    private Integer typeId;
    private String typeName;

    private String notice;
    private Integer userId;

    private Integer statusId;
    private String statusName;

}