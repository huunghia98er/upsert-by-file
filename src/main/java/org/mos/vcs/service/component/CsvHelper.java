package org.mos.vcs.service.component;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.mos.vcs.dto.UserDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Slf4j
public class CsvHelper implements FileHelper {
    static final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<UserDto> read(InputStream inputStream) throws IOException, CsvException {
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        try (CSVReader csvReader = new CSVReader(reader)) {
            csvReader.readNext();

            List<String[]> strings = csvReader.readAll();
            List<UserDto> dtos = new ArrayList<>();

            for (String[] string : strings) {
                log.info("Row {}", Arrays.toString(string));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                log.info("Date {}", string[6]);

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

}
