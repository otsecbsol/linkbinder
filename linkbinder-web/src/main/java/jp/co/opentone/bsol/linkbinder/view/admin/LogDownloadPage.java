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
package jp.co.opentone.bsol.linkbinder.view.admin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.ManagedBean;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * アプリケーションサーバーのログをダウンロードするページ.
 * 障害発生時等にこのページを利用することを想定している.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class LogDownloadPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -3872225361614464687L;

    /**
     * ダウンロード対象ファイルを特定するid.
     */
    @Required
    private Long id;

    /**
     * ダウンロード対象ファイル.
     */
    @Transfer
    private List<LogFile> files;

    /**
     * 空のインスタンスを生成する.
     */
    public LogDownloadPage() {
    }

    /**
     * このページの表示に必要なデータを初期化する.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 選択されたファイルをダウンロードする.
     * @return 遷移先.常にnull
     */
    public String download() {
        handler.handleAction(new DownloadAction(this));
        return null;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(List<LogFile> files) {
        this.files = files;
    }

    /**
     * @return the files
     */
    public List<LogFile> getFiles() {
        return files;
    }

    static class InitializeAction extends AbstractAction {
        /**
         * SerialVersionUId.
         */
        private static final long serialVersionUID = -4775153452592800374L;

        /** ログファイル格納先取得キー. */
        private static final String KEY_LOG_DIR = "dir.log";

        /** アクション発生元ページ. */
        private LogDownloadPage page;
        /**
         * このアクションが発生したページを指定してインスタンス化する.
         * @param page ページ
         */
        public InitializeAction(LogDownloadPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            checkPermission();
            setup();
        }

        private void checkPermission() throws ServiceAbortException {
            if (!page.isSystemAdmin()) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        }

        private void setup() throws ServiceAbortException {
            String dir = SystemConfig.getValue(KEY_LOG_DIR);
            if (StringUtils.isNotEmpty(dir)) {
                List<LogFile> files = collect(new File(dir));
                setFiles(files);
            }
        }

        private List<LogFile> collect(File dir) {
            List<LogFile> result = new ArrayList<LogFile>();
            if (!dir.exists() || !dir.isDirectory()) {
                return result;
            }

            long i = 0;
            for (File f : dir.listFiles()) {
                result.add(new LogFile(++i, f));
            }

            //  取得したファイルを更新日時の降順で並べ換え
            Collections.sort(result, LogComparator.COMPARATOR);

            return result;
        }

        private void setFiles(List<LogFile> files) {
            page.id = null;
            page.files = files;
        }
    }

    static class LogComparator implements Comparator<LogFile>, Serializable {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -7712502244772869613L;
        /** Logファイルを比較するオブジェクト. */
        public static final LogComparator COMPARATOR = new LogComparator();
        public int compare(LogFile o1, LogFile o2) {
            return Long.valueOf(o2.file.lastModified())
                    .compareTo(Long.valueOf(o1.file.lastModified()));
        }
    }

    static class DownloadAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 6517565478910528548L;
        /** アクション発生元ページ. */
        private LogDownloadPage page;
        /**
         * このアクションが発生したページを指定してインスタンス化する.
         * @param page ページ
         */
        public DownloadAction(LogDownloadPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            LogFile selected = getSelectedFile();
            if (selected != null) {
                try {
                    download(selected);
                } catch (IOException e) {
                    throw new ServiceAbortException(
                                "failed to download.",
                                e,
                                MessageCode.E_DOWNLOAD_FAILED);
                }
            }
        }

        private LogFile getSelectedFile() {
            Long id = page.id;
            for (LogFile file : page.files) {
                if (file.id.equals(id)) {
                    return file;
                }
            }
            return null;
        }

        private void download(LogFile file) throws IOException {
            File f = file.getFile();
            if (f.exists() && f.isFile() && f.canRead()) {
                BufferedInputStream in = null;
                try {
                    int length = (int) f.length();
                    in = new BufferedInputStream(new FileInputStream(f));
                    page.viewHelper.download(f.getName(), in, length);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
        }
    }

    /** ダウンロード対象ファイル. */
    public static class LogFile implements Serializable {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 3629786734701692350L;
        /** ラベル表示用の日付フォーマット. */
        private static final String TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

        /** このファイルを識別するID. */
        private Long id;
        /** ファイルの実体. */
        private File file;

        /**
         * IDとファイルを指定してインスタンス化する.
         * @param id id
         * @param file ファイル
         */
        public LogFile(Long id, File file) {
            this.id = id;
            this.file = file;
        }

        public Long getId() {
            return id;
        }

        public String getFileName() {
            return file.getName();
        }

        public String getLabel() {
            String fileName = file.getName();
            Date lastModified = new Date(file.lastModified());

            SimpleDateFormat f = new SimpleDateFormat(TIME_FORMAT);
            return String.format("%s (%s)", fileName, f.format(lastModified));
        }

        public File getFile() {
            return file;
        }
    }
}
