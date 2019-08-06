package com.grgbanking.grgacd.queue;

/**
 * 排队策略
 * @author tjshan
 * @since 2019/5/25 10:48
 */
public enum QueueStrategy {

    /**
     *排队策略-闲时优先
     */
    IDLE_FIRST("longest-idle-agent","闲时优先"),
    /**
     * 排队策略-最少服务时间
     */
    SERVICE_LAST_FIRST("agent-with-least-talk-time","最少服务时间");

//    Strategy:策略模式，定义来电根据哪些策略模式查找队列中的坐席.
//    Ring-all：同时呼叫所有的坐席，某个接听后其它坐席自动挂断.
//    Longest-idle-agent：最大空闲坐席优先。
//    Round-robin：最后一次坐席通话的优先.
//    Top-down：从上倒下查找坐席
//    agent-with-least-talk-time：最少通话时间的坐席优先
//    agent-with-fewest-calls：最少通话次数的坐席优先
//    sequentially-by-agent-order：按照规则和等级依次查找坐席 (Level：值越小等级越高 Position：值越小地位就越高)
//    random：随机


    private  String type;
    private String label;


    QueueStrategy(String code, String label) {
        this.type = code;
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }
}
