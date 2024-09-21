package org.example.ecommercefashion.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.entities.Email;
import org.example.ecommercefashion.entities.ProcessSend;
import org.example.ecommercefashion.repositories.ProcessSendRepository;
import org.example.ecommercefashion.services.ProcessService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {
    private final ProcessSendRepository processSendRepository;
    @Override
    public ProcessSend initializeProcess(Email email) {
        ProcessSend processSend = new ProcessSend();
        processSend.setCountFailed(0L);
        processSend.setCountSent(0L);
        processSend.setEmail(email);

        return processSendRepository.save(processSend);
    }
}
