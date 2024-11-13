package org.mos.vcs.service.component;

import com.opencsv.exceptions.CsvException;
import org.mos.vcs.dto.UserDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

public interface FileHelper {
    String[] HEADER = {"Tên", "Mật khẩu", "Email", "Số điện thoại", "Địa chỉ", "Quyền", "Ngày tạo"};
    String[] INVALID_HEADER = {"Tên", "Mật khẩu", "Email", "Số điện thoại", "Địa chỉ", "Quyền", "Ngày tạo", "Lỗi"};

    List<UserDto> read(InputStream inputStream) throws IOException, CsvException;

    void write(List<String[]> list, String filePath) throws IOException;
}
