package org.example.ecommercefashion.services;

import org.example.ecommercefashion.entities.Email;

public interface EmailService {
    Email createEmail(String to, String subject, String text);
}
