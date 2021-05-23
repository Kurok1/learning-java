package indi.kurok1.gateway.route;

import java.util.regex.Pattern;

/**
 * 路径匹配规则
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.23
 */
public class MatchRule {

    private long id;
    private String matchRuleValue;
    private String type = Type.DEFAULT;
    private String serviceName;
    private int order;//排序，数字越小优先级越高

    public String getMatchRuleValue() {
        return matchRuleValue;
    }

    public void setMatchRuleValue(String matchRuleValue) {
        this.matchRuleValue = matchRuleValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        if (order < 0)
            order = 0;
        this.order = order;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * 判断一个uri是否匹配
     * @param uri
     * @return 是否匹配成功
     */
    public boolean matched(String uri) {
        switch (type) {
            case Type.LIKE:return uri.contains(matchRuleValue);
            case Type.REGEX: {
                return Pattern.matches(matchRuleValue, uri);
            }
            case Type.EQUALS:
            default: return uri.equals(matchRuleValue);
        }
    }


    public static class Type {
        public static final String LIKE = "LIKE";//包含
        public static final String REGEX = "REGEX";//正则
        public static final String EQUALS = "EQUALS";//完全相等
        public static final String DEFAULT = EQUALS;//默认=完全相等
    }
}
