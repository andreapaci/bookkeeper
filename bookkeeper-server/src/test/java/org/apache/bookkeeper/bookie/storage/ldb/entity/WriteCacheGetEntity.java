package org.apache.bookkeeper.bookie.storage.ldb.entity;

public class WriteCacheGetEntity {

    private boolean validLedgerId;
    private boolean validEntryId;

    public WriteCacheGetEntity(boolean validLedgerId, boolean validEntryId) {
        this.validLedgerId = validLedgerId;
        this.validEntryId = validEntryId;
    }

    public boolean isValidLedgerId() {
        return validLedgerId;
    }

    public boolean isValidEntryId() {
        return validEntryId;
    }
}
