package by.epam.baranovsky.banking.controller.command.impl.gotocommand;

import by.epam.baranovsky.banking.controller.command.AbstractCommand;
import by.epam.baranovsky.banking.controller.constant.PageUrls;
import by.epam.baranovsky.banking.controller.constant.RequestAttributeNames;
import by.epam.baranovsky.banking.controller.constant.SessionAttributeName;
import by.epam.baranovsky.banking.entity.*;
import by.epam.baranovsky.banking.entity.criteria.Criteria;
import by.epam.baranovsky.banking.entity.criteria.EntityParameters;
import by.epam.baranovsky.banking.entity.criteria.SingularValue;
import by.epam.baranovsky.banking.entity.dto.OperationTransferObject;
import by.epam.baranovsky.banking.service.exception.ServiceException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Implementation of Command
 * used to forward user to home page.
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class GoToHomePageCommand extends AbstractCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute(SessionAttributeName.USER_ID);

        if(userId != null){
            try {
                User user = userService.getById(userId);
                request.setAttribute(RequestAttributeNames.USER_DATA, user);
                request.setAttribute(
                        RequestAttributeNames.OPERATIONS_DATA,
                        getUserOperationDTOs(userId));

            } catch (ServiceException e) {
                logger.error(e);
                RequestDispatcher dispatcher = request.getRequestDispatcher(PageUrls.ERROR_PAGE);
                dispatcher.forward(request, response);
            }
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(PageUrls.HOME_PAGE);
        dispatcher.forward(request, response);
    }

    /**
     * Collects operations tied to user and parses them into DTOs.
     * @param id ID of user.
     * @return List of Instances of OperationTransferObject
     * parsed from operations tied to user.
     * @throws ServiceException
     */
    private List<OperationTransferObject> getUserOperationDTOs(Integer id) throws ServiceException{

        List<Integer> userAccs = getUserAccountIds(id);
        List<Integer> userCards = getUserCardsIds(id);
        List<Operation> operations = getOperations(userAccs, userCards);

        List<OperationTransferObject> operationPackages = new ArrayList<>();

        for(Operation operation : operations){
            OperationTransferObject oto = new OperationTransferObject();

            oto.setIsAccrual(!userCards.contains(operation.getBankCardId()) && !userAccs.contains(operation.getAccountId()));
            oto.setTypeId(operation.getTypeId());
            oto.setValue(operation.getValue());
            if(operation.getAccountId() != null && operation.getAccountId() != 0){
                oto.setAccountNumber(accountService.findById(
                        operation.getAccountId()).getAccountNumber());
            }
            if(operation.getTargetAccountId() != null && operation.getTargetAccountId() != 0){
                oto.setTargetAccountNumber(accountService.findById(
                        operation.getTargetAccountId()).getAccountNumber());
            }
            if(operation.getBankCardId() != null && operation.getBankCardId() != 0){
                oto.setCardNumber(maskCardNumber(cardService.findById(
                        operation.getBankCardId()).getNumber()));
            }
            if(operation.getTargetBankCardId() != null && operation.getTargetBankCardId() != 0){
                oto.setTargetCardNumber(maskCardNumber(cardService.findById(
                        operation.getTargetBankCardId()).getNumber()));
            }
            oto.setBill(operation.getBillId());
            oto.setPenalty(operation.getPenaltyId());
            oto.setDate(operation.getOperationDate());
            oto.setCommission(operation.getCommission());
            operationPackages.add(oto);
        }
        operationPackages.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

        return operationPackages;
    }

    /**
     * Retrieves all operations related to passed lists of accounts and cards.
     * @param accountIds List of account IDs.
     * @param cardIds List of cards IDs.
     * @return List of operations that include any cards of account from passed lists.
     * @throws ServiceException
     */
    private List<Operation> getOperations(List<Integer> accountIds,
                                          List<Integer> cardIds) throws ServiceException {
        Criteria<EntityParameters.OperationParam> criteria = new Criteria<>(Criteria.SQL_OR);

        for(Integer id : cardIds){
            criteria.add(
                    EntityParameters.OperationParam.CARD,
                    new SingularValue<>(id));
            criteria.add(
                    EntityParameters.OperationParam.TARGET_CARD,
                    new SingularValue<>(id));
        }

        for(Integer id : accountIds){
            criteria.add(
                    EntityParameters.OperationParam.ACCOUNT,
                    new SingularValue<>(id));
            criteria.add(
                    EntityParameters.OperationParam.TARGET_ACCOUNT,
                    new SingularValue<>(id));
        }

        return operationService.findByCriteria(criteria);

    }

    /**
     * Retrieves all IDs of accounts of a user.
     * @param id ID of user.
     * @return List of IDs of all accounts belonging to user.
     * @throws ServiceException
     */
    private List<Integer> getUserAccountIds(Integer id) throws ServiceException {
        List<Account> userAccounts =  accountService.findByUserId(id);

        List<Integer> userAccsIds = new ArrayList<>();
        for(Account account : userAccounts){
            userAccsIds.add(account.getId());
        }

        return userAccsIds;
    }

    /**
     * Retrieves all IDs of cards of a user.
     * @param id ID of user.
     * @return List of IDs of all cards belonging to user.
     * @throws ServiceException
     */
    private List<Integer> getUserCardsIds(Integer id) throws ServiceException {
        List<BankingCard> userCards =  cardService.findByUser(id);

        List<Integer> userCardIds = new ArrayList<>();
        for(BankingCard card : userCards){
            userCardIds.add(card.getId());
        }

        return userCardIds;
    }


}
