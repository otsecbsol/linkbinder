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
package jp.co.opentone.bsol.linkbinder.service.rss.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.SuppressTrace;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.extension.rome.feed.synd.ExtendedSyndEntryImpl;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.DaoFinder;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.RSSCorrespon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchRSSCorresponCondition;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.rss.RSSService;

/**
 * RSSに関する処理を提供する.
 * $Date: 2011-07-04 13:09:23 +0900 (月, 04  7 2011) $
 * $Rev: 4409 $
 * $Author: nemoto $
 */
@Service
@Transactional(readOnly = true)
public class RSSServiceImpl extends AbstractService implements RSSService {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5529744023032277478L;

    /**
     * Dao取得クラス.
     */
    @Resource
    private DaoFinder daoFinder;

    /**
     * FEED TYPE.
     */
    private static final String FEED_TYPE = "rss_2.0";

    /**
     * RSSのTITLE.
     */
    private static final String RSS_TITLE = "Internal Correspondence";

    /**
     * RSSのitemのdescriptionの項目名と値の区切り文字.
     */
    private static final String DESCRIPTION_SEPARATOR = " : ";

    /**
     * RSSのitemのdescriptionの改行文字.
     */
    private static final String DESCRIPTION_BR = "<br />";

    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(RSSServiceImpl.class);

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.rss.RSSService#get(java.lang.String)
     */
    @SuppressTrace
    public String getRSS(String userId, String baseURL)
        throws ServiceAbortException {
        // 入力値チェック
        ArgumentValidator.validateNotEmpty(userId);
        ArgumentValidator.validateNotEmpty(baseURL);

        //  接続先データベース切り替えのため現在実行中ユーザーを指定されたユーザーIDに設定
        ProcessContext.getCurrentContext()
            .setValue(SystemConfig.KEY_USER_ID, userId);

        List<RSSCorrespon> rssCorresponList = getRSSCorresponList(userId);
        return makeRSSFromRSSCorresponList(rssCorresponList, baseURL);
    }

    /**
     * RSS対象となるコレポンを取得.
     * @param userId ユーザID
     * @return コレポン
     * @throws ServiceAbortException configの値が異常
     */
    private List<RSSCorrespon> getRSSCorresponList(String userId)
        throws ServiceAbortException {
        List<RSSCorrespon> corresponList = null;
        SearchRSSCorresponCondition condition = new SearchRSSCorresponCondition();
        condition.setUserId(userId);
        condition.setDayPeriod(
            getRSSDayPeriod(SystemConfig.getValue(Constants.RSS_DAY_PERIOD)));
        CorresponDao dao = daoFinder.getDao(CorresponDao.class);
        corresponList = dao.findRSSCorrespon(condition);
        return corresponList;
    }

