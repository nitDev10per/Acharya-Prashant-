import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class ImageLoader(private val context: Context) {

    private val memoryCache: LruCache<String, Bitmap>
    private val executor = Executors.newFixedThreadPool(4)

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    fun loadImage(
        url: String,
        imageView: ImageView,
        bitmapImg: Bitmap? = null,
        callback: (Int,Boolean) -> Unit,
        position: Int,
    ) {
        imageView.tag = url
        var bitmap: Bitmap?
        if(bitmapImg!=null){
            bitmap = bitmapImg
            CoroutineScope(Dispatchers.Main).launch {
                val loadedBitmap = withContext(Dispatchers.IO) {
                    bitmap
                }
                imageView.setImageBitmap(loadedBitmap)
            }
        }else{

            bitmap=getBitmapFromCache(url)
            if (bitmap != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val loadedBitmap = withContext(Dispatchers.IO) {
                        bitmap
                    }
                    imageView.setImageBitmap(loadedBitmap)
                }
            } else {
                executor.execute {
                    val bitmap = downloadBitmap(url)?.let { resizeBitmap(it, 500, 500) }
                    if (bitmap != null) {
                        addBitmapToCache(url, bitmap)
                        callback(position,true)
                        if (imageView.tag == url) {
                            imageView.post { CoroutineScope(Dispatchers.Main).launch {
                                val loadedBitmap = withContext(Dispatchers.IO) {
                                    bitmap
                                }
                                imageView.setImageBitmap(loadedBitmap)
                            } }
                        }
                    } else {
                        // Handle image load failure here (e.g., set a placeholder image)
                    }
                }
            }
        }

    }

    private fun getBitmapFromCache(key: String): Bitmap? {
        return memoryCache.get(key) ?: getBitmapFromDiskCache(key)?.also {
            memoryCache.put(key, it)
        }
    }

    private fun addBitmapToCache(key: String, bitmap: Bitmap) {
        if (getBitmapFromCache(key) == null) {
            memoryCache.put(key, bitmap)
            addBitmapToDiskCache(key, bitmap)
        }
    }
    fun getImage(url: String): Bitmap? {
        val bitmap = getBitmapFromCache(url)
        if (bitmap != null) {
            return bitmap
        }
        return bitmap
    }

    private fun getBitmapFromDiskCache(key: String): Bitmap? {
        val file = File(context.cacheDir, key.hashCode().toString())
        return if (file.exists()) BitmapFactory.decodeFile(file.path) else null
    }

    private fun addBitmapToDiskCache(key: String, bitmap: Bitmap) {
        val file = File(context.cacheDir, key.hashCode().toString())
        if (!file.exists()) {
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }

    fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun downloadBitmap(url: String): Bitmap? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


