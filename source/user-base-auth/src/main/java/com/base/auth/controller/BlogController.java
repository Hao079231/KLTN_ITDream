package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.blog.BlogDto;
import com.base.auth.dto.blog.BlogEducatorDto;
import com.base.auth.dto.blog.BlogStudentDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.blog.CreateBlogForm;
import com.base.auth.form.blog.RequestBlogForm;
import com.base.auth.form.blog.UpdateBlogForm;
import com.base.auth.mapper.BlogMapper;
import com.base.auth.model.Blog;
import com.base.auth.model.Category;
import com.base.auth.model.Educator;
import com.base.auth.model.criteria.BlogCriteria;
import com.base.auth.repository.BlogRepository;
import com.base.auth.repository.CategoryRepository;
import com.base.auth.repository.EducatorRepository;
import com.base.auth.service.UserBaseApiService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/v1/blog")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class BlogController extends ABasicController{
  @Autowired
  BlogRepository blogRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  EducatorRepository educatorRepository;

  @Autowired
  BlogMapper blogMapper;

  @Autowired
  UserBaseApiService userBaseApiService;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BL_ED_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateBlogForm createBlogForm, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }

    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Educator educator = educatorRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Educator not found", ErrorCode.USER_ERROR_NOT_FOUND));

    if (StringUtils.isEmpty(createBlogForm.getName()) && StringUtils.isEmpty(createBlogForm.getSubject())){
      throw new BadRequestException("Blog name and subject cannot both be blank at the same time",
          ErrorCode.BLOG_ERROR_NAME_SUBJECT_NOT_NULL);
    }

    boolean nameBlogNotNull = StringUtils.isNotEmpty(createBlogForm.getName());

    if (createBlogForm.getCategoryId() != null){
      if (!nameBlogNotNull){
        throw new BadRequestException("Cannot create blog if name is null", ErrorCode.BLOG_ERROR_NAME_NOT_NULL);
      }
      if (StringUtils.isNotEmpty(createBlogForm.getSubject())){
        throw new BadRequestException("Cannot create blog if both category and parent are exist", ErrorCode.BLOG_ERROR_NAME_SUBJECT_EXIST);
      }
      Boolean existBlog = blogRepository.existsByNameAndCategoryIdAndEducatorId(createBlogForm.getName(), createBlogForm.getCategoryId(), getCurrentUser());
      if (existBlog){
        throw new BadRequestException("Blog already exist", ErrorCode.BLOG_ERROR_EXIST);
      }
    } else if (createBlogForm.getParentId() != null){
      if (nameBlogNotNull){
        throw new BadRequestException("Cannot create blog if both category and parent are exist", ErrorCode.BLOG_ERROR_NAME_SUBJECT_EXIST);
      }
      if (StringUtils.isEmpty(createBlogForm.getSubject())){
        throw new BadRequestException("Cannot create blog if subject is null", ErrorCode.BLOG_ERROR_SUBJECT_NOT_NULL);
      }
      Boolean existSubject = blogRepository.existsBySubjectAndParentId(createBlogForm.getSubject(), createBlogForm.getParentId());
      if (existSubject){
        throw new BadRequestException("Subject already exist", ErrorCode.BLOG_ERROR_EXIST);
      }
    } else if (createBlogForm.getCategoryId() == null && createBlogForm.getParentId() == null){
      throw new BadRequestException("Cannot create the blog if both category and parent are null", ErrorCode.BLOG_ERROR_CATEGORY_PARENT_BOTH_NULL);
    } else {
      throw new BadRequestException("Cannot create the blog if both category and parent are not null", ErrorCode.BLOG_ERROR_CATEGORY_PARENT_BOTH_NOT_NULL);
    }

    Blog blog = blogMapper.fromCreateBlogFormToEntity(createBlogForm);
    blog.setEducator(educator);
    if (createBlogForm.getCategoryId() != null){
      Category category = categoryRepository.findById(createBlogForm.getCategoryId())
          .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
      blog.setCategory(category);
    }

    if (createBlogForm.getParentId() != null){
      Blog parent = blogRepository.findById(createBlogForm.getParentId())
          .orElseThrow(() -> new NotFoundException("Parent not found", ErrorCode.BLOG_ERROR_NOT_FOUND));
      blog.setParent(parent);
      parent.setStatus(ITDreamConstant.BLOG_STATUS_WAITING_APPROVE);
      blogRepository.save(parent);
    } else {
      blog.setStatus(ITDreamConstant.BLOG_STATUS_WAITING_APPROVE);
    }
    blogRepository.save(blog);
    apiMessageDto.setMessage(nameBlogNotNull ? "Create blog success" : "Create subject blog success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BL_L')")
  public ApiMessageDto<ResponseListDto<List<BlogDto>>> listByAdmin(BlogCriteria blogCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<BlogDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<BlogDto>> responseListDto = new ResponseListDto<>();
    Page<Blog> blogs = blogRepository.findAll(blogCriteria.getSpecification(), pageable);
    List<BlogDto> blogDtos = blogMapper.fromEntityToBlogDtoList(blogs.getContent());
    responseListDto.setContent(blogDtos);
    responseListDto.setTotalElements(blogs.getTotalElements());
    responseListDto.setTotalPages(blogs.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list blog success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator-list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BL_ED_L')")
  public ApiMessageDto<ResponseListDto<List<BlogEducatorDto>>> listByEducator(BlogCriteria blogCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<BlogEducatorDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<BlogEducatorDto>> responseListDto = new ResponseListDto<>();
    blogCriteria.setEducatorId(getCurrentUser());
    Page<Blog> blogs = blogRepository.findAll(blogCriteria.getSpecification(), pageable);
    List<BlogEducatorDto> blogDtos = blogMapper.fromEntityToBlogEducatorDtoList(blogs.getContent());
    responseListDto.setContent(blogDtos);
    responseListDto.setTotalElements(blogs.getTotalElements());
    responseListDto.setTotalPages(blogs.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list blog success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<BlogStudentDto>>> listByStudent(BlogCriteria blogCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<BlogStudentDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<BlogStudentDto>> responseListDto = new ResponseListDto<>();
    blogCriteria.setStatus(ITDreamConstant.BLOG_STATUS_ACTIVE);
    Page<Blog> blogs = blogRepository.findAll(blogCriteria.getSpecification(), pageable);
    List<BlogStudentDto> blogDtos = blogMapper.fromEntityToBlogStudentDtoList(blogs.getContent());
    responseListDto.setContent(blogDtos);
    responseListDto.setTotalElements(blogs.getTotalElements());
    responseListDto.setTotalPages(blogs.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list blog success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BL_V')")
  public ApiMessageDto<BlogDto> getByAdmin(@PathVariable Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<BlogDto> apiMessageDto = new ApiMessageDto<>();
    Blog blog = blogRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Blog not found"));
    ResponseListDto<List<BlogDto>> responseListDto = new ResponseListDto<>();
    BlogCriteria blogCriteria = new BlogCriteria();
    blogCriteria.setParentId(blog.getId());
    Pageable pageable = PageRequest.of(0, 100);
    Page<Blog> subjects = blogRepository.findAll(blogCriteria.getSpecification(), pageable);
    List<BlogDto> subjectDtos = blogMapper.fromEntityToBlogDtoList(subjects.getContent());
    responseListDto.setContent(subjectDtos);
    responseListDto.setTotalElements(subjects.getTotalElements());
    responseListDto.setTotalPages(subjects.getTotalPages());

    BlogDto blogDto = blogMapper.fromEntityToBlogDto(blog);
    blogDto.setSubjects(responseListDto);
    apiMessageDto.setData(blogDto);
    apiMessageDto.setMessage("Get detail blog success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BL_ED_V')")
  public ApiMessageDto<BlogEducatorDto> getByEducator(@PathVariable Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<BlogEducatorDto> apiMessageDto = new ApiMessageDto<>();
    Blog blog = blogRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Blog not found"));
    ResponseListDto<List<BlogEducatorDto>> responseListDto = new ResponseListDto<>();
    BlogCriteria blogCriteria = new BlogCriteria();
    blogCriteria.setParentId(blog.getId());
    Pageable pageable = PageRequest.of(0, 100);
    Page<Blog> subjects = blogRepository.findAll(blogCriteria.getSpecification(), pageable);
    List<BlogEducatorDto> subjectDtos = blogMapper.fromEntityToBlogEducatorDtoList(subjects.getContent());
    responseListDto.setContent(subjectDtos);
    responseListDto.setTotalElements(subjects.getTotalElements());
    responseListDto.setTotalPages(subjects.getTotalPages());

    BlogEducatorDto blogDto = blogMapper.fromEntityToBlogEducatorDto(blog);
    blogDto.setSubjects(responseListDto);
    apiMessageDto.setData(blogDto);
    apiMessageDto.setMessage("Get detail blog success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<BlogStudentDto> getByStudent(@PathVariable Long id){
    ApiMessageDto<BlogStudentDto> apiMessageDto = new ApiMessageDto<>();
    Blog blog = blogRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Blog not found"));
    ResponseListDto<List<BlogStudentDto>> responseListDto = new ResponseListDto<>();
    BlogCriteria blogCriteria = new BlogCriteria();
    blogCriteria.setParentId(blog.getId());
    Pageable pageable = PageRequest.of(0, 100);
    Page<Blog> subjects = blogRepository.findAll(blogCriteria.getSpecification(), pageable);
    List<BlogStudentDto> subjectDtos = blogMapper.fromEntityToBlogStudentDtoList(subjects.getContent());
    responseListDto.setContent(subjectDtos);
    responseListDto.setTotalElements(subjects.getTotalElements());
    responseListDto.setTotalPages(subjects.getTotalPages());

    BlogStudentDto blogDto = blogMapper.fromEntityToBlogStudentDto(blog);
    blogDto.setSubjects(responseListDto);
    apiMessageDto.setData(blogDto);
    apiMessageDto.setMessage("Get detail blog success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BL_U')")
  public ApiMessageDto<String> udpate(@Valid @RequestBody UpdateBlogForm updateBlogForm, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }

    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Blog blog = blogRepository.findById(updateBlogForm.getId())
        .orElseThrow(() -> new NotFoundException("Blog not found", ErrorCode.BLOG_ERROR_NOT_FOUND));

    if (StringUtils.isEmpty(updateBlogForm.getName()) && StringUtils.isEmpty(updateBlogForm.getSubject())){
      throw new BadRequestException("Blog name and subject cannot both be blank at the same time", ErrorCode.BLOG_ERROR_NAME_SUBJECT_NOT_NULL);
    }

    boolean nameBlogNotNull = StringUtils.isNotEmpty(updateBlogForm.getName());

    if (updateBlogForm.getCategoryId() != null){
      if (!nameBlogNotNull){
        throw new BadRequestException("Cannot update blog if name is null", ErrorCode.BLOG_ERROR_NAME_NOT_NULL);
      }
      if (StringUtils.isNotEmpty(updateBlogForm.getSubject())){
        throw new BadRequestException("Cannot update blog if both category and parent are exist", ErrorCode.BLOG_ERROR_NAME_SUBJECT_EXIST);
      }
      Boolean existBlog = blogRepository.existsByNameAndCategoryIdAndEducatorId(updateBlogForm.getName(), updateBlogForm.getCategoryId(), getCurrentUser());
      if (existBlog){
        throw new BadRequestException("Blog already exist", ErrorCode.BLOG_ERROR_EXIST);
      }
    } else if (updateBlogForm.getParentId() != null){
      if (nameBlogNotNull){
        throw new BadRequestException("Cannot update blog if both category and parent are exist", ErrorCode.BLOG_ERROR_NAME_SUBJECT_EXIST);
      }
      if (StringUtils.isEmpty(updateBlogForm.getSubject())){
        throw new BadRequestException("Cannot update blog if subject is null", ErrorCode.BLOG_ERROR_SUBJECT_NOT_NULL);
      }
      Boolean existSubject = blogRepository.existsBySubjectAndParentId(updateBlogForm.getSubject(), updateBlogForm.getParentId());
      if (existSubject){
        throw new BadRequestException("Subject already exist", ErrorCode.BLOG_ERROR_EXIST);
      }
    } else if (updateBlogForm.getCategoryId() == null && updateBlogForm.getParentId() == null){
      throw new BadRequestException("Cannot update the blog if both category and parent are null", ErrorCode.BLOG_ERROR_CATEGORY_PARENT_BOTH_NULL);
    } else {
      throw new BadRequestException("Cannot update the blog if both category and parent are not null", ErrorCode.BLOG_ERROR_CATEGORY_PARENT_BOTH_NOT_NULL);
    }

    blogMapper.fromUpdateBlogFormToEntity(updateBlogForm, blog);
    if (updateBlogForm.getCategoryId() != null){
      Category category = categoryRepository.findById(updateBlogForm.getCategoryId())
          .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
      blog.setCategory(category);
    }

    if (updateBlogForm.getParentId() != null){
      Blog parent = blogRepository.findById(updateBlogForm.getParentId())
          .orElseThrow(() -> new NotFoundException("Parent not found", ErrorCode.BLOG_ERROR_NOT_FOUND));
      blog.setParent(parent);
      parent.setStatus(ITDreamConstant.BLOG_STATUS_WAITING_APPROVE);
      blogRepository.save(parent);
    } else {
      blog.setStatus(ITDreamConstant.BLOG_STATUS_WAITING_APPROVE);
    }
    blogRepository.save(blog);
    apiMessageDto.setMessage(nameBlogNotNull ? "Update blog success" : "Update subject blog success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BL_ED_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Blog blog = blogRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Blog not found"));
    if (!blog.getImage().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(blog.getImage());
    }
    List<Blog> subjects = blogRepository.findAllByParentId(blog.getId());
    for (Blog subject : subjects){
      if (!subject.getImage().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
        userBaseApiService.deleteByFilePath(blog.getImage());
      }
      blogRepository.delete(subject);
    }
    blogRepository.delete(blog);
    apiMessageDto.setMessage("Delete blog success");
    return apiMessageDto;
  }
  
  @PutMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BL_AP')")
  public ApiMessageDto<String> approve(@Valid @RequestBody RequestBlogForm requestBlogForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Blog blog = blogRepository.findById(requestBlogForm.getId())
        .orElseThrow(() -> new NotFoundException("Blog not found", ErrorCode.BLOG_ERROR_NOT_FOUND));
    blog.setNotice(null);
    blog.setStatus(ITDreamConstant.BLOG_STATUS_ACTIVE);
    blogRepository.save(blog);
    apiMessageDto.setMessage("Approve blog success");
    return apiMessageDto;
  }

  @PutMapping(value = "/reject", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BL_AP')")
  public ApiMessageDto<String> reject(@Valid @RequestBody RequestBlogForm requestBlogForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Blog blog = blogRepository.findById(requestBlogForm.getId())
        .orElseThrow(() -> new NotFoundException("Blog not found", ErrorCode.BLOG_ERROR_NOT_FOUND));
    blog.setNotice(requestBlogForm.getNotice());
    blog.setStatus(ITDreamConstant.BLOG_STATUS_REJECT);
    blogRepository.save(blog);
    apiMessageDto.setMessage("Reject blog success");
    return apiMessageDto;
  }
}
