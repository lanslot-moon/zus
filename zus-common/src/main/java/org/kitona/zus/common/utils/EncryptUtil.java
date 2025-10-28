package org.kitona.zus.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * 文件加密工具类
 * ----------------------------------------------------------------
 * ｜    0-4位    ｜   4-20位  ｜   20-24位  ｜ 24-64位  ｜ 64-.....｜
 * ｜------------------------------------------------------------｜
 * ｜   4位版本号  ｜ 16位IV向量 ｜ 4位加密长度 ｜40位占位符 ｜ 文件内容 ｜
 * ----------------------------------------------------------------
 */
@SuppressWarnings("all")
@Slf4j
public final class EncryptUtil {

    private EncryptUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final String DEFAULT_ALGORITHM = "AES";
    private static final String DEFAULT_FULL_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * 解密文件
     *
     * @param decryptKey 解密秘钥
     * @param in         输入流
     * @param destFile   目标文件
     * @return
     */
    public static File decryptFile(String decryptKey, InputStream in, File destFile) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if (in == null) {
            throw new IllegalArgumentException("decryptFile InputStream cannot be null");
        }

        Files.createDirectories(destFile.getParentFile().toPath());
        destFile.createNewFile();

        //跳过版本
        in.skip(4);

        // 读取iv
        byte[] iv = in.readNBytes(16);
        if (iv.length != 16) {
            throw new IOException("iv error must be 16 bytes");
        }

        // 读取加密长度
        byte[] sizeBytes = in.readNBytes(4);
        if (sizeBytes.length != 4) {
            throw new IOException("shard size length error must be 4 bytes");
        }
        int encryptSize = byteToInt(sizeBytes);

        //跳过占位符
        in.skip(40);

        // 初始化Cipher
        Cipher cipher = initAESCipher(iv, decryptKey, Cipher.DECRYPT_MODE);

