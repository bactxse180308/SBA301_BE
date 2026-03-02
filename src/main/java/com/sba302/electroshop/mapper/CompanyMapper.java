package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CompanyRequest;
import com.sba302.electroshop.dto.response.CompanyResponse;
import com.sba302.electroshop.entity.Company;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);

    Company toEntity(CompanyRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Company company, CompanyRequest request);
}
