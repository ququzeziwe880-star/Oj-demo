package com.bite.job.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bite.job.domain.message.MessageText;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IMessageTextService {

    boolean batchInsert(List<MessageText> messageTextList);
}
