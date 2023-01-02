import java.util.HashMap;
import java.util.Random;

public class ProductExchange {

    private static final HashMap<String, Integer> receivedProducts = new HashMap<>();

    private static int averageProductionCapacity;

    private static final double TRADE_VALUE = 1.5;

    public static double exchangeProduct(String product, int quantity) {
        if (receivedProducts.containsKey(product)) {
            receivedProducts.replace(product, (receivedProducts.get(product) + quantity));
        } else {
            receivedProducts.put(product, quantity);
        }
        return TRADE_VALUE * quantity;
    }

    public static int determineCurrentDemand() {

        trackAverageProduction();

        //minimum demand can be 15% less than averageProductionCapacity
        int min = (int) (averageProductionCapacity - (averageProductionCapacity * 0.15));
        //maximum demand can be 30% above averageProductionCapacity
        int max = (int) (averageProductionCapacity * 1.3);

        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    private static void trackAverageProduction() {
        int productionSum = 0;

        for (Producer member : Community.getMembers()) {
            productionSum += member.getProductionCapacity();
        }

        averageProductionCapacity = productionSum / Community.getMembers().size();
    }

    public static void getReceivedProducts() {
        for (String key : receivedProducts.keySet()) {
            System.out.print(key + " ");
            System.out.println(receivedProducts.get(key));
        }
    }
}
