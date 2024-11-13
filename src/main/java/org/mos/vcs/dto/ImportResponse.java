package org.mos.vcs.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/13
 */

@Data
@Builder
public class ImportResponse {
    private Integer successCount;
    private Integer failCount;
    private String fileName;
}
