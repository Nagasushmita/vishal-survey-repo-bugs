package io.springboot.survey.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.springboot.survey.utils.Constants.CommonConstant.INVALID_PAGE_SIZE;

public class Pagination {

    public <T> List<T> surveyPagination(List<T>list, int page, int pageSize)
    {
        if(pageSize <= 0 || page < 0) {
            throw new IllegalArgumentException(INVALID_PAGE_SIZE+ pageSize);
        }
        int fromIndex = (page) * pageSize;
        if(list == null || list.size() < fromIndex){
            return Collections.emptyList();
        }
        List<T> subList=list.subList(fromIndex, Math.min(fromIndex + pageSize, list.size()));
        if(!subList.isEmpty())
            return subList;
        else
            return new ArrayList<>();

    }



}
