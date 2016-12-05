package cz.atria.lsd.integration.md.lis.api;

import java.io.InputStream;

/**
 * User: tnurtdinov
 * Date: 07.12.12
 * Time: 16:36
 */
public interface LisReferralAppendixStorage
{
	public InputStream getContent(String path);

	public void init();

	public void destroy() ;
}
