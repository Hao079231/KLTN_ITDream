package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.category.CategoryAutoCompleteDto;
import com.base.auth.dto.category.CategoryDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.form.category.CreateCategoryForm;
import com.base.auth.form.category.UpdateCategoryForm;
import com.base.auth.mapper.CategoryMapper;
import com.base.auth.model.Category;
import com.base.auth.model.criteria.CategoryCriteria;
import com.base.auth.repository.CourseRepository;
import com.base.auth.repository.CategoryRepository;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CategoryController extends ABasicController{
  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  CategoryMapper categoryMapper;

  @Autowired
  CourseRepository courseRepository;

  @PostMapping(value = "/create", produces= MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateCategoryForm createCategoryForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Boolean existCategory = categoryRepository.existsByName(createCategoryForm.getName());
    if (existCategory){
      throw new BadRequestException("Category already exist", ErrorCode.CATEGORY_ERROR_EXIST);
    }
    Category category = categoryMapper.fromCreateCategoryFormToEntity(createCategoryForm);
    categoryRepository.save(category);
    apiMessageDto.setMessage("Create category success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_L')")
  public ApiMessageDto<ResponseListDto<List<CategoryDto>>> getList(
      CategoryCriteria categoryCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CategoryDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CategoryDto>> responseListDto = new ResponseListDto<>();
    Page<Category> categories = categoryRepository.findAll(categoryCriteria.getSpecification(), pageable);
    responseListDto.setContent(categoryMapper.fromEntityToCategoryDtoList(categories.getContent()));
    responseListDto.setTotalElements(categories.getTotalElements());
    responseListDto.setTotalPages(categories.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list category success");
    return apiMessageDto;
  }

  @GetMapping("/auto-complete")
  public ApiMessageDto<ResponseListDto<List<CategoryAutoCompleteDto>>> listCategoryAutoComplete(
      CategoryCriteria categoryCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CategoryAutoCompleteDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CategoryAutoCompleteDto>> responseListDto = new ResponseListDto<>();
    Page<Category> categories = categoryRepository.findAll(categoryCriteria.getSpecification(), pageable);
    responseListDto.setContent(categoryMapper.fromEntityToCategoryAutoCompleteDtoList(categories.getContent()));
    responseListDto.setTotalElements(categories.getTotalElements());
    responseListDto.setTotalPages(categories.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list category success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateCategoryForm updateCategoryForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(updateCategoryForm.getId()).orElseThrow(()
    -> new NotFoundException("Category not found"));
    if (!Objects.equals(updateCategoryForm.getName(), category.getName())){
      Boolean existCategory = categoryRepository.existsByName(updateCategoryForm.getName());
      if (existCategory){
        throw new BadRequestException("Category already exist", ErrorCode.CATEGORY_ERROR_EXIST);
      }
    }
    categoryMapper.fromUpdateCategoryFormToEntity(updateCategoryForm, category);
    categoryRepository.save(category);
    apiMessageDto.setMessage("Update category success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
    Boolean existSimulation = courseRepository.existsByCategoryId(id);
    if (existSimulation){
      throw new BadRequestException("Category cannot be deleted", ErrorCode.CATEGORY_ERROR_DELETE);
    }
    categoryRepository.delete(category);
    apiMessageDto.setMessage("Delete category success");
    return apiMessageDto;
  }
}
