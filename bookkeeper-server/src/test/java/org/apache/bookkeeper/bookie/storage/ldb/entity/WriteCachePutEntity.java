package org.apache.bookkeeper.bookie.storage.ldb.entity;



public class WriteCachePutEntity {
    private int entryId;
    private int ledgerId;
    private boolean validEntry;

    public WriteCachePutEntity(int ledgerId, int entryId, boolean validEntry) {
        this.entryId = entryId;
        this.ledgerId = ledgerId;
        this.validEntry = validEntry;
    }

    public int getEntryId() {
        return entryId;
    }

    public int getLedgerId() {
        return ledgerId;
    }

    public boolean isValidEntry() {
        return validEntry;
    }
}
