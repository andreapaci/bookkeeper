package org.apache.bookkeeper.bookie.storage.ldb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.storage.ldb.entity.WriteCacheGetEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(value = Parameterized.class)
public class WriteCacheGetTest {

    private WriteCache writeCache;
    private ByteBufAllocator byteBufAllocator;
    private final int entryNumber = 10;
    private final int entrySize = 1024;
    private ByteBuf entry;
    private final int ledgerId = 2;
    private final int entryId = 1;
    private WriteCacheGetEntity writeEntity;
    private boolean expectedResults;


    @Before
    public void setUp() throws Exception {
        byteBufAllocator = UnpooledByteBufAllocator.DEFAULT;
        writeCache = new WriteCache(byteBufAllocator, entrySize * entryNumber);

        entry = byteBufAllocator.buffer(entrySize);
        System.out.println(entry.maxCapacity());
        ByteBufUtil.writeAscii(entry, "test");
        entry.writerIndex(entry.capacity());

        writeCache.put(ledgerId, entryId, entry);

    }

    @After
    public void tearDown() throws Exception {
        writeCache.clear();
        entry.release();
        writeCache.close();
    }

    //Parametri in input
    @Parameterized.Parameters
    public static Collection<?> getParameters(){
        return Arrays.asList(new Object[][] {
                {new WriteCacheGetEntity(true, true), true},
                {new WriteCacheGetEntity(false, true), false},
                {new WriteCacheGetEntity(true, false), false}
        });
    }

    public WriteCacheGetTest(WriteCacheGetEntity writeEntity, boolean expectedResults){
        this.writeEntity = writeEntity;
        this.expectedResults = expectedResults;
    }
    @Test
    public void getFromCache(){

        ByteBuf result = null;

        int actualLedgerId = ledgerId, actualEntryId = entryId;

        if(!writeEntity.isValidLedgerId()) actualLedgerId++;
        if(!writeEntity.isValidEntryId()) actualEntryId++;

        try{
            result = writeCache.get(actualLedgerId, actualEntryId);
        }
        catch(Exception e){
            e.printStackTrace();
            result = null;
        }

        if(writeEntity.isValidLedgerId() && writeEntity.isValidEntryId()){
            Assert.assertEquals(result, entry);
        }
        else{
            Assert.assertNull(result);
        }
    }

}