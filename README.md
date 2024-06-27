# Acharya-Prashant-
this is android app in which loading the image from URL without any third-party library. with efficient loading. Also use a cache Memory for fast loading and less internet consumption.


>It is done by The Kotlin.
>For the lazy loading and efficeant loading I am use a Recycle view.
>Using a background threads for parallel processing. 
>Not use any library for the image loading. I create a calss and some methods for loading it's. this is by my ideas & logics.
>I follow MVVM architecture. 
>Api call by the Retrofit and Handle the exception in the view model.
>Creates a simple ui Not focused on it design.
>In the view model I get the all BitMap data from the cache and then provide to the RecycleView So that is shuld be efficeant loading with thwe image.

 fun getMediaCache(context: Context) {
        viewModelScope.launch {
            val imageLoader = ImageLoader(context)
            val listBitmap = mutableMapOf<String, Bitmap>()

            // Check if mediaCoverages is not null and has elements
            val mediaList = mediaCoverages.value ?: return@launch

            withContext(Dispatchers.IO) {
                if (mediaList.isNotEmpty()) {
                    for (image in mediaList) {
                        val imageUrl = "${image.thumbnail.domain}/${image.thumbnail.basePath}/0/${image.thumbnail.key}"
                        val bitmap = imageLoader.getImage(imageUrl)
                        bitmap?.let { listBitmap[imageUrl] = it }
                    }
                }
            }

            _bitMapData.postValue(listBitmap)
        }
     }

    
>For the efficiently use cache & load ibitmap to image view. It needed to resize the BitMap.

fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
    
val bitmap = downloadBitmap(url)?.let { resizeBitmap(it, 500, 500) }


>I created a class for Image loader. then create a cache memory by LruCache it is a RAM part which use to fast getting data from memory.

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

}

>then I make this function for load image. First it check data on

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

>first download the bitMap if not have in a cache.
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
        
can clone the app and check my logics.
