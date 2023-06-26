package org.example;

import java.time.LocalDate;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        //TODO Create User class with method createUser
        // User class fields: name, age;
        // Notice that we can only create user with createUser method without using constructor or builder
        User user1 = User.createUser("Alice", 32);
        User user2 = User.createUser("Bob", 19);
        User user3 = User.createUser("Charlie", 20);
        User user4 = User.createUser("John", 27);

        //TODO Create factory that can create a product for a specific type: Real or Virtual
        // Product class fields: name, price
        // Product Real class additional fields: size, weight
        // Product Virtual class additional fields: code, expiration date
        Product realProduct1 = ProductFactory.createRealProduct("Product A", 20.50, 10, 25);
        Product realProduct2 = ProductFactory.createRealProduct("Product B", 50, 6, 17);

        Product virtualProduct1 = ProductFactory.createVirtualProduct("Product C", 100, "xxx", LocalDate.of(2023, 5, 12));
        Product virtualProduct2 = ProductFactory.createVirtualProduct("Product D", 81.25, "yyy", LocalDate.of(2024, 6, 20));

        //TODO Create Order class with method createOrder
        // Order class fields: User, List<Price>
        // Notice that we can only create order with createOrder method without using constructor or builder
        List<Order> orders = new ArrayList<>() {{
            add(Order.createOrder(user1, List.of(realProduct1, virtualProduct1, virtualProduct2)));
            add(Order.createOrder(user2, List.of(realProduct1, realProduct2)));
            add(Order.createOrder(user3, List.of(realProduct1, virtualProduct2)));
            add(Order.createOrder(user4, List.of(virtualProduct1, virtualProduct2, realProduct1, realProduct2)));
        }};

        //TODO 1). Create singleton class which will check the code is used already or not
        // Singleton class should have the possibility to mark code as used and check if code used
        VirtualProductCodeManager singletonClass = VirtualProductCodeManager.getInstance();
        singletonClass.useCode("xxx");
        boolean isCodeUsed = singletonClass.isCodeUsed("xxx");
        System.out.println("1. Create singleton class VirtualProductCodeManager");
        System.out.println("Is code used: " + isCodeUsed + "\n");

        //TODO 2). Create a functionality to get the most expensive ordered product
        Product mostExpensive = getMostExpensiveProduct(orders);
        System.out.println("2. Most expensive product: " + mostExpensive + "\n");

        //TODO 3). Create a functionality to get the most popular product(product bought by most users) among users
        Product mostPopular = getMostPopularProduct(orders);
        System.out.println("3. Most popular product: " + mostPopular + "\n");

        //TODO 4). Create a functionality to get average age of users who bought realProduct2
        double averageAge = calculateAverageAge(realProduct2, orders);
        System.out.println("4. Average age is: " + averageAge + "\n");

        //TODO 5). Create a functionality to return map with products as keys and a list of users
        // who ordered each product as values
        Map<Product, List<User>> productUserMap = getProductUserMap(orders);
        System.out.println("5. Map with products as keys and list of users as value \n");
        productUserMap.forEach((key, value) -> System.out.println("key: " + key + " " + "value: " + value + "\n"));

        //TODO 6). Create a functionality to sort/group entities:
        // a) Sort Products by price
        // b) Sort Orders by user age in descending order
        List<Product> productsByPrice = sortProductsByPrice(List.of(realProduct1, realProduct2, virtualProduct1, virtualProduct2));
        System.out.println("6. a) List of products sorted by price: " + productsByPrice + "\n");
        List<Order> ordersByUserAgeDesc = sortOrdersByUserAgeDesc(orders);
        System.out.println("6. b) List of orders sorted by user agge in descending order: " + ordersByUserAgeDesc + "\n");

        //TODO 7). Calculate the total weight of each order
        Map<Order, Integer> result = calculateWeightOfEachOrder(orders);
        System.out.println("7. Calculate the total weight of each order \n");
        result.forEach((key, value) -> System.out.println("order: " + key + " " + "total weight: " + value + "\n"));
    }

    private static Product getMostExpensiveProduct(List<Order> orders) {
        double maxPrice = 0;
        Product mostExpensiveProduct = null;
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                if (product.getPrice() > maxPrice) {
                    maxPrice = product.getPrice();
                    mostExpensiveProduct = product;
                }
            }
        }
        return mostExpensiveProduct;
    }

    private static Product getMostPopularProduct(List<Order> orders) {
        Map<Product, Integer> productCountMap = new HashMap<>();
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                productCountMap.put(product, productCountMap.getOrDefault(product, 0) + 1);
            }
        }

        int maxCount = 0;
        Product mostPopularProduct = null;
        for (Map.Entry<Product, Integer> entry : productCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostPopularProduct = entry.getKey();
            }
        }
        return mostPopularProduct;
    }

    private static double calculateAverageAge(Product product, List<Order> orders) {
        int totalAge = 0;
        int userCount = 0;
        for (Order order : orders) {
            if (order.getProducts().contains(product)) {
                User user = order.getUser();
                totalAge += user.getAge();
                userCount++;
            }
        }
        if (userCount > 0) {
            return (double) totalAge / userCount;
        } else {
            return 0;
        }
    }

    private static Map<Product, List<User>> getProductUserMap(List<Order> orders) {
        Map<Product, List<User>> productUserMap = new HashMap<>();
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                productUserMap.putIfAbsent(product, new ArrayList<>());
                productUserMap.get(product).add(order.getUser());
            }
        }
        return productUserMap;
    }

    private static List<Product> sortProductsByPrice(List<Product> products) {
        List<Product> sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(Comparator.comparingDouble(Product::getPrice));
        return sortedProducts;
    }

    private static List<Order> sortOrdersByUserAgeDesc(List<Order> orders) {
        List<Order> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort((o1, o2) -> Integer.compare(o2.getUser().getAge(), o1.getUser().getAge()));
        return sortedOrders;
    }


    private static Map<Order, Integer> calculateWeightOfEachOrder(List<Order> orders) {
        Map<Order, Integer> orderWeightMap = new HashMap<>();
        for (Order order : orders) {
            int totalWeight = 0;
            for (Product product : order.getProducts()) {
                if (product instanceof RealProduct) {
                    RealProduct realProduct = (RealProduct) product;
                    totalWeight += realProduct.getWeight();
                }
            }
            orderWeightMap.put(order, totalWeight);
        }
        return orderWeightMap;
    }
}

