package com.bite.friend.service.exam.impl;

import com.bite.common.core.constans.Constants;
import com.bite.common.core.domain.TableDataInfo;
import com.bite.common.core.utils.ThreadLocalUtil;
import com.bite.friend.domain.exam.dto.ExamQueryDTO;
import com.bite.friend.domain.exam.vo.ExamVO;
import com.bite.friend.manager.ExamCacheManager;
import com.bite.friend.mapper.exam.ExamMapper;
import com.bite.friend.service.exam.IExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ConcurrentModificationException;
import java.util.List;


@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamCacheManager examCacheManager;



    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        Long total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        List<ExamVO> examVOList;
        if (total == null || total <= 0){
            examVOList = list(examQueryDTO);
            examCacheManager.refreshCache(examQueryDTO.getType(),null);
            total = new PageInfo<>(examVOList).getTotal();
        }else {
            examVOList = examCacheManager.getExamVOList(examQueryDTO,null);
            total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        }
        if (CollectionUtils.isEmpty(examVOList)){
            return TableDataInfo.empty();
        }
        assembleExamVOList(examVOList);
        return TableDataInfo.success(examVOList,total);
    }

    private void assembleExamVOList(List<ExamVO> examVOList) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        List<Long> userExamIdList = examCacheManager.getAllUserExamList(userId);
        if (CollectionUtils.isEmpty(userExamIdList)){
            return;
        }
        for(ExamVO examVO : examVOList){
            if (userExamIdList.contains(examVO.getExamId())){
                examVO.setEnter(true);
            }
        }
    }

}
