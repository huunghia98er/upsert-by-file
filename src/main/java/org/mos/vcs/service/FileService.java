package org.mos.vcs.service;

import com.opencsv.exceptions.CsvException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.modelmapper.ModelMapper;
import org.mos.vcs.dto.ImportResponse;
import org.mos.vcs.dto.InvalidUser;
import org.mos.vcs.dto.UserDto;
import org.mos.vcs.entity.User;
import org.mos.vcs.repository.UserRepository;
import org.mos.vcs.service.component.FileHelper;
import org.mos.vcs.service.component.FileHelperFactory;
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
@Transactional
@RequiredArgsConstructor
public class FileService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final Validator validator;
    @PersistenceContext
    private final EntityManager entityManager;
    private final StorageEngineService storageEngineService;

    public ImportResponse uploadFile(MultipartFile file) throws IOException, CsvException {
        FileHelper fileHelper = FileHelperFactory.getFileHelper(Objects.requireNonNull(file.getContentType()));

        List<UserDto> dtos = fileHelper.read(file.getInputStream());

        List<String[]> invalidUsers = new ArrayList<>();
        Map<String, User> map = new HashMap<>();

        this.process(map, dtos, invalidUsers);

        String fileName = this.saveFile(fileHelper, invalidUsers);

        List<User> users = new ArrayList<>(map.values());

        if (!users.isEmpty()) {
            batchUpsertUsers(users);
        }

        return ImportResponse.builder()
                .successCount(users.size())
                .failCount(invalidUsers.size())
                .fileName(fileName)
                .build();
    }

    protected void batchUpsertUsers(List<User> users) {
        Session session = entityManager.unwrap(Session.class);

        try (session) {
            final int batchSize = 200;
            for (int i = 0; i < users.size(); i++) {
                session.merge(users.get(i));

                if ((i + 1) % batchSize == 0) {
                    log.info("Flush a batch of INSERT & release memory: {} time(s)", (i + 1) / batchSize);
                    session.flush();
                    session.clear();
                }
            }

            session.flush();
            session.clear();

            log.info("Flush the last time at commit time");

        } catch (Exception e) {
            log.error("Error during upsertUsers operation", e);
            throw e;
        }
    }

    private void process(Map<String, User> map, List<UserDto> dtos, List<String[]> invalidUsers) {
        Map<String, User> existingUserMap = userRepository.findByUsernameInAndEmailInAndPhoneIn(
                        dtos.stream().map(UserDto::getUsername).toList(),
                        dtos.stream().map(UserDto::getEmail).toList(),
                        dtos.stream().map(UserDto::getPhone).toList()
                )
                .stream()
                .collect(
                        Collectors.toMap(
                                u -> u.getUsername() + u.getEmail() + u.getPhone(),
                                u -> u)
                );

        for (UserDto dto : dtos) {
            String userKey = dto.getUsername() + dto.getEmail() + dto.getPhone();

            if (map.containsKey(userKey)) {
                log.warn("Duplicate record: {}", dto);
                InvalidUser invalidUser = modelMapper.map(dto, InvalidUser.class);
                String errorMessages = "Duplicate record";

                invalidUser.setError(errorMessages);
                invalidUsers.add(invalidUser.toStringArray());

                InvalidUser existUser = modelMapper.map(map.get(userKey), InvalidUser.class);
                existUser.setError(errorMessages);
                invalidUsers.add(existUser.toStringArray());
                continue;
            }

            Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

            if (violations.isEmpty()) {
                User existingUser = existingUserMap.get(userKey);

                User user = Optional.ofNullable(existingUser)
                        .map(u -> setNewData(dto, existingUser))
                        .orElseGet(() -> modelMapper.map(dto, User.class));

                map.put(userKey, user);
            } else {
                InvalidUser invalidUser = modelMapper.map(dto, InvalidUser.class);
                String errorMessages = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("\n"));

                invalidUser.setError(errorMessages);
                invalidUsers.add(invalidUser.toStringArray());
            }
        }
    }

    private String saveFile(FileHelper fileHelper, List<String[]> invalidUsers) {
        if (invalidUsers.isEmpty())
            return null;

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
        String fileName = "invalid_users_" + time;
        String filePath = "./data/" + fileName + ".csv";
        try {
            fileHelper.write(invalidUsers, filePath);
        } catch (IOException e) {
            log.warn("Error while writing file", e);
            return null;
        }
        storageEngineService.uploadFile(filePath, fileName, "text/csv");
        return fileName;
    }

    private User setNewData(UserDto dto, User user) {
        user.setPassword(dto.getPassword());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole());
        user.setCreatedDate(dto.getCreatedDate());
        return user;
    }

}
