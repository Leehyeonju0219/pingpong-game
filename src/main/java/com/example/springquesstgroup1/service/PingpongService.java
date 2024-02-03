package com.example.springquesstgroup1.service;

import com.example.springquesstgroup1.UserStatus;
import com.example.springquesstgroup1.dto.FakerApiResponse;
import com.example.springquesstgroup1.dto.FakerApiResponseData;
import com.example.springquesstgroup1.dto.InitRequest;
import com.example.springquesstgroup1.dto.SelectAllUsersResponse;
import com.example.springquesstgroup1.entity.User;
import com.example.springquesstgroup1.entity.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PingpongService {

    private final UserRepository userRepository;

    @Autowired
    public PingpongService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean init(InitRequest initRequest) {
        int seed = initRequest.getSeed();
        int quantity = initRequest.getQuantity();

        // faker api 호출
        String initURL = "https://fakerapi.it/api/v1/users" + "?_seed=" + seed + "&_quantity=" + quantity + "&_locale=ko_KR";
        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<FakerApiResponse<List<FakerApiResponseData>>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<FakerApiResponse<List<FakerApiResponseData>>> responseEntity = restTemplate.exchange(initURL, HttpMethod.GET, null, responseType);

        List<FakerApiResponseData> data;
        try {
            data = Objects.requireNonNull(responseEntity.getBody()).getData();
        } catch (NullPointerException e) {
            data = null;
        }

        if (data != null) {
            for (FakerApiResponseData fakerApiResponseData : data) {
                // 규칙에 따라 데이터 가공 및 저장
                User user = new User();
                user.setFakerId(fakerApiResponseData.getId());
                user.setName(fakerApiResponseData.getUsername());
                user.setEmail(fakerApiResponseData.getEmail());

                // 상태 설정
                if (fakerApiResponseData.getId() <= 30) {
                    user.setStatus(UserStatus.ACTIVE);
                } else if (fakerApiResponseData.getId() <= 60) {
                    user.setStatus(UserStatus.WAIT);
                } else {
                    user.setStatus(UserStatus.NON_ACTIVE);
                }

                // created_at, updated_at 설정 (현재 시간 기준)
                LocalDateTime now = LocalDateTime.now();
                user.setCreatedAt(now);
                user.setUpdatedAt(now);

                System.out.println(user.getFakerId());

                // 저장
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public SelectAllUsersResponse selectAllUsers(int size, int page) {
        int totalElements = userRepository.findAll().size();
        int totalPages = (int) Math.ceil((double)totalElements/size);

        if (totalElements == 0) {
            return new SelectAllUsersResponse(totalElements, totalPages, new ArrayList<>());
        }

        List<User> userList = userRepository.findUsersByIdBetween(size*page+1, size*(page+1));
        return new SelectAllUsersResponse(totalElements, totalPages, userList);
    }
}
