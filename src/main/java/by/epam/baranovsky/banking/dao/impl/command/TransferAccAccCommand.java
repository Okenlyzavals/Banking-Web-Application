package by.epam.baranovsky.banking.dao.impl.command;

import by.epam.baranovsky.banking.constant.DBMetadata;
import by.epam.baranovsky.banking.dao.exception.DAOException;
import by.epam.baranovsky.banking.dao.query.Query;
import by.epam.baranovsky.banking.entity.Operation;

import java.util.ArrayList;
import java.util.List;

public class TransferAccAccCommand extends AbstractTransferCommand {

    @Override
    public int create(Operation operation) throws DAOException {
        return master.executeTransaction(prepareQuery(operation));
    }

    @Override
    protected List<Query> prepareQuery(Operation operation) throws DAOException {
        double comm = (operation.getCommission() == null) ? 0d : operation.getCommission();
        OperationCommand.testNonNull(operation.getValue(),
                operation.getTargetAccountId(), operation.getAccountId());
        List<Query> queries = new ArrayList<>();
        queries.add(getBasicInsert(operation));
        queries.add(new Query(
                SQL_UPDATE_ACC,
                operation.getValue(),
                operation.getTargetAccountId()));
        queries.add(new Query(
                SQL_UPDATE_ACC,
                -(operation.getValue()+comm),
                operation.getAccountId()));
        queries.add(new Query(
                SQL_UPDATE_ACC,
                comm,
                DBMetadata.BANK_ACCOUNT_ID));
        return queries;
    }
}
