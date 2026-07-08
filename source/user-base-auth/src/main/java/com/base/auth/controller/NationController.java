package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.nation.NationAdminDto;
import com.base.auth.dto.nation.NationDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.nation.CreateNationForm;
import com.base.auth.form.nation.UpdateNationForm;
import com.base.auth.mapper.NationMapper;
import com.base.auth.model.Nation;
import com.base.auth.model.criteria.NationCriteria;
import com.base.auth.repository.JobPostRepository;
import com.base.auth.repository.NationRepository;
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
@RequestMapping("/v1/nation")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class NationController extends ABasicController{
  @Autowired
  NationRepository nationRepository;

  @Autowired
  JobPostRepository jobPostRepository;

  @Autowired
  NationMapper nationMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateNationForm createNationForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    validateNationKind(createNationForm.getKind(), createNationForm.getParentId());
    validateNationName(createNationForm.getName(), createNationForm.getKind(), createNationForm.getParentId(), null);
    Nation nation = nationMapper.fromCreateNationFormToEntity(createNationForm);
    if (createNationForm.getParentId() != null){
      Nation parent = nationRepository.findById(createNationForm.getParentId())
          .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));
      nation.setParent(parent);
    }
    nationRepository.save(nation);
    apiMessageDto.setMessage("Tạo địa chỉ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_L')")
  public ApiMessageDto<ResponseListDto<List<NationAdminDto>>> listByAdmin(NationCriteria nationCriteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải quản trị viên");
    }
    ApiMessageDto<ResponseListDto<List<NationAdminDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<NationAdminDto>> responseListDto = new ResponseListDto<>();
    Page<Nation> nations = nationRepository.findAll(nationCriteria.getSpecification(), pageable);
    List<NationAdminDto> nationAdminDtos = nationMapper.fromEntityToNationAdminDtoList(nations.getContent());
    responseListDto.setContent(nationAdminDtos);
    responseListDto.setTotalElements(nations.getTotalElements());
    responseListDto.setTotalPages(nations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách địa chỉ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<NationDto>>> listByClient(NationCriteria nationCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<NationDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<NationDto>> responseListDto = new ResponseListDto<>();
    Page<Nation> nations = nationRepository.findAll(nationCriteria.getSpecification(), pageable);
    List<NationDto> nationDtos = nationMapper.fromEntityToNationDtoList(nations.getContent());
    responseListDto.setContent(nationDtos);
    responseListDto.setTotalElements(nations.getTotalElements());
    responseListDto.setTotalPages(nations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách địa chỉ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_V')")
  public ApiMessageDto<NationAdminDto> getByAdmin(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải quản trị viên");
    }
    ApiMessageDto<NationAdminDto> apiMessageDto = new ApiMessageDto<>();
    Nation nation = nationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));
    NationAdminDto nationAdminDto = nationMapper.fromEntityToNationAdminDto(nation);
    apiMessageDto.setData(nationAdminDto);
    apiMessageDto.setMessage("Lấy thông tin địa chỉ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<NationDto> getByClient(@PathVariable("id") Long id){
    ApiMessageDto<NationDto> apiMessageDto = new ApiMessageDto<>();
    Nation nation = nationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));
    NationDto nationDto = nationMapper.fromEntityToNationDto(nation);
    apiMessageDto.setData(nationDto);
    apiMessageDto.setMessage("Lấy thông tin địa chỉ thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateNationForm updateNationForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Nation nation = nationRepository.findById(updateNationForm.getId())
        .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));
    validateNationKind(updateNationForm.getKind(), updateNationForm.getParentId());

    if (!updateNationForm.getName().equals(nation.getName())){
      validateNationName(updateNationForm.getName(), updateNationForm.getKind(), updateNationForm.getParentId(), nation.getId());
    }

    nationMapper.fromUpdateNationFormToEntity(updateNationForm, nation);
    if ((nation.getParent() == null && !updateNationForm.getKind().equals(ITDreamConstant.NATION_KIND_PROVINCE))
      || (updateNationForm.getParentId() != null && !updateNationForm.getParentId().equals(nation.getParent().getId()))){
      Nation parent = nationRepository.findById(updateNationForm.getParentId())
          .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));
      nation.setParent(parent);
    }
    nationRepository.save(nation);
    apiMessageDto.setMessage("Cập nhật địa chỉ thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Nation nation = nationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));

    if (nation.getKind().equals(ITDreamConstant.NATION_KIND_PROVINCE)) {
      nationRepository.setNullChildrenByParentId(id);
      jobPostRepository.setNullProvinceByProvinceId(id);
    } else if (nation.getKind().equals(ITDreamConstant.NATION_KIND_WARD)){
      jobPostRepository.setNullWardByWardId(id);
    }

    nationRepository.delete(nation);
    apiMessageDto.setMessage("Delete nation success");
    return apiMessageDto;
  }

  private void validateNationKind(Integer kind, Long parentId) {
    if (kind.equals(ITDreamConstant.NATION_KIND_PROVINCE)) {
      if (parentId != null) {
        throw new BadRequestException("Tỉnh / Thành phố không có phụ thuộc", ErrorCode.NATION_ERROR_NOT_PARENT);
      }
    } else {
      if (parentId == null) {
        throw new BadRequestException("Id phụ thuộc không được trống", ErrorCode.NATION_ERROR_NOT_FOUND);
      }

      Nation parent = nationRepository.findById(parentId)
          .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ", ErrorCode.NATION_ERROR_NOT_FOUND));

      if (kind.equals(ITDreamConstant.NATION_KIND_WARD) && !parent.getKind().equals(ITDreamConstant.NATION_KIND_PROVINCE)) {
        throw new BadRequestException("Xã / Phường phải thuộc về tỉnh / thành phố", ErrorCode.NATION_ERROR_NOT_PARENT_DISTRICT);
      }
    }
  }

  private void validateNationName(String name, Integer kind, Long parentId, Long currentId) {
    boolean exists;
    if (kind.equals(ITDreamConstant.NATION_KIND_PROVINCE)) {
      exists = currentId == null
          ? nationRepository.existsByNameAndKind(name, kind)
          : nationRepository.existsByNameAndKindAndIdNot(name, kind, currentId);
    } else {
      exists = currentId == null
          ? nationRepository.existsByNameAndParentId(name, parentId)
          : nationRepository.existsByNameAndParentIdAndIdNot(name, parentId, currentId);
    }

    if (exists) {
      throw new BadRequestException("Tên địa chỉ đã tồn tại trong cấp bậc này", ErrorCode.NATION_ERROR_EXIST);
    }
  }
}
