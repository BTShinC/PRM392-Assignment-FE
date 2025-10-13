package com.example.prm392_assignment_food.ui.customer;

import com.example.prm392_assignment_food.R;
import java.util.HashMap;
import java.util.Map;

public class IngredientHelper {
    private static final Map<String, Integer> ingredientImages = new HashMap<>();
    
    static {
        ingredientImages.put("Salt", R.drawable.salt);
        ingredientImages.put("Chicken", R.drawable.chicken);
        ingredientImages.put("Onion", R.drawable.onion);
        ingredientImages.put("Garlic", R.drawable.garlic);
        ingredientImages.put("Pappers", R.drawable.pappers);
        ingredientImages.put("Ginger", R.drawable.ginger);
        ingredientImages.put("Broccoli", R.drawable.broccoli);
        ingredientImages.put("Orange", R.drawable.orange);
        ingredientImages.put("Walnut", R.drawable.walnut);
    }
    
    public static int getIngredientImage(String ingredientName) {
        return ingredientImages.getOrDefault(ingredientName, R.drawable.salt);
    }
}
