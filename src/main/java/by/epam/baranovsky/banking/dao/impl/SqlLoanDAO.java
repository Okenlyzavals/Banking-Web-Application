package by.epam.baranovsky.banking.dao.impl;

import by.epam.baranovsky.banking.constant.DBMetadata;
import by.epam.baranovsky.banking.dao.LoanDAO;
import by.epam.baranovsky.banking.dao.exception.DAOException;
import by.epam.baranovsky.banking.dao.query.Query;
import by.epam.baranovsky.banking.dao.query.QueryMaster;
import by.epam.baranovsky.banking.dao.query.impl.SqlQueryMaster;
import by.epam.baranovsky.banking.dao.rowmapper.RowMapper;
import by.epam.baranovsky.banking.dao.rowmapper.RowMapperFactory;
import by.epam.baranovsky.banking.entity.Loan;
import by.epam.baranovsky.banking.entity.criteria.Criteria;
import by.epam.baranovsky.banking.entity.criteria.EntityParameters;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of LoanDAO for use with MySQL DB.
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class SqlLoanDAO implements LoanDAO {

    /** Mapper to parse ResultSet objects into entities. */
    private static final RowMapper<Loan> mapper = RowMapperFactory.getLoanRowMapper();

    /** Object that executes SQL queries. */
    private static final QueryMaster<Loan> queryMaster = new SqlQueryMaster<>(mapper);

    private static final String SQL_SELECT_ALL = String.format(
            "SELECT * FROM %s LEFT JOIN %s ON %s=%s",
            DBMetadata.LOANS_TABLE, DBMetadata.LOAN_STATUS_TABLE,
            DBMetadata.LOANS_STATUS_ID, DBMetadata.LOAN_STATUS_ID);

    private static final String SQL_SELECT_BY_ID = String.format("" +
            "%s WHERE %s=?", SQL_SELECT_ALL, DBMetadata.LOANS_ID);

    private static final String SQL_INSERT= String.format(
            "INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            DBMetadata.LOANS_TABLE, DBMetadata.LOANS_ID, DBMetadata.LOANS_SINGLE_PAYMENT_VALUE,
            DBMetadata.LOANS_STARTING_VALUE, DBMetadata.LOANS_TOTAL_VALUE,
            DBMetadata.LOANS_INTEREST, DBMetadata.LOANS_ISSUE_DATE,
            DBMetadata.LOANS_DUE_DATE, DBMetadata.LOANS_USER_ID,
            DBMetadata.LOANS_STATUS_ID, DBMetadata.LOANS_CARD_ID,
            DBMetadata.LOANS_ACCOUNT_ID);

    private static final String SQL_UPDATE= String.format(
            "UPDATE %s SET %s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=? WHERE %s=?",
            DBMetadata.LOANS_TABLE, DBMetadata.LOANS_SINGLE_PAYMENT_VALUE,
            DBMetadata.LOANS_STARTING_VALUE, DBMetadata.LOANS_TOTAL_VALUE,
            DBMetadata.LOANS_INTEREST, DBMetadata.LOANS_ISSUE_DATE,
            DBMetadata.LOANS_DUE_DATE, DBMetadata.LOANS_USER_ID,
            DBMetadata.LOANS_STATUS_ID, DBMetadata.LOANS_CARD_ID,
            DBMetadata.LOANS_ACCOUNT_ID,DBMetadata.LOANS_ID);

    private static final String SQL_UPDATE_ACC = String.format(
            "UPDATE %s SET %s=%s+? WHERE %s=?",
            DBMetadata.ACCOUNTS_TABLE, DBMetadata.ACCOUNTS_BALANCE,
            DBMetadata.ACCOUNTS_BALANCE, DBMetadata.ACCOUNTS_ID);

    private static final String SQL_DELETE= String.format(
            "DELETE FROM %s WHERE %s=? LIMIT 1",
            DBMetadata.LOANS_TABLE, DBMetadata.LOANS_ID);

    /**
     * {@inheritDoc}
     * @return Number of rows affected in DB.
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public Integer update(Loan entity) throws DAOException {
        return queryMaster.executeUpdate(
                SQL_UPDATE,
                entity.getSinglePaymentValue(),
                entity.getStartingValue(),
                entity.getTotalPaymentValue(),
                entity.getYearlyInterestRate(),
                new Date(entity.getIssueDate().getTime()),
                new Date(entity.getDueDate().getTime()),
                entity.getUserId(),
                entity.getStatusId(),
                entity.getCardId(),
                entity.getAccountId(),
                entity.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     *     Inserts a row into loans table,
     *     as well as subtracts the value of a loan
     *     from bank own account and adds it to
     *     a borrower's account.
     * </p>
     * @return Generated key of inserted loan row.
     * @throws DAOException if DAOException is thrown by QueryMaster.
     */
    @Override
    public Integer create(Loan entity) throws DAOException {
        List<Query> queries = new ArrayList<>();
        queries.add(new Query(SQL_INSERT,
                entity.getSinglePaymentValue(),
                entity.getStartingValue(),
                entity.getTotalPaymentValue(),
                entity.getYearlyInterestRate(),
                new Date(entity.getIssueDate().getTime()),
                new Date(entity.getDueDate().getTime()),
                entity.getUserId(),
                entity.getStatusId(),
                entity.getCardId(),
                entity.getAccountId()));
        queries.add(new Query(
                SQL_UPDATE_ACC,
                -entity.getStartingValue(),
                DBMetadata.BANK_ACCOUNT_ID));
        queries.add(new Query(SQL_UPDATE_ACC,
                entity.getStartingValue(),
                entity.getAccountId()));

        return queryMaster.executeTransaction(queries);
    }

    /**
     * {@inheritDoc}
     * @throws DAOException if DAOException is thrown by QueryMaster.
     */
    @Override
    public Loan findEntityById(Integer id) throws DAOException {
        return queryMaster.executeSingleEntityQuery(SQL_SELECT_BY_ID, id);
    }

    /**
     * {@inheritDoc}
     * @return Number of rows affected in DB.
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public Integer delete(Integer id) throws DAOException {
        return queryMaster.executeUpdate(SQL_DELETE, id);
    }

    /**
     * {@inheritDoc}
     * @return Number of rows affected in DB.
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public Integer delete(Loan entity) throws DAOException {
        return delete(entity.getId());
    }

    /**
     * {@inheritDoc}
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public List<Loan> findAll() throws DAOException {
        return queryMaster.executeQuery(SQL_SELECT_ALL);
    }

    /**
     * {@inheritDoc}
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public List<Loan> findByCriteria(Criteria<? extends EntityParameters.LoanParams> criteria) throws DAOException {
        Query query = criteria.generateQuery(SQL_SELECT_ALL);
        return queryMaster.executeQuery(query.getSqlQueryString(), query.getParameters());
    }
}
