package com.example.prm392_assignment_food.ui.admin;

import com.example.prm392_assignment_food.data.model.MenuItemResponse;

/**
 * Data class to hold information about a popular menu item.
 */
public class PopularItem {
    public final MenuItemResponse menuItem;
    public final int quantity;

    public PopularItem(MenuItemResponse menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }
}
