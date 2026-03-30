package org.woojukang.remixlab.domain.video.entity;



public final class VideoStatus {

    public static final String PENDING = "queued";
    public static final String PROCESSING = "in_progress";
    public static final String COMPLETED = "completed";
    public static final String FAILED = "failed";

    private VideoStatus() {}
}