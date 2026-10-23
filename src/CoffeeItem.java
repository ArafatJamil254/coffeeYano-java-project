public class CoffeeItem {
    private int id;
    private String name;
    private String category;
    private int price;
    private boolean available;

    public CoffeeItem(int id, String name, String category, int price, boolean available) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getPrice() { return price; }
    public boolean isAvailable() { return available; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(int price) { this.price = price; }
    public void setAvailable(boolean available) { this.available = available; }
}
