package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
import org.example.ecommercefashion.dtos.response.NotificationResponse;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.NotificationService;
import org.example.ecommercefashion.services.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Api(tags = "Notification", value = "Endpoints for notification management")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/user/{id}")
    public LoadMoreResponse<NotificationResponse> findAllNotificationByIdUser(
            @PathVariable("id") Long id,
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "limit", defaultValue = "15") Integer limit
    ) {
        return notificationService.findAllNotificationsByUserId(id, offset, limit);
    }

    @GetMapping("/unseen/user/{id}")
    public LoadMoreResponse<NotificationResponse> findAllUnSeenNotificationByIdUser(
            @PathVariable("id") Long id,
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "limit", defaultValue = "15") Integer limit
    ) {
        return notificationService.findAllUnSeenNotificationByIdUser(id, offset, limit);
    }

    @PatchMapping("/seen/user/{id}")
    public List<NotificationResponse> markSeenAllByIdUser(@RequestHeader("Authorization") String token,
                                                          @PathVariable("id") Long id) {
        return notificationService.markSeenAll(id, token);
    }

    @PatchMapping("/seen/{id}")
    public List<NotificationResponse> markSeenById(@RequestHeader("Authorization") String token,
                                                   @PathVariable("id") String id) {
        return notificationService.markSeenById(id, token);
    }

    @DeleteMapping("/{id}")
    public NotificationResponse deleteById(@RequestHeader("Authorization") String token,
                                           @PathVariable("id") String id) {
        return notificationService.deleteById(id, token);
    }

    @PostMapping("/test/sendAll")
    public void sendNotificationAll(@RequestHeader("Authorization") String token) {
        Long idUser = jwtService.decodeToken(token).getUserId();
        User user = userService.findUserOrDefault(idUser);
        notificationService.sendNotificationAll(user.getId(), NotificationCode.TEST, user.getFullName());
    }

    @PostMapping("/test/sendByPermission")
    public void sendNotificationToUsersWithPermission(@RequestHeader("Authorization") String token) {
        Long idUser = jwtService.decodeToken(token).getUserId();
        User user = userService.findUserOrDefault(idUser);
        notificationService.sendNotificationToUsersWithPermission(user.getId(), NotificationCode.TEST, user.getFullName());
    }

    @PostMapping("/test/sendByUsers")
    public void sendNotificationToUsers(@RequestHeader("Authorization") String token,
                                        @RequestParam(value = "ids", required = true) List<Long> ids) {
        Long idUser = jwtService.decodeToken(token).getUserId();
        User user = userService.findUserOrDefault(idUser);
        notificationService.sendNotificationToUsers(user.getId(), ids, NotificationCode.TEST, user.getFullName());
    }

    @PostMapping("/test/sendByUser")
    public void sendNotificationToUser(@RequestHeader("Authorization") String token,
                                       @RequestParam(name = "id", required = true) Long id) {
        Long idUser = jwtService.decodeToken(token).getUserId();
        User user = userService.findUserOrDefault(idUser);
        notificationService.sendNotificationToUser(user.getId(), id, NotificationCode.TEST, user.getFullName());
    }

}
