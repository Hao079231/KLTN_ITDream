package com.base.auth.mapper;

import com.base.auth.dto.comment.CommentDto;
import com.base.auth.dto.comment.CommentUserDto;
import com.base.auth.form.comment.CreateCommentForm;
import com.base.auth.form.comment.UpdateCommentForm;
import com.base.auth.model.Comment;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {TaskMapper.class, AccountMapper.class})
public interface CommentMapper {
  @Mapping(source = "content", target = "content")
  @BeanMapping(ignoreByDefault = true)
  Comment fromCreateCommentFormToEntity(CreateCommentForm createCommentForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "task", target = "task", qualifiedByName = "fromEntityToTaskDto")
  @Mapping(source = "user", target = "user", qualifiedByName = "fromAccountToDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCommentDto")
  CommentDto fromEntityToCommentDto(Comment comment);

  @IterableMapping(elementTargetType = CommentDto.class, qualifiedByName = "fromEntityToCommentDto")
  List<CommentDto> fromEntityToCommentDtoList(List<Comment> comments);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "task", target = "task", qualifiedByName = "fromEntityToTaskDisplayDto")
  @Mapping(source = "user", target = "user", qualifiedByName = "fromAccountToProfileDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCommentUserDto")
  CommentUserDto fromEntityToCommentUserDto(Comment comment);

  @IterableMapping(elementTargetType = CommentUserDto.class, qualifiedByName = "fromEntityToCommentUserDto")
  List<CommentUserDto> fromEntityToCommentUserDtoList(List<Comment> comments);


  @Mapping(source = "content", target = "content")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateCommentFormToEntity(UpdateCommentForm updateCommentForm, @MappingTarget Comment comment);
}
