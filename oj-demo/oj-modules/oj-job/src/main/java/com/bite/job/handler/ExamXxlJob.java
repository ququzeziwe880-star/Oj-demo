package com.bite.job.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bite.common.core.constans.CacheConstants;
import com.bite.common.core.constans.Constants;
import com.bite.common.core.enums.ExamListType;
import com.bite.common.redis.service.RedisService;
import com.bite.job.domain.exam.Exam;
import com.bite.job.domain.message.Message;
import com.bite.job.domain.message.MessageText;
import com.bite.job.domain.message.vo.MessageTextVO;
import com.bite.job.domain.user.UserScore;
import com.bite.job.mapper.exam.ExamMapper;
import com.bite.job.mapper.message.MessageMapper;
import com.bite.job.mapper.message.MessageTextMapper;
import com.bite.job.mapper.user.UserExamMapper;
import com.bite.job.mapper.user.UserSubmitMapper;
import com.bite.job.service.IMessageTextService;
import com.bite.job.service.impl.MessageService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ExamXxlJob {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IMessageTextService messageTextService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageTextMapper messageTextMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserExamMapper userExamMapper;
    @XxlJob("examListOrganizeHandler")
    public void examListOrganizeHandler(){
        // 统计哪些竞赛应该存入未完赛的列表，哪些该存储历史竞赛,统计出来之后，存入对应的缓存中
        log.info("***------examListOrganizeHandler");
        List<Exam> unFinishList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .gt(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByDesc(Exam::getCreateTime));
        refreshCache(unFinishList,CacheConstants.EXAM_UNFINISHED_LIST);


        List<Exam> histroyList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .le(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByDesc(Exam::getCreateTime));
        refreshCache(histroyList,CacheConstants.EXAM_HISTORY_LIST);

    }

    //查询前一天竞赛排名情况发送给客户
    @XxlJob("examResultHandler")
    public void examResultHandler(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusDatetime = now.minusDays(1);
        List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId,Exam::getTitle)
                .eq(Exam::getStatus, Constants.TRUE)
                .ge(Exam::getEndTime, minusDatetime)
                .le(Exam::getEndTime, now));
        if (CollectionUtil.isEmpty(examList)){
            return;
        }
        Set<Long> examIdSet = examList.stream().map(Exam::getExamId).collect(Collectors.toSet());
        List<UserScore> userScoreList = userSubmitMapper.selectUserScoreList(examIdSet);
        Map<Long, List<UserScore>> userScoreMap = userScoreList.stream().collect(Collectors.groupingBy(UserScore::getExamId));
        createMessage(examList,userScoreMap);
    }

    private void createMessage( List<Exam> examList, Map<Long, List<UserScore>> userScoreMap) {
        List<MessageText> messageTextList = new ArrayList<>();
        List<Message> messageList = new ArrayList<>();
        for(Exam exam : examList){
            List<UserScore> userScoreList = userScoreMap.get(exam.getExamId());
            int totalUser = userScoreList.size();
            int examRank = 1;
            for(UserScore userScore : userScoreList){
                String msgTitle = exam.getTitle() + "----排名情况";
                String msgContent = "您所参与的竞赛: " + exam.getTitle()
                        + ",本次参加竞赛一共" + totalUser + "人,你的排名是第" + examRank + "名";
                userScore.setExamRank(examRank);
                MessageText messageText = new MessageText();
                messageText.setMessageTitle(msgTitle);
                messageText.setMessageContent(msgContent);
                messageText.setCreateBy(Constants.SYSTEM_USER_ID);
                messageTextList.add(messageText);
                Message message = new Message();
                message.setSendId(Constants.SYSTEM_USER_ID);
                message.setCreateBy(Constants.SYSTEM_USER_ID);
                message.setRecId(userScore.getUserId());
                messageList.add(message);
                examRank++;
            }
            userExamMapper.updateUserScoreAndRank(userScoreList);
            redisService.rightPushAll(getExamRankListKey(exam.getExamId()),userScoreList);
        }
        messageTextService.batchInsert(messageTextList);
        Map<String,MessageTextVO> messageTextVOMap = new HashMap<>();
        for (int i = 0; i < messageList.size(); i++) {
            MessageText messageText = messageTextList.get(i);
            MessageTextVO messageTextVO = new MessageTextVO();
            BeanUtils.copyProperties(messageText,messageTextVO);
            String msgDetailKey = getMsgDetailKey(messageText.getTextId());
            messageTextVOMap.put(msgDetailKey,messageTextVO);
            Message message = messageList.get(i);
            message.setTextId(messageText.getTextId());
        }
        messageService.batchInsert(messageList);
        Map<Long, List<Message>> userMsgMap = messageList.stream().collect(Collectors.groupingBy(Message::getRecId));
        Iterator<Map.Entry<Long, List<Message>>> iterator = userMsgMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Long, List<Message>> entry = iterator.next();
            Long recId = entry.getKey();
            String userMsgListKey = getUserMsgListKey(recId);
            List<Long> userMsgTextIdList = entry.getValue().stream().map(Message::getTextId).collect(Collectors.toList());
            redisService.rightPushAll(userMsgListKey,userMsgTextIdList);
        }
        redisService.multiSet(messageTextVOMap);
    }


    //刷新缓存逻辑
    public void refreshCache(List<Exam> examList, String examListKey) {

        Map<String, Exam> examMap = new HashMap<>();
        List<Long> examIdList = new ArrayList<>();

        if (!CollectionUtil.isEmpty(examList)) {
            for (Exam exam : examList) {
                examMap.put(getDetailKey(exam.getExamId()), exam);
                examIdList.add(exam.getExamId());
            }
        }else{

        }

        redisService.multiSet(examMap);  //刷新详情缓存
        redisService.deleteObject(examListKey);
        redisService.rightPushAll(examListKey,examIdList);      //刷新列表缓存
    }

    private String getExamListKey(Integer examListType, Long userId) {
        if (ExamListType.EXAM_UN_FINISH_LIST.getValue().equals(examListType)) {
            return CacheConstants.EXAM_UNFINISHED_LIST;
        } else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
            return CacheConstants.EXAM_HISTORY_LIST;
        } else {
            return null;
        }
    }

    private String getDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL + examId;
    }

    private String getUserMsgListKey(Long userId){
        return CacheConstants.USER_MESSAGE_LIST + userId;
    }

    private String getMsgDetailKey(Long textId){
        return CacheConstants.MESSAGE_DETAIL + textId;
    }

    private String getExamRankListKey(Long examId){
        return CacheConstants.EXAM_RANK_LIST + examId;
    }
}
