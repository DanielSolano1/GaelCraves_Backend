package com.gaelcraves.project3.GaelCravings_Backend.DTO;

import java.math.BigDecimal;

public class AdminStats {

    private int pendingOrders;
    private BigDecimal todayRevenue;
    private int totalUsers;
    private int menuItems;

    public int getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(int pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(int menuItems) {
        this.menuItems = menuItems;
    }
}
