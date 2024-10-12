package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
import org.example.ecommercefashion.dtos.response.NotificationResponse;
import org.example.ecommercefashion.services.NotificationService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
