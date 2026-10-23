import java.util.ArrayList;

public class CoffeeOrder {
    public static class Item {
        private String name;
        private int price;
        private int quantity;

        public Item(String name, int price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getName() { return name; }
        public int getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public int getSubtotal() { return price * quantity; }
    }

    private int id;
    private String date;
    private String customer;
    private String paymentMethod;
    private ArrayList<Item> items;

    public CoffeeOrder(int id, String date, String customer, String paymentMethod, ArrayList<Item> items) {
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.items = new ArrayList<>(items);
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getCustomer() { return customer; }
    public String getPaymentMethod() { return paymentMethod; }
    public ArrayList<Item> getItems() { return new ArrayList<>(items); }

    public int getTotal() {
        int total = 0;
        for (Item item : items) total += item.getSubtotal();
        return total;
    }

    public String getItemSummary() {
        int count = 0;
        for (Item item : items) count += item.getQuantity();
        return count + (count == 1 ? " drink" : " drinks");
    }

    public String getReceipt() {
        StringBuilder text = new StringBuilder();
        text.append("                COFFEEYANO\n");
        text.append("         YOUR LITTLE COFFEE CORNER\n");
        text.append("----------------------------------------\n");
        text.append("Order #").append(id).append("\n");
        text.append("Date: ").append(date).append("\n");
        text.append("Customer: ").append(customer).append("\n");
        text.append("Checkout: ").append(paymentMethod).append(" (demo)\n");
        text.append("----------------------------------------\n");
        for (Item item : items) {
            text.append(item.getQuantity()).append(" x ").append(item.getName());
            text.append("\n    Tk ").append(item.getPrice()).append(" each  =  Tk ");
            text.append(item.getSubtotal()).append("\n");
        }
        text.append("----------------------------------------\n");
        text.append("TOTAL                         Tk ").append(getTotal()).append("\n\n");
        text.append("Demo order. No real payment was made.\n");
        text.append("Thank you for stopping by!\n");
        return text.toString();
    }
}
