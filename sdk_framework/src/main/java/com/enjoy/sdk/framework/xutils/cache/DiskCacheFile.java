package com.enjoy.sdk.framework.xutils.cache;

import com.enjoy.sdk.framework.xutils.common.util.IOUtil;
import com.enjoy.sdk.framework.xutils.common.util.ProcessLock;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * 磁盘缓存文件, 操作完成后必须及时调用close()方法关闭.
 */
public final class DiskCacheFile extends File implements Closeable {

    /*package*/ DiskCacheEntity cacheEntity;
    /*package*/ ProcessLock lock;

    /**
     * @param cacheEntity
     * @param path
     * @param lock        lock name: path
     */
    /*package*/ DiskCacheFile(DiskCacheEntity cacheEntity, String path, ProcessLock lock) {
        super(path);
        this.cacheEntity = cacheEntity;
        this.lock = lock;
    }

    @Override
    public void close() throws IOException {
        IOUtil.closeQuietly(lock);
    }

    public DiskCacheFile commit() throws IOException {
        return getDiskCache().commitDiskCacheFile(this);
    }

    public LruDiskCache getDiskCache() {
        String dirName = this.getParentFile().getName();
        return LruDiskCache.getDiskCache(dirName);
    }

    public DiskCacheEntity getCacheEntity() {
        return cacheEntity;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
}
