import java.util.HashMap;
import java.util.Locale;

public class Bank {

    private static final HashMap<String, Money> depositors = new HashMap<>();
    private static final HashMap<String, Money> borrowers = new HashMap<>();

    private static final double FLP = 0.25; //Fractional Lending Percentage
    private static final double IR = 0.10; //InterestRate

    private static final Money overallBankBalance = new Money(10000.00); //all the money the bank including borrowed out money
    private static final Money currentBankBalance = new Money(0.0); // var:overallBankBalance minus borrowed money

    private enum TransactionTypes {
        WITHDRAWAL,
        DEPOSIT,
        LOAN,
        REPAYMENT
    }


    public static void depositMoney(String name, double amount) {
        name = name.toLowerCase(Locale.ROOT);
        if (Bank.depositors.containsKey(name)) {
            Bank.depositors.get(name).increaseValue(amount);
        } else {
            Bank.depositors.put(name, new Money(amount));
        }
        triggerBalanceAdjustment(TransactionTypes.DEPOSIT, amount);
    }

    public static boolean withdrawMoney(String name, double amount) {
        if (depositors.containsKey(name)) {
            if (hasEnoughMoney(depositors.get(name), amount)) {
                depositors.get(name).decreaseValue(amount);
                triggerBalanceAdjustment(TransactionTypes.WITHDRAWAL, amount);
                return true;
            }
        }
        return false;
    }

    private static boolean hasEnoughMoney(Money userMoney, double value) {
        return userMoney.getValue() >= value;
    }

    /**
     * @return returns the repayment amount per period(event cycle) over the term
     */
    public static double borrowFromBank(Producer producer, double amount, int term) {
        Money immediateFLPValue = calculateImmediateFLPValue();

        if ((Bank.currentBankBalance.getValue() - amount) > immediateFLPValue.getValue()) {
            if ((producer.getMoney().getValue() * 0.5) >= amount) {
                Bank.borrowers.put(producer.getName(), new Money(amount));
                triggerBalanceAdjustment(TransactionTypes.LOAN, amount);
                return calculateRepaymentAmount(amount, term);
            }
        }
        return 0.0; //Means you can't borrow
    }

    public static double maxPossibleLoan(Producer producer) {
        return producer.getMoney().getValue() * 0.5;
    }

    private static double calculateRepaymentAmount(double amount, int term) {
        return (amount * IR) / term;
    }

    private static Money calculateImmediateFLPValue() {
        return new Money(Bank.overallBankBalance.getValue() * FLP);
    }

    private static double calculateBankCurrentBalance() {
        return overallDepositedAmount() - overallBorrowedAmount();
    }

    private static double overallDepositedAmount() {
        double overallDepositedAmount = 0;
        for (Money depositValue : Bank.depositors.values()) {
            overallDepositedAmount += depositValue.getValue();
        }
        return overallDepositedAmount;
    }

    private static double overallBorrowedAmount() {

        double borrowedAmount = 0;

        for (Money borrowedValue : Bank.borrowers.values()) {
            borrowedAmount += borrowedValue.getValue();
        }
        return borrowedAmount;
    }

    public static void receiveLoanRepayment(String name, double amount) {
        Bank.borrowers.replace(name, new Money(Bank.borrowers.get(name).getValue() - amount));
        triggerBalanceAdjustment(TransactionTypes.REPAYMENT, amount);
    }

    private static void triggerBalanceAdjustment(TransactionTypes adjustmentType, double amount) {

        switch (adjustmentType) {
            case DEPOSIT:
                Bank.overallBankBalance.increaseValue(amount);
                Bank.currentBankBalance.increaseValue(amount);
                break;
            case WITHDRAWAL:
                Bank.overallBankBalance.decreaseValue(amount);
                Bank.currentBankBalance.decreaseValue(amount);
                break;
            case LOAN:
                Bank.currentBankBalance.decreaseValue(amount);
                break;
            case REPAYMENT:
                Bank.currentBankBalance.increaseValue(amount);
                break;
        }
    }

    public static double getUserDepositedBalance(String name) {
        if (depositors.containsKey(name))
            return depositors.get(name).getValue();
        else return 0.0;
    }

    public static double getUserBorrowedAmount(String name) {
        if (borrowers.containsKey(name))
            return borrowers.get(name).getValue();
        else return 0.0;
    }

}
