import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CopyUtil {
    //    private static String localPath = new File(System.getProperty("user.dir")).getParent();
    private static String localPath = new File("C:\\Users\\Admin\\Desktop\\test\\testJava\\script").getParent();
    private static String projectPath;
    private static String BackupName = "default";

    public static void main(String[] args) throws IOException {
        init();
        replaceBackupName();
        copyFiles();

    }

    private static void replaceBackupName() {
        String fileName = localPath + "/conf/config.properties";
        String oldstr;
        String newStr;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(fileName, "rw");
            String line;
            long lastPoint = 0;
            while ((line = raf.readLine()) != null) {
                final long ponit = raf.getFilePointer();
                if (line.startsWith("dir=")) {
                    oldstr = line.substring(4);
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    newStr = df.format(new Date());
                    String str = line.replace(oldstr, newStr);
                    BackupName = newStr;
                    raf.seek(lastPoint);
                    raf.writeBytes(str);
                    throw new IMFindException();
                }
                lastPoint = ponit;
            }
        } catch (IMFindException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void init() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(localPath + "/conf/config.properties"));
        String line = reader.readLine();
        projectPath = line.substring(line.indexOf("=") + 1);
    }

    private static void copyFiles() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(localPath + "/conf/diffFile.txt"));
        String str;
        String source;
        String target;
        while ((str = reader.readLine()) != null) {
            source = getSourceFileName(str);
            target = getTargetFileName(str);
            copyFile(source, target);
        }
        reader.close();


    }

    private static String getSourceFileName(String str) {
        String source;
        if (str.endsWith(".java")) {
            source = projectPath + "/" + str.replaceAll("/src/main/java", "/target/classes").replaceAll(".java", ".class");
        } else {
            source = projectPath + "/" + str;
        }
        return source;
    }

    private static String getTargetFileName(String str) {
        String target = localPath + "/doc/" + BackupName + "/local/";
        if (str.endsWith(".java")) {
            String s = str.replaceAll("/src/main/java", "").replaceAll(".java", ".class");
            target = target + "jar/" + s;
        } else if (str.endsWith(".business.xml")) {
            target = null;
        } else if (str.endsWith(".namingsql.xml")) {
            target = null;
        } else if (str.endsWith(".service.xml")) {
            target = null;
        } else if (str.endsWith(".usl")) {
            target = null;
        } else if (str.endsWith(".js")) {
            target = null;
        } else if (str.endsWith(".")) {
            target = null;
        } else {
            target = target + "others/" + str.replaceAll("/src/main", "");
        }
        return target;
    }

    private static void copyFile(String sourceFile, String targetFile) throws IOException {
        File source = new File(sourceFile);
        if (!source.exists()) {
            // TODO 记录被删除文件
            return;
        }
        File target = new File(targetFile);
        File parentFile = target.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(target);
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = in.read(b)) > 0) {
            out.write(b, 0, len);
        }
        out.close();
        in.close();
    }

    private static class IMFindException extends Exception {
    }
}