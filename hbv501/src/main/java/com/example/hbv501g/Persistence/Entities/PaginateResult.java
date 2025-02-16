package com.example.hbv501g.Persistence.Entities;

import java.util.List;

public class PaginateResult<T> {
    public int pageNumber;
    public int maxPage;
    public List<T> results;

    public PaginateResult(Integer page, List<T> pagable, int perPage) {
        if (page == null || page <= 0) {page = 1;}
        page--; // 0 indexing is just more convenient
        if (page*perPage >= pagable.size()) {
            page = (pagable.size()-1)/perPage;
        }
        int endIndex = (page+1)*perPage;
        if (endIndex > pagable.size()) {
            endIndex = pagable.size();
        }
        results = pagable.subList(page*perPage, endIndex);
        pageNumber = page+1; // Back to 1 indexing
        maxPage = (pagable.size()+perPage-1)/perPage; // Division, rounding up
    }
}