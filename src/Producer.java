public class Producer {

    private final String name;
    private final String product;
    private int producedQuantity;
    private final Money money;

    private int productionCapacity;
    private final Money unitProductionCost;
    private boolean increaseProduction;

    private double monthlyRepaymentAmount;
    private double moneyOwing;

    {
        this.money = new Money(100);
        this.productionCapacity = 100;
        this.unitProductionCost = new Money(1.0);
        this.increaseProduction = false;
        this.moneyOwing = 0;
    }

    public Producer(String name, String product) {
        this.name = name;
        this.product = product;

    }

    public void produce() {
        int amountToProduce = determineProductionLimit();

        if (this.money.getValue() < this.unitProductionCost.getValue()) return;

        double costToProduce = amountToProduce * this.unitProductionCost.getValue();

        if (this.money.getValue() >= costToProduce) {
            this.money.decreaseValue(costToProduce);
            this.producedQuantity = this.producedQuantity + amountToProduce;
            System.out.println("Had enough money at a cost of" + costToProduce);
        } else {
            amountToProduce = (int) (this.money.getValue() / this.unitProductionCost.getValue());
            this.money.setValue(this.money.getValue() % this.unitProductionCost.getValue());

            System.out.println("Didn't have enough money, only used " + amountToProduce);

            this.producedQuantity += amountToProduce;
        }

        System.out.println("I produced " + amountToProduce);

    }

    public int determineProductionLimit() {
        return this.productionCapacity - this.producedQuantity;
    }

    public void sellProduct() {
        this.increaseProduction = false;
        int currentDemand = ProductExchange.determineCurrentDemand();
        int quantityToSell = determineQuantityToSell(currentDemand);

        double moneyReceived = ProductExchange.exchangeProduct(this.product, quantityToSell);
        this.money.increaseValue(moneyReceived);

        System.out.println("I was able to sell " + quantityToSell);
        System.out.println("At the amount of " + moneyReceived);

        if (this.increaseProduction) increaseProductionCapacity();
    }


    private int determineQuantityToSell(int productDemand) {
        if (this.producedQuantity >= productDemand) {
            this.producedQuantity -= productDemand;
            return productDemand;
        } else {
            this.increaseProduction = true;
            int quantityToSell = this.producedQuantity;
            this.producedQuantity = 0;
            return quantityToSell;
        }
    }

    private void increaseProductionCapacity() {

        double costToIncrease = determineProductionIncreaseCost();
        System.out.println("Had to increase production at a cost of " + costToIncrease);

        if (costToIncrease <= this.money.getValue()) {
            this.money.decreaseValue(costToIncrease);
            this.productionCapacity *= 1.2;
            this.unitProductionCost.decreaseValue(this.unitProductionCost.getValue() * 0.05);
        } else {
            double costDeficit = costToIncrease - this.money.getValue();
            if (withdrawFromBank(costDeficit)) increaseProductionCapacity();
            else if (takeABankLoan(costDeficit)) increaseProductionCapacity();
        }
    }


    private int determineProductionIncreaseCost() {
        return this.productionCapacity / 2;
    }


    private boolean withdrawFromBank(double amount) {

        double bankBalance = Bank.getUserDepositedBalance(this.name);
        double possibleAmount;

//        if (bankBalance == 0) return false;

        possibleAmount = Math.min(amount, bankBalance);

        this.money.increaseValue(possibleAmount);
        System.out.println("Had to withdraw from the bank. Was able to get " + possibleAmount);
        return Bank.withdrawMoney(this.name, possibleAmount);
    }

    private boolean takeABankLoan(double amount) {

        double maxLoanAmount = Bank.maxPossibleLoan(this);
        System.out.println("Needed a bank loan of " + maxLoanAmount);

        if (amount < maxLoanAmount) {
            double provisionalRepaymentAmount = Bank.borrowFromBank(this, amount, 12);

            if (provisionalRepaymentAmount == 0.0) return false;

            this.monthlyRepaymentAmount += provisionalRepaymentAmount;
            this.moneyOwing += Bank.getUserBorrowedAmount(this.name);
            this.money.increaseValue(amount);
            return true;
        }
        System.out.println("The loan didn't work out. Production capacity remained the same");
        return false;
    }

    public void makeLoanRepayment() {
        this.moneyOwing -= this.monthlyRepaymentAmount;
        this.money.decreaseValue(this.monthlyRepaymentAmount);
        Bank.receiveLoanRepayment(this.name, this.monthlyRepaymentAmount);
    }

    public String getName() {
        return this.name;
    }

    public String getProduct() {
        return this.product;
    }

    public Money getMoney() {
        return this.money;
    }

    public int getProductionCapacity() {
        return this.productionCapacity;
    }

    public double getMoneyOwing() {
        return this.moneyOwing;
    }


    @Override
    public String toString() {
        return "Producer{" +
                "name='" + name + '\'' +
                ", product='" + product + '\'' +
                ", producedQuantity=" + producedQuantity +
                ", money=" + money.getValue() +
                ", productionCapacity=" + productionCapacity +
                ", unitProductionCost=" + unitProductionCost.getValue() +
                ", monthlyRepaymentAmount=" + monthlyRepaymentAmount +
                ", moneyOwing=" + moneyOwing +
                '}';
    }
}
