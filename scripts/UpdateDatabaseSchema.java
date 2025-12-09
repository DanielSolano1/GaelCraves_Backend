package com.gaelcraves.project3.GaelCravings_Backend.scripts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class UpdateDatabaseSchema {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require";
        String username = "postgres.cfmztcfqrvinpwxqqwxa";
        String password = "chabyss*";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {

            System.out.println("✅ Connected to database!");
            System.out.println("🔧 Adding nutritional columns...");

            // Add nutritional columns
            stmt.execute("ALTER TABLE food_item ADD COLUMN IF NOT EXISTS protein INTEGER");
            stmt.execute("ALTER TABLE food_item ADD COLUMN IF NOT EXISTS carbohydrates INTEGER");
            stmt.execute("ALTER TABLE food_item ADD COLUMN IF NOT EXISTS fat INTEGER");
            
            // Add descriptive columns
            stmt.execute("ALTER TABLE food_item ADD COLUMN IF NOT EXISTS description VARCHAR(1000)");
            stmt.execute("ALTER TABLE food_item ADD COLUMN IF NOT EXISTS image_url VARCHAR(500)");
            stmt.execute("ALTER TABLE food_item ADD COLUMN IF NOT EXISTS category VARCHAR(100)");
            stmt.execute("ALTER TABLE food_item ADD COLUMN IF NOT EXISTS is_available BOOLEAN DEFAULT TRUE");

            // Update existing rows
            stmt.execute("UPDATE food_item SET is_available = TRUE WHERE is_available IS NULL");

            System.out.println("✅ Schema updated successfully!");
            System.out.println("");
            System.out.println("📊 Now populating complete menu...");

            // Get menu_id
            var rs = stmt.executeQuery("SELECT menu_id FROM menu WHERE menu_name = 'GaelCraves Menu'");
            int menuId = 0;
            if (rs.next()) {
                menuId = rs.getInt("menu_id");
            } else {
                System.out.println("❌ GaelCraves Menu not found!");
                return;
            }

            // Delete existing items
            stmt.execute("DELETE FROM food_item WHERE menu_id = " + menuId);
            System.out.println("🗑️  Cleared existing food items");

            // Insert new items with nutritional info
            String[] items = {
                // Burgers & Sandwiches
                "('Chicken Sandwich + Fries', 'Crispy chicken sandwich served with golden fries', 11.99, 850, 35, 95, 38, 'Burgers & Sandwiches', TRUE, " + menuId + ")",
                "('Cheeseburger + Fries', 'Classic cheeseburger with melted cheese served with golden fries', 11.99, 920, 38, 88, 45, 'Burgers & Sandwiches', TRUE, " + menuId + ")",
                "('Double Cheeseburger + Fries', 'Two beef patties with double cheese served with golden fries', 13.99, 1150, 52, 90, 62, 'Burgers & Sandwiches', TRUE, " + menuId + ")",
                "('Hamburger + Fries', 'Classic hamburger served with golden fries', 10.99, 850, 32, 86, 40, 'Burgers & Sandwiches', TRUE, " + menuId + ")",
                
                // Wings
                "('5 Wings + Fries', 'Five crispy chicken wings served with golden fries', 10.99, 720, 42, 65, 32, 'Wings', TRUE, " + menuId + ")",
                "('10 Wings + Fries', 'Ten crispy chicken wings served with golden fries', 16.99, 1180, 68, 72, 58, 'Wings', TRUE, " + menuId + ")",
                
                // Sides
                "('Fries', 'Golden crispy french fries', 3.99, 365, 4, 48, 17, 'Sides', TRUE, " + menuId + ")",
                "('Onion Rings', 'Crispy battered onion rings', 4.99, 410, 5, 52, 20, 'Sides', TRUE, " + menuId + ")",
                "('Mozzarella Sticks', 'Breaded mozzarella cheese sticks', 5.99, 470, 18, 38, 26, 'Sides', TRUE, " + menuId + ")",
                "('Mac n Cheese Bites', 'Crispy fried mac and cheese bites', 5.99, 450, 14, 42, 24, 'Sides', TRUE, " + menuId + ")",
                "('Chicken Tenders', 'Crispy breaded chicken tenders', 7.99, 520, 38, 35, 24, 'Sides', TRUE, " + menuId + ")",
                
                // Beverages
                "('Soda', 'Fountain drink - Coke, Sprite, or other flavors', 1.99, 150, 0, 39, 0, 'Beverages', TRUE, " + menuId + ")",
                "('Water', 'Bottled water', 1.49, 0, 0, 0, 0, 'Beverages', TRUE, " + menuId + ")",
                "('Sweet Tea', 'Southern style sweet iced tea', 1.99, 120, 0, 32, 0, 'Beverages', TRUE, " + menuId + ")"
            };

            String insertSQL = "INSERT INTO food_item (name, description, price, calories, protein, carbohydrates, fat, category, is_available, menu_id) VALUES ";
            
            for (int i = 0; i < items.length; i++) {
                stmt.execute(insertSQL + items[i]);
                System.out.println("  ✓ Added: " + items[i].substring(2, items[i].indexOf("',") - 1));
            }

            System.out.println("");
            System.out.println("========================================");
            System.out.println("✅ Database updated successfully!");
            System.out.println("========================================");
            System.out.println("📊 Added " + items.length + " food items");
            System.out.println("📁 Categories: Burgers & Sandwiches, Wings, Sides, Beverages");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
