package by.epam.baranovsky.banking.dao.rowmapper.impl;

import by.epam.baranovsky.banking.constant.DBMetadata;
import by.epam.baranovsky.banking.dao.rowmapper.RowMapper;
import by.epam.baranovsky.banking.entity.Account;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of RowMapper
 * used to build Account entities.
 *
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class AccountRowMapper implements RowMapper<Account> {

    /**
     * {@inheritDoc}
     * @param resultSet JDBC ResultSet of a query.
     * @return instance of Account.
     * @throws SQLException
     */
    @Override
    public Account map(ResultSet resultSet) throws SQLException {

        Account account = new Account();

        account.setId(resultSet.getInt(DBMetadata.ACCOUNTS_ID));
        account.setAccountNumber(resultSet.getString(DBMetadata.ACCOUNTS_NUMBER));
        account.setBalance(resultSet.getDouble(DBMetadata.ACCOUNTS_BALANCE));
        account.setYearlyInterestRate(resultSet.getDouble(DBMetadata.ACCOUNTS_INTEREST));
        account.setStatusId(resultSet.getInt(DBMetadata.ACCOUNTS_ACCOUNT_STATUS_ID));
        account.setStatusName(resultSet.getString(DBMetadata.ACCOUNT_STATUS_DESC));
        account.setUsers(null);

        return account;
    }
}

