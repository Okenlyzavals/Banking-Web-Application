package by.epam.baranovsky.banking.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.util.Date;

/**
 * Java bean that represents a bill that is assigned to a user.
 *
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Bill extends Entity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double value;
    private Date issueDate;
    private Date dueDate;

    private Integer userId;
    private Integer bearerId;
    private Integer paymentAccountId;

    private Integer statusId;
    private String statusName;

    private Integer penaltyId;
    private Integer loanId;

    private String notice;
}
