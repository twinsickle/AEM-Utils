package com.twinsickle.aem.utils.ssh;

import com.jcraft.jsch.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SFTP implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(SFTP.class);
    private static final String SFTP_CHANNEL = "sftp";
    private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
    private static final String DISABLE = "no";
    private static final String PREFERRED_AUTHENTICATIONS = "PreferredAuthentications";
    private static final String PASSWORD = "password";

    private JSch client = new JSch();
    private Session session;
    private SSHCredentials creds;
    private ChannelSftp sftp;

    private SFTP(SSHCredentials creds){
        this.creds = creds;
    }

    public static SFTP open(SSHCredentials creds) throws SFTPConnectionFailedException{
        SFTP sftp = new SFTP(creds);
        sftp.open();
        return sftp;
    }

    private void open() throws SFTPConnectionFailedException{
        try {
            Properties config = new Properties();
            config.put(STRICT_HOST_KEY_CHECKING, DISABLE);
            config.put(PREFERRED_AUTHENTICATIONS, PASSWORD);
            session = client.getSession(creds.getUsername(), creds.getUrl(), creds.getPort());
            session.setConfig(config);
            session.setPassword(creds.getPassword());
            session.connect();
            sftp = (ChannelSftp) session.openChannel(SFTP_CHANNEL);
            sftp.connect();
        } catch (JSchException je){
            throw new SFTPConnectionFailedException("Failed to open SFTP connection");
        }
    }

    public Optional<String> getFileName(String path, String regex){
        return tryGet(() -> {
            Vector files = sftp.ls(path);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(StringUtils.EMPTY);
            for (Object file : files) {
                if (file instanceof ChannelSftp.LsEntry) {
                    ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) file;
                    if (!entry.getAttrs().isDir()) {
                        matcher.reset(entry.getFilename());
                        if (matcher.matches()) {
                            return entry.getFilename();
                        }
                    }
                }
            }
            return null;
        });
    }

    public Optional<InputStream> getFile(String fileName){
        if(fileName != null){
            return tryGet(() -> sftp.get(fileName));
        }
        return Optional.empty();
    }

    public Optional<String> getCurrentDir(){
        return tryGet(() -> sftp.pwd());
    }

    private boolean dirExists(String dirName){
        return getCurrentDir()
                .map(currentDir -> createPath(currentDir, dirName))
                .map(this::checkDirStat)
                .orElse(Boolean.FALSE);
    }

    private boolean checkDirStat(String dirPath){
        return tryGet(() -> sftp.stat(dirPath)).isPresent();
    }

    public void makeDir(String dirName){
        boolean exists = dirExists(dirName);
        if(!exists){
            getCurrentDir()
                    .ifPresent(currentDir -> createDir(createPath(currentDir, dirName)));
        }
    }

    private void createDir(String fullPath){
        trySet(() -> sftp.mkdir(fullPath));
    }

    public void moveFile(String oldFileName, String folder, String newFileName){
        getCurrentDir()
                .ifPresent(currentDir -> {
                    String oldFilePath = createPath(currentDir, oldFileName);
                    String newFilePath = createPath(currentDir, folder, newFileName);
                    trySet(() -> sftp.rename(oldFilePath, newFilePath));
                });
    }

    public String createPath(String... pathPieces){
        if(pathPieces.length == 0){
            return StringUtils.EMPTY;
        }

        if(StringUtils.equals(pathPieces[0], FileSystem.SEPARATOR)){
            return pathPieces[0] + StringUtils.join(
                    ArrayUtils.subarray(pathPieces, 1, pathPieces.length),
                    FileSystem.SEPARATOR);
        }

        return StringUtils.join(pathPieces, FileSystem.SEPARATOR);
    }

    @Override
    public void close(){
        if(sftp != null){
            sftp.disconnect();
        }

        if(session != null){
            session.disconnect();
        }
    }

    private <T> Optional<T> tryGet(SftpSupplier<T> supplier){
        try {
            return Optional.ofNullable(supplier.supply());
        } catch (SftpException se){
            LOG.info("SFTP Exception Encountered", se);
        }
        return Optional.empty();
    }

    private void trySet(SftpConsumer consumer){
        try {
            consumer.consume();
        } catch (SftpException se){
            LOG.info("SFTP Exception Encountered", se);
        }
    }

    @FunctionalInterface
    private interface SftpConsumer {
        void consume() throws SftpException;
    }

    @FunctionalInterface
    private interface SftpSupplier<T> {
        T supply() throws SftpException;
    }
}
