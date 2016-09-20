/*
 * Copyright 2016 OPEN TONE Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.opentone.bsol.linkbinder;


import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.log.LogbackConfigurationLoader;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.subscriber.RedisSubscriber;
import jp.co.opentone.bsol.linkbinder.subscriber.Subscriber;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashSet;
import java.util.Map;

/**
 * アプリケーションのエントリーポイント.
 * Created by OPEN TONE on 2015/07/08.
 */
public class Application implements AutoCloseable, Runnable {

    /** アプリケーションコンテキスト. */
    private ApplicationContext context;
    /** Redis subscriber. */
    private RedisSubscriber redisSubscriber;
    /** メイン処理を行うオブジェクト. */
    private Map<String, Subscriber> subscribers;

    /** コマンド引数解析結果. */
    private boolean validArguments;
    /**
     * リソースディレクトリ.
     * jar外部のjdbc.properties,logback.{xml|groovy}を使用する場合にその配置場所を指定する.
     */
    @Option(name="-r", aliases="--resource-dir", metaVar="/path/to/resources", usage="リソースファイルディレクトリ。\njdbc.propertiesやlogback.{xml|groovy}を配置したフォルダのフルパスを指定")
    private String resourceDir = "/tmp";

    /**
     * コマンドライン引数を解析し結果を {@link #validArguments} に格納する.
     * @param args コマンドライン引数
     */
    private void parseArguments(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            initSystemProperties();
            validArguments = true;
        } catch (CmdLineException e) {
            parser.printUsage(System.err);
            System.err.println("  Example: java -jar linkbinder-subscriber-x.x.x.war"+parser.printExample(ExampleMode.ALL));
            validArguments = false;
        }
    }

    /**
     * リソースディレクトリをシステムプロパティに設定する.
     */
    private void initSystemProperties() {
        // for jdbc.properties
        System.setProperty("LB_RESOURCE_DIR", resourceDir);
        // for logback.xml
        System.setProperty(
                LogbackConfigurationLoader.OPT_LOGBACK_DIR, resourceDir);
    }

    /**
     * アプリケーションを初期化する.
     * @param args コマンドライン引数
     */
    public Application(String[] args) {
        parseArguments(args);
        if (!validArguments) {
            return;
        }

        context = createApplicationContext();
        setupProcessContext();

        redisSubscriber = context.getBean(RedisSubscriber.class);
        subscribers = context.getBeansOfType(Subscriber.class);
        redisSubscriber.setSubscribers(new HashSet<>(subscribers.values()));
    }

    private ApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
                "spring/aop.xml",
                "spring/dao.xml",
                "spring/applicationContext.xml");
    }

    private User setupUser() {
        User user = context.getBean(User.class);
        return user;
    }

    private void setupProcessContext() {
        ProcessContext pc = ProcessContext.getCurrentContext();
        User user = setupUser();

        pc.setValue(SystemConfig.KEY_USER_ID, user.getUserId());
        pc.setValue(SystemConfig.KEY_USER, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        if (redisSubscriber != null) {
            redisSubscriber.stop();
        }
//        if (subscribers != null) {
//            subscribers.values().parallelStream().forEach(s -> s.stop());
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        redisSubscriber.start();
//        subscribers.values().parallelStream().forEach(s -> s.start());
    }

    /**
     * このアプリケーションのエントリーポイント.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try (Application app = new Application(args)) {
            if (app.validArguments) {
                app.run();
            }
        }
    }
}
