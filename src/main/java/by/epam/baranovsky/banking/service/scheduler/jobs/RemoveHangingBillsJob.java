package by.epam.baranovsky.banking.service.scheduler.jobs;

import by.epam.baranovsky.banking.constant.ConfigManager;
import by.epam.baranovsky.banking.constant.ConfigParams;
import by.epam.baranovsky.banking.constant.DBMetadata;
import by.epam.baranovsky.banking.entity.Bill;
import by.epam.baranovsky.banking.entity.criteria.Criteria;
import by.epam.baranovsky.banking.entity.criteria.EntityParameters;
import by.epam.baranovsky.banking.entity.criteria.SingularValue;
import by.epam.baranovsky.banking.service.exception.ServiceException;
import org.quartz.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * A job that removes bills with unspecified due date after a certain time.
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class RemoveHangingBillsJob extends AbstractJob{

    public static final Integer TIME_LIMIT_HANGING = Integer.valueOf(
            ConfigManager.getInstance().getValue(ConfigParams.BILL_HANGING_TIME_LIMIT));
    private static final String NAME = "hangingBillsRemoval";
    private static final JobDetail DETAIL = JobBuilder.newJob(RemoveHangingBillsJob.class)
            .withIdentity(NAME, GROUP_NAME)
            .build();
    /**
     * Fires every 8 hours.
     */
    private static final Trigger TRIGGER = newTrigger()
            .withIdentity(NAME, GROUP_NAME)
            .withSchedule(cronSchedule("0 0 0/8 ? * * *"))
            .forJob(NAME, GROUP_NAME)
            .build();

    /**
     * Checks if bills are hanging and deletes hanging bills.
     * @param jobExecutionContext context of the job
     * @throws JobExecutionException if ServiceException occurs
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try{
            for(Bill bill : getPendingBills()){
                if(isHanging(bill)){
                    billService.delete(bill);
                }
            }
        } catch (ServiceException e) {
            logger.error("Unable to execute ganging bill removal", e);
            throw new JobExecutionException("Unable to execute ganging bill removal", e);
        }
    }

    /**
     * Checks if bill is hanging.
     * @param bill bill to check
     * @return {@code true} if more than
     * TIME_LIMIT_HANGING in months has passed since issue date.
     */
    private boolean isHanging(Bill bill){

        Period period = Period.between(
                bill.getIssueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                LocalDate.now());

        return period.toTotalMonths()>=TIME_LIMIT_HANGING;
    }

    private List<Bill> getPendingBills() throws ServiceException {
        Criteria<EntityParameters.BillParam> criteria = new Criteria<>();
        criteria.add(EntityParameters.BillParam.STATUS_ID, new SingularValue<>(DBMetadata.BILL_STATUS_PENDING));

        List<Bill> bills = billService.findByCriteria(criteria);
        bills.removeIf(bill -> bill.getDueDate() != null);
        return bills;
    }

    public static JobDetail getDetail() {
        return DETAIL;
    }

    public static Trigger getTrigger() {
        return TRIGGER;
    }
}
