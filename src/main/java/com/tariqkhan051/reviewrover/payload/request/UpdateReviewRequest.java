package com.tariqkhan051.reviewrover.payload.request;

import java.util.Optional;

import com.tariqkhan051.reviewrover.models.EReviewStatus;

public class UpdateReviewRequest {
    
    private long id;
    private EReviewStatus status;

    public EReviewStatus getStatus() {
        return status;
    }

    public void setStatus(Optional<EReviewStatus> status) {
        if (status.isPresent()) {
            this.status = status.get();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
