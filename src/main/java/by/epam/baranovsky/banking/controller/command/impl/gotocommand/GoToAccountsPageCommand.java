package by.epam.baranovsky.banking.controller.command.impl.gotocommand;

import by.epam.baranovsky.banking.constant.DBMetadata;
import by.epam.baranovsky.banking.controller.command.AbstractCommand;
import by.epam.baranovsky.banking.controller.constant.PageUrls;
import by.epam.baranovsky.banking.controller.constant.RequestAttributeNames;
import by.epam.baranovsky.banking.controller.constant.SessionAttributeName;
import by.epam.baranovsky.banking.entity.Account;
import by.epam.baranovsky.banking.service.exception.ServiceException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Command
 * used to forward user to the page that lists all their accounts.
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class GoToAccountsPageCommand extends AbstractCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute(SessionAttributeName.USER_ID);
        try{
            List<Account> allUserAccounts = accountService.findByUserId(userId);
            parseIntoCategoriesAndPutIntoRequest(allUserAccounts, request);
        }catch (ServiceException e){
            logger.error(e);
            RequestDispatcher dispatcher = request.getRequestDispatcher(PageUrls.ERROR_PAGE);
            dispatcher.forward(request, response);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(PageUrls.ACCOUNTS_PAGE);
        dispatcher.forward(request, response);
    }

    /**
     * Parses list of accounts into different lists by their type
     * and sets them as request attributes.
     * @param list List of accounts to parse.
     * @param request Servlet request
     */
    private void parseIntoCategoriesAndPutIntoRequest(List<Account> list, HttpServletRequest request){
        List<Account> blockedList = new ArrayList<>();
        List<Account> suspendedList = new ArrayList<>();
        List<Account> unlockedList = new ArrayList<>();
        List<Account> pendingList = new ArrayList<>();

        for(Account account : list){
            if(account.getStatusId().equals(DBMetadata.ACCOUNT_STATUS_BLOCKED)){
                blockedList.add(account);
            } else if(account.getStatusId().equals(DBMetadata.ACCOUNT_STATUS_PENDING)){
                pendingList.add(account);
            } else if(account.getStatusId().equals(DBMetadata.ACCOUNT_STATUS_SUSPENDED)){
                suspendedList.add(account);
            } else if(account.getStatusId().equals(DBMetadata.ACCOUNT_STATUS_UNLOCKED)){
                unlockedList.add(account);
            }
        }

        request.setAttribute(RequestAttributeNames.BLOCKED_ACCS, blockedList);
        request.setAttribute(RequestAttributeNames.UNLOCKED_ACCS, unlockedList);
        request.setAttribute(RequestAttributeNames.SUSPENDED_ACCS, suspendedList);
        request.setAttribute(RequestAttributeNames.PENDING_ACCS_COUNT, pendingList.size());

    }

}
