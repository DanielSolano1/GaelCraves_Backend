package com.gaelcraves.project3.GaelCravings_Backend.scripts;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Script to populate the database with GaelCraves menu items
 * Run this to add all menu items from the actual menu
 */
public class AddGaelCravesMenu {
    
    public static void main(String[] args) {
        // Database connection details from environment or defaults
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASS");
        
        if (dbUrl == null || dbUser == null || dbPass == null) {
            System.out.println("❌ Database credentials not found in environment variables");
            System.out.println("Set DB_URL, DB_USER, and DB_PASS environment variables");
            System.out.println("\nOr run with arguments:");
            System.out.println("java AddGaelCravesMenu <db_url> <db_user> <db_pass>");
            
            if (args.length >= 3) {
                dbUrl = args[0];
                dbUser = args[1];
                dbPass = args[2];
            } else {
                return;
            }
        }
        
        System.out.println("🔗 Connecting to database...");
        System.out.println("DB URL: " + dbUrl);
        System.out.println("DB User: " + dbUser);
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            System.out.println("✅ Connected successfully!");
            
            // Read SQL file
            System.out.println("\n📖 Reading SQL script...");
            InputStream inputStream = AddGaelCravesMenu.class
                .getClassLoader()
                .getResourceAsStream("scripts/add_gaelcraves_menu.sql");
            
            if (inputStream == null) {
                System.out.println("❌ Could not find add_gaelcraves_menu.sql");
                return;
            }
            
            String sql = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
            
            System.out.println("✅ SQL script loaded");
            
            // Execute SQL
            System.out.println("\n🚀 Executing SQL script...");
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            
            System.out.println("✅ Menu items added successfully!");
            
            // Verify
            System.out.println("\n📊 Verifying food items...");
            var rs = stmt.executeQuery(
                "SELECT name, category, price, protein, calories FROM food_item ORDER BY food_item_id"
            );
            
            System.out.println("\n=== FOOD ITEMS IN DATABASE ===");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.printf("%d. %s | Category: %s | $%.2f | %dg protein | %d cal%n",
                    count,
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("protein"),
                    rs.getInt("calories")
                );
            }
            
            System.out.println("\n✅ Total items: " + count);
            System.out.println("\n🎉 Database populated successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
