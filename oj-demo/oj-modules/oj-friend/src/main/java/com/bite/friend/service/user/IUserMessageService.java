package com.bite.friend.service.user;

import com.bite.common.core.domain.PageQueryDTO;
import com.bite.common.core.domain.TableDataInfo;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
public interface IUserMessageService {
    TableDataInfo list(PageQueryDTO dto);
}
