package process.filepackage;

import process.fund.constant.FundConstant;
import utils.DateUtil;
import utils.EmailUtil;
import utils.LogUtil;

import javax.mail.MessagingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FilePackageApp {
    /**
     * 读取文件路径列表，将对应文件打包成压缩包
     *
     * @param pathListFile 包含文件路径的文件（每行一个路径）
     * @param zipFilePath  输出的压缩包路径
     * @throws IOException 文件操作异常
     */
    public void packFilesToZip(String pathListFile, String zipFilePath) throws IOException {
        // 读取文件路径列表
        List<String> filePaths = Files.readAllLines(Paths.get(pathListFile));

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String filePath : filePaths) {
                // 去除路径两端的空白字符
                filePath = filePath.trim();
                if (filePath.isEmpty()) {
                    continue;
                }

                File file = new File(filePath);
                if (!file.exists()) {
                    System.out.println("警告：文件不存在，跳过: " + filePath);
                    continue;
                }

                if (file.isFile()) {
                    // 添加文件到压缩包
                    addFileToZip(file, filePath, zos);
                } else if (file.isDirectory()) {
                    // 如果是目录，递归添加目录中的所有文件
                    addDirectoryToZip(file, filePath, zos);
                }
            }

            System.out.println("文件打包完成: " + zipFilePath);
        }
    }

    /**
     * 将单个文件添加到压缩包
     */
    private void addFileToZip(File file, String filePath, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            // 使用原始路径作为压缩包内的路径
            ZipEntry zipEntry = new ZipEntry(filePath);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
            System.out.println("已添加文件: " + filePath);
        }
    }

    /**
     * 将目录及其内容添加到压缩包
     */
    private void addDirectoryToZip(File dir, String basePath, ZipOutputStream zos) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            String filePath = basePath + "/" + file.getName();
            if (file.isFile()) {
                addFileToZip(file, filePath, zos);
            } else if (file.isDirectory()) {
                addDirectoryToZip(file, filePath, zos);
            }
        }
    }

    /**
     * 解压压缩包并按路径替换对应文件
     *
     * @param zipFilePath     压缩包路径
     * @param targetDirectory 目标解压目录（可选，如果为null则按原路径解压）
     * @throws IOException 文件操作异常
     */
    public void extractAndReplaceFiles(String zipFilePath, String targetDirectory) throws IOException {
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();

                // 确定目标文件路径
                String targetPath;
                if (targetDirectory != null) {
                    targetPath = Paths.get(targetDirectory, entryName).toString();
                } else {
                    targetPath = entryName;
                }

                File targetFile = new File(targetPath);

                if (entry.isDirectory()) {
                    // 创建目录
                    targetFile.mkdirs();
                    System.out.println("创建目录: " + targetPath);
                } else {
                    // 创建父目录
                    File parentDir = targetFile.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    // 如果文件已存在，先备份
                    if (targetFile.exists()) {
                        String backupPath = targetPath + ".backup." + System.currentTimeMillis();
                        Files.copy(targetFile.toPath(), Paths.get(backupPath));
                        System.out.println("备份原文件: " + backupPath);
                    }

                    // 解压文件
                    try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                        System.out.println("解压文件: " + targetPath);
                    }
                }

                zis.closeEntry();
            }

            System.out.println("文件解压完成");
        }
    }

    /**
     * 创建文件路径列表文件的辅助方法
     *
     * @param filePaths  文件路径数组
     * @param outputFile 输出的路径列表文件
     */
    public void createPathListFile(String[] filePaths, String outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (String path : filePaths) {
                writer.println(path);
            }
        }
        System.out.println("路径列表文件已创建: " + outputFile);
    }

    // 使用示例
    public static void main(String[] args) throws MessagingException {
        FilePackageApp manager = new FilePackageApp();

        // 1是打包2是解包
        int state = 1;
        String backupFilePath = "backup.zip";

        try {
            if (state == 1) {
                // 示例1：创建路径列表文件
                String[] filesToPack = {
                        "src/main/resources"
                };
                manager.createPathListFile(filesToPack, "src/main/file_paths.txt");

                // 示例2：打包文件
                manager.packFilesToZip("src/main/file_paths.txt", backupFilePath);

                // 示例3：解压并替换文件（解压到指定目录）
                // manager.extractAndReplaceFiles("backup.zip", "C:/restore");

                // 将打包文件发到邮箱
                // 收件人信息
                String mailTo = FundConstant.RECEIVER_EMAIL_NAME;
                String subject = "文档：FilePackageApp同步打包文件" + DateUtil.getCurrentDate();
                EmailUtil.sendEmail(EmailUtil.EmailSendType.ONE_SIX_THREE
                        , mailTo, subject, subject, backupFilePath);
                LogUtil.info("Email sent successfully.");
            } else if (state == 2) {
                // 示例4：解压并按原路径替换文件
                manager.extractAndReplaceFiles("backup.zip", null);
            }
        } catch (IOException e) {
            System.err.println("操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
