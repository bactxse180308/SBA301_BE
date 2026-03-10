package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminReplyRequest {

    @NotBlank(message = "Reply comment is required")
    @Size(max = 2000, message = "Reply comment must not exceed 2000 characters")
    private String replyComment;
}
