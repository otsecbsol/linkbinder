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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import com.google.common.collect.Lists;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.google.vision.GoogleVisionApiClient;
import jp.co.opentone.bsol.framework.core.google.vision.GoogleVisionApiConfiguration;
import jp.co.opentone.bsol.framework.core.google.vision.GoogleVisionApiRequestFile;
import jp.co.opentone.bsol.framework.core.google.vision.GoogleVisionApiResponse;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.AttachmentDao;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.code.AttachmentFileType;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.ImageTextDetectionService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * このサービスでは画像ファイルからのテキスト抽出処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class ImageTextDetectionServiceImpl extends AbstractService implements
        ImageTextDetectionService {
    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ImageTextDetectionService.class);
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -2795456058133877087L;

    /**
     * 空のインスタンスを生成する.
     */
    public ImageTextDetectionServiceImpl() {
    }

    private GoogleVisionApiClient newApiClient() {
        GoogleVisionApiConfiguration config = GoogleVisionApiConfiguration.builder()
                .use(BooleanUtils.toBoolean(SystemConfig.getValue(Constants.KEY_GOOGLE_VISION_USE)))
                .accountFilePath(SystemConfig.getValue(Constants.KEY_GOOGLE_VISION_SERVICE_ACCOUNT_FILE))
                .applicationName(SystemConfig.getValue(Constants.KEY_GOOGLE_VISION_APPLICATION_NAME))
                .maxResult(NumberUtils.toInt(SystemConfig.getValue(Constants.KEY_GOOGLE_VISION_API_RESULT_COUNT), 0))
                .build();

        return new GoogleVisionApiClient(config);
    }

    /*
     * @inheritDoc
     */
    @Override
    @Transactional(readOnly = false)
    public void detectTextAndFill(List<Attachment> attachments) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(attachments);

        List<Attachment> attachmentsToDetect = Lists.newArrayList();
        GoogleVisionApiClient client = newApiClient();
        for (Attachment a : attachments) {
            if (AttachmentFileType.IMAGE != Attachment.detectFileTypeByFileName(a.getFileName())) {
                continue;
            }
            if (a.getOrgExtractedText() == null) {
                client.addRequestFile(new GoogleVisionApiRequestFile(
                        a.getFileId(),
                        a.getFileName(),
                        a.getContent()));
            }
            attachmentsToDetect.add(a);
        }

        if (client.isReady()) {
            GoogleVisionApiResponse response = client.execute();

            AttachmentDao dao = getDao(AttachmentDao.class);
            for (Attachment a : attachmentsToDetect) {
                String description = response.getDescriptionByFileId(a.getFileId());
                a.setOrgExtractedText(description);
                a.setExtractedText(description);

                try {
                    dao.update(a);
                } catch (KeyDuplicateException | StaleRecordException e) {
                    throw new ServiceAbortException(e);
                }
            }
        }
    }
}