    /**
     * RSS対象コレポンとなる期日を取得する.
     * @param date 対象期間
     * @return RSS対象となるコレポンの更新日時下限
     * @throws ServiceAbortException configの値が異常
     */
    private Date getRSSDayPeriod(String rssDayPeriod) throws ServiceAbortException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.getNow());
        if (log.isDebugEnabled()) {
            log.debug("cal(before) [{}]", cal.getTime());
        }
        try {
            // 時分秒とミリ秒を0にセットし、(1 - configの値)足す。
            // configの値が20、現在時刻が2/20 12:26:09.250だとすると、
            // (1)時分秒とミリ秒に0をセット --> 2/20 00:00:00.000
            // (2)日付に1 - config値(= -19)を足す --> 2/1 00:00:00.000
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.DATE, 1 - Integer.parseInt(rssDayPeriod));
        } catch (NumberFormatException e) {
            ServiceAbortException sae =  new ServiceAbortException(
                "config parameter[" + Constants.RSS_DAY_PERIOD + "] error."
                + "　value[" + rssDayPeriod + "]");
            sae.initCause(e);
            throw sae;
        }
        if (log.isDebugEnabled()) {
            log.debug("cal(after) [{}]", cal.getTime());
        }

        return cal.getTime();
    }

    /**
     * コレポンリストからRSSを生成する.
     * @param rssCorresponList コレポンリスト
     * @param baseURL BaseURL
     * @return RSS
     * @throws ServiceAbortException 設定値からRSSを出力する際に例外発生
     */
    private String makeRSSFromRSSCorresponList(
        List<RSSCorrespon> rssCorresponList, String baseURL) throws ServiceAbortException {
        String resultRSS = null;

        SyndFeed feed = (SyndFeed) new SyndFeedImpl();
        feed.setFeedType(FEED_TYPE);
        feed.setTitle(RSS_TITLE);
        feed.setLink("");
        feed.setDescription("");
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        if (rssCorresponList != null) {
            for (RSSCorrespon c : rssCorresponList) {
                entries.add(makeEntryFromRSSCorrespon(c, baseURL));
            }
        }
        feed.setEntries(entries);

        try {
            SyndFeedOutput output = new SyndFeedOutput();
            resultRSS = output.outputString(feed);
        } catch (FeedException e) {
            // 設定された値から生成されるXMLが異常だった際に発生. 起こり得ない.
            ServiceAbortException sae =
                new ServiceAbortException("RSS outputString error");
            sae.initCause(e);
            throw sae;
        }
        return resultRSS;
    }

    /**
     * コレポンからRSSのエントリー(それぞれのItem)を作成.
     */
    private SyndEntry makeEntryFromRSSCorrespon(
        RSSCorrespon c, String baseURL) {
        if (log.isDebugEnabled()) {
            log.debug("id[{}]", c.getId());
        }
        SyndEntry entry = new ExtendedSyndEntryImpl();

        // title設定
        entry.setTitle(c.getSubject());
        // description設定
        entry.setDescription(makeDescriptionFromRSSCorrespon(c, baseURL));
        // link(とguid)設定
        entry.setLink(baseURL + "correspon/correspon.jsf?id=" + c.getId()
                + "&projectId=" + c.getProjectId());
        // author設定
        SyndPerson author = new SyndPersonImpl();
        author.setEmail(c.getProjectId() + " : " + c.getProjectNameE());
        List<SyndPerson> authors = new ArrayList<SyndPerson>();
        authors.add(author);
        entry.setAuthors(authors);
        // category設定
        SyndCategory category = new SyndCategoryImpl();
        category.setName(c.getCategory().getLabel());
        List<SyndCategory> categories = new ArrayList<SyndCategory>();
        categories.add(category);
        entry.setCategories(categories);
        // pubDate(とdc:date)設定
        entry.setPublishedDate(c.getUpdatedAt());

        return entry;
    }

    /**
     * Itemのdescription(本文)を作成.
     * @param c コレポン
     * @param baseURL BaseURL
     * @return itemのdescription
     */
    private SyndContent makeDescriptionFromRSSCorrespon(
        RSSCorrespon c, String baseURL) {
        SyndContent description = new SyndContentImpl();
        description.setType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(makeDescriptionLine("No.", c.getId()));
        sb.append(makeDescriptionLine("Correspondence No.", c.getCorresponNo()));
        sb.append(makeDescriptionLine("From", c.getFromCorresponGroup()));
        sb.append(makeDescriptionLine("To", c.getToGroupName()));
        sb.append(makeDescriptionLine("Cc", c.getCcGroupName()));
        sb.append(makeDescriptionLine("Type", c.getCorresponType()));
        sb.append(makeDescriptionLine("Subject", c.getSubject()));
        sb.append(makeDescriptionLine("Workflow Status", c.getWorkflowStatus()));
        sb.append(makeDescriptionLine("Created on", c.getCreatedAt()));
        sb.append(makeDescriptionLine("Issued on", c.getIssuedAt()));
        sb.append(makeDescriptionLine("Deadline for Reply", c.getDeadlineForReply()));
        sb.append(makeDescriptionLine("Created by", c.getCreatedBy()));
        sb.append(makeDescriptionLine("Issued by", c.getIssuedBy()));
        sb.append(makeDescriptionLine("Reply Required", c.getReplyRequired()));
        sb.append(makeDescriptionLine(true, "URL", baseURL  + "correspon/correspon.jsf?id="
            + convertDescriptionString(c.getId()) + "&projectId="
            + convertDescriptionString(c.getProjectId())));
        description.setValue(sb.toString());
        return description;
    }

    /**
     * descriptionに表示する一行を作成する(改行つき).
     * @param key 項目名
     * @param value 値
    */
    private String makeDescriptionLine(String key, Object value) {
        return makeDescriptionLine(false, key, value);
    }

    /**
     * descriptionに表示する一行を作成する.
     * @param lastLineFlag 最終行か否か(最終行ならば改行を出力しない)
     * @param key 項目名
     * @param value 値
     * @return descriptionに表示する一行の文字列
     */
    private String makeDescriptionLine(boolean lastLineFlag, String key, Object value) {
        String result = key + DESCRIPTION_SEPARATOR + convertDescriptionString(value);
        if (lastLineFlag) {
            return result;
        } else {
            return result + DESCRIPTION_BR;
        }
    }

    /**
     * オブジェクトをRSSのdescription用文字列に変換する.
     * <h3>変換規則(変換結果がnullの場合には""に変換)</h3>
     * <ul>
     * <li>User型 → 名前/従業員番号</li>
     * <li>Date型 → yyyy-MM-dd</li>
     * <li>CorresponGroup型 → getName()</li>
     * <li>CorresponType, WorkflowStatus, ReplyRequired → getLabel()</li>
     * <li>その他 → toString()</li>
     * </ul>
     * @param object
     * @return 変換後文字列
     */
    private String convertDescriptionString(Object object) {
        String result = null;

        if (object instanceof User) {
            result = ((User) object).getNameE() + "/" + ((User) object).getEmpNo();
        } else if (object instanceof Date) {
            result = DateUtil.convertDateToString((Date) object);
        } else if (object instanceof CorresponGroup) {
            result = ((CorresponGroup) object).getName();
        } else if (object instanceof CorresponType) {
            result = ((CorresponType) object).getLabel();
        } else if (object instanceof WorkflowStatus) {
            result = ((WorkflowStatus) object).getLabel();
        } else if (object instanceof ReplyRequired) {
            result = ((ReplyRequired) object).getLabel();
        } else if (object != null) {
            result = object.toString();
        }

        if (result == null) {
            result = "";
        }

        return result;
    }
}
