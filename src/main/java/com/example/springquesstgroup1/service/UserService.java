package com.example.springquesstgroup1.service;

import com.example.springquesstgroup1.UserStatus;
import com.example.springquesstgroup1.dto.*;
import com.example.springquesstgroup1.entity.*;
import com.example.springquesstgroup1.repository.RoomRepository;
import com.example.springquesstgroup1.repository.UserRepository;
import com.example.springquesstgroup1.repository.UserRoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public boolean init(InitRequest initRequest) {
        userRoomRepository.deleteAll();
        userRepository.deleteAll();
        roomRepository.deleteAll();

        resetIdSequence("user");
        resetIdSequence("user_room");
        resetIdSequence("room");

        int seed = initRequest.getSeed();
        int quantity = initRequest.getQuantity();

        // faker api 호출
        String initURL = "https://fakerapi.it/api/v1/users" + "?_seed=" + seed + "&_quantity=" + quantity + "&_locale=ko_KR";
        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<FakerApiResponse<List<FakerApiResponseData>>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<FakerApiResponse<List<FakerApiResponseData>>> responseEntity = restTemplate.exchange(initURL, HttpMethod.GET, null, responseType);

        List<FakerApiResponseData> data = new ArrayList<>();
        try {
            for (int i = 0; i < Objects.requireNonNull(responseEntity.getBody()).getTotal(); i++) {
                data.add(Objects.requireNonNull(responseEntity.getBody()).getData().get(i));
            }
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

                // 저장
                userRepository.save(user);
            }
        } else return false;
        return true;
    }

    // id 초기화
    private void resetIdSequence(String tableName) {
        String sql = "ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1";
        entityManager.createNativeQuery(sql).executeUpdate();
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
