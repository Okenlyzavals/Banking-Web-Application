package by.epam.baranovsky.banking.dao.rowmapper.impl;

import by.epam.baranovsky.banking.constant.DBMetadata;
import by.epam.baranovsky.banking.dao.rowmapper.RowMapper;
import by.epam.baranovsky.banking.entity.Loan;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of RowMapper
 * used to build Loan entities.
 *
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class LoanRowMapper implements RowMapper<Loan> {

    /**
     * {@inheritDoc}
     * @param resultSet JDBC ResultSet of a query.
     * @return instance of Loan.
     * @throws SQLException
     */
    @Override
    public Loan map(ResultSet resultSet) throws SQLException {
        Loan loan = new Loan();

        loan.setId(resultSet.getInt(DBMetadata.LOANS_ID));

        loan.setSinglePaymentValue(resultSet.getDouble(DBMetadata.LOANS_SINGLE_PAYMENT_VALUE));
        loan.setStartingValue(resultSet.getDouble(DBMetadata.LOANS_STARTING_VALUE));
        loan.setTotalPaymentValue(resultSet.getDouble(DBMetadata.LOANS_TOTAL_VALUE));
        loan.setYearlyInterestRate(resultSet.getDouble(DBMetadata.LOANS_INTEREST));

        loan.setIssueDate(resultSet.getDate(DBMetadata.LOANS_ISSUE_DATE));
        loan.setDueDate(resultSet.getDate(DBMetadata.LOANS_DUE_DATE));

        loan.setUserId(resultSet.getInt(DBMetadata.LOANS_USER_ID));

        loan.setStatusId(resultSet.getInt(DBMetadata.LOANS_STATUS_ID));
        loan.setStatusName(resultSet.getString(DBMetadata.LOAN_STATUS_NAME));
        loan.setAccountId(resultSet.getInt(DBMetadata.LOANS_ACCOUNT_ID));

        loan.setCardId(
                resultSet.getInt(DBMetadata.LOANS_CARD_ID) != 0
                        ? resultSet.getInt(DBMetadata.LOANS_CARD_ID)
                        : null);

        return loan;
    }

}
