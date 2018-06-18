import java.util.ArrayList;
import java.util.Scanner;

@SuppressWarnings("ALL")
public class init {
    static int close = 0;
    static int admin = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<user> u = new ArrayList<>();
        u.add(new user("admin", "admin", 0));
        u.add(new user("akash", "password", 0));
        u.add(new user("vignesh", "Admin@123", 1));
        for (user e : u) {
            e.encryptPassword();
        }
        ArrayList<food> f = new ArrayList<>();
        ArrayList<Integer> used_tokens = new ArrayList<>();
        openCanteen(f);
        while (true) {
            try {
                admin = 0;
                System.out.println("1 for login, 2 for signup, 3 to exit system: ");
                int first = Integer.parseInt(sc.nextLine());
                if (first == 3) {
                    break;
                }
                int user_index = -1;
                if (first == 1) {
                    System.out.println("Enter username and password: ");
                    String user = sc.nextLine();
                    String pass = sc.nextLine();
                    user_index = userLogin(u, user, pass);
                    if (user_index == 0) {
                        admin = 1;
                    }
                    if (user_index == 99) {
                        System.out.println("Authentication Failed");
                        continue;
                    }
                    if (admin == 1) {
                        while (true) {
                            System.out.println("CHOOSE: 1 for add, 2 for modify, 3 for del, 4 for add balance, 5 to close canteen, 6 to open canteen, 99 to exit admin box");
                            int ch = Integer.parseInt(sc.nextLine());
                            if (ch == 99) {
                                break;
                            }
                            switch (ch) {
                                case 1:
                                    admin_add(sc, f);
                                    break;
                                case 2:
                                    admin_modify(sc, f);
                                    break;
                                case 3:
                                    admin_delete(sc, f);
                                    break;
                                case 4:
                                    System.out.println("Enter user name and amount: ");
                                    String temp_user = sc.nextLine();
                                    int temp_amount = Integer.parseInt(sc.nextLine());
                                    for (user e : u) {
                                        if (e.username.compareTo(temp_user) == 0) {
                                            e.addBalance(temp_amount);
                                        }
                                    }
                                    break;
                                case 5:
                                    System.out.println("Canteen Closed :(");
                                    closeCanteen(u, f);
                                    break;
                                case 6:
                                    System.out.println("Canteen now open!!!");
                                    openCanteen(f);
                                    break;
                                default:
                                    System.out.println("Incorrect Input");
                            }
                        }
                    }
                } else if (first == 2) {
                    System.out.println("Enter new username and password");
                    String temp_user = sc.nextLine();
                    String temp_pass = sc.nextLine();
                    u.add(new user(temp_user, temp_pass, 0));
                    u.get(u.size() - 1).encryptPassword();
                    user_index = u.size() - 1;
                }
                System.out.println("Press 1 to view Balance: ");
                int press = Integer.parseInt(sc.nextLine());
                if (press == 1)
                    System.out.println("Balance: " + u.get(user_index).balance);
                System.out.println("Press 1 to order food: ");
                press = Integer.parseInt(sc.nextLine());
                if (press == 1) {
                    if (close == 0)
                        orderFood(sc, used_tokens, f, u, user_index);
                    else
                        System.out.println("Sorry! Canteen is closed right now.");
                }
            } catch (Exception e) {
                System.out.println("Some type mismatch error occured! Back to login!!!");
                continue;
            }
        }
        closeCanteen(u, f);
    }

    private static void orderFood(Scanner sc, ArrayList<Integer> used_tokens, ArrayList<food> f, ArrayList<user> u, int user_index) {
        ArrayList<String> orderedItem = new ArrayList<>();
        ArrayList<Integer> orderedCost = new ArrayList<>();
        System.out.println("Menu: ");
        for (food foo : f) {
            System.out.println("Item: " + foo.item + " Available: " + foo.available_count + " Price: " + foo.price_per_unit + " Max: " + foo.max_per_user);
        }
        int choice = 1;
        System.out.println("Enter your choice, Press 99 to exit at item choice! ");
        while (true) {
            System.out.println("Press number in sequence to order: ");
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 99)
                break;
            choice -= 1;
            System.out.println("Enter number: ");
            int num = Integer.parseInt(sc.nextLine());
            if (num > f.get(choice).available_count) {
                System.out.println("Sorry! Not sufficient available. Retry.");
                continue;
            }
            if (num > f.get(choice).max_per_user) {
                System.out.println("Sorry! Exceeded max per user.");
                continue;
            }
            int total_cost = f.get(choice).price_per_unit * num;
            if (total_cost > u.get(user_index).balance) {
                System.out.println("You dont have sufficient balance! Please re-evaluate order!");
                continue;
            }
            System.out.println("Item Added!!!");
            f.get(choice).sold += num;
            orderedItem.add(f.get(choice).item);
            orderedCost.add(total_cost);
            f.get(choice).available_count -= num;
            u.get(user_index).balance -= total_cost;
        }
        if (orderedCost.size() != 0) {
            u.get(user_index).used_last = true;
            int token = (int) (Math.random() * 200);
            while (used_tokens.contains(token)) {
                token = (int) (Math.random() * 200);
            }
            used_tokens.add(token);
            System.out.println("Token: " + token);
            int total_cost_final = 0;
            for (int i = 0; i < orderedCost.size(); i++) {
                System.out.println("ID: " + (i + 1) + " Item: " + orderedItem.get(i) + " Cost: " + orderedCost.get(i));
                total_cost_final += orderedCost.get(i);
            }
            System.out.println("Total Cost: " + total_cost_final);
        }
    }

    public static void admin_add(Scanner sc, ArrayList<food> f) {
        System.out.println("Enter item name, Available Count, Price per Unit, Max units per User(99 for Ultd)");
        String temp_item = sc.nextLine();
        int temp_avail_count = Integer.parseInt(sc.nextLine());
        int temp_ppu = Integer.parseInt(sc.nextLine());
        int temp_mpu = Integer.parseInt(sc.nextLine());
        f.add(new food(temp_item, temp_avail_count, temp_ppu, temp_mpu));
    }

    public static void admin_modify(Scanner sc, ArrayList<food> f) {
        System.out.println("Enter item name: ");
        String temp_item = sc.nextLine();
        int found = 0;
        for (food e : f) {
            if (temp_item.compareTo(e.item) == 0) {
                System.out.println("Enter new Available Count: ");
                e.available_count = Integer.parseInt(sc.nextLine());
                System.out.println("Enter new Price per Unit: ");
                e.price_per_unit = Integer.parseInt(sc.nextLine());
                System.out.println("Enter new Max units per User: ");
                e.max_per_user = Integer.parseInt(sc.nextLine());
                found = 1;
            }
        }
        if (found == 0)
            System.out.println("Item not in the menu :(");
    }

    public static void admin_delete(Scanner sc, ArrayList<food> f) {
        System.out.println("Enter item name: ");
        String temp_item = sc.nextLine();
        int temp_index = 0;
        for (food e : f) {
            if (temp_item.compareTo(e.item) == 0) {
                temp_index = f.indexOf(e);
            }
        }
        f.remove(temp_index);
    }

    public static int userLogin(ArrayList<user> u, String user, String pass) {
        String temp = "";
        for (int i = 0; i < pass.length(); i++) {
            char ch = pass.charAt(i);
            if (Character.isLetterOrDigit(ch))
                ch += 1;
            temp = ch + temp;
        }
        pass = temp;
        for (user e : u) {
            if (user.compareTo(e.username) == 0 && pass.compareTo(e.password) == 0) {
                return u.indexOf(e);
            }
        }
        return 99;
    }

    public static void openCanteen(ArrayList<food> f) {
        close = 0;
        f.clear();
        f.add(new food("Dosa", 5, 30, 99));
        f.add(new food("Idli", 15, 10, 99));
        f.add(new food("Masala Dosa", 3, 50, 1));
        f.add(new food("Vada", 10, 10, 2));
        f.add(new food("Pongal", 8, 25, 99));
        //f.add(new food("Puri", 7, 15, 99));
        //f.add(new food("Matar", 7, 20, 99));
    }

    public static void closeCanteen(ArrayList<user> u, ArrayList<food> f) {
        close = 1;
        for (user e : u) {
            e.day++;
            if (e.day % 3 == 0) {
                if (e.used_last) {
                    e.balance += 300;
                    e.used_last = false;
                }
            }
        }
        for (food e : f) {
            e.showOnClose();
        }

    }
}

@SuppressWarnings("ALL")
class user {
    String username;
    String password;
    int day;
    int shift_employee;
    int balance;
    boolean used_last;

    public user(String usr, String pass, int se) {
        username = usr;
        password = pass;
        day = 1;
        shift_employee = se;
        balance = 300;
        used_last = false;
    }

    public void addBalance(int val) {
        balance += val;
    }

    public void encryptPassword() {
        String temp = "";
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isLetterOrDigit(ch))
                ch += 1;
            temp = ch + temp;
        }
        password = temp;
    }

}

class food {
    String item;
    int sold;
    int available_count;
    int price_per_unit;
    int max_per_user;

    public food(String it, int avail_c, int ppu, int mpu) {
        item = it;
        sold = 0;
        available_count = avail_c;
        price_per_unit = ppu;
        max_per_user = mpu;
    }

    public void showOnClose() {
        System.out.println("Item: " + item);
        System.out.println("Sold: " + sold);
        System.out.println("Available Count: " + available_count);
    }
}
