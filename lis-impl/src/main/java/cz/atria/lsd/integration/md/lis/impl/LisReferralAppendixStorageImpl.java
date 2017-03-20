package cz.atria.lsd.integration.md.lis.impl;

import cz.atria.lsd.integration.md.lis.api.LisReferralAppendixStorage;
import cz.atria.md.referral.TooLargeReferralAppendixException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: tnurtdinov
 * Date: 07.12.12
 * Time: 16:36
 */

public class LisReferralAppendixStorageImpl implements LisReferralAppendixStorage
{
    private static Logger logger = LoggerFactory.getLogger(LisReferralAppendixStorageImpl.class);
	private FTPClient client;

	private  String host;

	private  String user;

	private String password;

	private long maxFileSize = 100000000;

	public LisReferralAppendixStorageImpl(String host, String user, String password, Long maxFileSize)
	{
		this.host = host;
		this.user = user;
		this.password = password;
		if (maxFileSize != null)
		{
			this.maxFileSize = maxFileSize;
		}
	}

	public LisReferralAppendixStorageImpl()
	{
	}

	public void init()
	{
		if (client == null)
			client = new FTPClient();
		try
		{
			client.connect(host);
			client.login(user, password);
		}
		catch (IOException e)
		{
            logger.error("LisReferralAppendixStorage login failed", e);
			throw new RuntimeException(e);
		}
	}

	public void destroy()
	{
		try
		{
			client.logout();
			client.disconnect();
		}
		catch (IOException e)
		{
            logger.error("LisReferralAppendixStorage logout failed", e);
			throw new RuntimeException(e);
		}
	}

	private  String parsePath(String ftpPath)
	{
		//ftp://user:password@prime.kirkazan.ru/in/tnurdinov/mcl.xml"
		String [] url   = ftpPath.split(host);
		if(url.length>1)
			return url[1];
		else
			return url[0];
	}
	@Override
	public InputStream getContent(String path)
	{
		try
		{
			String actualPath = parsePath(path);
			FTPFile[] ftpFiles = client.listFiles(actualPath);
			if (ftpFiles.length>1)
				throw new RuntimeException("more than one file by path = " + path);
			if (ftpFiles.length < 1)
				throw new RuntimeException("not found file by path = " + path);
			if (ftpFiles.length == 1 && ftpFiles[0].getSize()> maxFileSize)
			{
				throw new TooLargeReferralAppendixException("file by path = " + path + " is too large");
			}
			return client.retrieveFileStream(actualPath);
		}
		catch (IOException e)
		{
            logger.error("Cannot get content", e);
			return null;
		}
	}
}
