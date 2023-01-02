import java.util.ArrayList;
import java.util.List;

public class Community {

    private static final List<Producer> members = new ArrayList<>();

    public static List<Producer> getMembers() {
        return members;
    }

    public static void main(String[] args) {

        members.add(new Producer("Sete", "Chicken"));
        members.add(new Producer("Vans", "Beef"));



        for (int i = 0; i < 5; i++) {

            for (Producer member : members) {
                System.out.println(member);
                member.produce();
                member.sellProduct();
                
                if(member.getMoneyOwing() > 0){
                    member.makeLoanRepayment();
                }
                System.out.println(member);
                System.out.println("-------------");
            }
            System.out.println("***********************");

        }

    }
}
