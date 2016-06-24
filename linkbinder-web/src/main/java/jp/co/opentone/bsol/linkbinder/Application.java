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


import java.net.URL;
import java.util.Arrays;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;

import jp.co.opentone.bsol.framework.core.log.LogbackConfigurationLoader;

/**
 * アプリケーションのエントリーポイント.
 * Created by OPEN TONE on 2015/07/08.
 */
public class Application implements AutoCloseable, Runnable {

    public static final ClassLoader CL = Application.class.getClassLoader();
    public static final URL MY_URL =
            Application.class.getProtectionDomain().getCodeSource().getLocation();

    /** 起動サーバー. */
    private Server server;
    /** アプリケーションコンテキスト. */
    private WebAppContext context;

    /** コマンド引数解析結果. */
    private boolean validArguments;
    /** ポート番号. */
    @Option(name="-p", aliases="--port", metaVar="9000", usage="起動ポート番号。指定ない場合は8080")
    private int port = 8080;
    /** コンテキストルート. */
    @Option(name="-c", aliases="--context-prefix", metaVar="/linkbinder", usage="URLプレフィックス。指定ない場合は \"/\"")
    private String prefix = "/";
    /**
     * リソースディレクトリ.
     * war外部のjdbc.properties,logback.{xml|groovy}を使用する場合にその配置場所を指定する.
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
            System.err.println("  Example: java -jar linkbinder-x.x.x.war"+parser.printExample(ExampleMode.ALL));
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
     * @param webXml web.xmlの配置場所を表すURL
     * @param args コマンドライン引数
     */
    public Application(URL webXml, String[] args) {
        parseArguments(args);
        if (!validArguments) {
            return;
        }

        server = createServer();
        context = createWebAppContext(webXml, createConfigurations());
    }

    private Server createServer() {
        return new Server(port);
    }

    private WebAppContext createWebAppContext(URL webXml, Configuration[] configurations) {
        WebAppContext context = new WebAppContext();
        String war = webXml == null
                ? "src/main/webapp"
                : MY_URL.toExternalForm();

        context.setWar(war);
        context.setContextPath(prefix);
        if (webXml != null) {
            context.getMetaData().setWebInfClassesDirs(
                    Arrays.asList(Resource.newResource(MY_URL)));
        }
        context.setConfigurations(configurations);

        return context;
    }

    private Configuration[] createConfigurations() {
        Configuration[] configurations = {
                new AnnotationConfiguration(),
                new WebInfConfiguration(),
                new WebXmlConfiguration(),
                new MetaInfConfiguration(),
                new FragmentConfiguration(),
                new EnvConfiguration(),
                new PlusConfiguration(),
                new JettyWebXmlConfiguration()
        };

        return configurations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        server.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        server.setHandler(context);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * このアプリケーションのエントリーポイント.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        URL url = CL.getResource("WEB-INF/web.xml");

        @SuppressWarnings("resource")
        Application app = new Application(url, args);
        if (app.validArguments) {
            app.run();
        }
    }

}
