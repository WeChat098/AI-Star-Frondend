package com.edu.yudada.scoring;

import com.edu.yudada.common.ErrorCode;
import com.edu.yudada.exception.BusinessException;
import com.edu.yudada.model.entity.App;
import com.edu.yudada.model.entity.UserAnswer;
import com.edu.yudada.model.enums.AppScoringStrategyEnum;
import com.edu.yudada.model.enums.AppTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Deprecated
public class ScoringStrategyContext {

    @Resource
    private CustomScoreScoringStrategy customScoreScoringStrategy;

    @Resource
    private CustomTestScoringStrategy customTestScoringStrategy;

    /**
     * 评分
     *
     * @param choiceList
     * @param app
     * @return
     * @throws Exception
     */
    public UserAnswer doScore(List<String> choiceList, App app) throws Exception { //提供一个应用程序，已经用户选择的答案，之后进行评分
        AppTypeEnum appTypeEnum = AppTypeEnum.getEnumByValue(app.getAppType());// 首先根据输出的app，获取到当前的app类型
        // 看一下现在是得分类的应用，还是测评类的应用，然后使用枚举类方法获取得到type，对应的就是0和1
        AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(app.getScoringStrategy());
        // 查看当前app的评分策略是什么
        // 如果当前app的评分策略和类型没有出现过 那么就直接报错
        if (appTypeEnum == null || appScoringStrategyEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }
        // 根据不同的应用类别和评分策略，选择对应的策略执行
        switch (appTypeEnum) { // 根据不同的应用类别和评分策略，选择不同的判题逻辑
            case SCORE:
                switch (appScoringStrategyEnum) {
                    case CUSTOM:
                        return customScoreScoringStrategy.doScore(choiceList, app);
                    case AI:
                        break;
                }
                break;
            case TEST:
                switch (appScoringStrategyEnum) {
                    case CUSTOM:
                        return customTestScoringStrategy.doScore(choiceList, app);
                    case AI:
                        break;
                }
                break;
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
