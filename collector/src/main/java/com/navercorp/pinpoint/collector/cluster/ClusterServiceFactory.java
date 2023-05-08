/*
 * Copyright 2021 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.collector.cluster;

import com.navercorp.pinpoint.collector.cluster.zookeeper.ZookeeperClusterService;
import com.navercorp.pinpoint.collector.config.CollectorClusterProperties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Objects;

public class ClusterServiceFactory implements FactoryBean<ClusterService>, InitializingBean, DisposableBean {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private CollectorClusterProperties collectorClusterProperties;
    private ClusterPointRouter clusterPointRouter;

    private ClusterService clusterService;

    @Override
    public ClusterService getObject() throws Exception {
        return this.clusterService;
    }

    public void setClusterProperties(CollectorClusterProperties collectorClusterProperties) {
        this.collectorClusterProperties = Objects.requireNonNull(collectorClusterProperties, "collectorClusterProperties");
    }

    public void setClusterPointRouter(ClusterPointRouter clusterPointRouter) {
        this.clusterPointRouter = Objects.requireNonNull(clusterPointRouter, "clusterPointRouter");
    }


    @Override
    public void destroy() throws Exception {
        this.clusterService.tearDown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.clusterService = newClusterService();
        this.clusterService.setUp();
    }

    private ClusterService newClusterService() {
        if (collectorClusterProperties.isClusterEnable()) {
            return new ZookeeperClusterService(collectorClusterProperties, clusterPointRouter);
        }
        logger.info("pinpoint-collector cluster disable");
        return new DisableClusterService();
    }

    @Override
    public Class<?> getObjectType() {
        return ClusterService.class;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }


}
