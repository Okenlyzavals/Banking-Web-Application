package by.epam.baranovsky.banking.service.impl;

import by.epam.baranovsky.banking.dao.BillDAO;
import by.epam.baranovsky.banking.dao.exception.DAOException;
import by.epam.baranovsky.banking.dao.factory.impl.SqlDAOFactory;
import by.epam.baranovsky.banking.entity.Bill;
import by.epam.baranovsky.banking.entity.criteria.Criteria;
import by.epam.baranovsky.banking.entity.criteria.EntityParameters;
import by.epam.baranovsky.banking.service.BillService;
import by.epam.baranovsky.banking.service.exception.ServiceException;
import by.epam.baranovsky.banking.service.exception.ValidationException;
import by.epam.baranovsky.banking.service.validator.BillValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BillService.
 * Provides utils for working with Bill entities.
 *
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class BillServiceImpl implements BillService {

    private final BillValidator validator = new BillValidator();
    private final BillDAO billDAO = SqlDAOFactory.getInstance().getBillDAO();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bill> findByCriteria(Criteria<? extends EntityParameters.BillParam> criteria) throws ServiceException {
        List<Bill> bills = new ArrayList<>();
        try {
            bills = billDAO.findByCriteria(criteria);
        } catch (DAOException e) {
            throw new ServiceException("Unable to retrieve bills from DB",e);
        }
        return bills;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bill findById(Integer id) throws ServiceException {
        Bill bill;
        try{
            bill = billDAO.findEntityById(id);
        } catch (DAOException e) {
            throw new ServiceException("Unable to retrieve bill from DB",e);
        }
        return bill;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Bill bill) throws ServiceException {
        Integer result;
        try{
            if(!validator.validate(bill)){
                throw new ValidationException("Invalid input!");
            }
            if(bill.getId() == null || billDAO.findEntityById(bill.getId()) == null){
                throw new ValidationException("No bill with such ID");
            }
            result = billDAO.update(bill);
        } catch (DAOException e) {
            throw  new ServiceException("Unable to update bill in DB.",e);
        }
        return result>0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bill create(Bill bill) throws ServiceException {
        Bill result;
        try{
            if(!validator.validate(bill)){
                throw new ValidationException("Invalid input!");
            }
            result = billDAO.findEntityById(billDAO.create(bill));
        } catch (DAOException e) {
            throw  new ServiceException("Unable to create bill in DB.",e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Integer id) throws ServiceException {
        try {
            return delete(billDAO.findEntityById(id));
        } catch (DAOException e) {
            throw  new ServiceException("Unable to delete bill from DB.",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Bill bill) throws ServiceException {
        Integer res;
        try {
            if(!validator.validate(bill)){
                throw new ValidationException("Wrong input.");
            }
            res = billDAO.delete(bill);
        } catch (DAOException e) {
            throw  new ServiceException("Unable to delete bill from DB.",e);
        }
        return res>0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bill> findAll() throws ServiceException {
        List<Bill> bills;
        try {
            bills = billDAO.findAll();
        } catch (DAOException e) {
            throw  new ServiceException("Unable to retrieve bills from DB.",e);
        }
        return bills;
    }
}
