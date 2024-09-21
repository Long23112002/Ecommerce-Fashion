package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.dtos.request.ChangePasswordRequest;
import org.example.ecommercefashion.dtos.request.OptCodeRequest;
import org.example.ecommercefashion.dtos.request.UserRequest;
import org.example.ecommercefashion.dtos.request.UserRoleAssignRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.RoleResponse;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Role;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.EmailService;
import org.example.ecommercefashion.services.OTPService;
import org.example.ecommercefashion.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final EntityManager entityManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OTPService otpService;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest userRequest) throws Exception {
        User user = userRepository.findUserByEmailOrPhoneNumber(userRequest.getEmail(), userRequest.getPhoneNumber());
        if (user != null) {
            throw new ExceptionHandle(HttpStatus.FOUND, ErrorMessage.USER_EXISTED);
        } else {
            emailService.sendingOtpWithEmail(userRequest.getEmail());

            user = new User();
            if (userRequest.getAvatar() == null) {
                user.setAvatar(avatarDefault());
            }
            FnCommon.copyProperties(user, userRequest);
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            user = userRepository.save(user);

            return mapEntityToResponse(user);
        }
    }

    @Override
    public void signUp(UserRequest userRequest) throws Exception {
        User user = userRepository.findByEmail(userRequest.getEmail());
        if (user != null) {
            throw new ExceptionHandle(HttpStatus.FOUND, ErrorMessage.USER_EXISTED);
        } else {
            // gửi email chứa otp
            emailService.sendingOtpWithEmail(userRequest.getEmail());
            log.info("gửi email chứa otp");

            // lưu tạm info user vào 1 hashmap
            user = new User();
            if (userRequest.getAvatar() == null) {
                user.setAvatar(avatarDefault());
            }
            FnCommon.copyProperties(user, userRequest);
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            otpService.storeTemporaryUser(user);
        }
    }

    @Override
    public String verifyUser(OptCodeRequest request) {
//        boolean verifyCode = otpService.verifyOTP(request);
//        Optional<String> otp = otpService.getOtp(request.getEmailUser());
//        if (otp == null ) {
//            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.OTP_IS_EXPIRED);
//        }
//        User user = otpService.getTemporaryUser(request.getEmailUser());
//        userRepository.save(user);
//
//        return mapEntityToResponse(user);

        Optional<String> cachedEmail = otpService.getOtp(request.getEmailUser());

        if (cachedEmail.isPresent() && cachedEmail.get().equals(request.getOtpCode())) {
            log.info("OTP_FOUND_REMOVING_THE_OTP", cachedEmail.get());
            otpService.deleteOtp(request.getEmailUser());
            return "save success";

        } else if (cachedEmail.isPresent() && !cachedEmail.get().equals(request.getEmailUser())) {
            return ("Otp expired");
        } else {
            return ("Otp invalid");
        }
        // save user into database
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = entityManager.find(User.class, id);
        if (userRequest.getAvatar() == null) {
            user.setAvatar(avatarDefault());
        }
        if (user == null) {
            return null;
        }
        FnCommon.copyProperties(user, userRequest);
        entityManager.merge(user);
        return mapEntityToResponse(user);
    }

    @Override
    @Transactional
    public MessageResponse deleteUser(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            user.setDeleted(true);
            entityManager.merge(user);
        }
        return MessageResponse.builder().message("User deleted successfully").build();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            return null;
        }
        return mapEntityToResponse(user);
    }

    @Transactional
    public MessageResponse assignRoleAdmin(String email) {
        User user =
                Optional.ofNullable(userRepository.findByEmail(email))
                        .orElseThrow(
                                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));

        user.setIsAdmin(true);
        entityManager.merge(user);

        return MessageResponse.builder().message("Role assigned successfully").build();
    }

    @Override
    public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        User user =
                Optional.ofNullable(userRepository.findByEmail(changePasswordRequest.getEmail()))
                        .orElseThrow(
                                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
        String currentPassword = user.getPassword();
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), currentPassword)) {
            throw new ExceptionHandle(
                    HttpStatus.BAD_REQUEST, ErrorMessage.CURRENT_PASSWORD_SAME_NEW_PASSWORD.val());
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        entityManager.merge(user);
        return MessageResponse.builder().message("Password changed successfully").build();
    }

    @Override
    public ResponsePage<User, UserResponse> getAllUsers(UserParam userParam, Pageable pageable) {
        Page<User> userPage = userRepository.filterUser(userParam, pageable);
        return new ResponsePage<>(userPage, UserResponse.class);
    }

    @Override
    @Transactional
    public MessageResponse assignUserRole(UserRoleAssignRequest userRoleAssignRequest) {
        User user =
                Optional.ofNullable(userRepository.findByEmail(userRoleAssignRequest.getEmail()))
                        .orElseThrow(
                                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));

        for (Long roleId : userRoleAssignRequest.getRoleIds()) {
            Role role =
                    Optional.ofNullable(entityManager.find(Role.class, roleId))
                            .orElseThrow(
                                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ROLE_NOT_FOUND));
            user.getRoles().add(role);
        }

        entityManager.merge(user);
        return MessageResponse.builder().message("Role assigned successfully").build();
    }

    private UserResponse mapEntityToResponse(User user) {
        UserResponse userResponse = new UserResponse();
        FnCommon.copyProperties(userResponse, user);
        if (user.getRoles() != null) {
            Set<RoleResponse> roleResponses =
                    user.getRoles().stream().map(this::mapEntityToResponse).collect(Collectors.toSet());
            userResponse.setRoles(roleResponses);
        } else {
            userResponse.setRoles(Collections.emptySet());
        }

        return userResponse;
    }

    private RoleResponse mapEntityToResponse(Role role) {
        RoleResponse roleResponse = new RoleResponse();
        FnCommon.copyProperties(roleResponse, role);
        return roleResponse;
    }

    private String avatarDefault() {
        return "https://scontent.fhan18-1.fna.fbcdn.net/v/t1.30497-1/453178253_471506465671661_2781666950760530985_n.png?stp=dst-png_s200x200&_nc_cat=1&ccb=1-7&_nc_sid=136b72&_nc_eui2=AeGpt-IzdO8nSbIthaK0yMISWt9TLzuBU1Ba31MvO4FTULwl6agze3fL9zZt1hbXkxGnZ0S8ZnZYCACyZt-MJXrQ&_nc_ohc=VVXDQ2ftWTsQ7kNvgFsi6op&_nc_ht=scontent.fhan18-1.fna&oh=00_AYD57d7dbnmi8QDkVFuJasFjTrN7RyXY3KZlU7_wIHXELA&oe=67008E3A";
    }
}
