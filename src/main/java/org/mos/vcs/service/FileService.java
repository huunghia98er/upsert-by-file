package org.mos.vcs.service;

import com.opencsv.exceptions.CsvException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.mos.vcs.dto.ImportResponse;
import org.mos.vcs.dto.InvalidUser;
import org.mos.vcs.dto.UserDto;
import org.mos.vcs.entity.User;
import org.mos.vcs.repository.UserRepository;
import org.mos.vcs.service.component.CsvHelper;
import org.mos.vcs.service.component.FileHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final Validator validator;
    private final StorageEngineService storageEngineService;

    public ImportResponse uploadFile(MultipartFile file) throws IOException, CsvException {
        FileHelper fileHelper = new CsvHelper();
        List<UserDto> dtos = fileHelper.read(file.getInputStream());

        List<User> users = new ArrayList<>();
        List<String[]> invalidUsers = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();

        for (UserDto dto : dtos) {
            if (map.containsKey(dto.getUsername() + dto.getEmail())) {
                log.warn("Duplicate record: {}", dto);
                users.remove(modelMapper.map(dto, User.class));
                continue;
            }
            map.put(dto.getUsername() + dto.getEmail(), 1);

            Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
            if (violations.isEmpty()) {
                users.add(modelMapper.map(dto, User.class));
            } else {
                InvalidUser invalidUser = modelMapper.map(dto, InvalidUser.class);
                String errorMessages = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("\n"));

                invalidUser.setError(errorMessages);
                invalidUsers.add(invalidUser.toStringArray());
            }
        }

        String fileName = null;
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
        if (!invalidUsers.isEmpty()) {
            fileName = "invalid_users_" + time;
            String filePath = "./data/" + fileName + ".csv";
            fileHelper.write(invalidUsers, filePath);
            storageEngineService.uploadFile(filePath, fileName, "text/csv");
        }

        userRepository.saveAllAndFlush(users);

        return ImportResponse.builder()
                .successCount(users.size())
                .failCount(invalidUsers.size())
                .fileName(fileName)
                .build();
    }
}
