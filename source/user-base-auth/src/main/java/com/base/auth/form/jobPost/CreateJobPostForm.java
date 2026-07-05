package com.base.auth.form.jobPost;

import com.base.auth.validation.JobPostRoleType;
import com.base.auth.validation.JobPostType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateJobPostForm {
  @NotEmpty(message = "Tiêu đề không được để trống")
  @ApiModelProperty(name = "title")
  private String title;

  @NotEmpty(message = "Nội dung không được để trống")
  @ApiModelProperty(name = "content")
  private String content;

  @NotBlank(message = "Ảnh bìa không được để trống")
  @ApiModelProperty(name = "image")
  private String image;

  @JobPostType
  @ApiModelProperty(name = "type")
  private Integer type;

  @JobPostRoleType
  @ApiModelProperty(name = "roleType")
  private Integer roleType;

  @NotBlank(message = "Đường dẫn trang tuyển dụng liên kết không được để trống")
  @ApiModelProperty(name = "jobUrl")
  private String jobUrl;

  @ApiModelProperty(name = "address")
  private String address;

  @ApiModelProperty(name = "provinceId")
  private Long provinceId;

  @ApiModelProperty(name = "wardId")
  private Long wardId;

  @Future(message = "Ngày tổ chức phải ở tương lai")
  @ApiModelProperty(name = "date")
  private Date date;

  @Future(message = "Ngày kết thúc phải ở tương lai")
  @ApiModelProperty(name = "endDate")
  private Date endDate;

  @NotNull(message = "Danh sách bài mô phỏng không được để trống")
  @ApiModelProperty(name = "simulationIds")
  private List<Long> simulationIds;
}
