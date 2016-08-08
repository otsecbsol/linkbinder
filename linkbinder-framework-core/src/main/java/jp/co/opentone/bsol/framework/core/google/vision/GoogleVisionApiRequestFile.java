package jp.co.opentone.bsol.framework.core.google.vision;

/**
 * @author openotne
 */
public class GoogleVisionApiRequestFile {
    private String fileId;
    private String filename;
    private byte[] content;

    public GoogleVisionApiRequestFile(String fileId, String filename, byte[] content) {
        this.fileId = fileId;
        this.filename = filename;
        this.content = content;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getContent() {
        return content;
    }
}
