package org.mos.vcs.service.component;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.mos.vcs.dto.UserDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Slf4j
class CsvHelper implements FileHelper {

    private CsvHelper() {
    }

    public static CsvHelper getInstance() {
        return CsvHelperHolder.INSTANCE;
    }

    @Override
    public List<UserDto> read(InputStream inputStream) throws IOException, CsvException {
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        try (CSVReader csvReader = new CSVReader(reader)) {
            csvReader.readNext();

            List<String[]> strings = csvReader.readAll();
            List<UserDto> dtos = new ArrayList<>();

            for (String[] string : strings) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                LocalDateTime localDateTime;
                Instant instant;
                try {
                    localDateTime = LocalDateTime.parse(string[6], formatter);
                    instant = localDateTime.atZone(ZoneId.of("UTC")).toInstant();
                } catch (Exception e) {
                    log.error("Invalid date format: {}", string[6]);
                    instant = null;
                }

                UserDto dto = UserDto.builder()
                        .username(string[0])
                        .password(string[1])
                        .email(string[2])
                        .phone(string[3])
                        .address(string[4])
                        .role(string[5])
                        .createdDate(instant)
                        .build();
                dtos.add(dto);
            }
            return dtos;
        }
    }

    @Override
    public void write(List<String[]> list, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(INVALID_HEADER);
            writer.writeAll(list);
        }
    }

    private static class CsvHelperHolder {
        private static final CsvHelper INSTANCE = new CsvHelper();
    }

}
