package by.epam.baranovsky.banking.service.impl;

import by.epam.baranovsky.banking.dao.LoanDAO;
import by.epam.baranovsky.banking.dao.exception.DAOException;
import by.epam.baranovsky.banking.dao.factory.impl.SqlDAOFactory;
import by.epam.baranovsky.banking.entity.Loan;
import by.epam.baranovsky.banking.entity.criteria.Criteria;
import by.epam.baranovsky.banking.entity.criteria.EntityParameters;
import by.epam.baranovsky.banking.service.LoanService;
import by.epam.baranovsky.banking.service.exception.ServiceException;
import by.epam.baranovsky.banking.service.exception.ValidationException;
import by.epam.baranovsky.banking.service.validator.LoanValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of LoanService.
 * Provides utils for working with Loan entities.
 *
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class LoanServiceImpl implements LoanService {

    private final LoanValidator validator = new LoanValidator();
    private final LoanDAO loanDAO = SqlDAOFactory.getInstance().getLoanDAO();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Loan> findByCriteria(Criteria<? extends EntityParameters.LoanParams> criteria) throws ServiceException {
        List<Loan> loans = new ArrayList<>();
        try {
            loans = loanDAO.findByCriteria(criteria);
        } catch (DAOException e) {
            throw new ServiceException("Unable to retrieve loans from DB",e);
        }
        return loans;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Loan findById(Integer id) throws ServiceException {
        Loan loan;
        try{
            loan = loanDAO.findEntityById(id);
        } catch (DAOException e) {
            throw new ServiceException("Unable to retrieve loan from DB",e);
        }
        return loan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Loan loan) throws ServiceException {
        Integer result;
        try{
            if(!validator.validate(loan)){
                throw new ValidationException("Invalid input!");
            }
            if(loan.getId() == null || loanDAO.findEntityById(loan.getId()) == null){
                throw new ValidationException("No loan with such ID");
            }
            result = loanDAO.update(loan);
        } catch (DAOException e) {
            throw  new ServiceException("Unable to update loan in DB.",e);
        }
        return result>0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Loan create(Loan loan) throws ServiceException {
        Loan result;
        try{
            if(!validator.validate(loan)){
                throw new ValidationException("Invalid input!");
            }
            result = loanDAO.findEntityById(loanDAO.create(loan));
        } catch (DAOException e) {
            throw  new ServiceException("Unable to create loan in DB.",e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Integer id) throws ServiceException {
        try {
            return delete(loanDAO.findEntityById(id));
        } catch (DAOException e) {
            throw  new ServiceException("Unable to delete loan from DB.",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Loan loan) throws ServiceException {
        Integer res;
        try {
            if(!validator.validate(loan)){
                throw new ValidationException("Wrong input.");
            }
            res = loanDAO.delete(loan);
        } catch (DAOException e) {
            throw  new ServiceException("Unable to delete loan from DB.",e);
        }
        return res>0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Loan> findAll() throws ServiceException {
        List<Loan> loans;
        try {
            loans = loanDAO.findAll();
        } catch (DAOException e) {
            throw  new ServiceException("Unable to retrieve loans from DB.",e);
        }
        return loans;
    }
}
