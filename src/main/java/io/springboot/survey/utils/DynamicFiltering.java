package io.springboot.survey.utils;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.util.Set;

public class DynamicFiltering {

    public MappingJacksonValue dynamicObjectFiltering(Object value, Set<String>fields,String id)
    {
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(value);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(fields);
        FilterProvider filters=new SimpleFilterProvider().addFilter(id,filter);
        mappingJacksonValue.setFilters(filters);
        return mappingJacksonValue;
    }
}
