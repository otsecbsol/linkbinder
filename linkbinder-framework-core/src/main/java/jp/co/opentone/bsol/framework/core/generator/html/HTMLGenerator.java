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
package jp.co.opentone.bsol.framework.core.generator.html;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.MethodInvocationException;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;

/**
 * HTML形式のデータを生成するクラス.
 * @author opentone
 */
public class HTMLGenerator implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5208287830841580624L;

    /**
     * テンプレートファイル名.
     */
    private String templateFileName;
    /**
     * 出力データ.
     */
    private Object data;
    /**
     * 出力データユーティリティ.
     */
    private Object util;

    /**
     * 空のインスタンスを生成する.
     */
    public HTMLGenerator() {
    }

    /**
     * HTMLデータ生成に必要な情報を指定してインスタンスを生成する.
     * @param templateFileName
     *            テンプレートファイル名
     * @param data
     *            出力データ
     */
    public HTMLGenerator(String templateFileName, Object data) {
        this.setTemplateFileName(templateFileName);
        this.setData(data);
    }

    /**
     * テンプレートファイルとDTOのマージを行い、ファイルに出力せずにファイルデータを返す.
     * マッピングする項目はテンプレートファイル上に「"$data." + DTOのインスタンスフィールド名」で指定する.
     * また、ユーティリティを使用する際はテンプレートファイル上に「"$util." + メソッド名（引数）」で指定する.
     * @return byteData ファイルデータ
     */
    public byte[] generate() {
        VelocityEngine velocity = createVelocityEngine();
        VelocityContext context = setupVelocityContext();
        Template template = getVelocityTemplate(velocity);
        return generateContents(velocity, context, template);
    }

    private VelocityEngine createVelocityEngine() {
        VelocityEngine velocity = new VelocityEngine();
        Properties p = new Properties();
        try {
            p.load(getClass().getResourceAsStream(SystemConfig.getValue("velocity.config")));
            velocity.init(p);
            return velocity;
        } catch (IOException e) {
            throw new GeneratorFailedException(e);
        } catch (Exception e) {
            throw new GeneratorFailedException(e);
        }
    }

    private VelocityContext setupVelocityContext() {
        VelocityContext context = new VelocityContext();
        context.put("data", data);
        if (util != null) {
            context.put("util", util);
        }
        return context;
    }

    private Template getVelocityTemplate(VelocityEngine velocity) {
        try {
            return velocity.getTemplate(templateFileName);
        } catch (ResourceNotFoundException e) {
            throw new GeneratorFailedException(e);
        } catch (ParseErrorException e) {
            throw new GeneratorFailedException(e);
        } catch (Exception e) {
            throw new GeneratorFailedException(e);
        }
    }

    private byte[] generateContents(
        VelocityEngine velocity, VelocityContext context, Template template) {

        String encoding = (String) velocity.getProperty("output.encoding");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new OutputStreamWriter(bos, encoding));
            template.merge(context, w);
            w.flush();

            return bos.toByteArray();
        } catch (ResourceNotFoundException e) {
            throw new GeneratorFailedException(e);
        } catch (ParseErrorException e) {
            throw new GeneratorFailedException(e);
        } catch (MethodInvocationException e) {
            throw new GeneratorFailedException(e);
        } catch (IOException e) {
            throw new GeneratorFailedException(e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    throw new GeneratorFailedException(e);
                }
            }
        }
    }

    public void setTemplateFileName(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setUtil(Object util) {
        this.util = util;
    }

    public Object getUtil() {
        return util;
    }
}
