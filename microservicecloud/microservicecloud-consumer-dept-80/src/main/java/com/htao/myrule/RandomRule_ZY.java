package com.htao.myrule;

import java.util.List;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

/**
* @author  HTao
* @version 创建时间：2021年5月11日 下午5:10:31
* 类说明
*/
public class RandomRule_ZY extends AbstractLoadBalancerRule {
	
	// total = 0 // 当total==5以后，我们指针才能往下走，
	// index = 0 // 当前对外提供服务的服务器地址，
	// total需要重新置为零，但是已经达到过一个5次，我们的index = 1
	// 分析：我们5次，但是微服务只有8001 8002 8003 三台，OK？
	// 
	
	private int total=0; //当前服务轮询到的次数
	private int currentIndex =0; //当前提供服务的机器号
   
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;

        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> upList = lb.getReachableServers(); //获取所有可响应机器
            List<Server> allList = lb.getAllServers(); //获取所有机器

            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }
            
//            int index = rand.nextInt(serverCount);
//            server = upList.get(index);
            
            if (total<5) {
				server = upList.get(currentIndex);
				total++;
			}else {
				total=0;
				currentIndex++;
				if (currentIndex>=upList.size()) { //当轮询到最后一台机器时指针归零
					currentIndex=0;
				}
			}

            if (server == null) {  //如果轮询到的机器没能响应，则将线程挂起，重新进入下一次循环
                /*
                 * The only time this should happen is if the server list were
                 * somehow trimmed. This is a transient condition. Retry after
                 * yielding.
                 */
                Thread.yield(); //使线程处于就绪状态，等待下一次时间碎片
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }

            // Shouldn't actually happen.. but must be transient or a bug.
            server = null;
            Thread.yield();
        }

        return server;

    }

	@Override
	public Server choose(Object key) {
		return choose(getLoadBalancer(), key);
	}

	@Override
	public void initWithNiwsConfig(IClientConfig clientConfig) {
		// TODO Auto-generated method stub
		
	}
}