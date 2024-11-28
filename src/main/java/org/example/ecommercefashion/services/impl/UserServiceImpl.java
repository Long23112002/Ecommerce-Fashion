package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.dtos.request.*;
import org.example.ecommercefashion.dtos.response.*;
import org.example.ecommercefashion.entities.EmailJob;
import org.example.ecommercefashion.entities.Role;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.RefreshTokenRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.CartService;
import org.example.ecommercefashion.services.OTPService;
import org.example.ecommercefashion.services.UserService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final EntityManager entityManager;

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final RefreshTokenRepository refreshTokenRepository;

  private final EmailJob emailJob;

  private final OTPService otpService;

  private final JwtService jwtService;

  @Autowired private RedisTemplate<String, String> redisTemplate;

  @Autowired private CartService cartService;

  @Override
  @Transactional
  public UserResponse createUser(UserRequest userRequest) throws JobExecutionException {
    // Lấy email từ request
    String email = userRequest.getEmail();

    String emailStatus = redisTemplate.opsForValue().get(email);

        if (!"done".equals(emailStatus)) {
          throw new ExceptionHandle(HttpStatus.BAD_REQUEST,
     ErrorMessage.EMAIL_NOT_VERIFIED.val());
        }

    validateEmail(userRequest.getEmail());
    validatePhone(userRequest.getPhoneNumber());

    // Tạo đối tượng User
    User user = new User();

    if (userRequest.getAvatar() == null) {
      user.setAvatar(avatarDefault());
    }

    FnCommon.copyProperties(user, userRequest);
    user.setIsVerified(true);
    user.setSlugEmail(userRequest.getEmail());
    user.setSlugFullName(userRequest.getFullName());
    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

    entityManager.persist(user);

    cartService.create(new CartRequest(user.getId(), new ArrayList<>()));

    return mapEntityToResponse(user);
  }

  @Override
  public void sendOtp(String email) throws JobExecutionException {
    boolean exists = userRepository.existsByEmailAndDeleted(email, false);

    if (exists) {
      throw new ExceptionHandle(
          HttpStatus.BAD_REQUEST, "Email " + email + " đã tồn tại trong hệ thống!");
    }
    sendEmailOtp(email);
  }

  @Override
  public List<User> findAllEntityUserByIds(Collection<Long> ids) {
    return userRepository.findAllById(ids);
  }

  @Override
  @Transactional
  public UserResponse updateUser(Long id, UserRequest userRequest) {
    User user = entityManager.find(User.class, id);
    if (!Objects.equals(user.getEmail(), userRequest.getEmail())) {
      validateEmail(userRequest.getEmail());
    }

    if (!Objects.equals(user.getPhoneNumber(), userRequest.getPhoneNumber())) {
      validatePhone(userRequest.getPhoneNumber());
    }

    if (userRequest.getAvatar() == null) {
      user.setAvatar(avatarDefault());
    }

    FnCommon.copyProperties(user, userRequest);
    user.setSlugEmail(userRequest.getEmail());
    user.setSlugFullName(userRequest.getFullName());
    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    entityManager.merge(user);
    return mapEntityToResponse(user);
  }

  @Override
  @Transactional
  public UserResponse updateUser(Long id, UserInfoUpdateRequest userUpdateRequest, String token) {
    User user = entityManager.find(User.class, id);
    if (user == null) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
    }
    if(!user.getId().equals(jwtService.getIdUserByToken(token))){
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_PERMISSION_DENIED);
    }
    if (userUpdateRequest.getAvatar() == null) {
      userUpdateRequest.setAvatar(user.getAvatar());
    }
    FnCommon.copyProperties(user, userUpdateRequest);
    user.setSlugFullName(userUpdateRequest.getFullName());
    entityManager.merge(user);
    return mapEntityToResponse(user);
  }

  @Override
  @Transactional
  public MessageResponse deleteUser(Long id) {

    User user = entityManager.find(User.class, id);

    if (user != null) {
      refreshTokenRepository.deleteByUserId(user.getId());
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

  @Override
  public User findUserOrDefault(Long id) {
    return userRepository
        .findById(id)
        .filter(entity -> !entity.getDeleted())
        .orElse(getDeletedUser());
  }

  @Override
  public User getDeletedUser() {
    return User.builder()
        .fullName("Tài khoản đã bị xóa")
        .avatar(avatarDefault())
        .deleted(true)
        .build();
  }

  @Override
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

  @Transactional
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

  @Override
  public void validEmail(OtpRequest otpRequest) {
    Optional<OtpResponse> otpResponse = otpService.getOtp(otpRequest.getEmail());
    if (otpResponse.isPresent()) {
      if (!otpResponse.get().getOtp().equals(otpRequest.getOtp())) {
        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.OTP_NOT_MATCH.val());
      }
    } else {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.OTP_EXPIRED.val());
    }
    otpService.deleteOtp(otpRequest.getEmail());
    String email = otpRequest.getEmail();
    redisTemplate.opsForValue().set(email, "done", 10, TimeUnit.MINUTES);
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

  private void validatePhone(String phoneNumber) {
    if (userRepository.existsByPhoneNumberAndDeleted(phoneNumber, false)) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PHONE_EXISTED.val());
    }
  }

  private void validateEmail(String email) {
    if (userRepository.existsByEmailAndDeleted(email, false)) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.EMAIL_EXISTED.val());
    }
  }

  private void sendEmailOtp(String email) throws JobExecutionException {
    emailJob.sendOtpEmail(email);
  }
}