class User {
    private String name;
    private int age;

    private User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static User createUser(String name, int age) {
        return new User(name, age);
    }

    public int getAge() {
        return age;
    }
}

abstract class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}

class RealProduct extends Product {
    private int size;
    private int weight;

    public RealProduct(String name, double price, int size, int weight) {
        super(name, price);
        this.size = size;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}

class VirtualProduct extends Product {
    private String code;
    private LocalDate expirationDate;

    public VirtualProduct(String name, double price, String code, LocalDate expirationDate) {
        super(name, price);
        this.code = code;
        this.expirationDate = expirationDate;
    }
}

class ProductFactory {
    public static RealProduct createRealProduct(String name, double price, int size, int weight) {
        return new RealProduct(name, price, size, weight);
    }

    public static VirtualProduct createVirtualProduct(String name, double price, String code, LocalDate expirationDate) {
        return new VirtualProduct(name, price, code, expirationDate);
    }
}

class VirtualProductCodeManager {
    private static VirtualProductCodeManager instance;
    private Set<String> usedCodes;

    private VirtualProductCodeManager() {
        usedCodes = new HashSet<>();
    }

    public static VirtualProductCodeManager getInstance() {
        if (instance == null) {
            instance = new VirtualProductCodeManager();
        }
        return instance;
    }

    public void useCode(String code) {
        usedCodes.add(code);
    }

    public boolean isCodeUsed(String code) {
        return usedCodes.contains(code);
    }
}

class Order {
    private User user;
    private List<Product> products;

    private Order(User user, List<Product> products) {
        this.user = user;
        this.products = products;
    }

    public static Order createOrder(User user, List<Product> products) {
        return new Order(user, products);
    }

    public User getUser() {
        return user;
    }

    public List<Product> getProducts() {
        return products;
    }
}