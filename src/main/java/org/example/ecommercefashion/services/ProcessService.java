package org.example.ecommercefashion.services;
import org.example.ecommercefashion.entities.Email;
import org.example.ecommercefashion.entities.ProcessSend;
public interface ProcessService {
    ProcessSend initializeProcess(Email email);
}
