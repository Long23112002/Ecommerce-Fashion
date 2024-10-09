package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
import org.example.ecommercefashion.dtos.response.NotificationResponse;
import org.example.ecommercefashion.entities.Notification;
import org.example.ecommercefashion.services.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Api(tags = "Notification", value = "Endpoints for notification management")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{id}")
    public LoadMoreResponse<NotificationResponse> findAllChatByIdChatRoom(
            @PathVariable("id") Long id,
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "limit", defaultValue = "15") Integer limit
    ) {
        return notificationService.findAllNotificationsByUserId(id, offset, limit);
    }

}
