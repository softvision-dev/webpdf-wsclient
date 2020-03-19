package net.webpdf.wsclient.schema.beans.limits;

public class UserLimitsBean implements LimitsBean {

    private int diskSpaceLimit;
    private int maxFiles;
    private int uploadLimit;

    /**
     * Returns the disk space limit of the affected user type.
     *
     * @return The disk space limit of the affected user type.
     */
    public int getDiskSpaceLimit() {
        return diskSpaceLimit;
    }

    /**
     * Sets the disk space limit of the affected user type.
     *
     * @param diskSpaceLimit The disk space limit of the affected user type.
     */
    public void setDiskSpaceLimit(int diskSpaceLimit) {
        this.diskSpaceLimit = diskSpaceLimit;
    }

    /**
     * Returns the maximum number of files for the affected user type.
     *
     * @return The maximum number of files for the affected user type.
     */
    public int getMaxFiles() {
        return maxFiles;
    }

    /**
     * Sets the maximum number of files for the affected user type.
     *
     * @param maxFiles The maximum number of files for the affected user type.
     */
    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }

    /**
     * Returns the upload limit of the affected user type.
     *
     * @return The upload limit of the affected user type.
     */
    public int getUploadLimit() {
        return uploadLimit;
    }

    /**
     * Sets the upload limit of the affected user type.
     *
     * @param uploadLimit The upload limit of the affected user type.
     */
    public void setUploadLimit(int uploadLimit) {
        this.uploadLimit = uploadLimit;
    }

    /**
     * Whether or not usage limits exist for the user type.
     *
     * @return True, if limits have been set for the affected user type.
     */
    @Override
    public boolean hasLimits() {
        return this.uploadLimit > 0 || this.maxFiles > 0 || this.diskSpaceLimit > 0;
    }
}
