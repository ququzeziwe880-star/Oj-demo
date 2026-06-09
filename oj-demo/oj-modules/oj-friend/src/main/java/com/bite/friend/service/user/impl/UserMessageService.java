package com.bite.friend.service.user.impl;

import com.bite.common.core.constans.Constants;
import com.bite.common.core.domain.PageQueryDTO;
import com.bite.common.core.domain.TableDataInfo;
import com.bite.common.core.enums.ExamListType;
import com.bite.common.core.utils.ThreadLocalUtil;
import com.bite.friend.domain.exam.vo.ExamVO;
import com.bite.friend.domain.message.vo.MessageTextVO;
import com.bite.friend.manager.MessageCacheManager;
import com.bite.friend.mapper.message.MessageTextMapper;
import com.bite.friend.service.user.IUserMessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class UserMessageService implements IUserMessageService {

    @Autowired
    private MessageCacheManager messageCacheManager;

    @Autowired
    private MessageTextMapper messageTextMapper;
    @Override
    public TableDataInfo list(PageQueryDTO dto) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID,Long.class);
        Long total = messageCacheManager.getListSize(userId);
        List<MessageTextVO> messageTextVOList;
        if (total == null || total <= 0){
            PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
            messageTextVOList = messageTextMapper.selectUserMessageList(userId);
            messageCacheManager.refreshCache(userId);
            total = new PageInfo<>(messageTextVOList).getTotal();
        }else {
            messageTextVOList = messageCacheManager.getMessageTextVOList(dto,userId);
        }
        if (CollectionUtils.isEmpty(messageTextVOList)){
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(messageTextVOList,total);
    }
}
