package jp.co.opentone.bsol.framework.core.google.vision;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author openotne
 */
public class GoogleVisionApiResponse {

    public Map<String, String> descriptions = Maps.newHashMap();

    public void addDescription(String fileId, String description) {
        descriptions.put(fileId, description);
    }

    public String getDescriptionByFileId(String fileId) {
        return descriptions.get(fileId);
    }
}
