public class CoffeeShop {

    double order(DrinkType beverage, SizeType size) {

        PriceDB db = new PriceDB();
        double price = db.findPrice(size, beverage);

        System.out.println("You ordered a " + size + " " + beverage);
        System.out.printf("That will be $%5.2f\n", price);

        return price;
    }

}
