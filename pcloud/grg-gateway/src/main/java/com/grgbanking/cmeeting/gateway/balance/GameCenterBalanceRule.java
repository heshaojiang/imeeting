package com.grgbanking.cmeeting.gateway.balance;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang.math.RandomUtils;

import java.util.List;

/**
 * @author hsjiang
 * @version 1.0
 * @Description: TODO
 * @date 2019/7/29/029
 **/
public class GameCenterBalanceRule  extends AbstractLoadBalancerRule {

    @Override
    public Server choose(Object key) {//这里的key就是过滤器中传过来的userId
        List<Server> allServers = this.getLoadBalancer().getAllServers();
        List<Server> servers = this.getLoadBalancer().getReachableServers();
        if (servers.isEmpty()) {
            return null;
        }
        if (servers.size() == 1) {
            return servers.get(0);
        }
        if (key == null) {
            return randomChoose(servers);
        }
        return hashKeyChoose(servers, key);
    }
    /**
     *
     * <p>Description:随机返回一个服务实例 </p>
     * @param servers
     * @return
     * @author wgs
     * @date  2019年3月15日 下午2:25:23
     *
     */
    private Server randomChoose(List<Server> servers) {
        int randomIndex = RandomUtils.nextInt(servers.size());
        return servers.get(randomIndex);
    }
    /**
     *
     * <p>Description:使用key的hash值，和服务实例数量求余，选择一个服务实例 </p>
     * @param servers
     * @param key
     * @return
     * @author wgs
     * @date  2019年3月15日 下午2:25:36
     *
     */
    private Server hashKeyChoose(List<Server> servers, Object key) {
        int hashCode = Math.abs(key.hashCode());
        if (hashCode < servers.size()) {
            return servers.get(hashCode);
        }
        int index = hashCode % servers.size();
        return servers.get(index);

    }

    @Override
    public void initWithNiwsConfig(IClientConfig config) {

    }

}
