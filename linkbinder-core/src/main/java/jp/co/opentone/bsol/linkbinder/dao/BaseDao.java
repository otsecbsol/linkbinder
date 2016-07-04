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
package jp.co.opentone.bsol.linkbinder.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;

import jp.co.opentone.bsol.framework.core.dao.DataSourceSelector;
import jp.co.opentone.bsol.framework.core.exception.ReflectionRuntimeException;

/**
 * 当システム内の全てのDaoの最上位クラス.
 *
 * @author opentone
 *
 */
public class BaseDao extends SqlMapClientDaoSupport implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 3039177945884371425L;

    /**
     * 前方一致検索時にエスケープする文字：%.
     */
    private static final String PERCENT = "%";

    /**
     * 前方一致検索時にエスケープする文字：_.
     */
    private static final String UNDER_BAR = "_";

    /**
     * SQL定義ファイルのnamespace.
     */
    private String namespace;

    /**
     * 実行環境により利用するDataSourceを切り換えるためのオブジェクト.
     */
    @Resource
    private DataSourceSelector selector;

    /**
     * SQL定義ファイルの名前空間を指定してインスタンス化する.
     * @param namespace
     *            SQL定義ファイルのnamespace
     */
    public BaseDao(String namespace) {
        this.namespace = namespace;
    }

    /**
     * 初期化.
     */
    @PostConstruct
    public void initialize() {
        initDataSource();
    }

    private void initDataSource() {
        selectDataSource(selector);
    }

    /**
     * 実行環境毎の適切なデータソースを選択し このオブジェクトに保持する.
     * @param dataSourceSelector
     *            データソース切り替えオブジェクト
     */
    public void selectDataSource(DataSourceSelector dataSourceSelector) {
        super.setDataSource(dataSourceSelector.select());
    }

    /**
     * SqlMapClientを設定する. 親クラスであるSqlMapClientDaoSupportはSqlMapClientが必須なので
     * このメソッド内で親クラスのプロパティを設定する.
     * @param sqlMapClient
     *            SqlMapClient
     */
    @Resource
    public final void setMySqlMapClient(SqlMapClient sqlMapClient) {
        super.setSqlMapClient(sqlMapClient);
    }

    /**
     * SQL定義ファイル内のSQL IDにnamespaceを付与して返す.
     * @param id
     *            ID
     * @return namespaceを付与したID. namespaceが未設定の場合はidをそのまま返す.
     */
    protected String getSqlId(String id) {
        if (StringUtils.isNotEmpty(namespace)) {
            return namespace + "." + id;
        } else {
            return id;
        }
    }

    /**
     * 検索条件オブジェクトを前方一致検索のオブジェクトに変換する.
     *
     * ※注意：オブジェクトのフィールドには未対応
     *         (元のオブジェクトの値も書き換わってしまう。)
     *
     * @param <C> 検索条件のクラス
     * @param condition 検索条件
     * @param fields 前方一致検索を行うフィールド名
     * @return 前方一致検索オブジェクト
     * @throws ReflectionRuntimeException オブジェクトの変換に失敗
     */
    @SuppressWarnings("unchecked")
    protected <C> C getLikeSearchCondition(Object condition, List<String> fields)
            throws ReflectionRuntimeException {
        C clone = null;
        try {
            clone = (C) condition.getClass().getConstructor().newInstance();

            PropertyUtils.copyProperties(clone, condition);

            // エスケープ
            escape(clone, fields);
        } catch (NoSuchMethodException e) {
            throw new ReflectionRuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new ReflectionRuntimeException(e);
        } catch (SecurityException e) {
            throw new ReflectionRuntimeException(e);
        } catch (InstantiationException e) {
            throw new ReflectionRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionRuntimeException(e);
        }

        return clone;
    }

    /**
     * 前方一致検索用の文字列をエスケープする.
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void escape(Object clone, List<String> fields)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String escapeChar = (String) PropertyUtils.getProperty(clone, "escapeChar");
        String escapeEscapeChar = escapeChar + escapeChar;
        String escapePercent = escapeChar + PERCENT;
        String escapeUnderBar = escapeChar + UNDER_BAR;
        for (String field : fields) {
            Object object = PropertyUtils.getProperty(clone, field);
            if (object != null) {
                String value = String.valueOf(object);
                // エスケープ文字自体を変換
                value = value.replaceAll(escapeChar, escapeEscapeChar);
                // %を変換
                value = value.replaceAll(PERCENT, escapePercent);
                // _を変換
                value = value.replaceAll(UNDER_BAR, escapeUnderBar);

                PropertyUtils.setProperty(clone, field, value);
            }
        }
    }
}
