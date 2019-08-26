package songwei520.map3synthesis1.mapUtils;


import android.content.Context;
import android.util.Log;

import com.amap.api.maps.model.Tile;
import com.amap.api.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;


public class LocalTileTileProvider implements TileProvider {
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    public static final int BUFFER_SIZE = 1024;

    private String tileFilePath;
    private String tileUrl;
    private Context context;
    private Calendar expiration;
    private int serverCount;

    public LocalTileTileProvider(Context context, String name, String tileUrl, int serverCount) {
        this.context = context;
        this.tileUrl = tileUrl;
        this.serverCount = serverCount;
        tileFilePath = "/sdcard/fw/tile/" + name + "/";
        expiration = Calendar.getInstance();
        expiration.add(Calendar.MONTH, -3);
    }

    @Override
    public final Tile getTile(int x, int y, int zoom) {
        String dirPath = tileFilePath + zoom + "/" + x + "/";
        String filePath = dirPath + y + ".til";
        byte[] image = readTileImage(dirPath, filePath, x, y, zoom);
        return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
    }

    private byte[] readTileImage(String dirPath, String filePath, int x, int y, int zoom) {
        InputStream in = null;
        ByteArrayOutputStream buffer = null;

        File file = new File(filePath);
        //if (AppData.GetInstance(this.context).isLog)
            Log.i("LOGTAG", "x:" + x + ",y:" + y + ",zoom:" + zoom + ",exists:" + file.exists() + ",Last:" + file.lastModified() + ",expired:" + expiration.getTimeInMillis());
        if (file.exists() && file.lastModified() > expiration.getTimeInMillis()) {
            try {

//                byte[] buffer = new byte[in.available()];
//                in.read(buffer);
//                return buffer;

                in = new FileInputStream(file);
                return readInputStream(in);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return getFile(dirPath, filePath, x, y, zoom);
        }
    }

    private byte[] getFile(String dirPath, String filePath, int x, int y, final int zoom) {
        InputStream in = null;
        GZIPInputStream gzipStream = null;
        HttpURLConnection conn = null;
        try {
            String url = String.format(tileUrl, x, y, zoom).replace("{$s}", String.valueOf((int) (Math.random() * serverCount)));
            //if (AppData.GetInstance(this.context).isLog)
                Log.i("LOGTAG", "Tile:" + url);
            URL myurl = new URL(url);
            // 获得连接
            conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(2000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            //conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            //conn.setRequestProperty("Accept-Encoding","gzip, deflate");
            //conn.setRequestProperty("Connection","keep-alive");
            // conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                in = conn.getInputStream();//获得图片的数据流
                //gzipStream= new GZIPInputStream(in);
                byte[] data = readInputStream(in);
                if (data != null)
                    saveFile(dirPath, filePath, data, x, y, zoom);
                return data;
            } else {
                return null;
            }
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(6, TimeUnit.SECONDS)
//                    .readTimeout(6, TimeUnit.SECONDS)
//                    .build();
//            final Request request = new Request
//                    .Builder()
//                    .get()
//                    .url(url)
//                    .build();
//            Call call = client.newCall(request);
//            Response response = call.execute();
//            InputStream inputStream = response.body().byteStream();
//            try {
//                byte[] data = readInputStream(inputStream);
//                saveFile(data, x, y, zoom);
//                return data;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (gzipStream != null) {
                try {
                    gzipStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) try {
                in.close();
            } catch (Exception ignored) {
            }
            if (conn != null)
                conn.disconnect();
        }
    }

    private static byte[] readInputStream(InputStream in) {
        int len = 0;
        ByteArrayOutputStream out = null;
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            out = new ByteArrayOutputStream();
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);  //把数据写入内存
            }
            out.close();  //关闭内存输出流
            return out.toByteArray(); //把内存输出流转换成byte数组
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out != null) try {
                out.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void saveFile(String dirPath, String filePath, byte[] bytes, int x, int y, int zoom) {
        File dirFile = new File(dirPath);
        // 文件夹不存在则创建文件夹
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        if (dirFile.exists()) {
            File file = new File(filePath);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public int getTileHeight() {

        return TILE_HEIGHT;
    }

    @Override
    public int getTileWidth() {

        return TILE_WIDTH;
    }

}
