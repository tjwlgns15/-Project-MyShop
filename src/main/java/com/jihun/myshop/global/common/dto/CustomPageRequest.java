package com.jihun.myshop.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPageRequest {
    private int page = 0;
    private int size = 10;
    private String sort;
    private String direction;

    public PageRequest toPageRequest() {
        if (sort != null && !sort.isEmpty()) {
            Sort.Direction sortDirection = direction != null && direction.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(sortDirection, sort));
        }
        return PageRequest.of(page, size);
    }
}