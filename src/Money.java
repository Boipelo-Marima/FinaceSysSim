import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {

    private BigDecimal value;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(double value) {
        BigDecimal val = BigDecimal.valueOf(value);
        val = roundToTwoDecimals(val);
        this.value = val;
    }
    public Money(BigDecimal value) {
        value = roundToTwoDecimals(value);
        this.value = value;
    }

    public void increaseValue(double addValue) {
        BigDecimal val = BigDecimal.valueOf(addValue);
        val = roundToTwoDecimals(val);
        this.value = value.add(val);
    }

    public void decreaseValue(double minusValue){
        BigDecimal val = BigDecimal.valueOf(minusValue);
        val = roundToTwoDecimals(val);
        this.value = value.subtract(val);
    }

    public double getValue() {
        return value.doubleValue();
    }

    public void setValue(double value) { this.value = new Money(value).value; }

    private BigDecimal roundToTwoDecimals(BigDecimal val) {
        return val.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

//    public static Money roundToTwoDecimals(Money val) {
//        return new Money((val.getValue()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//    }
}
