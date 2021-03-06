package by.epam.baranovsky.banking.dao.rowmapper.impl;

import by.epam.baranovsky.banking.constant.DBMetadata;
import by.epam.baranovsky.banking.dao.rowmapper.RowMapper;
import by.epam.baranovsky.banking.entity.BankingCard;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Implementation of RowMapper
 * used to build BankingCard entities.
 *
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class CardRowMapper implements RowMapper<BankingCard> {

    /**
     * {@inheritDoc}
     * @param resultSet JDBC ResultSet of a query.
     * @return instance of BankingCard.
     * @throws SQLException
     */
    @Override
    public BankingCard map(ResultSet resultSet) throws SQLException {
        BankingCard card = new BankingCard();

        card.setId(resultSet.getInt(DBMetadata.BANK_CARDS_ID));

        card.setCvc(resultSet.getString(DBMetadata.BANK_CARDS_CVC));
        card.setNumber(resultSet.getString(DBMetadata.BANK_CARDS_NUMBER));
        card.setPin(resultSet.getString(DBMetadata.BANK_CARDS_PIN));

        card.setRegistrationDate(resultSet.getDate(DBMetadata.BANK_CARDS_REGISTRATION_DATE));
        card.setExpirationDate(resultSet.getDate(DBMetadata.BANK_CARDS_EXPIRATION_DATE));

        card.setUserId(resultSet.getInt(DBMetadata.BANK_CARDS_USER_ID));

        card.setStatusId(resultSet.getInt(DBMetadata.BANK_CARDS_STATUS_ID));
        card.setStatusName(resultSet.getString(DBMetadata.CARD_STATUS_NAME));

        card.setCardTypeId(resultSet.getInt(DBMetadata.BANK_CARDS_TYPE_ID));
        card.setCardTypeName(resultSet.getString(DBMetadata.CARD_TYPE_NAME));

        card.setAccountId(
                resultSet.getInt(DBMetadata.BANK_CARDS_ACCOUNT_ID) != 0
                        ? resultSet.getInt(DBMetadata.BANK_CARDS_ACCOUNT_ID)
                        : null);
        card.setOverdraftMax(
                resultSet.getDouble(DBMetadata.BANK_CARDS_OVERDRAFT_MAXIMUM) != 0
                        ? resultSet.getDouble(DBMetadata.BANK_CARDS_OVERDRAFT_MAXIMUM)
                        : null);
        card.setOverdraftInterestRate(
                resultSet.getDouble(DBMetadata.BANK_CARDS_OVERDRAFT_INTEREST) != 0
                        ? resultSet.getDouble(DBMetadata.BANK_CARDS_OVERDRAFT_INTEREST)
                        : null);
        card.setBalance(
                resultSet.getDouble(DBMetadata.BANK_CARDS_BALANCE) != 0
                        ? resultSet.getDouble(DBMetadata.BANK_CARDS_BALANCE)
                        : null);


        return card;
    }
}
