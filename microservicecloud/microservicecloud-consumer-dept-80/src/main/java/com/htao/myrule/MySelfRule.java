package com.htao.myrule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RetryRule;

/**
* @author  HTao
* @version 创建时间：2021年5月11日 下午3:09:23
* 类说明
*/
@Configuration
public class MySelfRule {
	
	@Bean
	public IRule myRule() { // return new RoundRobinRule(); //
//		return new RandomRule();// Ribbon默认是轮询，用我们重新选择的随机算法替代默认的轮询。
//		return new RetryRule();
		return new RandomRule_ZY(); //自定义服务均衡模式
	}
}
