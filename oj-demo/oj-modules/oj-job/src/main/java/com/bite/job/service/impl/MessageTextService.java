package com.bite.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bite.job.domain.message.MessageText;
import com.bite.job.mapper.message.MessageTextMapper;
import com.bite.job.service.IMessageTextService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageTextService extends ServiceImpl<MessageTextMapper,MessageText> implements IMessageTextService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInsert(List<MessageText> messageTextList){
        return saveBatch(messageTextList);
    }
}
