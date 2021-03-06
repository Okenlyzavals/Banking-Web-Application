package by.epam.baranovsky.banking.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.util.Date;

/**
 * Java bean that represents a loan that is assigned to a user.
 *
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Loan extends Entity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double singlePaymentValue;
    private Double startingValue;
    private Double totalPaymentValue;
    private Double yearlyInterestRate;
    private Date issueDate;
    private Date dueDate;
    private Integer userId;
    private Integer statusId;
    private String statusName;
    private Integer cardId;
    private Integer accountId;


}
