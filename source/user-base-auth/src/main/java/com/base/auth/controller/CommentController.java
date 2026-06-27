package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.comment.CommentDto;
import com.base.auth.dto.comment.CommentUserDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.form.comment.CreateCommentForm;
import com.base.auth.form.comment.UpdateCommentForm;
import com.base.auth.mapper.CommentMapper;
import com.base.auth.model.Account;
import com.base.auth.model.Comment;
import com.base.auth.model.Task;
import com.base.auth.model.criteria.CommentCriteria;
import com.base.auth.repository.AccountRepository;
import com.base.auth.repository.CommentRepository;
import com.base.auth.repository.TaskRepository;
import java.util.List;
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
@RequestMapping("/v1/comment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CommentController extends ABasicController{
  @Autowired
  CommentRepository commentRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  TaskRepository taskRepository;

  @Autowired
  CommentMapper commentMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CM_US_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateCommentForm form, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Account user = accountRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng", ErrorCode.USER_ERROR_NOT_FOUND));
    Task task = taskRepository.findById(form.getTaskId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy nhiệm vụ", ErrorCode.TASK_ERROR_NOT_FOUND));
    Comment comment = commentMapper.fromCreateCommentFormToEntity(form);
    comment.setTask(task);
    comment.setUser(user);
    if (form.getParentId() != null){
      Comment parent = commentRepository.findById(form.getParentId())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy bình luận", ErrorCode.COMMENT_ERROR_NOT_FOUND));
      if (!parent.getTask().getId().equals(task.getId())) {
        throw new BadRequestException("Bình luận cha không thuộc cùng một nhiệm vụ", ErrorCode.COMMENT_ERROR_INVALID_PARENT);
      }
      Comment rootComment = parent.getRoot() == null ? parent : parent.getRoot();
      comment.setRoot(rootComment);
      comment.setParent(parent);
    }
    commentRepository.save(comment);
    apiMessageDto.setMessage("Tạo bình luận thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CM_L')")
  public ApiMessageDto<ResponseListDto<List<CommentDto>>> list(CommentCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CommentDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CommentDto>> responseListDto = new ResponseListDto<>();
    Page<Comment> comments = commentRepository.findAll(criteria.getSpecification(), pageable);
    List<CommentDto> commentDtos = commentMapper.fromEntityToCommentDtoList(comments.getContent());

    for (int i = 0; i < comments.getContent().size(); i++) {
      Comment entity = comments.getContent().get(i);
      if (entity.getParent() != null) {
        commentDtos.get(i).setReplyToUser(entity.getParent().getUser().getUsername());
        commentDtos.get(i).setParentId(entity.getParent().getId());
      }
      if (entity.getRoot() != null) {
        commentDtos.get(i).setRootId(entity.getRoot().getId());
      }
    }

    responseListDto.setContent(commentDtos);
    responseListDto.setTotalElements(comments.getTotalElements());
    responseListDto.setTotalPages(comments.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/user_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CM_US_L')")
  public ApiMessageDto<ResponseListDto<List<CommentUserDto>>> listByUser(CommentCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CommentUserDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CommentUserDto>> responseListDto = new ResponseListDto<>();
    Page<Comment> comments = commentRepository.findAll(criteria.getSpecification(), pageable);
    List<CommentUserDto> commentDtos = commentMapper.fromEntityToCommentUserDtoList(comments.getContent());
    for (int i = 0; i < comments.getContent().size(); i++) {
      Comment entity = comments.getContent().get(i);
      if (entity.getParent() != null) {
        commentDtos.get(i).setReplyToUser(entity.getParent().getUser().getUsername());
        commentDtos.get(i).setParentId(entity.getParent().getId());
      }
      if (entity.getRoot() != null) {
        commentDtos.get(i).setRootId(entity.getRoot().getId());
      }
    }

    responseListDto.setContent(commentDtos);
    responseListDto.setTotalElements(comments.getTotalElements());
    responseListDto.setTotalPages(comments.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CM_US_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateCommentForm form, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Comment comment = commentRepository.findById(form.getId()).
        orElseThrow(() -> new NotFoundException("Không tìm thấy bình luận", ErrorCode.COMMENT_ERROR_NOT_FOUND));
    if (!comment.getUser().getId().equals(getCurrentUser())){
      throw new BadRequestException("Không thể cập nhật bình luận", ErrorCode.COMMENT_ERROR_NOT_UPDATE);
    }
    commentMapper.fromUpdateCommentFormToEntity(form, comment);
    commentRepository.save(comment);
    apiMessageDto.setMessage("Cập nhật bình luận thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CM_US_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Comment comment = commentRepository.findById(id).
        orElseThrow(() -> new NotFoundException("Không tìm thấy bình luận", ErrorCode.COMMENT_ERROR_NOT_FOUND));
    if (!comment.getUser().getId().equals(getCurrentUser())){
      throw new BadRequestException("Không thể xóa bình luận", ErrorCode.COMMENT_ERROR_NOT_DELETE);
    }
    commentRepository.deleteAllByParent(comment);
    commentRepository.delete(comment);
    apiMessageDto.setMessage("Xóa bình luận thành công");
    return apiMessageDto;
  }
}