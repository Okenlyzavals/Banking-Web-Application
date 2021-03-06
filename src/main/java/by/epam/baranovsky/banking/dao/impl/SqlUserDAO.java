package by.epam.baranovsky.banking.dao.impl;

import by.epam.baranovsky.banking.constant.DBMetadata;
import by.epam.baranovsky.banking.dao.UserDAO;
import by.epam.baranovsky.banking.dao.exception.DAOException;
import by.epam.baranovsky.banking.dao.query.Query;
import by.epam.baranovsky.banking.dao.query.QueryMaster;
import by.epam.baranovsky.banking.dao.query.impl.SqlQueryMaster;
import by.epam.baranovsky.banking.dao.rowmapper.RowMapper;
import by.epam.baranovsky.banking.dao.rowmapper.RowMapperFactory;
import by.epam.baranovsky.banking.entity.User;
import by.epam.baranovsky.banking.entity.criteria.Criteria;
import by.epam.baranovsky.banking.entity.criteria.EntityParameters;

import java.util.List;

/**
 * Implementation of UserDAO for use with MySQL DB.
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class SqlUserDAO implements UserDAO {


    /** Mapper to parse ResultSet objects into entities. */
    private static final RowMapper<User> mapper = RowMapperFactory.getUserRowMapper();

    /** Object that executes SQL queries. */
    private static final QueryMaster<User> queryMaster = new SqlQueryMaster<>(mapper);

    private static final String SQL_SELECT_ALL = String.format(
            "SELECT * FROM %s LEFT JOIN %s ON %s=%s",
            DBMetadata.USERS_TABLE, DBMetadata.USER_ROLES_TABLE,
            DBMetadata.USERS_ROLE_ID, DBMetadata.USER_ROLES_ID);

    private static final String SQL_FIND_BY_ID = String.format(
            "%s WHERE %s=?",
            SQL_SELECT_ALL, DBMetadata.USERS_ID);

    private static final String SQL_FIND_BY_EMAIL = String.format(
            "%s WHERE %s=?",
            SQL_SELECT_ALL, DBMetadata.USERS_EMAIL);

    private static final String SQL_INSERT = String.format(
            "INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES (DEFAULT,?,?,?,?,?,?,?,?,NOW(),NOW(),?)",
            DBMetadata.USERS_TABLE,
            DBMetadata.USERS_ID, DBMetadata.USERS_EMAIL,
            DBMetadata.USERS_PASSWORD, DBMetadata.USERS_SURNAME,
            DBMetadata.USERS_NAME, DBMetadata.USERS_PATRONYMIC,
            DBMetadata.USERS_PASS_SERIES, DBMetadata.USERS_PASS_NUMBER,
            DBMetadata.USERS_BIRTHDATE, DBMetadata.USERS_LAST_LOGIN,
            DBMetadata.USERS_DATE_CREATED, DBMetadata.USERS_ROLE_ID);


    private static final String SQL_UPDATE = String.format(
            "UPDATE %s SET %s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=? WHERE %s=?",
            DBMetadata.USERS_TABLE, DBMetadata.USERS_EMAIL,
            DBMetadata.USERS_PASSWORD, DBMetadata.USERS_SURNAME,
            DBMetadata.USERS_NAME, DBMetadata.USERS_PATRONYMIC,
            DBMetadata.USERS_PASS_SERIES, DBMetadata.USERS_PASS_NUMBER,
            DBMetadata.USERS_BIRTHDATE, DBMetadata.USERS_LAST_LOGIN,
            DBMetadata.USERS_ROLE_ID, DBMetadata.USERS_ID);


    private static final String SQL_DELETE = String.format(
            "DELETE FROM %s WHERE %s=? LIMIT 1",
            DBMetadata.USERS_TABLE, DBMetadata.USERS_ID);

    /**
     * {@inheritDoc}
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public User findByEmail(String userEmail) throws DAOException {
        return queryMaster.executeSingleEntityQuery(
                SQL_FIND_BY_EMAIL,
                userEmail);
    }

    /**
     * {@inheritDoc}
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public List<User> findByCriteria(Criteria<? extends EntityParameters.UserParams> criteria) throws DAOException {
        Query query = criteria.generateQuery(SQL_SELECT_ALL);
        return queryMaster.executeQuery(query.getSqlQueryString(), query.getParameters());
    }

    /**
     * {@inheritDoc}
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public User findEntityById(Integer id) throws DAOException {
        return queryMaster.executeSingleEntityQuery(
                SQL_FIND_BY_ID,
                id);
    }

    /**
     * {@inheritDoc}
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public List<User> findAll() throws DAOException {
        return queryMaster.executeQuery(SQL_SELECT_ALL);
    }

    /**
     * {@inheritDoc}
     * @return Number of rows affected in DB.
     * @throws DAOException if QueryMaster throws DAOException
     */
    @Override
    public Integer update(User user) throws DAOException {

        java.util.Date birthdate = user.getBirthDate();
        java.util.Date lastLogin = user.getLastLogin();

        return queryMaster.executeUpdate(
                SQL_UPDATE,
                user.getEmail(),
                user.getPassword(),
                user.getLastName(),
                user.getFirstName(),
                user.getPatronymic(),
                user.getPassportSeries(),
                user.getPassportNumber(),
                new java.sql.Date(birthdate.getTime()),
                new java.sql.Date(lastLogin.getTime()),
                user.getRoleId(),
                user.getId());
    }

    /**
     * {@inheritDoc}
     * <p>Password of passed user is not hashed or encoded in any way or manner.</p>
     * @return Generated key of inserted row.
     * @throws DAOException if DAOException is thrown by QueryMaster.
     */
    @Override
    public Integer create(User user) throws DAOException {
        return queryMaster.executeUpdate(
                SQL_INSERT,
                user.getEmail(),
                user.getPassword(),
                user.getLastName(),
                user.getFirstName(),
                user.getPatronymic(),
                user.getPassportSeries(),
                user.getPassportNumber(),
                new java.sql.Date(user.getBirthDate().getTime()),
                user.getRoleId());
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
    public Integer delete(User entity) throws DAOException {
        return delete(entity.getId());
    }

}
