package by.epam.baranovsky.banking.controller.command.impl.account;

import by.epam.baranovsky.banking.constant.*;
import by.epam.baranovsky.banking.controller.command.AbstractCommand;
import by.epam.baranovsky.banking.controller.constant.PageUrls;
import by.epam.baranovsky.banking.controller.constant.RequestAttributeNames;
import by.epam.baranovsky.banking.controller.constant.RequestParamName;
import by.epam.baranovsky.banking.controller.constant.SessionAttributeName;
import by.epam.baranovsky.banking.entity.Account;
import by.epam.baranovsky.banking.service.exception.ServiceException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Implementation of Command
 * used for requesting employees
 * to create new account.
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class NewAccCommand extends AbstractCommand {

    private static final Double DEFAULT_BALANCE = 0d;
    private static final Double DEFAULT_RATE = 0d;
    private static final Integer MAX_ACC_REQUESTS = Integer.valueOf(ConfigManager.getInstance().getValue(ConfigParams.ACCOUNT_REQUESTS_MAX));
    private static final String DEFAULT_COUNTRY_CODE="BY";
    private static final String REDIRECT_TO_ACCS=String.format(
            "%s?%s=%s",
            RequestParamName.CONTROLLER,
            RequestParamName.COMMAND_NAME,
            CommandName.GOTO_ACCOUNTS);


    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String countryCode = request.getParameter(RequestParamName.ACCOUNT_COUNTRY);

        try{
            if(tooManyRequests(request)){
                request.setAttribute(RequestAttributeNames.ERROR_MSG, Message.TOO_MANY_CREATION_REQUESTS);
                RequestDispatcher dispatcher = request.getRequestDispatcher(REDIRECT_TO_ACCS);
                dispatcher.forward(request, response);
                return;
            }

            Account account = new Account();
            String number;
            do{
                number = generateNumber(countryCode);
            } while (accountService.findByNumber(number) != null);

            account.setAccountNumber(number);
            account.addUser((Integer) request.getSession().getAttribute(SessionAttributeName.USER_ID));
            account.setBalance(DEFAULT_BALANCE);
            account.setStatusId(DBMetadata.ACCOUNT_STATUS_PENDING);
            account.setYearlyInterestRate(DEFAULT_RATE);

            accountService.create(account);
            response.sendRedirect(REDIRECT_TO_ACCS);
        } catch (ServiceException e) {
            logger.error(e);
            RequestDispatcher dispatcher = request.getRequestDispatcher(PageUrls.ERROR_PAGE);
            dispatcher.forward(request, response);
        }

    }

    /**
     * Generates number for account.
     * @param countryCode Country code of an account.
     * @return String representation of account number: a country code (2 letters)
     * and an 18-digit number.
     */
    private String generateNumber(String countryCode){

        if(countryCode == null || countryCode.isBlank()){
            countryCode = DEFAULT_COUNTRY_CODE;
        }

        Random random = new Random();
        return String.format(
                "%2s%04d%04d%04d%04d%02d",
                countryCode,
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(100));
    }

    /**
     * Checks if user has requested too many accounts.
     * @param request Servlet request.
     * @return {@code true} if user has requested more than {@code MAX_ACC_REQUESTS} accounts,
     * {@code false} otherwise.
     * @throws ServiceException
     */
    private boolean tooManyRequests(HttpServletRequest request) throws ServiceException {
        Integer userId = (Integer) request.getSession().getAttribute(SessionAttributeName.USER_ID);

        List<Account> pendingAccounts = accountService.findByUserId(userId);
        pendingAccounts.removeIf(account -> !account.getStatusId().equals(DBMetadata.ACCOUNT_STATUS_PENDING));

        return pendingAccounts.size() >= MAX_ACC_REQUESTS;
    }


}
