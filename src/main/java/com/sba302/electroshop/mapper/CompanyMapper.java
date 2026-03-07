package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CompanyRequest;
import com.sba302.electroshop.dto.request.CreateCompanyRequest;
import com.sba302.electroshop.dto.response.CompanyResponse;
import com.sba302.electroshop.dto.response.CreateCompanyResponse;
import com.sba302.electroshop.entity.Company;
import com.sba302.electroshop.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);

    Company toEntity(CompanyRequest request);

    // Map từ CreateCompanyRequest -> Company (Bản chất là tạo mới nên ignore status và approvedAt)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    Company toEntity(CreateCompanyRequest request);

    // Map Company + User thành CreateCompanyResponse
    @Mapping(target = "userId",                source = "user.userId")
    @Mapping(target = "userEmail",             source = "user.email")
    @Mapping(target = "userFullName",          source = "user.fullName")
    @Mapping(target = "companyId",             source = "company.companyId")
    @Mapping(target = "companyName",           source = "company.companyName")
    @Mapping(target = "taxCode",               source = "company.taxCode")
    @Mapping(target = "email",                 source = "company.email")
    @Mapping(target = "phone",                 source = "company.phone")
    @Mapping(target = "address",               source = "company.address")
    @Mapping(target = "representativeName",    source = "company.representativeName")
    @Mapping(target = "representativePosition",source = "company.representativePosition")
    @Mapping(target = "website",               source = "company.website")
    @Mapping(target = "foundingDate",          source = "company.foundingDate")
    @Mapping(target = "businessType",          source = "company.businessType")
    @Mapping(target = "employeeCount",         source = "company.employeeCount")
    @Mapping(target = "industry",              source = "company.industry")
    @Mapping(target = "logoUrl",               source = "company.logoUrl")
    @Mapping(target = "status",                source = "company.status")
    @Mapping(target = "approvedAt",            source = "company.approvedAt")
    CreateCompanyResponse toCreateCompanyResponse(Company company, User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Company company, CompanyRequest request);
}
