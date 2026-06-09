package com.bite.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bite.job.domain.message.Message;
import com.bite.job.domain.message.MessageText;
import com.bite.job.mapper.message.MessageMapper;
import org.springframework.stereotype.Service;
import com.bite.job.service.IMessageService;

import java.util.List;

@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> implements IMessageService{

    @Override
    public boolean batchInsert(List<Message> messageList) {
        return saveBatch(messageList);
    }
}
