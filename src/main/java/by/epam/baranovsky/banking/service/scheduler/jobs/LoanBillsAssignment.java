package by.epam.baranovsky.banking.service.scheduler.jobs;

import by.epam.baranovsky.banking.constant.ConfigManager;
import by.epam.baranovsky.banking.constant.ConfigParams;
import by.epam.baranovsky.banking.entity.Bill;
import by.epam.baranovsky.banking.entity.Loan;
import by.epam.baranovsky.banking.entity.Penalty;
import by.epam.baranovsky.banking.service.exception.ServiceException;
import org.quartz.*;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * A job that assigns monthly payments for loans.
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class LoanBillsAssignment extends AbstractJob{

    private static final String NAME = "loanBillsAssignment";
    private static final JobDetail DETAIL = JobBuilder.newJob(LoanBillsAssignment.class)
            .withIdentity(NAME, GROUP_NAME)
            .build();
    /**
     * Fires every first day of the month.
     */
    private static final Trigger TRIGGER = newTrigger()
            .withIdentity(NAME, GROUP_NAME)
            .withSchedule(cronSchedule("0 0 0 1 * ? *"))
            .forJob(NAME, GROUP_NAME)
            .build();

    /**
     * Checks if loan still needs payments and assigns them in form of Bills.
     * @param jobExecutionContext
     * @throws JobExecutionException if ServiceException occurs
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            List<Loan> loans = loanService.findAll();

            for(Loan loan : loans){

                if(loan.getStatusId().equals(LOAN_STATUS_PENDING)){
                    assignPayments(loan);
                }
                if(loan.getStatusId().equals(LOAN_STATUS_OVERDUE)){
                    assignPaymentsOverdue(loan);
                }

            }

        } catch (ServiceException e) {
            logger.error("Unable to assign new bills for loans",e);
            throw new JobExecutionException("Unable to assign new bills for loans",e);
        }
    }

    /**
     * Creates new bill for a loan.
     * @param loan loan to assign bill to.
     * @throws ServiceException
     */
    private void assignPayments(Loan loan) throws ServiceException {
        Bill newLoanBill = new Bill();
        newLoanBill.setLoanId(loan.getId());
        newLoanBill.setUserId(loan.getUserId());
        newLoanBill.setIssueDate(new Date());
        newLoanBill.setDueDate(null);
        newLoanBill.setStatusId(BILL_STATUS_PENDING);
        newLoanBill.setPenaltyId(null);
        newLoanBill.setPaymentAccountId(BANK_ACCOUNT_ID);
        newLoanBill.setValue(loan.getSinglePaymentValue());

        billService.create(newLoanBill);

    }

    /**
     * Assigns bills and payments for overdue loans.
     * @param loan anoverdue loan
     * @throws ServiceException
     */
    private void assignPaymentsOverdue(Loan loan) throws ServiceException {

        double percentage = Math.pow(1d + (loan.getYearlyInterestRate()/100d), 1d/12d);

        double overdueValue = loan.getTotalPaymentValue()*(percentage-1d);

        Double penaltyPercentage = Double.valueOf(ConfigManager.getInstance().getValue(ConfigParams.LOAN_PENALTY));
        Penalty penalty = new Penalty();
        penalty.setStatusId(PENALTY_STATUS_UNASSIGNED);
        penalty.setUserId(loan.getUserId());
        penalty.setNotice(penaltyPercentage*100 + "% of loan starting sum for every month of overdue after the first");
        penalty.setTypeId(PENALTY_TYPE_FEE);
        penalty.setValue(loan.getStartingValue()*penaltyPercentage);
        penalty.setPaymentAccountId(BANK_ACCOUNT_ID);

        penalty = penaltyService.create(penalty);

        Bill newLoanBill = new Bill();
        newLoanBill.setLoanId(loan.getId());
        newLoanBill.setUserId(loan.getUserId());
        newLoanBill.setIssueDate(new Date());
        newLoanBill.setDueDate(java.sql.Date.valueOf(LocalDate.now().plusMonths(1)));
        newLoanBill.setStatusId(BILL_STATUS_PENDING);
        newLoanBill.setPenaltyId(penalty.getId());
        newLoanBill.setPaymentAccountId(BANK_ACCOUNT_ID);
        newLoanBill.setValue(overdueValue);

        billService.create(newLoanBill);

        loan.setTotalPaymentValue(loan.getTotalPaymentValue()+overdueValue);
        loanService.update(loan);

    }

    public static JobDetail getDetail(){
        return DETAIL;
    }

    public static Trigger getTrigger(){
        return TRIGGER;
    }
}
