package by.epam.baranovsky.banking.controller.command.impl;

import by.epam.baranovsky.banking.constant.CommandName;
import by.epam.baranovsky.banking.constant.Message;
import by.epam.baranovsky.banking.controller.command.AbstractCommand;
import by.epam.baranovsky.banking.controller.constant.PageUrls;
import by.epam.baranovsky.banking.controller.constant.RequestAttributeNames;
import by.epam.baranovsky.banking.controller.constant.RequestParamName;
import by.epam.baranovsky.banking.controller.constant.SessionAttributeName;
import by.epam.baranovsky.banking.entity.User;
import by.epam.baranovsky.banking.service.exception.ServiceException;
import by.epam.baranovsky.banking.service.exception.ValidationException;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of Command
 * used for editing of user data.
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public class EditUserCommand extends AbstractCommand {

    private static final String REDIRECT_TO_HOME=String.format(
            "%s?%s=%s",
            RequestParamName.CONTROLLER,
            RequestParamName.COMMAND_NAME,
            CommandName.GOTO_MAIN);


    /**
     * {@inheritDoc}
     * <p>
     *     User can only edit his personal data.
     * </p>
     * <p>
     *     If editing was successful, redirects to home page.
     *     Otherwise, forwards back to edit data page.
     * </p>
     */
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object userData = request.getSession().getAttribute(RequestAttributeNames.USER_DATA);

        if(validatePassword(request)){
            try{
                userService.updateUser(getUpdatedUser(request));
                response.sendRedirect(REDIRECT_TO_HOME);
            } catch (ValidationException e){
                request.setAttribute(RequestAttributeNames.ERROR_MSG, e.getMessage());
                request.setAttribute(RequestAttributeNames.USER_DATA, userData);
                RequestDispatcher dispatcher = request.getRequestDispatcher(PageUrls.EDIT_USER_PAGE);
                dispatcher.forward(request,response);
            } catch (ServiceException e) {
                logger.error("User edit error: ", e);
                request.setAttribute(RequestAttributeNames.ERROR_MSG, Message.EDIT_USER_EXCEPTION);
                request.setAttribute(RequestAttributeNames.USER_DATA, userData);
                RequestDispatcher dispatcher = request.getRequestDispatcher(PageUrls.EDIT_USER_PAGE);
                dispatcher.forward(request,response);
            }
        }else{
            request.setAttribute(RequestAttributeNames.ERROR_MSG, Message.EDIT_USER_PASS_MISMATCH);
            request.setAttribute(RequestAttributeNames.USER_DATA, userData);
            RequestDispatcher dispatcher = request.getRequestDispatcher(PageUrls.EDIT_USER_PAGE);
            dispatcher.forward(request,response);
        }

    }

    private boolean validatePassword(HttpServletRequest request){
        String pass = request.getParameter(RequestParamName.PASSWORD);

        if(pass == null || pass.isBlank()){
            return true;
        }

        String passConfirm = request.getParameter(RequestParamName.CONF_PASSWORD);
        return pass.equals(passConfirm);
    }

    private User getUpdatedUser(HttpServletRequest request) throws ServiceException {

        User user = userService.getById((Integer) request.getSession().getAttribute(SessionAttributeName.USER_ID));

        String email = request.getParameter(RequestParamName.EMAIL);
        String password = request.getParameter(RequestParamName.PASSWORD);

        String firstName = request.getParameter(RequestParamName.NAME);
        String lastName = request.getParameter(RequestParamName.SURNAME);
        String patronymic = request.getParameter(RequestParamName.PATRONYMIC);

        String passportSeries = request.getParameter(RequestParamName.PASSPORT_SERIES);
        String passportNumber = request.getParameter(RequestParamName.PASSPORT_NUMBER);

        String birthdateStr = request.getParameter(RequestParamName.BIRTHDATE);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date birthdate = null;
        try {
            birthdate = format.parse(birthdateStr);
        } catch (ParseException e) {
            logger.error("Error parsing birth date ",e);
        }

        if(password != null && !password.isBlank()){
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        }

        user.setEmail(email);
        user.setPassportNumber(passportNumber);
        user.setPassportSeries(passportSeries);
        user.setPassportSeries(passportSeries);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPatronymic(patronymic);
        user.setBirthDate(birthdate);

        return user;
    }

}
