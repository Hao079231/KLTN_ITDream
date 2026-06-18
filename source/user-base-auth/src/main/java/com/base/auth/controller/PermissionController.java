package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.permission.CreatePermissionForm;
import com.base.auth.form.permission.UpdatePermissionForm;
import com.base.auth.model.Permission;
import com.base.auth.repository.GroupRepository;
import com.base.auth.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/permission")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class PermissionController extends ABasicController{
    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    GroupRepository groupRepository;

    @PostMapping(value = "/create", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PER_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreatePermissionForm createPermissionForm, BindingResult bindingResult) {
        if (!isSuperAdmin()){
            throw new UnauthorizationException("Không được phép tạo");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Permission permission = permissionRepository.findFirstByName(createPermissionForm.getName());
        if(permission != null){
            throw new BadRequestException("Tên quyền đã tồn tại", ErrorCode.PERMISSION_ERROR_EXIST);
        }
        permission = new Permission();
        permission.setName(createPermissionForm.getName());
        permission.setAction(createPermissionForm.getAction());
        permission.setDescription(createPermissionForm.getDescription());
        permission.setShowMenu(createPermissionForm.getShowMenu());
        permission.setNameGroup(createPermissionForm.getNameGroup());
        permission.setPCode(createPermissionForm.getPermissionCode());
        permissionRepository.save(permission);
        apiMessageDto.setMessage("Tạo quyền thành công");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PER_L')")
    public ApiMessageDto<List<Permission>> list() {
        ApiMessageDto<List<Permission>> apiMessageDto = new ApiMessageDto<>();
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Không được phép lấy danh sách.");
        }
        Page<Permission> accounts = permissionRepository.findAll(PageRequest.of(0, 1000, Sort.by(new Sort.Order(Sort.Direction.DESC, "createdDate"))));
        apiMessageDto.setData(accounts.getContent());
        apiMessageDto.setMessage("Lấy danh sách quyền thành công");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PER_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdatePermissionForm updatePermissionForm, BindingResult bindingResult){
        if (!isSuperAdmin()){
            throw new UnauthorizationException("Không được phép cập nhật");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Permission permission = permissionRepository.findById(updatePermissionForm.getId())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền", ErrorCode.PERMISSION_ERROR_NOT_FOUND));

        if (!updatePermissionForm.getName().equals(permission.getName())){
            Boolean existPermission = permissionRepository.existsByName(updatePermissionForm.getName());
            if (existPermission){
                throw new BadRequestException("Tên quyền đã tồn tại", ErrorCode.PERMISSION_ERROR_EXIST);
            }
        }

        if (!updatePermissionForm.getAction().equals(permission.getAction())){
            Boolean existPermission = permissionRepository.existsByAction(updatePermissionForm.getAction());
            if (existPermission){
                throw new BadRequestException("Hành động của quyền đã tồn tại", ErrorCode.PERMISSION_ERROR_EXIST);
            }
        }

        permission.setName(updatePermissionForm.getName());
        permission.setDescription(updatePermissionForm.getDescription());
        permission.setAction(updatePermissionForm.getAction());
        permission.setPCode(updatePermissionForm.getPermissionCode());
        permission.setNameGroup(updatePermissionForm.getNameGroup());
        permission.setShowMenu(updatePermissionForm.getShowMenu());
        permissionRepository.save(permission);
        apiMessageDto.setMessage("Cập nhật quyền thành công");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PER_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id){
        if (!isSuperAdmin()){
            throw new UnauthorizationException("Không được phép xóa");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền"));
        Boolean usedPermission = groupRepository.existsByPermissionId(id);
        if (usedPermission){
            throw new BadRequestException("Không thể xóa quyền", ErrorCode.PERMISSION_ERROR_DELETE);
        }
        permissionRepository.delete(permission);
        apiMessageDto.setMessage("Xóa quyền thành công");
        return apiMessageDto;
    }
}