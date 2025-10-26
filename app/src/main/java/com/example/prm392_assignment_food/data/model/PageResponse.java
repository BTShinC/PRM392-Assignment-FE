package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Generic wrapper cho Spring Data Page response
 * @param <T> Type của content (MenuItemResponse, MenuCategoryResponse, etc.)
 */
public class PageResponse<T> {
    @SerializedName("content")
    private List<T> content;
    
    @SerializedName("totalElements")
    private Long totalElements;
    
    @SerializedName("totalPages")
    private Integer totalPages;
    
    @SerializedName("number")
    private Integer number;
    
    @SerializedName("size")
    private Integer size;
    
    @SerializedName("first")
    private Boolean first;
    
    @SerializedName("last")
    private Boolean last;

    public PageResponse() {
    }

    // Getters
    public List<T> getContent() { return content; }
    public Long getTotalElements() { return totalElements; }
    public Integer getTotalPages() { return totalPages; }
    public Integer getNumber() { return number; }
    public Integer getSize() { return size; }
    public Boolean getFirst() { return first; }
    public Boolean getLast() { return last; }

    // Setters
    public void setContent(List<T> content) { this.content = content; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    public void setNumber(Integer number) { this.number = number; }
    public void setSize(Integer size) { this.size = size; }
    public void setFirst(Boolean first) { this.first = first; }
    public void setLast(Boolean last) { this.last = last; }
}


