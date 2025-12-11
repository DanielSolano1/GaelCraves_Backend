import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CheckHerokuDatabase {
    
    private static final String DB_URL = "jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String DB_USER = "postgres.cfmztcfqrvinpwxqqwxa";
    private static final String DB_PASS = "chabyss*";
    
    public static void main(String[] args) {
        System.out.println("🔍 Checking Heroku Database Tables...");
        System.out.println("======================================\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("✅ Connected to database successfully!\n");
            
            // Check all tables
            checkAllTables(conn);
            
            // Check roles
            checkRoles(conn);
            
            // Check users
            checkUsers(conn);
            
            // Check user roles
            checkUserRoles(conn);
            
            // Check menus
            checkMenus(conn);
            
            // Check food items
            checkFoodItems(conn);
            
            // Check orders
            checkOrders(conn);
            
            System.out.println("\n✅ Database check complete!");
            
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void checkAllTables(Connection conn) throws SQLException {
        System.out.println("📋 ALL TABLES:");
        System.out.println("-------------");
        
        String query = "SELECT table_name FROM information_schema.tables " +
                      "WHERE table_schema = 'public' ORDER BY table_name";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("  " + count + ". " + rs.getString("table_name"));
            }
            System.out.println("  Total tables: " + count + "\n");
        }
    }
    
    private static void checkRoles(Connection conn) throws SQLException {
        System.out.println("🔐 ROLES TABLE:");
        System.out.println("-------------");
        
        String query = "SELECT * FROM roles ORDER BY role_id";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("  ID: " + rs.getInt("role_id") + 
                                 ", Name: " + rs.getString("role_name"));
            }
            System.out.println("  Total roles: " + count + "\n");
        } catch (SQLException e) {
            System.out.println("  ⚠️  Table might not exist or no data: " + e.getMessage() + "\n");
        }
    }
    
    private static void checkUsers(Connection conn) throws SQLException {
        System.out.println("👥 USERS TABLE:");
        System.out.println("-------------");
        
        String query = "SELECT user_id, email, first_name, last_name, created_at " +
                      "FROM users ORDER BY created_at DESC LIMIT 10";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("  " + count + ". " + rs.getString("email") + 
                                 " (" + rs.getString("first_name") + " " + 
                                 rs.getString("last_name") + ")");
            }
            
            // Get total count
            String countQuery = "SELECT COUNT(*) as total FROM users";
            try (Statement countStmt = conn.createStatement();
                 ResultSet countRs = countStmt.executeQuery(countQuery)) {
                if (countRs.next()) {
                    System.out.println("  Total users: " + countRs.getInt("total") + "\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("  ⚠️  Table might not exist or no data: " + e.getMessage() + "\n");
        }
    }
    
    private static void checkUserRoles(Connection conn) throws SQLException {
        System.out.println("👮 ADMIN USERS:");
        System.out.println("-------------");
        
        String query = "SELECT u.user_id, u.email, u.first_name, " +
                      "STRING_AGG(r.role_name, ', ') as roles " +
                      "FROM users u " +
                      "LEFT JOIN user_roles ur ON u.user_id = ur.user_id " +
                      "LEFT JOIN roles r ON ur.role_id = r.role_id " +
                      "GROUP BY u.user_id, u.email, u.first_name " +
                      "HAVING STRING_AGG(r.role_name, ', ') LIKE '%ADMIN%'";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("  " + count + ". " + rs.getString("email") + 
                                 " - Roles: " + rs.getString("roles"));
            }
            if (count == 0) {
                System.out.println("  ⚠️  No admin users found!");
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println("  ⚠️  Error: " + e.getMessage() + "\n");
        }
    }
    
    private static void checkMenus(Connection conn) throws SQLException {
        System.out.println("📖 MENUS TABLE:");
        System.out.println("-------------");
        
        String query = "SELECT menu_id, menu_name, description FROM menu ORDER BY menu_name";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("  " + count + ". " + rs.getString("menu_name") + 
                                 " (ID: " + rs.getInt("menu_id") + ")");
            }
            System.out.println("  Total menus: " + count + "\n");
        } catch (SQLException e) {
            System.out.println("  ⚠️  Table might not exist or no data: " + e.getMessage() + "\n");
        }
    }
    
    private static void checkFoodItems(Connection conn) throws SQLException {
        System.out.println("🍔 FOOD ITEMS TABLE:");
        System.out.println("------------------");
        
        String query = "SELECT COUNT(*) as total FROM food_item";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("  Total food items: " + total);
                
                if (total > 0) {
                    // Show items by menu
                    String byMenuQuery = "SELECT m.menu_name, COUNT(f.food_item_id) as item_count " +
                                        "FROM menu m " +
                                        "LEFT JOIN food_item f ON m.menu_id = f.menu_id " +
                                        "GROUP BY m.menu_name";
                    
                    try (Statement menuStmt = conn.createStatement();
                         ResultSet menuRs = menuStmt.executeQuery(byMenuQuery)) {
                        System.out.println("  Items by menu:");
                        while (menuRs.next()) {
                            System.out.println("    - " + menuRs.getString("menu_name") + 
                                             ": " + menuRs.getInt("item_count") + " items");
                        }
                    }
                }
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println("  ⚠️  Table might not exist or no data: " + e.getMessage() + "\n");
        }
    }
    
    private static void checkOrders(Connection conn) throws SQLException {
        System.out.println("📦 ORDERS TABLE:");
        System.out.println("--------------");
        
        String query = "SELECT COUNT(*) as total FROM orders";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("  Total orders: " + total);
                
                if (total > 0) {
                    // Show orders by status
                    String statusQuery = "SELECT status, COUNT(*) as count " +
                                        "FROM orders GROUP BY status";
                    
                    try (Statement statusStmt = conn.createStatement();
                         ResultSet statusRs = statusStmt.executeQuery(statusQuery)) {
                        System.out.println("  Orders by status:");
                        while (statusRs.next()) {
                            System.out.println("    - " + statusRs.getString("status") + 
                                             ": " + statusRs.getInt("count"));
                        }
                    }
                }
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println("  ⚠️  Table might not exist or no data: " + e.getMessage() + "\n");
        }
    }
}
