package com.ntloc.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Meta {
    private int currentPage;
    private int totalPages;
    private int totalItems;
    private int itemsPerPage;
}
