package org.mos.vcs.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.mos.vcs.dto.UserDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Configuration
public class MapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<Map<String, String>, UserDto> myConverter = context -> {
            Map<String, String> s = context.getSource();
            UserDto d = context.getDestination();
            d.setUsername(s.get("Tên"));
            d.setPassword(s.get("Mật khẩu"));
            d.setEmail(s.get("Email"));
            d.setPhone(s.get("Số điện thoại"));
            d.setAddress(s.get("Địa chỉ"));
            d.setRole(s.get("Quyền"));
            return d;
        };

        mapper.addConverter(myConverter);
        return mapper;
    }
}
