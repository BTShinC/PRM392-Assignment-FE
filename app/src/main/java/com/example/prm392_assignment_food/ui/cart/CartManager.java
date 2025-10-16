package com.example.prm392_assignment_food.ui.cart;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp CartManager sử dụng mẫu Singleton để quản lý trạng thái của giỏ hàng.
 * Điều này đảm bảo rằng chỉ có một giỏ hàng duy nhất trong toàn bộ ứng dụng.
 */
public class CartManager {

    // Biến static để giữ instance duy nhất của lớp
    private static CartManager instance;
    // Danh sách các món hàng trong giỏ
    private List<Cart> cartItems;
    private CartManager() {
        cartItems = new ArrayList<>();
    }
    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }
    /**
     * Thêm một món hàng vào giỏ.
     * Nếu món hàng đã tồn tại, chỉ tăng số lượng lên.
     * @param item Món hàng cần thêm.
     */
    public void addItem(Cart item) {
        for (Cart cartItem : cartItems) {
            // Dùng tên để kiểm tra xem món hàng đã có trong giỏ chưa
            if (cartItem.getName().equals(item.getName())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                return; // Thoát khỏi hàm sau khi tăng số lượng
            }
        }
        // Nếu món hàng chưa có, thêm mới vào danh sách
        cartItems.add(item);
    }

    /**
     * Lấy danh sách tất cả các món hàng trong giỏ.
     * @return List<Cart>
     */
    public List<Cart> getCartItems() {
        return cartItems;
    }

    /**
     * Cập nhật số lượng của một món hàng.
     * Nếu số lượng mới là 0, món hàng sẽ bị xóa khỏi giỏ.
     * @param item Món hàng cần cập nhật.
     * @param newQuantity Số lượng mới.
     */
    public void updateItemQuantity(Cart item, int newQuantity) {
        if (newQuantity <= 0) {
            cartItems.remove(item);
        } else {
            item.setQuantity(newQuantity);
        }
    }

    /**
     * Tính tổng giá trị của giỏ hàng.
     * @return Tổng tiền.
     */
    public int calculateTotalPrice() {
        int total = 0;
        for (Cart item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}
