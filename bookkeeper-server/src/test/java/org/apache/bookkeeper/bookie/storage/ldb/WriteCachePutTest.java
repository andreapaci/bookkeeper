package org.apache.bookkeeper.bookie.storage.ldb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.storage.ldb.entity.WriteCachePutEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class WriteCachePutTest {

    private WriteCache writeCache;
    private ByteBufAllocator byteBufAllocator;
    private final int entryNumber = 10;
    private final int entrySize = 1024;
    private ByteBuf entry;
    private WriteCachePutEntity writeEntity;
    private boolean expectedResults;


    //Parametri in input
    @Parameterized.Parameters
    public static Collection<?> getParameters(){


        return Arrays.asList(new Object[][] {
                {new WriteCachePutEntity(-1, 0, true), false},
                {new WriteCachePutEntity(0, 1, false), false},
                {new WriteCachePutEntity(1, -1, true), false},
                {new WriteCachePutEntity(1, 1, true), true}

        });
    }

    public WriteCachePutTest(WriteCachePutEntity writeEntity, boolean expectedResults){
        this.writeEntity = writeEntity;
        this.expectedResults = expectedResults;

    }

    @Before
    public void setUp() throws Exception {
        byteBufAllocator = UnpooledByteBufAllocator.DEFAULT;
        writeCache = new WriteCache(byteBufAllocator, entrySize * entryNumber);

        if(writeEntity.isValidEntry()) {

            entry = byteBufAllocator.buffer(entrySize);
            ByteBufUtil.writeAscii(entry, "test");
            entry.writerIndex(entry.capacity());
        }
        else
            entry = null;
    }

    @After
    public void tearDown() throws Exception {
        writeCache.clear();
        if(entry != null) entry.release();
        writeCache.close();
    }



    @Test
    public void putFromCache(){

        boolean result;

        try{

            result = writeCache.put(writeEntity.getLedgerId(), writeEntity.getEntryId(), entry);
        }
        catch (Exception e){
            e.printStackTrace();
            result = false;
        }

        Assert.assertEquals(result, this.expectedResults);

    }
}