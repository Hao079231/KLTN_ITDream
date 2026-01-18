package com.base.auth.mapper;

import com.base.auth.dto.chapter.ChapterDisplayDto;
import com.base.auth.dto.chapter.ChapterDto;
import com.base.auth.form.chapter.CreateChapterForm;
import com.base.auth.form.chapter.UpdateChapterForm;
import com.base.auth.model.Chapter;
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
    uses = {CourseMapper.class})
public interface ChapterMapper {
  @Mapping(source = "name", target = "name")
  @BeanMapping(ignoreByDefault = true)
  Chapter fromCreateChapterFormToEntity(CreateChapterForm createChapterForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "chapterOrder", target = "chapterOrder")
  @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToChapterDto")
  ChapterDto fromEntityToChapterDto(Chapter chapter);

  @IterableMapping(elementTargetType = ChapterDto.class, qualifiedByName = "fromEntityToChapterDto")
  List<ChapterDto> fromEntityToChapterDtoList(List<Chapter> chapters);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDisplayDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToChapterDisplayDto")
  ChapterDisplayDto fromEntityToChapterDisplayDto(Chapter chapter);

  @IterableMapping(elementTargetType = ChapterDisplayDto.class, qualifiedByName = "fromEntityToChapterDisplayDto")
  List<ChapterDisplayDto> fromEntityToChapterDisplayDtoList(List<Chapter> chapters);

  @Mapping(source = "name", target = "name")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateChapterFormToEntity(UpdateChapterForm updateChapterForm, @MappingTarget Chapter chapter);
}
