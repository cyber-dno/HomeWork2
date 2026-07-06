package db;

import classifier.TransactionType;
import entity.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class DBManager {
    private static DBManager instance;
    private static final DBConnectionManager CONNECTION_MANAGER = DBConnectionManager.getInstance();

    public static synchronized DBManager getInstance() {
        if (instance == null)
            instance = new DBManager();
        return instance;
    }

    public void addUser(User user) {
        Connection connection = CONNECTION_MANAGER.get();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO banking.User (name, email, phone, id) VALUES (?, ?, ?, ?)"
        )) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPhoneNumber());
            preparedStatement.setString(4, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        Connection connection = CONNECTION_MANAGER.get();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT * FROM banking.User;"
             )) {
            while (resultSet.next()) {
                users.add(userBuild(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }

        return users;
    }

    public User getUser(String id) {
        Connection connection = CONNECTION_MANAGER.get();
        User user = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM banking.User WHERE id = ?"
        )) {
            preparedStatement.setString(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = userBuild(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }

        return user;
    }

    public void deleteUser(User user) {
        Connection connection = CONNECTION_MANAGER.get();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM banking.User WHERE id = ?"
        )) {
            preparedStatement.setString(1, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }
    }

    public void addBankAccount(BankAccount bankAccount) {
        Connection connection = CONNECTION_MANAGER.get();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO banking.BankAccount (user_id, number, balance) VALUES (?, ?, ?)"
        )) {
            preparedStatement.setString(1, bankAccount.getUser().getId());
            preparedStatement.setString(2, bankAccount.getNumber());
            preparedStatement.setBigDecimal(3, bankAccount.getBalance());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }
    }

    public BankAccount getBankAccount(String number) {
        Connection connection = CONNECTION_MANAGER.get();
        BankAccount bankAccount = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM banking.BankAccount WHERE number = ?"
        )) {
            preparedStatement.setString(1, number);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bankAccount = bankAccountBuild(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }

        return bankAccount;
    }

    public void updateBalance(String number, BigDecimal balance) {
        Connection connection = CONNECTION_MANAGER.get();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE banking.BankAccount SET balance = ? WHERE number = ?"
        )) {
            preparedStatement.setBigDecimal(1, balance);
            preparedStatement.setString(2, number);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }
    }

    public void addTransaction(Transaction transaction) {
        Connection connection = CONNECTION_MANAGER.get();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO banking.Transaction (user_id, amount, type, source, target) VALUES (?, ?, ?, ?, ?)"
        )) {
            preparedStatement.setString(1, transaction.getUser().getId());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setString(3, String.valueOf(transaction.getType()));
            preparedStatement.setString(4, getValueOrNull(transaction.getSource(), User::getId));
            preparedStatement.setString(5, getValueOrNull(transaction.getTarget(), User::getId));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }
    }

    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        Connection connection = CONNECTION_MANAGER.get();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT * FROM banking.Transaction;"
             )) {
            while (resultSet.next()) {
                transactions.add(transactionBuild(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }

        return transactions;
    }

    public List<Transaction> getTransactionsByBankAccount(BankAccount bankAccount) {
        List<Transaction> transactions = new ArrayList<>();

        Connection connection = CONNECTION_MANAGER.get();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM banking.transaction " +
                        "WHERE user_id = ( " +
                        "    SELECT u.id FROM banking.bankaccount acc " +
                        "    JOIN banking.user u on acc.user_id = u.id " +
                        "    WHERE acc.number = ?) "
        )) {
            preparedStatement.setString(1, bankAccount.getNumber());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(transactionBuild(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }

        return transactions;
    }

    public List<Transaction> getTransactionsByUser(User user) {
        List<Transaction> transactions = new ArrayList<>();

        Connection connection = CONNECTION_MANAGER.get();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM banking.transaction " +
                        "WHERE user_id = ? "
        )) {
            preparedStatement.setString(1, user.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(transactionBuild(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }

        return transactions;
    }

    public Transaction getTransaction(String id) {
        Connection connection = CONNECTION_MANAGER.get();
        Transaction transaction = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM banking.Transaction WHERE id = ?"
        )) {
            preparedStatement.setString(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    transaction = transactionBuild(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CONNECTION_MANAGER.disconnect(connection);
        }

        return transaction;
    }

    private Transaction transactionBuild(ResultSet resultSet) throws SQLException {
        return new TransactionBuilder().reset()
                .setId(String.valueOf(resultSet.getInt("id")))
                .setUser(getUser(resultSet.getString("user_id")))
                .setAmount(resultSet.getBigDecimal("amount"))
                .setType(TransactionType.valueOf(resultSet.getString("type")))
                .setDate(resultSet.getTimestamp("date").toLocalDateTime())
                .setSource(getUser(resultSet.getString("source")))
                .setTarget(getUser(resultSet.getString("target")))
                .getTransaction();
    }

    private BankAccount bankAccountBuild(ResultSet resultSet) throws SQLException {
        return new BankAccountBuilder().reset()
                .setNumber(resultSet.getString("number"))
                .setUser(getUser(resultSet.getString("user_id")))
                .setBalance(resultSet.getBigDecimal("balance"))
                .getBankAccount();
    }

    private User userBuild(ResultSet resultSet) throws SQLException {
        return new UserBuilder().reset()
                .setId(resultSet.getString("id"))
                .setName(resultSet.getString("name"))
                .setEmail(resultSet.getString("email"))
                .setPhoneNumber(resultSet.getString("phone"))
                .getUser();
    }

    private <T, R> R getValueOrNull(T object, Function<T, R> mapper) {
        return Optional.ofNullable(object)
                .map(mapper)
                .orElse(null);
    }
}