        // 读取文件
        try (FileOutputStream out = new FileOutputStream(destFile);
             CipherOutputStream cipherOut = new CipherOutputStream(out, cipher)) {
            byte[] buffer = new byte[encryptSize];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                cipherOut.write(buffer, 0, bytesRead);
            }
            return destFile;
        } catch (Exception e) {
            throw new IOException("Error during file decryption", e);
        }
    }


    public static void decryptFile(String decryptKey, InputStream inputStream, OutputStream outputStream) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        //跳过版本
        inputStream.skip(4);

        // 读取iv
        byte[] iv = inputStream.readNBytes(16);
        if (iv.length != 16) {
            throw new IOException("iv error must be 16 bytes");
        }

        // 读取加密长度
        byte[] sizeBytes = inputStream.readNBytes(4);
        if (sizeBytes.length != 4) {
            throw new IOException("shard size length error must be 4 bytes");
        }

        //跳过占位符
        inputStream.skip(40);

        // 初始化Cipher
        Cipher cipher = initAESCipher(iv, decryptKey, Cipher.DECRYPT_MODE);

        // 读取文件
        int encryptSize = byteToInt(sizeBytes);
        try (CipherOutputStream cipherOut = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[encryptSize];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOut.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new IOException("Error during file decryption", e);
        }
    }

    /**
     * 加密文件
     *
     * @param encryptKey            加密秘钥
     * @param iv                    iv
     * @param version               版本
     * @param shardEncryptionLength 每个分片加密长度
     * @param inputFile             输入文件
     * @param destFile              目标文件
     */
    public static File encryptFile(String encryptKey, byte[] iv, int version, int shardEncryptionLength, File inputFile, File destFile) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("Input file does not exist");
        }

        Files.createDirectories(destFile.getParentFile().toPath());
        destFile.createNewFile();

        // 需要16位iv向量写入
        if (iv.length != 16) {
            throw new IOException("iv error must 16 length");
        }

        Cipher cipher = initAESCipher(iv, encryptKey, Cipher.ENCRYPT_MODE);

        try (FileInputStream in = new FileInputStream(inputFile);
             FileOutputStream out = new FileOutputStream(destFile);
             CipherOutputStream cipherOut = new CipherOutputStream(out, cipher)) {

            // 写入版本号（这里假设为0）
            out.write(ByteBuffer.allocate(4).putInt(version).array());

            // 写入IV
            out.write(iv);

            // 写入加密数据长度占位符
            out.write(ByteBuffer.allocate(4).putInt(shardEncryptionLength).array());

            // 写入占位符
            for (int i = 0; i < 40; i++) {
                out.write(0);
            }

            // 加密并写入数据
            byte[] buffer = new byte[shardEncryptionLength];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                cipherOut.write(buffer, 0, bytesRead);
            }

            return destFile;
        } catch (Exception e) {
            throw new IOException("Error during file encryption", e);
        }
    }


    /**
     * 加密文件
     *
     * @param encryptKey            加密秘钥
     * @param iv                    16位的IV向量
     * @param shardEncryptionLength 分片加密的长度，一般1024,可以根据内存调整
     * @param inputStream           文件输入流
     * @param outputStream          文件输出流
     * @throws IOException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static void encryptFile(String encryptKey, byte[] iv, int version, int shardEncryptionLength, InputStream inputStream, OutputStream outputStream) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        // 需要16位iv向量写入
        if (iv.length != 16) {
            throw new IOException("iv error must 16 length");
        }

        Cipher cipher = initAESCipher(iv, encryptKey, Cipher.ENCRYPT_MODE);

        try (CipherOutputStream cipherOut = new CipherOutputStream(outputStream, cipher)) {

            // 写入版本号（这里假设为0）
            outputStream.write(ByteBuffer.allocate(4).putInt(version).array());

            // 写入IV
            outputStream.write(iv);

            // 写入加密数据长度占位符
            outputStream.write(ByteBuffer.allocate(4).putInt(shardEncryptionLength).array());

            // 写入占位符
            for (int i = 0; i < 40; i++) {
                outputStream.write(0);
            }

            // 加密并写入数据
            byte[] buffer = new byte[shardEncryptionLength];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOut.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new IOException("Error during file encryption", e);
        }
    }


    /**
     * 加密文件
     *
     * @param encryptKey            加密秘钥
     * @param shardEncryptionLength 分片加密的长度，一般1024,可以根据内存调整
     * @param inputStream           文件输入流
     * @param outputStream          文件输出流
     * @throws IOException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static void encryptFile(String encryptKey, int shardEncryptionLength, InputStream inputStream, OutputStream outputStream) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 生成随机的IV
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        encryptFile(encryptKey, iv, 1, shardEncryptionLength, inputStream, outputStream);
    }


    /**
     * AES 密钥字符串长度及其字节编码总是匹配 AES 的标准密钥长度（128 位/16 字节、192 位/24 字节或 256 位/32 字节）将能够预防 InvalidKeyException 异常的出现
     */
    private static Cipher initAESCipher(byte[] iv, String sKey, int cipherMode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        sKey = adaptEncryptKey(sKey);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(sKey.getBytes(StandardCharsets.UTF_8), EncryptUtil.DEFAULT_ALGORITHM);
        Cipher cipher = Cipher.getInstance(EncryptUtil.DEFAULT_FULL_ALGORITHM);
        cipher.init(cipherMode, key, zeroIv);
        return cipher;
    }

    /**
     * 根据传入的密钥字符串自动适配到 AES 标准密钥长度。
     * AES支持的标准密钥长度为128位、192位、或者256位(16字节、24字节、或者32字节).
     */
    private static String adaptEncryptKey(String sKey) {
        final int aesKeyLength128 = 16;
        final int aesKeyLength192 = 24;
        final int aesKeyLength256 = 32;

        if (sKey.length() >= aesKeyLength256) {
            // 截取到 256 位
            return sKey.substring(0, aesKeyLength256);
        } else if (sKey.length() >= aesKeyLength192) {
            // 截取到 192 位
            return sKey.substring(0, aesKeyLength192);
        } else if (sKey.length() >= aesKeyLength128) {
            // 填充到 128 位
            return sKey.substring(0, aesKeyLength128);
        }
        return String.format("%-16s", sKey).replace(' ', '0');
    }


    /**
     * bytes长度转成Int
     *
     * @param bytes
     * @return
     */
    private static int byteToInt(byte[] bytes) {
        if (bytes.length != 4) {
            throw new IllegalArgumentException("Byte array must be 4 bytes long");
        }
        return ByteBuffer.wrap(bytes).getInt();
    }


    /**
     * 文件下载输出流
     */
    public static class FileDownloadBufferInputStream extends InputStream {
        private final HttpURLConnection connection;
        private final InputStream inputStream;

        public FileDownloadBufferInputStream(String downloadUrl, String contentType) throws IOException {
            this.connection = createDownloadUrlBufferInputStream(downloadUrl, contentType);
            try {
                this.inputStream = connection.getInputStream();
            } catch (IOException e) {
                throw new IOException("Failed to get input stream from connection", e);
            }
        }

        public FileDownloadBufferInputStream(String downloadUrl) throws IOException {
            this.connection = createDownloadUrlBufferInputStream(downloadUrl, null);
            try {
                this.inputStream = connection.getInputStream();
            } catch (IOException e) {
                throw new IOException("Failed to get input stream from connection", e);
            }
        }

        private HttpURLConnection createDownloadUrlBufferInputStream(String downloadUrl, String contentType) throws IOException {
            URL url;
            try {
                url = new URL(downloadUrl);
            } catch (MalformedURLException e) {
                throw new IOException("Invalid URL: " + downloadUrl, e);
            }

            // 打开连接
            HttpURLConnection httpURLConnection;
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new IOException("Failed to open connection: " + downloadUrl, e);
            }

            // 设置请求方法
            try {
                httpURLConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                throw new IOException("Failed to set request method", e);
            }

            httpURLConnection.setRequestProperty("Accept", "*/*");
            if (StringUtils.isBlank(contentType)) {
                // 设置请求头（根据需要）
                httpURLConnection.setRequestProperty("Accept", contentType);
            }
            return httpURLConnection;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read(); // 读取单个字节
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return inputStream.read(b, off, len); // 读取字节数组
        }

        @Override
        public void close() throws IOException {
            inputStream.close(); // 关闭输入流
            int responseCode = connection.getResponseCode(); // 获取响应码
            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.info("FileSocketUtils FileDownloadBufferInputStream.close文件下载成功，响应代码: {}", responseCode);
            } else {
                log.error("FileSocketUtils FileDownloadBufferInputStream.close文件下载失败，响应代码: {},错误信息:{}", responseCode, connection.getResponseMessage());
                throw new IOException("FileDownloadBufferInputStream download error:" + connection.getResponseMessage());
            }
            connection.disconnect(); // 关闭连接
        }

        @Override
        public int available() throws IOException {
            return inputStream.available(); // 返回可读字节数
        }

        @Override
        public long skip(long n) throws IOException {
            return inputStream.skip(n); // 跳过指定字节数
        }
    }


    /**
     * 文件上传输出流
     */
    public static class FileUploadBufferOutputStream extends OutputStream {

        private final HttpURLConnection connection;

        private final OutputStream outputStream;

        // 需要上传的文件路径中是否设置了文件类型
        public FileUploadBufferOutputStream(String uploadUrl, String contentType) throws IOException {
            this.connection = createUploadUrlBufferOutputStream(uploadUrl, contentType);
            try {
                this.outputStream = connection.getOutputStream(); // 获取连接的输出流
            } catch (IOException e) {
                throw new IOException("Failed to get output stream from connection", e);
            }
        }

        // 需要上传的文件路径中是否设置了文件类型
        public FileUploadBufferOutputStream(String uploadUrl) throws IOException {
            this.connection = createUploadUrlBufferOutputStream(uploadUrl, null);
            try {
                this.outputStream = connection.getOutputStream(); // 获取连接的输出流
            } catch (IOException e) {
                throw new IOException("Failed to get output stream from connection", e);
            }
        }

        private HttpURLConnection createUploadUrlBufferOutputStream(String uploadUrl, String contentType) throws IOException {
            // 创建 URL 对象
            URL url = null;
            try {
                url = new URL(uploadUrl);
            } catch (MalformedURLException e) {
                throw new IOException("Invalid URL: " + uploadUrl, e);
            }
            // 打开连接
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new IOException("Failed to open connection: " + uploadUrl, e);
            }

            // 设置请求方法
            httpURLConnection.setDoOutput(true);
            try {
                httpURLConnection.setRequestMethod("PUT");
            } catch (ProtocolException e) {
                throw new IOException("Failed to set request method", e);
            }

            if (StringUtils.isNotBlank(contentType)) {
                httpURLConnection.setRequestProperty("Content-Type", contentType);
            }
            // 设置请求头
            return httpURLConnection;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b); // 将单个字节写入输出流
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            outputStream.write(b, off, len); // 将字节数组的部分写入输出流
        }

        @Override
        public void flush() throws IOException {
            outputStream.flush(); // 刷新输出流
        }

        @Override
        public void close() throws IOException {
            try {
                int responseCode = connection.getResponseCode(); // 获取响应码
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    log.info("FileSocketUtils FileUploadBufferOutputStream.close文件上传成功，响应代码: {}", responseCode);
                } else {
                    log.error("FileSocketUtils FileUploadBufferOutputStream.close文件上传失败，响应代码: {},错误信息:{}", responseCode, connection.getResponseMessage());
                    throw new IOException("FileUploadBufferOutputStream upload error:" + connection.getResponseMessage());
                }
            } finally {
                outputStream.close(); // 关闭输出流
                connection.disconnect(); // 关闭连接
            }
        }
    }
}