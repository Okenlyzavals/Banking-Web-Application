package by.epam.baranovsky.banking.service.impl;

import by.epam.baranovsky.banking.dao.UserDAO;
import by.epam.baranovsky.banking.dao.exception.DAOException;
import by.epam.baranovsky.banking.dao.factory.impl.SqlDAOFactory;
import by.epam.baranovsky.banking.entity.User;
import by.epam.baranovsky.banking.entity.criteria.Criteria;
import by.epam.baranovsky.banking.entity.criteria.EntityParameters;
import by.epam.baranovsky.banking.service.exception.ServiceException;
import by.epam.baranovsky.banking.service.exception.ValidationException;
import by.epam.baranovsky.banking.service.validator.UserValidator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserServiceImpl implements by.epam.baranovsky.banking.service.UserService {

    private static final Integer DEFAULT_ROLE = 2;
    private static volatile UserServiceImpl instance = null;
    private final UserValidator validator = new UserValidator();
    private final UserDAO userDAO = SqlDAOFactory.getInstance().getUserDAO();

    private UserServiceImpl() {}

    public static UserServiceImpl getInstance() {
        if (instance == null) {
            synchronized (UserServiceImpl.class) {
                if (instance == null) {
                    instance = new UserServiceImpl();
                }
            }
        }
        return instance;
    }


    @Override
    public User loginUser(String email, String password) throws ServiceException {
        User user;

        try {
            user = userDAO.findByEmail(email);
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                throw new ValidationException("Wrong email or password.");
            }
            user.setLastLogin(new Date());
            updateUser(user);
            user = userDAO.findEntityById(user.getId());
            user.setPassword(null);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return user;
    }

    @Override
    public User registerUser(String userEmail, String userPassword, String surname,
                             String name, String patronymic, String passportSeries,
                             String passportNumber, Date birthDate) throws ServiceException {

        User user = new User();
        user.setEmail(userEmail);
        user.setPassword(userPassword);
        user.setFirstName(name);
        user.setLastName(surname);
        user.setPatronymic(patronymic);
        user.setPassportNumber(passportNumber);
        user.setPassportSeries(passportSeries);
        user.setBirthDate(birthDate);
        user.setRoleId(DEFAULT_ROLE);

        try{
            if (!validator.validate(user)) {
                throw new ValidationException("Invalid input!");
            }
            if(userDAO.findByEmail(user.getEmail()) != null){
                throw new ValidationException("User already exists!");
            }
            user.setPassword(BCrypt.hashpw(userPassword, BCrypt.gensalt()));
            user = userDAO.findEntityById(userDAO.create(user));
            user.setPassword(null);
        } catch (DAOException e){
            throw new ServiceException(e);
        }

        return user;
    }

    @Override
    public List<User> getAll() throws ServiceException {

        List<User> users = new ArrayList<>();

        try {
            users = userDAO.findAll();
            for(User user : users){
                user.setPassword(null);
            }
        } catch (DAOException e) {
            throw new ServiceException(e);
        }

        return users;
    }

    @Override
    public User getById(Integer id) throws ServiceException {

        User user;

        try{
            user = userDAO.findEntityById(id);
            user.setPassword(null);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }

        return user;
    }

    @Override
    public List<User> getByCriteria(Criteria<EntityParameters.UserParams> criteria) throws ServiceException {
        List<User> users = new ArrayList<>();
        try {
            users = userDAO.findByCriteria(criteria);
            for(User user : users){
                user.setPassword(null);
            }
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return users;
    }

    @Override
    public Integer updateUser(User user) throws ServiceException {

        Integer result = 0;

        try{
            if(user.getPassword() == null){
                user.setPassword(userDAO.findEntityById(user.getId()).getPassword());
            }
            if(!validator.validate(user)){
                throw new ValidationException();
            }
            if(user.getId() == null || userDAO.findEntityById(user.getId())== null){
                throw new ValidationException("No user with such ID");
            }
            result = userDAO.update(user);
            user.setPassword(null);

        } catch (DAOException e) {
            throw new ServiceException(e);
        }

        return result;
    }

    @Override
    public Integer deleteUser(User user) throws ServiceException {
        Integer result = 0;

        try{
            result = userDAO.delete(user);

        } catch (DAOException e) {
            throw new ServiceException(e);
        }

        return result;
    }
}