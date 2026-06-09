package com.bite.job.service;

import com.bite.job.domain.message.Message;
import com.bite.job.domain.message.MessageText;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IMessageService {

    boolean batchInsert(List<Message> messageList);

}
